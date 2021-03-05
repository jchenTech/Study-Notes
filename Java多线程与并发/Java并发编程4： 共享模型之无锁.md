> - Java中 **synchronized** 和 **ReentrantLock** 等 独占锁 就是 **`悲观锁`** 思想的实现
> - 在Java中**java.util.concurrent.atomic包下面的原子变量类**就是使用了**`乐观锁`**的一种实现方式 **`CAS`** 实现的

- 管程即`monitor`是阻塞式的悲观锁实现并发控制，这章我们将通过非阻塞式的乐观锁的来实现并发控制

## 1. 问题提出

- 有如下需求，保证`account.withdraw`取款方法的线程安全，下面使用`synchronized`保证线程安全

```java
/**
 * Description: 使用重量级锁synchronized来保证多线程访问共享资源发生的安全问题
 */
@Slf4j(topic = "guizy.Test1")
public class Test1 {

    public static void main(String[] args) {
        Account.demo(new AccountUnsafe(10000));
        Account.demo(new AccountCas(10000));
    }
}

class AccountUnsafe implements Account {
    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        // 通过这里加锁就可以实现线程安全，不加就会导致线程安全问题
        synchronized (this) {
            balance -= amount;
        }
    }
}


interface Account {
    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * Java8之后接口新特性，可以添加默认方法
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        ts.forEach(thread -> thread.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()
                + " cost: " + (end - start) / 1000_000 + " ms");
    }
}
```

### 解决思路-无锁

- 上面的代码中使用`synchronized加锁`操作来保证线程安全，但是 **synchronized加锁操作太耗费资源 (因为底层使用了操作系统mutex指令，造成内核态和用户态的切换)**，这里我们使用 **无锁** 来解决此问题

```java
class AccountCas implements Account {
  //使用原子整数: 底层使用CAS+重试的机制
  private AtomicInteger balance;

  public AccountCas(int balance) {
    this.balance = new AtomicInteger(balance);
  }

  @Override
  public Integer getBalance() {
    //得到原子整数的值
    return balance.get();
  }

  @Override
  public void withdraw(Integer amount) {
    while(true) {
      //获得修改前的值
      int prev = balance.get();
      //获得修改后的值
      int next = prev - amount;
      //比较并设置值
      /*
        此时的prev为共享变量的值，如果prev被别的线程改了.也就是说: 自己读到的共享变量的值 和 共享变量最新值 不匹配,
        就继续where(true),如果匹配上了，将next值设置给共享变量.
        
        AtomicInteger中value属性，被volatile修饰，就是为了确保线程之间共享变量的可见性.
      */
      if(balance.compareAndSet(prev, next)) {
        break;
      }
    }
  }
}
```

## 2. CAS 与 volatile (重点)

> 使用原子操作来保证线程访问共享资源的安全性，cas+重试的机制来确保(乐观锁思想)，相对于悲观锁思想的`synchronized,reentrantLock`来说，cas的方式效率会更好!

### 2.1 cas + 重试 的原理

前面看到的`AtomicInteger`的解决方法，内部并没有用锁来保护共享变量的线程安全。那么它是如何实现的呢？

```java
@Override
public void withdraw(Integer amount) {
    // 核心代码
    // 需要不断尝试，直到成功为止
    while (true){
        // 比如拿到了旧值 1000
        int prev = balance.get();
        // 在这个基础上 1000-10 = 990
        int next = prev - amount;
        /*
         compareAndSet 保证操作共享变量安全性的操作:
         ① 线程A首先获取balance.get(),拿到当前的balance值prev
         ② 根据这个prev值 - amount值 = 修改后的值next
         ③ 调用compareAndSet方法，首先会判断当初拿到的prev值,是否和现在的
          balance值相同;
          3.1、如果相同,表示其他线程没有修改balance的值，此时就可以将next值
            设置给balance属性
          3.2、如果不相同,表示其他线程也修改了balance值，此时就设置next值失败，
        然后一直重试，重新获取balance.get()的值,计算出next值,
        并判断本次的prev和balnce的值是否相同...重复上面操作
    */
        if (atomicInteger.compareAndSet(prev,next)){
            break;
        }
    }
}
```

其中的关键是 `compareAndSwap`（比较并设置值），它的简称就是 `CAS` （也有 Compare And Swap 的说法），它必须是原子操作。

![1594776811158](https://img-blog.csdnimg.cn/img_convert/436a166f7e783537464879edf14ccc0c.png)

**注意 :**

- 其实 `CAS` 的底层是 **lock cmpxchg** 指令（X86 架构），在单核 CPU 和多核 CPU 下都能够保证【比较-交换】的 **原子性**。
- 在多核状态下，某个核执行到带 lock 的指令时，CPU 会让总线锁住，当这个核把此指令执行完毕，再开启总线。这个过程中不会被线程的调度机制所打断，保证了多个线程对内存操作的准确性，是原子的。
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210202190842397.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

### 2.2 volatile的作用

在上面代码中的`AtomicInteger类`，保存值的value属性使用了`volatile` 修饰。获取共享变量时，为了保证该变量的可见性，需要使用 `volatile` 修饰。

volatile可以用来修饰 **`成员变量和静态成员变量`**，他可以避免线程从自己的工作缓存中查找变量的值，必须到主存中获取它的值，线程操作 volatile 变量都是直接操作主存。即一个线程对 volatile 变量的修改，对另一个线程可见。

> 注意：volatile 仅仅保证了共享变量的可见性，让其它线程能够看到最新值，但不能解决指令交错问题（不能保证原子性）

- CAS 必须借助 volatile 才能读取到共享变量的最新值来实现【比较并交换】的效果

### 2.3 为什么无锁效率高

- 使用CAS+重试---无锁情况下，即使重试失败，线程始终在高速运行，没有停歇，而 `synchronized`会让线程在没有获得锁的时候，发生上下文切换，进入阻塞。
  - 打个比喻：线程就好像高速跑道上的赛车，高速运行时，速度超快，一旦发生上下文切换，就好比赛车要减速、熄火，等被唤醒又得重新打火、启动、加速… 恢复到高速运行，代价比较大
- 但无锁情况下，因为线程要保持运行，需要额外 CPU 的支持，CPU 在这里就好比高速跑道，没有额外的跑道，线程想高速运行也无从谈起，虽然不会进入阻塞，但由于没有分到时间片，仍然会进入可运行状态，还是会导致上下文切换。

### 2.4 CAS 的特点

结合 `CAS` 和 `volatile` 可以实现无锁并发，适用于线程数少、多核 CPU 的场景下。

- `CAS` 是基于`乐观锁`的思想：最乐观的估计，不怕别的线程来修改共享变量，就算改了也没关系，我吃亏点再重试呗。

- `synchronized` 是基于`悲观锁`的思想：最悲观的估计，得防着其它线程来修改共享变量，我上了锁你们都别想改，我改完了解开锁，你们才有机会。

- CAS 体现的是无锁并发、无阻塞并发，请仔细体会这两句话的意思

  - 因为没有使用 synchronized，所以线程不会陷入阻塞，这是效率提升的因素之一
  - 但如果竞争激烈(写操作多)，可以想到重试必然频繁发生，反而效率会受影响

## 3. 原子整数

`java.util.concurrent.atomic`并发包提供了

- **`AtomicInteger`：整型原子类**
- AtomicLong：长整型原子类

- AtomicBoolean ：布尔型原子类

上面三个类提供的方法几乎相同，所以我们将以 `AtomicInteger`为例子来介绍。`AtomicInteger` 内部都是通过`cas`的原理来实现的

```java
public static void main(String[] args) {
    AtomicInteger i = new AtomicInteger(0);
    
    // 获取并自增（i = 0，结果 i = 1，返回 0），类似于 i++
    System.out.println(i.getAndIncrement());
    
    // 自增并获取（i = 1，结果 i = 2，返回 2），类似于 ++i
    System.out.println(i.incrementAndGet());
    
    // 自减并获取（i = 2，结果 i = 1，返回 1），类似于 --i
    System.out.println(i.decrementAndGet());
    
    // 获取并自减（i = 1，结果 i = 0，返回 1），类似于 i--
    System.out.println(i.getAndDecrement());
    
    // 获取并加值（i = 0，结果 i = 5，返回 0）
    System.out.println(i.getAndAdd(5));
    
    // 加值并获取（i = 5，结果 i = 0，返回 0）
    System.out.println(i.addAndGet(-5));
    
    // 获取并更新（i = 0，p 为 i 的当前值，结果 i = -2，返回 0）
    // 函数式编程接口，其中函数中的操作能保证原子，但函数需要无副作用
    System.out.println(i.getAndUpdate(p -> p - 2));
    
    // 更新并获取（i = -2，p 为 i 的当前值，结果 i = 0，返回 0）
    // 函数式编程接口，其中函数中的操作能保证原子，但函数需要无副作用
    System.out.println(i.updateAndGet(p -> p + 2));
    
    // 获取并计算（i = 0，p 为 i 的当前值，x 为参数1，结果 i = 10，返回 0）
    // 函数式编程接口，其中函数中的操作能保证原子，但函数需要无副作用
    // getAndUpdate 如果在 lambda 中引用了外部的局部变量，要保证该局部变量是 final 的
    // getAndAccumulate 可以通过 参数1 来引用外部的局部变量，但因为其不在 lambda 中因此不必是 final
    System.out.println(i.getAndAccumulate(10, (p, x) -> p + x));
    
    // 计算并获取（i = 10，p 为 i 的当前值，x 为参数1值，结果 i = 0，返回 0）
    // 函数式编程接口，其中函数中的操作能保证原子，但函数需要无副作用
    System.out.println(i.accumulateAndGet(-10, (p, x) -> p + x));
}
```

举个例子: updateAndGet的实现

```java
public static void main(String[] args) {

    AtomicInteger i = new AtomicInteger(5);

    updateAndGet(i, new IntUnaryOperator() {
        @Override
        public int applyAsInt(int operand) {
            return operand / 2;
        }
    });
    System.out.println(i.get()); // 2
}

public static void updateAndGet(AtomicInteger i, IntUnaryOperator operator) {
    while (true) {
        int prev = i.get(); // 5
        int next = operator.applyAsInt(prev);
        if (i.compareAndSet(prev, next)) {
            break;
        }
    }
}
```

步骤：

- 调用`updateAndGet`方法，将共享变量 i，`IntUnaryOperator`对象传递过去
- `updateAndGet`方法内部，传过来的`operator`对象，调用`IntUnaryOperator`中的`applyAsInt`方法，实际调用的就是传递过来的对象的方法，进行 / 操作
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201227190220505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

## 4. 原子引用 (AtomicReference)

为什么需要原子引用类型 ? (引用数据类型原子类)
- `AtomicReference`
- `AtomicMarkableReference`
- `AtomicStampedReference` 

为什么需要原子引用类型？**保证引用类型的共享变量是线程安全的（确保这个原子引用没有引用过别人）。**

基本类型原子类只能更新一个变量，如果需要原子更新多个变量，需要使用引用类型原子类。

例子 : 使用原子引用实现BigDecimal存取款的线程安全：

```java
public interface DecimalAccount {
    // 获取余额
    BigDecimal getBalance();
    // 取款
    void withdraw(BigDecimal amount);
    /**
    * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
    * 如果初始余额为 10000 那么正确的结果应当是 0
    */
    static void demo(DecimalAccount account) {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(account.getBalance());
    }
}
```

下面这个是不安全的实现过程：

```java
class DecimalAccountUnsafe implements DecimalAccount {
    BigDecimal balance;
    public DecimalAccountUnsafe(BigDecimal balance) {
        this.balance = balance;
    }
    @Override
    public BigDecimal getBalance() {
        return balance;
    }
    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal balance = this.getBalance();
        this.balance = balance.subtract(amount);
    }
}
```

解决代码如下：在`AtomicReference`类中，存在一个value类型的变量，保存对BigDecimal对象的引用。

```java
public class Test1 {

    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalAccountCas(new BigDecimal("10000")));
    }
}

class DecimalAccountCas implements DecimalAccount {

    //原子引用，泛型类型为小数类型
    private final AtomicReference<BigDecimal> balance;

    public DecimalAccountCas(BigDecimal balance) {
        this.balance = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true) {
            BigDecimal prev = balance.get();
            BigDecimal next = prev.subtract(amount);
            if (balance.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}


interface DecimalAccount {
    // 获取余额
    BigDecimal getBalance();

    // 取款
    void withdraw(BigDecimal amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(DecimalAccount account) {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(account.getBalance());
    }
}
```

### 4.1 ABA 问题 (重点)

如下程序所示，虽然 在other方法中存在两个线程对共享变量进行了修改，但是修改之后又变成了原值，main线程对修改过共享变量的过程是不可见的，这种操作这对业务代码并无影响。

```java
public class Test1 {

    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) {
        new Thread(() -> {
            String pre = ref.get();
            System.out.println("change");
            try {
                other();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Sleeper.sleep(1);
            //把ref中的A改为C
            System.out.println("change A->C " + ref.compareAndSet(pre, "C"));
        }).start();
    }

    static void other() throws InterruptedException {
        new Thread(() -> {
          // 此时ref.get()为A,此时共享变量ref也是A,没有被改过，此时CAS
          // 可以修改成功，B
            System.out.println("change A->B " + ref.compareAndSet(ref.get(), "B"));
        }).start();
        Thread.sleep(500);
        new Thread(() -> {
          // 同上, 修改为A
            System.out.println("change B->A " + ref.compareAndSet(ref.get(), "A"));
        }).start();
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201227200916774.png)

主线程仅能判断出共享变量的值与最初值 A是否相同，不能感知到这种从 A 改为 B 又改回 A 的情况，如果主线程希望：只要有其它线程【动过】共享变量，那么自己的 cas 就算失败，这时，仅比较值是不够的，需要再加一个版本号。使用`AtomicStampedReference`来解决。

### 4.2 AtomicStampedReference

解决ABA问题

```java
public class Test1 {
    //指定版本号
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) {
        new Thread(() -> {
            String pre = ref.getReference();
            //获得版本号
            int stamp = ref.getStamp(); // 此时的版本号还是第一次获取的
            System.out.println("change");
            try {
                other();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //把ref中的A改为C,并比对版本号，如果版本号相同，就执行替换，并让版本号+1
            System.out.println("change A->C stamp " + stamp + ref.compareAndSet(pre, "C", stamp, stamp + 1));
        }).start();
    }

    static void other() throws InterruptedException {
        new Thread(() -> {
            int stamp = ref.getStamp();
            System.out.println("change A->B stamp " + stamp + ref.compareAndSet(ref.getReference(), "B", stamp, stamp + 1));
        }).start();
        Thread.sleep(500);
        new Thread(() -> {
            int stamp = ref.getStamp();
            System.out.println("change B->A stamp " + stamp + ref.compareAndSet(ref.getReference(), "A", stamp, stamp + 1));
        }).start();
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201227201334161.png)

### 4.3 AtomicMarkableReference

- `AtomicStampedReference` 可以给原子引用加上版本号，**追踪原子引用整个的变化过程**，如：A -> B -> A ->C，通过AtomicStampedReference，我们可以知道，引用变量中途被更改了几次。
- 但是有时候，并不关心引用变量更改了几次，只是单纯的关心是否更改过，所以就有了`AtomicMarkableReference`

![1594803309714](https://img-blog.csdnimg.cn/img_convert/23026a8d2e27e7a4f474d15f0e3684bc.png)

```java
@Slf4j(topic = "guizy.TestABAAtomicMarkableReference")
public class TestABAAtomicMarkableReference {
    public static void main(String[] args) throws InterruptedException {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        
        // 参数2 mark 可以看作一个标记，表示垃圾袋满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, true);
        log.debug("主线程 start...");
        
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());
        
        new Thread(() -> {
            log.debug("打扫卫生的线程 start...");
            bag.setDesc("空垃圾袋");
            while (!ref.compareAndSet(bag, bag, true, false)) {
            }
            log.debug(bag.toString());
        }).start();
        
        Thread.sleep(1000);
        log.debug("主线程想换一只新垃圾袋？");
        
        boolean success = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了么？" + success);
        log.debug(ref.getReference().toString());
    }
}

class GarbageBag {
    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
}

23:00:24.062 guizy.TestABAAtomicMarkableReference [main] - 主线程 start...
23:00:24.069 guizy.TestABAAtomicMarkableReference [main] - com.guizy.cas.GarbageBag@2be94b0f 装满了垃圾
23:00:24.312 guizy.TestABAAtomicMarkableReference [Thread-0] - 打扫卫生的线程 start...
23:00:24.313 guizy.TestABAAtomicMarkableReference [Thread-0] - com.guizy.cas.GarbageBag@2be94b0f 空垃圾袋
23:00:25.313 guizy.TestABAAtomicMarkableReference [main] - 主线程想换一只新垃圾袋？
23:00:25.314 guizy.TestABAAtomicMarkableReference [main] - 换了么？false
23:00:25.314 guizy.TestABAAtomicMarkableReference [main] - com.guizy.cas.GarbageBag@2be94b0f 空垃圾袋
```

举例2:

```java
public class Test1 {
    //指定版本号
    static AtomicMarkableReference<String> ref = new AtomicMarkableReference<>("A", true);

    public static void main(String[] args) {
        new Thread(() -> {
            String pre = ref.getReference();
            System.out.println("change");
            try {
                other();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //把str中的A改为C,并比对版本号，如果版本号相同，就执行替换，并让版本号+1
            System.out.println("change A->C mark " + ref.compareAndSet(pre, "C", true, false));
        }).start();
    }

    static void other() throws InterruptedException {
        new Thread(() -> {
            System.out.println("change A->A mark " + ref.compareAndSet(ref.getReference(), "A", true, false));
        }).start();
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201227201642314.png)

AtomicStampedReference和AtomicMarkableReference两者的区别：

- **`AtomicStampedReference`** 需要我们传入整型变量作为版本号，来判定是否被更改过
- `AtomicMarkableReference`需要我们传入布尔变量 作为标记，来判断是否被更改过

## 5. 原子数组 (AtomicIntegerArray)

保证数组内的元素的线程安全，使用原子的方式更新数组里的某个元素

- `AtomicIntegerArray`：整形数组原子类
- `AtomicLongArray`：长整形数组原子类
- `AtomicReferenceArray`：引用类型数组原子类

上面三个类提供的方法几乎相同，所以我们这里以 `AtomicIntegerArray` 为例子来介绍。实例代码

- 普通数组内元素，多线程访问造成安全问题

```java
public class AtomicArrayTest {
    public static void main(String[] args) {
        demo(
            () -> new int[10],
            array -> array.length,
            (array, index) -> array[index]++,
            array -> System.out.println(Arrays.toString(array))
        );
    }

    /**
     * 参数1，提供数组、可以是线程不安全数组或线程安全数组
     * 参数2，获取数组长度的方法
     * 参数3，自增方法，回传 array, index
     * 参数4，打印数组的方法
     */
    // supplier 提供者 无中生有 ()->结果
    // function 函数 一个参数一个结果 (参数)->结果 , BiFunction (参数1,参数2)->结果
    // consumer 消费者 一个参数没结果 (参数)->void, BiConsumer (参数1,参数2)->void
    private static <T> void demo(Supplier<T> arraySupplier, Function<T, Integer> lengthFun,
                                 BiConsumer<T, Integer> putConsumer, Consumer<T> printConsumer) {
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();
        int length = lengthFun.apply(array);

        for (int i = 0; i < length; i++) {
            // 创建10个线程，每个线程对数组作 10000 次操作
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(array, j % length);
                }
            }));
        }

        ts.forEach(t -> t.start()); // 启动所有线程
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }); // 等所有线程结束

        printConsumer.accept(array);
    }
}

[9870, 9862, 9774, 9697, 9683, 9678, 9679, 9668, 9680, 9698]
```

- 使用`AtomicIntegerArray`来创建安全数组

```java
demo(
    ()-> new AtomicIntegerArray(10),
    (array) -> array.length(),
    (array, index) -> array.getAndIncrement(index),
    array -> System.out.println(array)
);

demo(
    ()-> new AtomicIntegerArray(10),
    AtomicIntegerArray::length,
    AtomicIntegerArray::getAndIncrement,
    System.out::println
);

[10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]
```

## 6. 字段更新器

保证多线程访问同一个对象的成员变量时，成员变量的线程安全性。

- `AtomicReferenceFieldUpdater` —引用类型的属性
- AtomicIntegerFieldUpdater —整形的属性
- AtomicLongFieldUpdater —长整形的属性

> 注意：利用字段更新器，可以针对对象的某个域（Field）进行原子操作，只能配合 `volatile` 修饰的字段使用，否则会出现异常。
>

```java
Exception in thread "main" java.lang.IllegalArgumentException: Must be volatile type
```

- 例子

```java
@Slf4j(topic = "guizy.AtomicFieldTest")
public class AtomicFieldTest {
    public static void main(String[] args) {
        Student stu = new Student();
        // 获得原子更新器
        // 泛型
        // 参数1 持有属性的类 参数2 被更新的属性的类
        // newUpdater中的参数：第三个为属性的名称
        AtomicReferenceFieldUpdater updater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        // 期望的为null, 如果name属性没有被别的线程更改过，默认就为null，此时匹配，就可以设置name为张三
        System.out.println(updater.compareAndSet(stu, null, "张三"));
        System.out.println(updater.compareAndSet(stu, stu.name, "王五"));
        System.out.println(stu);
    }
}

class Student {
    volatile String name;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}

true
true
Student{name='王五'}
```

## 7. 原子累加器 (LongAdder) (重要)

- LongAddr
- LongAccumulator
- DoubleAddr
- DoubleAccumulator

### 7.1 累加器性能比较

 AtomicLong，LongAddr性能比较：

```java
@Slf4j(topic = "guizy.Test")
public class Test {
    public static void main(String[] args) {
        System.out.println("----AtomicLong----");
        for (int i = 0; i < 5; i++) {
            demo(() -> new AtomicLong(), adder -> adder.getAndIncrement());
        }

        System.out.println("----LongAdder----");
        for (int i = 0; i < 5; i++) {
            demo(() -> new LongAdder(), adder -> adder.increment());
        }
    }

    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        // 4 个线程，每人累加 50 万
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start) / 1000_000);
    }
}

----AtomicLong----
20000000 cost:646
20000000 cost:707
20000000 cost:689
20000000 cost:713
20000000 cost:657
----LongAdder----
20000000 cost:148
20000000 cost:139
20000000 cost:130
20000000 cost:122
20000000 cost:116
```

LongAddr

- 性能提升的原因很简单，就是在有竞争时，设置多个累加单元（但不会超过cpu的核心数），Therad-0 累加 Cell[0]，而 Thread-1 累加Cell[1]… 最后将结果汇总。这样它们在累加时操作的不同的 Cell 变量，**因此减少了 CAS 重试失败**，从而提高性能。

AtomicLong

- 之前AtomicLong等都是在一个共享资源变量上进行竞争，`while(true)` 循环进行CAS重试，性能没有`LongAdder`高

### 7.2 原码之LongAdder 

LongAdder 是并发大师 @author Doug Lea （大哥李）的作品，设计的非常精巧。LongAdder 类有几个关键域

```java
// 累加单元数组, 懒惰初始化
transient volatile Cell[] cells;

// 基础值, 如果没有竞争, 则用 cas 累加这个域
transient volatile long base;

// 在 cells 创建或扩容时, 置为 1, 表示加锁
transient volatile int cellsBusy;
```

#### CAS锁

```java
// 不要用于实践！！！
public class LockCas {
    private AtomicInteger state = new AtomicInteger(0);
    public void lock() {
        while (true) {
            if (state.compareAndSet(0, 1)) {
                break;
            }
        }
    }
    public void unlock() {
        log.debug("unlock...");
        state.set(0);
    }
}
```



#### 原理之伪共享

其中 Cell 即为累加单元

```java
// 防止缓存行伪共享
@sun.misc.Contended
static final class Cell {
    volatile long value;
    Cell(long x) { value = x; }

    // 最重要的方法, 用来 cas 方式进行累加, prev 表示旧值, next 表示新值
    final boolean cas(long prev, long next) {
        return UNSAFE.compareAndSwapLong(this, valueOffset, prev, next);
    }
    // 省略不重要代码
}
```

下面讨论 @sun.misc.Contended 注解的重要意义

- 缓存行伪共享得从**缓存**说起
- 缓存与内存的速度比较

![img](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150051.png)

![img](https://img-blog.csdnimg.cn/img_convert/b4d95b0315026ad343cda2dd2d2f3c2a.png)

因为 CPU 与 内存的速度差异很大，需要靠预读数据至**缓存**来提升效率。而缓存以**缓存行**为单位，每个缓存行对应着一块内存，一般是 **64 byte**（8 个 long）缓存的加入会造成数据副本的产生，即同一份数据会缓存在不同核心的缓存行中

CPU 要保证数据的**一致性** (缓存一致性)，如果某个 CPU 核心更改了数据，其它 CPU 核心对应的整个缓存行必须失效

![img](https://img-blog.csdnimg.cn/img_convert/56de3a77bb6f96ba480f622083864c36.png)

因为 Cell 是数组形式，在内存中是连续存储的，一个 Cell 为 24 字节（16 字节的对象头和 8 字节的 value），因此缓存行可以存下 2 个的 Cell 对象。这样问题来了：
- Core-0 要修改 Cell[0]
- Core-1 要修改 Cell[1]

无论谁修改成功，都会导致对方 Core 的缓存行失效，比如 Core-0 中 Cell[0]=6000，Cell[1]=8000 要累加 Cell[0]=6001，Cell[1]=8000 ，这时会让 Core-1 的缓存行失效

`@sun.misc.Contended` 用来解决这个问题，它的原理是在使用此注解的对象或字段的前后各增加 128 字节大小的 padding（空白），从而让 CPU 将对象预读至缓存时占用不同的缓存行，这样，不会造成对方缓存行的失效

![img](https://img-blog.csdnimg.cn/img_convert/4f0aefac3f7bfaece8703efa77e342fa.png)



#### add方法分析

LongAdder 进行累加操作是调用 increment 方法，它又调用 add 方法。

```java
public void increment() {
    add(1L);
}
```

**第一步：add 方法分析，流程图如下**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210203225824450.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl81MDI4MDU3Ng==,size_16,color_FFFFFF,t_70)

源码如下：

```java
public void add(long x) {
    // as 为累加单元数组, b 为基础值, x 为累加值
    Cell[] as; long b, v; int m; Cell a;
    // 进入 if 的两个条件
    // 1. as 有值, 表示已经发生过竞争, 进入 if
    // 2. cas 给 base 累加时失败了, 表示 base 发生了竞争, 进入 if
    // 3. 如果 as 没有创建, 然后 cas 累加成功就返回，累加到 base 中 不存在线程竞争的时候用到。
    if ((as = cells) != null || !casBase(b = base, b + x)) {
        // uncontended 表示 cell 是否有竞争，这里赋值为 true 表示有竞争
        boolean uncontended = true;
        if (
            // as 还没有创建
            as == null || (m = as.length - 1) < 0 ||
            // 当前线程对应的 cell 还没有被创建，a为当线程的cell
            (a = as[getProbe() & m]) == null ||
            // 给当前线程的 cell 累加失败 uncontended=false ( a 为当前线程的 cell )
            !(uncontended = a.cas(v = a.value, v + x))
        ) {
            // 当 cells 为空时，累加操作失败会调用方法，
            // 当 cells 不为空，当前线程的 cell 创建了但是累加失败了会调用方法，
            // 当 cells 不为空，当前线程 cell 没创建会调用这个方法
            // 进入 cell 数组创建、cell 创建的流程
            longAccumulate(x, null, uncontended);
        }
    }
}

```

**第二步：longAccumulate 方法分析，流程图如下：**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210203233904164.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl81MDI4MDU3Ng==,size_16,color_FFFFFF,t_70)

源码如下：

```java
final void longAccumulate(long x, LongBinaryOperator fn,
                          boolean wasUncontended) {
    int h;
    // 当前线程还没有对应的 cell, 需要随机生成一个 h 值用来将当前线程绑定到 cell
    if ((h = getProbe()) == 0) {
        // 初始化 probe
        ThreadLocalRandom.current();
        // h 对应新的 probe 值, 用来对应 cell
        h = getProbe();
        wasUncontended = true;
    }
    // collide 为 true 表示需要扩容
    boolean collide = false;
    for (;;) {
        Cell[] as; Cell a; int n; long v;
        // 已经有了 cells
        if ((as = cells) != null && (n = as.length) > 0) {
            // 但是还没有当前线程对应的 cell
            if ((a = as[(n - 1) & h]) == null) {
                // 为 cellsBusy 加锁, 创建 cell, cell 的初始累加值为 x
                // 成功则 break, 否则继续 continue 循环
                if (cellsBusy == 0) {       // Try to attach new Cell
                    Cell r = new Cell(x);   // Optimistically create
                    if (cellsBusy == 0 && casCellsBusy()) {
                        boolean created = false;
                        try {               // Recheck under lock
                            Cell[] rs; int m, j;
                            if ((rs = cells) != null &&
                                (m = rs.length) > 0 &&
                                // 判断槽位确实是空的
                                rs[j = (m - 1) & h] == null) {
                                rs[j] = r;
                                created = true;
                            }
                        } finally {
                            cellsBusy = 0;
                        }
                        if (created)
                            break;
                        continue;           // Slot is now non-empty
                    }
                }
                // 有竞争, 改变线程对应的 cell 来重试 cas
                else if (!wasUncontended)
                    wasUncontended = true;
                // cas 尝试累加, fn 配合 LongAccumulator 不为 null, 配合 LongAdder 为 null
                else if (a.cas(v = a.value, ((fn == null) ? v + x : fn.applyAsLong(v, x))))
                    break;
                // 如果 cells 长度已经超过了最大长度, 或者已经扩容, 改变线程对应的 cell 来重试 cas
                else if (n >= NCPU || cells != as)
                    collide = false;
                // 确保 collide 为 false 进入此分支, 就不会进入下面的 else if 进行扩容了
                else if (!collide)
                    collide = true;
                // 加锁
                else if (cellsBusy == 0 && casCellsBusy()) {
                    // 加锁成功, 扩容
                    continue;
                }
                // 改变线程对应的 cell
                h = advanceProbe(h);
            }
            // 还没有 cells, cells==as是指没有其它线程修改cells，as和cells引用相同的对象，使用casCellsBusy()尝试给 cellsBusy 加锁
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                // 加锁成功, 初始化 cells, 最开始长度为 2, 并填充一个 cell
                // 成功则 break;
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            // 上两种情况失败, 尝试给 base 使用casBase累加
            else if (casBase(v = base, ((fn == null) ? v + x : fn.applyAsLong(v, x))))
                break;
        }
    }

```

#### sum 方法分析

获取最终结果通过 sum 方法，将各个累加单元的值加起来就得到了总的结果。

```java
public long sum() {
    Cell[] as = cells; Cell a;
    long sum = base;
    if (as != null) {
        for (int i = 0; i < as.length; ++i) {
            if ((a = as[i]) != null)
                sum += a.value;
        }
    }
    return sum;
}

```



## 9. Unsafe (重点)

### 9.1 Unsafe 对象的获取

`Unsafe` 对象提供了非常底层的，操作内存、线程的方法，`Unsafe` 对象不能直接调用，只能通过`反射`获得。`LockSupport` 的 `park` 方法，`cas` 相关的方法底层都是通过`Unsafe`类来实现的。

```java
public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    // Unsafe 使用了单例模式，unsafe 对象是类中的一个私有的变量 
    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
    theUnsafe.setAccessible(true);
    Unsafe unsafe = (Unsafe)theUnsafe.get(null);

}

```



### 9.2 Unsafe 模拟实现 CAS 操作

```java
public class Code_14_UnsafeTest {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        // 创建 unsafe 对象
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe)theUnsafe.get(null);

        // 拿到偏移量
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        // 进行 cas 操作
        Teacher teacher = new Teacher();
        unsafe.compareAndSwapLong(teacher, idOffset, 0, 100);
        unsafe.compareAndSwapObject(teacher, nameOffset, null, "lisi");

        System.out.println(teacher);
    }

}

@Data
class Teacher {

    private volatile int id;
    private volatile String name;

}


```



### 9.3 Unsafe 模拟实现原子整数

```java
public class Code_15_UnsafeAccessor {

    public static void main(String[] args) {
        Account.demo(new MyAtomicInteger(10000));
    }
}

class MyAtomicInteger implements Account {

    private volatile Integer value;
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = UNSAFE.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    public MyAtomicInteger(Integer value) {
        this.value = value;
    }

    public Integer get() {
        return value;
    }

    public void decrement(Integer amount) {
        while (true) {
            Integer preVal = this.value;
            Integer nextVal = preVal - amount;
            if(UNSAFE.compareAndSwapObject(this, valueOffset, preVal, nextVal)) {
                break;
            }
        }
    }

    @Override
    public Integer getBalance() {
        return get();
    }

    @Override
    public void withdraw(Integer amount) {
        decrement(amount);
    }
}

```



## 10. 本章小结

- CAS 与 volatile
- API
  - 原子整数
  - 原子引用
  - 原子数组
  - 字段更新器
  - 原子累加器
- Unsafe

* 原理方面
* LongAdder 源码
* 伪共享

