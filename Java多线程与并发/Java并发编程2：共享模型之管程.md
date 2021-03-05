![在这里插入图片描述](https://img-blog.csdnimg.cn/20210202223437264.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

## 1. 线程安全问题 (重点)

> Java3y : [多线程基础](https://mp.weixin.qq.com/s/TPZ2NBFy6niBq7b6FOJp4Q)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219094730191.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219094820781.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219094936132.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219095022810.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

### 1.1 Java线程安全问题

线程出现问题的根本原因是因为**线程上下文切换**，导致**线程里的指令没有执行完就切换执行其它线程**了，下面举一个例子：两个线程对初始值为 0 的静态变量一个做自增，一个做自减，各做 5000 次，结果是 0 吗？

```java
public class Test {
	static int count = 0;
	public static void main(String[] args) throws InterruptedException {
	    Thread t1 = new Thread(()->{
	        for (int i = 1; i < 5000; i++){
	            count++;
	        }
	    });
	    Thread t2 =new Thread(()->{
	        for (int i = 1; i < 5000; i++){
	            count--;
	        }
	    });
	    t1.start();
	    t2.start();
	    t1.join(); // 主线程等待t1线程执行完
	    t2.join(); // 主线程等待t2线程执行完
	    
	    // main线程只有等待t1，t2线程都执行完之后，才能打印count，否则main线程不会等待t1,t2
	    // 直接就打印count的值为0
	    log.debug("count的值是{}",count);
	}
}

// 打印: 并不是我们期望的0值，为什么呢? 看下文分析
09:42:42.921 guizy.ThreadLocalDemo [main] - count的值是511 
```

### 1.2 问题分析

我将从字节码的层面进行分析：

- 因为在Java中对静态变量的 自增/自减 并不是**原子操作**

```java
getstatic i // 获取静态变量i的值
iconst_1 // 准备常量1
iadd // 自增
putstatic i // 将修改后的值存入静态变量i
    
getstatic i // 获取静态变量i的值
iconst_1 // 准备常量1
isub // 自减
putstatic i // 将修改后的值存入静态变量i
```

- 可以看到`count++` 和 `count--` 操作实际都是需要这个`4个指令`完成的，那么这里问题就来了！**Java 的内存模型如下，完成静态变量的自增，自减需要在主存和工作内存中进行数据交换：**

![1583569253392](https://img-blog.csdnimg.cn/img_convert/9e7c4a40edd2941dcb71bc21f257f05a.png)

如果代码是**正常按顺序**运行的，那么count的值不会计算错

![1583569326977](https://img-blog.csdnimg.cn/img_convert/ecc665ab68ffcd2a26b643bae17897cd.png)

- 出现负数的情况：一个线程没有完成一次**完整的自增/自减**(多个指令) 的操作，就被别的线程进行操作，此时就会出现**线程安全**问题

  > 下图解释:
  >
  > - 首先线程2去静态变量中读取到值0，准备常数1，完成isub减法，变-1操作，正常还剩下一个putstatic i写入-1的过程; 最后的指令没有执行，就被线程1抢去了cpu的执行权;
  > - 此时线程1进行操作，读取静态变量0，准备常数1，iadd加法，i=1，此时将putstatic i写入 1; 当线程2重新获取到cpu的执行权时，它通过自身的程序计数器知道自己该执行putstatic 写入-1了; 此时它就直接将结果写为-1

![1583569380639](https://img-blog.csdnimg.cn/img_convert/30f018c3fac1dc4765a7d7cf737455e6.png)

出现正数的情况：同上类似; **主要就是因为线程的++/--操作不是一个原子操作，在执行4条指令期间被其他线程抢夺cpu**

![1583569416016](https://img-blog.csdnimg.cn/img_convert/707f99c73c014671549b9cdf8976a78c.png)

------

### 1.3 临界区 Critical Section

- 一个程序运行多线程本身是没有问题的
- 问题出现在 **多个线程共享资源(临界资源)** 的时候
  - 多个线程同时对共享资源进行读操作本身也没有问题 - **对读操作没问题**
  - 问题出现在对对共享资源同时进行读写操作时就有问题了 - **同时读写操作有问题**
- 先定义一个叫做**临界区**的概念：一段代码内如果**存在对共享资源的多线程读写操作**，那么称这段代码为临界区; 共享资源也成为**临界资源**

```java
static int counter = 0;
static void increment() 
// 临界区 
{   
   counter++; 
}

static void decrement() 
// 临界区 
{ 
   counter--; 
}
```

### 1.4 竞态条件 Race Condition

- 多个线程在临界区执行，那么由于**代码指令的执行不确定而导致的结果问题**，称为**竞态条件**

## 2. synchronized 解决方案

**为了避免临界区中的竞态条件发生**，由多种手段可以达到

- **阻塞式解决方案：** `synchronized ，Lock (ReentrantLock)`
- **非阻塞式解决方案：** `原子变量 (CAS)`

现在讨论使用`synchronized`来进行解决，即俗称的**对象锁**，它采用互斥的方式让**同一时刻至多只有一个线程持有对象锁，其他线程如果想获取这个锁就会阻塞住**，这样就能保证拥有锁的线程可以安全的执行临界区内的代码，**不用担心线程上下文切换**

> **注意:** 虽然Java 中**互斥**和**同步**都可以采用 `synchronized 关键字`来完成，但它们还是有区别的：
>
> - 互斥是保证临界区的竞态条件发生，**同一时刻只能有一个线程执行临界区的代码**
> - 同步是由于线程执行的先后，**顺序不同但是需要一个线程等待其它线程运行到某个点**。

### 2.1 synchronized语法

```java
synchronized(对象) { // 线程1获得锁， 那么线程2的状态是(blocked)
 	临界区
}
```

- 上面的实例程序使用`synchronized`后如下，计算出的结果是正确！

```java
static int counter = 0;
static final Object room = new Object();
public static void main(String[] args) throws InterruptedException {
     Thread t1 = new Thread(() -> {
         for (int i = 0; i < 5000; i++) {
         	 // 对临界资源(共享资源的操作) 进行 加锁
             synchronized (room) {
             counter++;
        	}
 		}
 	}, "t1");
     Thread t2 = new Thread(() -> {
         for (int i = 0; i < 5000; i++) {
             synchronized (room) {
             counter--;
         }
     }
     }, "t2");
     t1.start();
     t2.start();
     t1.join();
     t2.join();
     log.debug("{}",counter);
}

09:56:24.210 guizy.ThreadLocalDemo [main] - count的值是0
```

### 2.2 synchronized原理

- `synchronized`实际上**利用对象锁保证了临界区代码的原子性**，临界区内的代码在外界看来是不可分割的，**不会被线程切换所打断**
- 小故事
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219110609489.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

你可以做这样的类比：

- `synchronized(对象)` 中的对象，可以想象为一个房间`（room）`，有唯一入口（门）房间只能一次进入一人
  进行计算，线程 t1，t2 想象成两个人
- 当线程 t1 执行到 `synchronized(room)` 时就好比 t1 进入了这个房间，并锁住了门拿走了钥匙，在门内执行
  `count++` 代码
- 这时候如果 t2 也运行到了 `synchronized(room)` 时，它发现门被锁住了，只能在门外等待，发生了上下文切
  换，阻塞住了
- 这中间即使 t1 的 cpu 时间片不幸用完，被踢出了门外（不要错误理解为锁住了对象就能一直执行下去哦），
  这时门还是锁住的，t1 仍拿着钥匙，t2 线程还在阻塞状态进不来，只有下次轮到 t1 自己再次获得时间片时才
  能开门进入
- 当 t1 执行完 `synchronized(){}` 块内的代码，这时候才会从 obj 房间出来并解开门上的锁，唤醒 t2 线程把钥
  匙给他。t2 线程这时才可以进入 obj 房间，锁住了门拿上钥匙，执行它的 `count--` 代码

![1583571633729](https://img-blog.csdnimg.cn/img_convert/e7dda8af005cdee39d206896de899745.png)
思考:

synchronized 实际是用**对象锁**保证了**临界区内代码的原子性**，临界区内的代码对外是不可分割的，不会被线程切
换所打断。

- 如果把 `synchronized(obj)` 放在for循环的外面，如何理解？

  - for循环也是一个原子操作，表现出原子性
  
- 如果t1 `synchronized(obj1)`而 t2 `synchronized(obj2)`会怎么运行？

  - 因为t1，t2拿到不是同一把对象锁，所以他们仍然会发现安全问题 – 必须要是同一把对象锁

- 如果t1 `synchronized(obj)` 而 t2 没有加会怎么样 ?

  - 因为t2没有加锁，所以t2，不需要获取t1的锁，直接就可以执行下面的代码，仍然会出现安全问题

小总结：

- 当多个线程对临界资源进行写操作的时候，此时会造成线程安全问题，如果使用`synchronized`关键字，对象锁一定要是多个线程共有的，才能避免竞态条件的发生。

### 2.3 synchronized 加在方法上

- 加在**实例方法**上，锁对象就是对象实例

```java
public class Demo {
	//在方法上加上synchronized关键字
	public synchronized void test() {
	
	}
	//等价于
	public void test() {
		synchronized(this) {
		
		}
	}
}
```

- 加在**静态方法**上，锁对象就是当前类的Class实例

```java
public class Demo {
	//在静态方法上加上synchronized关键字
	public synchronized static void test() {
	
	}
	//等价于
	public void test() {
		synchronized(Demo.class) {
		
		}
	}
}
```

#### 面向对象的改进

把需要保护的共享变量放入一个类

```java
class Room {
    int value = 0;

    public void increment() {
        synchronized (this) {
            value++;
        }
    }

    public void decrement() {
        synchronized (this) {
            value--;
        }
    }

    public int get() {
        synchronized (this) {
            return value;
        }
    }
}

@Slf4j
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        Room room = new Room();
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                room.increment()
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                room.decrement();
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("count: {}", room.get());
    }
}
```

## 3. 线程八锁案例分析

- 其实就是考察`synchronized` 锁住的是哪个对象，如果锁住的是**同一对象,** 就不会出现**线程安全**问题

1、锁住同一个对象都是this（e1对象），结果为：12或者21

```java
/**
 * Description: 不会出现安全问题, 打印结果顺序为: 1/2 或 2/1
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象就是this, 也就是e1
    public synchronized void a() {
        log.debug("1");
    }
//    public void a () {
//        synchronized (this) {
//            log.debug("1");
//        }
//    }

    // 锁对象也是this, e1
    public synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e1.b()).start();
    }
}
```

2、锁住同一个对象都是this（e1对象），结果为：1s后1，2 或 2，1s后1

```java
/**
 * Description: 不会出现安全问题, 打印结果顺序为: 1s后1,2 或 2,1s后1
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象就是this, 也就是e1
    public synchronized void a(){
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象也是this, e1
    public synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e1.b()).start();
    }
}
```

3、a，b锁住同一个对象都是this（e1对象），c没有上锁。结果为：3，1s后1，2 || 2，3，1s后1 || 3，2，1s后1

```java
/**
 * Description: 会出现安全问题, 因为前两个线程, 执行run方法时, 都对相同的对象加锁;
 *              而第三个线程,调用的方法c, 并没有加锁, 所以它可以同前两个线程并行执行;
 *  打印结果顺序为: 分析: 因为线程3和线程1,2肯定是并行执行的, 所以有以下情况
 *               3,1s后1,2 || 2,3,1s后1 || 3,2,1s后1
 *               至于 1,3,2的情况是不会发生的, 可以先调用到1,但需要sleep一秒.3肯定先执行了
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象就是this, 也就是e1
    public synchronized void a() throws InterruptedException {
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象也是this, e1
    public synchronized void b() {
        log.debug("2");
    }

    public void c() {
        log.debug("3");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        new Thread(() -> {
            try {
                e1.a();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> e1.b()).start();
        new Thread(() -> e1.c()).start();
    }
}
```

4、a锁住对象this（n1对象），b锁住对象this（n2对象），不互斥。结果为：2，1s后1

```java
/**
 * Description: 会出现安全问题, 线程1的锁对象为e1, 线程2的锁对象为e2. 所以他们会同一时刻执行1,2
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象是e1
    public synchronized void a() {
    	Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象是e2
    public synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        EightLockTest e2 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e2.b()).start();
    }
}
```

5、a锁住的是EightLockTest.class对象，b锁住的是this(e1),不会互斥; 结果: 2,1s后1

```java
/**
 * Description: 会发生安全问题, 因为a锁住的是EightLockTest.class对象, b锁住的是this(e1),不会互斥
 *              结果: 2,1s后1
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象是EightLockTest.class类对象
    public static synchronized void a() {
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象是e2
    public synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e1.b()).start();
    }
}
```

6、a,b锁住的是EightLockTest.class对象，会发生互斥; 结果为：2,1s后1 || 1s后1,2

```java
/**
 * Description: 不会发生安全问题, 因为a,b锁住的是EightLockTest.class对象, 会发生互斥
 *              结果: 2,1s后1 || 1s后1,2
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象是EightLockTest.class类对象
    public static synchronized void a() {
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象是EightLockTest.class类对象
    public static synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e1.b()).start();
    }
}
1234567891011121314151617181920212223242526
```

7、a锁住的是EightLockTest.class对象，b锁住的是this(e1),不会互斥; 结果: 2,1s后1

```java
/**
 * Description: 会发生安全问题, 因为a锁住的是EightLockTest.class对象, b锁住的是this(e1),不会互斥
 *              结果: 2,1s后1
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象是EightLockTest.class类对象
    public static synchronized void a() {
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象是this,e2对象
    public synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        EightLockTest e2 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e2.b()).start();
    }
}
```

8、a,b锁住的是EightLockTest.class对象, 会发生互斥; 结果为：2,1s后1 || 1s后1,2

```java
/**
 * Description: 不会发生安全问题, 因为a,b锁住的是EightLockTest.class对象, 会发生互斥
 *              结果: 2,1s后1 || 1s后1,2
 *
 * @author guizy
 * @date 2020/12/19 11:24
 */
@Slf4j(topic = "guizy.EightLockTest")
public class EightLockTest {
    // 锁对象是EightLockTest.class类对象
    public static synchronized void a() {
        Thread.sleep(1000);
        log.debug("1");
    }

    // 锁对象是EightLockTest.class类对象
    public static synchronized void b() {
        log.debug("2");
    }

    public static void main(String[] args) {
        EightLockTest e1 = new EightLockTest();
        EightLockTest e2 = new EightLockTest();
        new Thread(() -> e1.a()).start();
        new Thread(() -> e2.b()).start();
    }
}
```

## 4. 变量的线程安全分析

### 4.1 成员变量和静态变量是否线程安全？

- 如果变量没有在线程间共享，那么变量是安全的
- 如果变量在线程间共享
  - 如果只有读操作，则线程安全
  - 如果有**读写操作**，则这段代码是**临界区**，需要考虑线程安全

### 4.2 局部变量线程是否线程安全？

- 局部变量是安全的
- 但局部变量**引用的对象**则未必 （要看该对象是否被共享且被执行了读写操作）
  - 如果该对象**没有逃离方法的作用范围**，它是**线程安全**的
  - 如果该对象逃离方法的作用范围，需要考虑线程安全

### 4.3 局部变量线程安全分析 (重要)

- 局部变量表是存在于栈帧中，而虚拟机栈中又包括很多栈帧，虚拟机栈是线程私有的；
- 局部变量是安全的，示例如下

```java
public static void test1() {
     int i = 10;
     i++;
}
```

- 每个线程调用 test1() 方法时局部变量 i，会在**每个线程的栈帧内存中被创建多份，因此不存在共享**

```shell
public static void test1();
 descriptor: ()V
 flags: ACC_PUBLIC, ACC_STATIC
 Code:
	 stack=1, locals=1, args_size=0
	 0: bipush 10
	 2: istore_0
	 3: iinc 0, 1
	 6: return
	 LineNumberTable:
	 line 10: 0
	 line 11: 3
	 line 12: 6
	 LocalVariableTable:
	 Start Length Slot Name Signature
	 3 4 0 i I
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020121913434871.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)



- 如果**局部变量引用的对象逃离方法的范围**，那么要考虑线程安全问题的，代码示例如下

> - 循环创建了100个线程, 在线程体里面都调用了`method1`方法，在method1方法中又循环调用了100次`method2,method3`方法。方法2，3都使用到了成员变量`arrayList`, 此时的问题就是: 1个线程它会循环调用100次方法2和3，一共有100个线程，此时100个线程操作的共享资源就是`arrayList`成员变量 ，而且还进行了`读写`操作。必然会造成线程不安全的问题。

```java
public class Test15 {
    public static void main(String[] args) {
        UnsafeTest unsafeTest = new UnsafeTest();
        for (int i =0;i<100;i++){
            new Thread(()->{
                unsafeTest.method1();
            },"线程"+i).start();
        }
    }
}
class UnsafeTest{
    ArrayList<String> arrayList = new ArrayList<>();
    public void method1(){
        for (int i = 0; i < 100; i++) {
            method2();
            method3();
        }
    }
    private void method2() {
        arrayList.add("1");
    }
    private void method3() {
        arrayList.remove(0);
    }
}

输出：
Exception in thread "线程1" Exception in thread "线程2" java.lang.ArrayIndexOutOfBoundsException: -1
```



- 无论哪个线程中的 method2 和 method3 引用的都是同一个对象中的 list 成员变量

- 一个 ArrayList ，在添加一个元素的时候，它可能会有两步来完成：

  - 第一步：在 `arrayList[size]`的位置存放此元素
  - 第二步：`size++`
  
- 在**单线程**运行的情况下，如果 `size = 0`，添加一个元素后，此元素在位置 0，而且 size=1；(没问题)

- 在**多线程**情况下，比如有两个线程，**线程 A 先将元素存放在位置 0。但是此时 CPU 进行上下文切换 (线程A还没来得及size++)**，**线程 B 得到运行的机会。线程B也向此 ArrayList 添加元素，因为此时 Size 仍等于0** （注意哦，我们假设的是添加一个元素是要两个步骤哦，而线程A仅仅完成了步骤1），所以线程B也将元素存放在位置0。然后线程A和线程B都继续运行，**都增加 size 的值**。

- 那好，现在我们来看看 `ArrayList` 的情况，**元素实际上只有一个，存放在位置 0**，而 `size 却等于 2`。这就是“线程不安全”了。

![1583589268096](https://img-blog.csdnimg.cn/img_convert/589573a2de17a1bc7ca5728a20248ef5.png)

![1583587571334](https://img-blog.csdnimg.cn/img_convert/fbc6e2e0d0cc62e37fcf70984813091e.png)



- 可以将`list`修改成**局部变量**，局部变量存放在栈帧中，栈帧又存放在虚拟机栈中，**虚拟机栈是作为线程私有的;**
- 因为method1方法，将`arrayList`传给method2，method3方法，此时他们三个方法共享这同一个arrayList, 此时**不会被其他线程访问到**，所以不会出现线程安全问题，因为**这三个方法使用的同一个线程**。
- 在外部，创建了100个线程, 每个线程都会调用`method1`方法, 然后都会再从新创建一个新的`arrayList`对象, 这个新对象再传递给method2,method3方法.

```java
class UnsafeTest {
    public void method1() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            method2(arrayList);
            method3(arrayList);
        }
    }

    private void method2(List<String> arrayList) {
        arrayList.add("1");
    }

    private void method3(List<String> arrayList) {
        arrayList.remove(0);
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219151354651.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

思考 private 或 final的重要性 (重要)

> 提高线程的安全性

方法访问修饰符带来的思考: 如果把 method2 和 method3 的方法修改为`public` 会不会导致线程安全问题；分情况:

- 情况1：有其它线程调用 method2 和 method3

  - 只修改为`public`修饰，此时**不会出现线程安全的问题**，**即使线程2调用method2/3方法, 给2/3方法传过来的`list对象`也是线程2调用method1方法时，传递给method2/3的list对象，不可能是线程1调用method1方法传的对象。** 

- 情况2：在情况1 的基础上，为 ThreadSafe 类添加子类，子类覆盖 method2 或 method3方法，即如下所示： 从这个例子可以看出 private 或 final提供【安全】的意义所在，请体会开闭原则中的【闭】

  > - 如果改为`public`, 此时子类可以重写父类的方法, 在子类中开线程来操作`list对象`, 此时就会**出现线程安全问题: 子类和父类共享了list对象**
  > - 如果改为`private`, **子类就不能重写父类的私有方法**, 也就不会出现线程安全问题; 所以`private修饰符`是可以避免线程安全问题.
  > - 所以如果不想子类重写父类的方法的时候, 我们可以将父类中的方法设置为`private, final`修饰的方法, 此时子类就无法影响父类中的方法了!

```java
class ThreadSafe {
    public final void method1(int loopNumber) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < loopNumber; i++) {
            method2(list);
            method3(list);
        }
    }
    private void method2(ArrayList<String> list) {
        list.add("1");
    }
    public void method3(ArrayList<String> list) {
        list.remove(0);
    }
}
class ThreadSafeSubClass extends ThreadSafe{
    @Override
    public void method3(ArrayList<String> list) {
        new Thread(() -> {
            list.remove(0);
        }).start();
    }
}
```



### 4.4 常见线程安全类

- String
- Integer
- StringBuffer
- Random
- Vector
- Hashtable
- java.util.concurrent 包下的类 JUC

**重点：**

- 这里说它们是线程安全的是指，**多个线程调用它们同一个实例的某个方法时，是线程安全的** , 也可以理解为 **它们的每个方法是原子的**
- 它们的每个方法是原子的（方法都被加上了synchronized）
- 但注意它们**多个方法的组合不是原子的**，所以可能**会出现线程安全问题**

```java
Hashtable table = new Hashtable();

new Thread(()->{
	// put方法增加了synchronized
 	table.put("key", "value1");
}).start();

new Thread(()->{
 	table.put("key", "value2");
}).start();
```

#### 线程安全类方法的组合

- 但注意它们多个方法的组合不是原子的，见下面分析，这里只能是get方法内部是线程安全的, put方法内部是线程安全的。组合起来使用还是会受到**上下文切换**的影响


```java
Hashtable table = new Hashtable();
// 线程1，线程2
if( table.get("key") == null) {
 table.put("key", value);
}
```

![1583590979975](https://img-blog.csdnimg.cn/img_convert/3f38b860d4b0a73d3bc446598d38c867.png)

#### 不可变类的线程安全

- `String`和`Integer`类都是不可变的类，因为其类内部状态是不可改变的，因此它们的方法都是线程安全的, 都被`final`修饰，不能被继承。
- 肯定有些人他们知道`String` 有 `replace`，`substring` 等方法【可以】改变值啊，**其实调用这些方法返回的已经是一个新创建的对象了！** (在字符串常量池中当修改了String的值,它不会再原有的基础上修改, 而是会重新开辟一个空间来存储)

### 4.5 示例分析——是否线程安全

示例一

- Servlet运行在Tomcat环境下并只有一个实例，因此会被Tomcat的多个线程共享使用，因此存在成员变量的共享问题。

```java
public class MyServlet extends HttpServlet {
	 // 是否安全？  否：HashMap不是线程安全的，HashTable是
	 Map<String,Object> map = new HashMap<>();
	 // 是否安全？  是:String 为不可变类，线程安全
	 String S1 = "...";
	 // 是否安全？ 是
	 final String S2 = "...";
	 // 是否安全？ 否：不是常见的线程安全类
	 Date D1 = new Date();
	 // 是否安全？  否：引用值D2不可变，但是日期里面的其它属性比如年月日可变。与字符串的最大区别是Date里面的属性可变。
	 final Date D2 = new Date();
 
	 public void doGet(HttpServletRequest request,HttpServletResponse response) {
	  // 使用上述变量
	 }
}
```

示例二

- 分析线程是否安全，先对**类的成员变量，类变量，局部变量**进行考虑，如果变量会在各个线程之间共享，那么就得考虑线程安全问题了，如果变量A引用的是线程安全类的实例，并且只调用该线程安全类的一个方法，那么该变量A是线程安全的的。下面对实例一进行分析：此类不是线程安全的。**`MyAspect`切面类只有一个实例，成员变量`start` 会被多个线程同时进行读写操作**
- **Spring中的Bean都是`单例`的, 除非使用`@Scope`修改为多例。**

```java
@Aspect
@Component 
public class MyAspect {
        // 是否安全？不安全, 因为MyAspect是单例的
        private long start = 0L;

        @Before("execution(* *(..))")
        public void before() {
            start = System.nanoTime();
        }

        @After("execution(* *(..))")
        public void after() {
            long end = System.nanoTime();
            System.out.println("cost time:" + (end-start));
        }
    }
```

示例三

- 此例是典型的三层模型调用，`MyServlet` `UserServiceImpl` `UserDaoImpl`类都只有一个实例，`UserDaoImpl`类中没有成员变量，`update`方法里的变量引用的对象不是线程共享的，所以是线程安全的；`UserServiceImpl`类中只有一个线程安全的`UserDaoImpl`类的实例，那么`UserServiceImpl`类也是线程安全的，同理 `MyServlet`也是线程安全的
- Servlet调用Service, Service调用Dao这三个方法使用的是`同一个线程`。

```java
public class MyServlet extends HttpServlet {
	 // 是否安全    是：UserService不可变，虽然有一个成员变量,
	 			// 但是是私有的, 没有地方修改它
	 private UserService userService = new UserServiceImpl();
	 
	 public void doGet(HttpServletRequest request, HttpServletResponse response) {
	 	userService.update(...);
	 }
}

public class UserServiceImpl implements UserService {
	 // 是否安全     是：Dao不可变, 其没有成员变量
	 private UserDao userDao = new UserDaoImpl();
	 
	 public void update() {
	 	userDao.update();
	 }
}

public class UserDaoImpl implements UserDao { 
	 // 是否安全   是：没有成员变量，无法修改其状态和属性
	 public void update() {
	 	String sql = "update user set password = ? where username = ?";
	 	// 是否安全   是：不同线程创建的conn各不相同，都在各自的栈内存中
	 	try (Connection conn = DriverManager.getConnection("","","")){
	 	// ...
	 	} catch (Exception e) {
	 	// ...
	 	}
	 }
}
```

示例四

- 跟示例二大体相似，`UserDaoImpl`类中`有成员变量`，那么**多个线程可以对成员变量**`conn` 同时进行操作，`故是不安全的`

```java
public class MyServlet extends HttpServlet {
    // 是否安全
    private UserService userService = new UserServiceImpl();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        userService.update(...);
    }
}

public class UserServiceImpl implements UserService {
    // 是否安全
    private UserDao userDao = new UserDaoImpl();
    public void update() {
       userDao.update();
    }
}

public class UserDaoImpl implements UserDao {
    // 是否安全: 不安全; 当多个线程,共享conn, 一个线程拿到conn,刚创建一个连接赋值给conn, 此时另一个线程进来了, 直接将conn.close
    //另一个线程恢复了, 拿到conn干事情, 此时conn都被关闭了, 出现了问题
    private Connection conn = null;
    public void update() throws SQLException {
        String sql = "update user set password = ? where username = ?";
        conn = DriverManager.getConnection("","","");
        // ...
        conn.close();
    }
}
```

示例五

- 跟示例三大体相似，`UserServiceImpl`类的update方法中UserDao是作为局部变量存在的，所以每个线程访问的时候都会新建有一个`UserDao`对象，新建的对象是线程独有的，所以**是线程安全的**

```java
public class MyServlet extends HttpServlet {
    // 是否安全
    private UserService userService = new UserServiceImpl();
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        userService.update(...);
    }
}
public class UserServiceImpl implements UserService {
    public void update() {
        UserDao userDao = new UserDaoImpl();
        userDao.update();
    }
}
public class UserDaoImpl implements UserDao {
    // 是否安全
    private Connection = null;
    public void update() throws SQLException {
        String sql = "update user set password = ? where username = ?";
        conn = DriverManager.getConnection("","","");
        // ...
        conn.close();
    }
}
```

示例六

- 私有变量sdf被暴露出去了, 发生了逃逸

```java
public abstract class Test {
    public void bar() {
        // 是否安全
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        foo(sdf);
    }
    public abstract foo(SimpleDateFormat sdf);
    public static void main(String[] args) {
        new Test().bar();
    }
}
```

- 其中 foo 的行为是不确定的，可能导致不安全的发生，被称之为**外星方法**，**因为foo方法可以被重写，导致线程不安全。** 在String类中就考虑到了这一点，String类是`final`的，**子类不能重写它的方法。**

```java
public void foo(SimpleDateFormat sdf) {
    String dateStr = "1999-10-11 00:00:00";
    for (int i = 0; i < 20; i++) {
        new Thread(() -> {
            try {
                sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```

## 5. 习题分析

### 5.1 卖票练习

测试下面代码是否存在线程安全问题，并尝试改正

```java
package cn.itcast.n4.exercise;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

@Slf4j(topic = "c.ExerciseSell")
public class ExerciseSell {
    public static void main(String[] args) throws InterruptedException {
        // 模拟多人买票
        TicketWindow window = new TicketWindow(1000);

        // 所有线程的集合（由于threadList在主线程中，不被共享，因此使用ArrayList不会出现线程安全问题）
        List<Thread> threadList = new ArrayList<>();
        // 卖出的票数统计(Vector为线程安全类)
        List<Integer> amountList = new Vector<>();
        for (int i = 0; i < 2000; i++) {
            Thread thread = new Thread(() -> {
                // 买票
                int amount = window.sell(random(5));
                // 统计买票数
                amountList.add(amount);
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        // 统计卖出的票数和剩余票数
        log.debug("余票：{}",window.getCount());
        log.debug("卖出的票数：{}", amountList.stream().mapToInt(i -> i).sum());
    }

    // Random 为线程安全
    static Random random = new Random();

    // 随机 1~5
    public static int random(int amount) {
        return random.nextInt(amount) + 1;
    }
}

// 售票窗口
class TicketWindow {
	// 票总数
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    // 获取余票数量
    public int getCount() {
        return count;
    }

    // 售票
    public synchronized int sell(int amount) {
        if (this.count >= amount) {
            this.count -= amount;
            return amount;
        } else {
            return 0;
        }
    }
}
```

### 5.2 转账练习

测试下面代码是否存在线程安全问题，并尝试改正。在该问题中，可能发生线程不安全的变量为money，transfer方法中 `this.setMoney(this.getMoney() - amount);` 操作是非原子的，因此执行会分如下几步：

1. 计算出 `money - amount`
2. 将`money - amount` 赋值给money

当线程1调用a向b转账100元，线程2 b向a转账200元的过程可能为：

1. **a.money - amount = 900 （线程1）**
2. b.money - amount = 800（转换到线程2）
3. b.money = 800
4. a.money - amount = 1000 + 200 = 1200
5. a.money = 1200
6. **a.money = 900 （转换到线程1）**

我们可以看到原本a的数量应该为1100，此时只有900，是线程不安全的。

```java
package cn.itcast.n4.exercise;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j(topic = "c.ExerciseTransfer")
public class ExerciseTransfer {
    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomAmount());
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 查看转账2000次后的总金额
        log.debug("total:{}", (a.getMoney() + b.getMoney()));
    }

    // Random 为线程安全
    static Random random = new Random();

    // 随机 1~100
    public static int randomAmount() {
        return random.nextInt(100) + 1;
    }
}

// 账户
class Account {
    private int money;

    public Account(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    // 转账
    public void transfer(Account target, int amount) {
        synchronized(Account.class) {   //锁住Account类，因为涉及到A.money和B.money。
            if (this.money >= amount) {
                this.setMoney(this.getMoney() - amount);
                target.setMoney(target.getMoney() + amount);
            }
        }
    }
}

// 没问题, 最终的结果仍然是 2000元
```



> [Java3yのsynchronized](https://mp.weixin.qq.com/s?__biz=MzU4NzA3MTc5Mg==&mid=2247483980&idx=1&sn=c9b620834adb889ad8ccedb6afdcaed1&chksm=fdf0ea13ca8763058e59bde10f752264a5de6fb9dd087ca2a4290f90da4b2c2e78407613c5cd&scene=178&cur_album_id=1657204970858872832#rd)

## 6. Monitor 概念

### 6.1 Java 对象头 (重点)

**对象头包含两部分：`运行时元数据（Mark Word）`和`类型指针 (Klass Word)`**

1. 运行时元数据
   - **哈希值（HashCode）**，可以看作是**堆中对象的地址**
   - **GC分代年龄（年龄计数器）** (用于新生代from/to区晋升老年代的标准, 阈值为15)
   - **锁状态标志** （用于JDK1.6对synchronized的优化 -> 轻量级锁）
   - **线程持有的锁**
   - **偏向线程ID** （用于JDK1.6对synchronized的优化 -> 偏向锁）
   - 偏向时间戳
2. 类型指针
   - 指向**类元数据InstanceKlass**，确定该对象所属的类型。指向的其实是方法区中存放的类元信息

说明：**如果对象是数组，还需要记录数组的长度**

- 以 32 位虚拟机为例，普通对象的对象头结构如下，其中的`Klass Word`为`类型指针`，指向`方法区`对应的`Class对象`；

![1583651065372](https://img-blog.csdnimg.cn/img_convert/ac7274e0d0dbe8c8b25f43f2aa1ed1d6.png)

数组对象：
![1583651088663](https://img-blog.csdnimg.cn/img_convert/52c45594819f30fb8a9d6087682be254.png)

**其中 Mark Word 结构为: `无锁(001)、偏向锁(101)、轻量级锁(00)、重量级锁(10)`**
![1583651590160](https://img-blog.csdnimg.cn/img_convert/0ffaeb7ddf7d71801bfd3eeb00754162.png)

所以一个对象的结构如下：
![1583678624634](https://img-blog.csdnimg.cn/img_convert/1844b5e3159baa3c8fb78478daa1580b.png)

### 6.2 Monitor 原理 (Synchronized底层实现-重量级锁)

> **多线程同时访问临界区: 使用重量级锁**
>
> - JDK6对Synchronized的优先状态：**偏向锁–>轻量级锁–>重量级锁**

- `Monitor`被翻译为`监视器`或者`管程`

每个Java对象都可以关联一个(操作系统的)Monitor，如果使用`synchronized`给对象上锁（重量级），该**对象头的MarkWord**中就被设置为**指向Monitor对象的指针**。

> 下图原理解释:
>
> - 当Thread2访问到 `synchronized(obj)` 中的共享资源的时候
>   - 首先会将`synchronized`中的锁对象中对象头的`MarkWord`去尝试指向操作系统的`Monitor`对象。让锁对象中的MarkWord和Monitor对象相关联。如果关联成功, 将obj对象头中的`MarkWord`的对象状态从01改为10。
>   - 因为Monitor没有和其他的obj的MarkWord相关联, 所以`Thread2`就成为了该`Monitor`的Owner(所有者)。
>   - 又来了个`Thread1`执行`synchronized(obj)`代码，它首先会看看能不能执行该`临界区`的代码；它会检查obj是否关联了Montior，此时已经有关联了，它就会去看看该Montior有没有所有者(Owner)，发现有所有者了(Thread2)；`Thread1`也会和该Monitor关联, 该线程就会进入到它的`EntryList(阻塞队列)`;
>   - 当`Thread2`执行完临界区代码后，Monitor的`Owner(所有者)`就空出来了。此时就会通知Monitor中的EntryList阻塞队列中的线程, 这些线程通过竞争, 成为新的所有者

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219192811839.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

![1583652360228](https://img-blog.csdnimg.cn/img_convert/98c3189e41fd654fe34ead273ec76eba.png)

- 刚开始时`Monitor`中的`Owner为null`
- 当Thread-2 执行 `synchronized(obj){}` 代码时就会将Monitor的所有者Owner 设置为 Thread-2，上锁成功，Monitor中同一时刻只能有一个Owner
- 当Thread-2 占据锁时，如果线程Thread-3，Thread-4也来执行`synchronized(obj){}`代码，就会进入`EntryList`中变成`BLOCKED状态`
- Thread-2 执行完同步代码块的内容，然后唤醒 `EntryList` 中等待的线程来竞争锁，**竞争时是非公平的 (仍然是抢占式)**
- 图中 `WaitSet` 中的Thread-0，Thread-1 是之前获得过锁，但条件不满足进入 `WAITING` 状态的线程，后面讲`wait-notify` 时会分析

> **注意：**
>
> - synchronized 必须是进入同一个锁对象的monitor 才有上述的效果; —> 也就要使用同一把锁
> - 不加 synchronized的锁对象不会关联监视器，不遵从以上规则

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219200615817.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

> 它加锁就是依赖底层操作系统的 `mutex`相关指令实现, 所以会造成**用户态和内核态之间的切换**, 非常耗性能 !
>
> - 在JDK6的时候，对synchronized进行了优化，引入了`轻量级锁, 偏向锁`，它们是在JVM的层面上进行加锁逻辑，就没有了切换的消耗~

### 6.3 synchronized原理

代码如下

```java
static final Object lock = new Object();
static int counter = 0;
public static void main(String[] args) {
    synchronized (lock) {
        counter++;
    }
}
```

- 反编译后的部分字节码
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219201521709.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

> 注意：方法级别的 synchronized 不会在字节码指令中有所体现

### 6.4 synchronized 原理进阶

- 小故事

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219202939493.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219203225659.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/202101191526347.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)



#### 轻量级锁

> 通过锁记录的方式，场景 : 多个线程交替进入临界区

- `轻量级锁`的使用场景: 如果一个对象虽然有多个线程要对它进行加锁，但是**加锁的时间是错开的（也就是没有人可以竞争的）**，那么可以使用轻量级锁来进行优化。
- 轻量级锁对使用者是透明的，即语法仍然是`synchronized` (jdk6对synchronized的优化)，假设有两个方法同步块，利用同一个对象加锁
- eg：线程A来操作临界区的资源，给资源加锁，到执行完临界区代码，释放锁的过程，没有线程来竞争，此时就可以使用`轻量级锁`；如果这期间有线程来竞争的话, 就会`升级为重量级锁(synchronized)`

```java
static final Object obj = new Object();
public static void method1() {
     synchronized( obj ) {
         // 同步块 A
         method2();
     }
}
public static void method2() {
     synchronized( obj ) {
         // 同步块 B
     }
}
```

- 每次指向到`synchronized代码块`时，都会在栈帧中创建**锁记录（Lock Record）**对象，每个线程都会包括一个锁记录的结构，锁记录内部可以储存对象的**MarkWord**和**锁对象引用reference**
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210119153328743.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

![1583755737580](https://img-blog.csdnimg.cn/img_convert/4d42844c6ddc19a6f94d954ae5a0cc79.png)

- 让锁记录中的`Object reference`指向锁对象地址，并且尝试用`CAS(compare and sweep)`将栈帧中的锁记录的`(lock record 地址 00)`替换Object对象的`Mark Word`，将`Mark Word` 的值(01)存入`锁记录(lock record地址)`中——相互替换
 - 01 表示 无锁 (看Mark Word结构, 数字的含义)
- 00表示 轻量级锁
    ![1583755888236](https://img-blog.csdnimg.cn/img_convert/87f63c5373eed35d0bf65e0b510a7660.png)

**重点:**

- 如果对象头中的锁为无锁01，cas替换成功，获得了轻量级锁，那么对象的对象头储存的就是锁记录的地址和状态00，如下所示

  - 此时栈帧中存储了对象的对象头中的**锁状态标志，年龄计数器，哈希值**等； 对象的对象头中就存储了**栈帧中锁记录的地址和状态00**，这样的话对象就知道了是哪个线程锁住自己。
    ![1583755964276](https://img-blog.csdnimg.cn/img_convert/68c40cfc17498b1514fe31af8a896eaf.png)

  

- 如果cas替换失败，有两种情况：① 锁膨胀 ② 重入锁失败

  - 1、如果是其它线程已经持有了该Object的轻量级锁，那么表示有竞争，将进入`锁膨胀阶段`。此时对象Object对象头中已经存储了别的线程的`锁记录地址 00`，指向了其他线程;
  - 2、如果是自己的线程已经执行了synchronized进行加锁，那么再添加一条 Lock Record 作为重入的计数。

 

在上面代码中，临界区中又调用了`method2`，method2中又进行了一次synchronized加锁操作, 此时就会在虚拟机栈中再开辟一个method2方法对应的栈帧（栈顶），该栈帧中又会存在一个独立的`Lock Record`，此时它发现**对象的对象头中指向的就是自己线程中栈帧的锁记录**；加锁也就失败了。这种现象就叫做**`锁重入`**；线程中有多少个锁记录，就能表明该线程对这个对象加了几次锁 (锁重入计数)
![1583756190177](https://img-blog.csdnimg.cn/img_convert/9e36f456637862001c9c58ded2651a3b.png)

**轻量级锁解锁流程 :**

- 当线程退出synchronized代码块的时候，如果获取的是取值为 null 的锁记录 ，表示有**锁重入**，这时**重置锁记录**，表示重入计数减一
  ![1583756357835](https://img-blog.csdnimg.cn/img_convert/6e5c1ae4a693ec26d8c629fda57737c3.png)

- 当线程退出synchronized代码块的时候，如果获取的锁记录取值不为 null，那么使用cas将Mark Word的值恢复给对象，将直接替换的内容还原。

  - 成功则解锁成功 （轻量级锁解锁成功）
  - 失败，表示有竞争, 则说明**轻量级锁进行了锁膨胀**或**已经升级为重量级锁，进入重量级锁解锁流程 (Monitor流程)**

#### 锁膨胀

- 如果在尝试加轻量级锁的过程中，cas替换操作无法成功，这是有一种情况就是其它线程已经为这个对象加上了轻量级锁，这时就要进行**锁膨胀(有竞争)**，**将轻量级锁变成重量级锁。**

- 当 Thread-1 进行轻量级加锁时，Thread-0 已经对该对象加了轻量级锁, 此时发生**锁膨胀**
  ![1583757433691](https://img-blog.csdnimg.cn/img_convert/c3e0a92d418c70280fd2a0bb730eea82.png)

- 这时Thread-1加轻量级锁失败，进入锁膨胀流程

  - 因为Thread-1线程加轻量级锁失败, 轻量级锁没有阻塞队列的概念, 所以此时就要为对象申请Monitor锁(重量级锁)，让Object指向重量级锁地址 10，然后自己进入Monitor 的EntryList 变成BLOCKED状态

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219214748700.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

- 当Thread-0 线程执行完synchronized同步块时，**使用cas将Mark Word的值恢复给对象头**, 肯定恢复失败,因为对象的对象头中存储的是重量级锁的地址，状态变为10了，之前的是00，肯定恢复失败。那么会**进入重量级锁的解锁过程**，即按照Monitor的地址找到Monitor对象，将Owner设置为null，唤醒EntryList中的Thread-1线程

#### 自旋锁优化

当发生**重量级锁竞争**的时候，还可以使用**自旋**来进行优化 (不加入Monitor的阻塞队列EntryList中)，如果当前线程自旋成功（即在自旋的时候持锁的线程释放了锁），那么当前线程就可以不用进行上下文切换(持锁线程执行完synchronized同步块后，释放锁，Owner为空，唤醒阻塞队列来竞争，胜出的线程得到cpu执行权的过程) 就获得了锁。

优化的点：不用将`线程`加入到阻塞队列，减少cpu切换。

1. 自旋重试成功的情况
   ![1583758113724](https://img-blog.csdnimg.cn/img_convert/39ed180b2ab7eae1bc37ebba0a819c4c.png)
2. 自旋重试失败的情况，自旋了一定次数还是没有等到持锁的线程释放锁，线程2就会加入Monitor的阻塞队列(EntryList)
   ![1583758136650](https://img-blog.csdnimg.cn/img_convert/36162c78749df99fcd83560e3896aef0.png)

- 自旋会占用 CPU 时间，单核 CPU 自旋就是浪费，多核 CPU 自旋才能发挥优势。
- 在 Java 6 之后自旋锁是自适应的，比如对象刚刚的一次自旋操作成功过，那么认为这次自旋成功的可能性会高，就多自旋几次；反之，就少自旋甚至不自旋，总之，比较智能。Java 7 之后不能控制是否开启自旋功能

#### 偏向锁 (biased lock)

> 场景: 没有竞争的时候，一个线程中多次使用`synchronized`需要重入加锁的情况; **(只有一个线程进入临界区)**
>
> - 在经常需要竞争的情况下就不使用偏向锁，因为偏向锁是默认开启的，我们可以通过JVM的配置，将偏向锁给关闭

在轻量级的锁中，我们可以发现，如果同一个线程对同一个对象进行**重入锁**时，**也需要执行CAS替换操作，这是有点耗时。**

那么java6开始引入了偏向锁，将进入临界区的线程的ID，直接设置给锁对象的Mark word，下次该线程又获取锁，发现线程ID是自己，就不需要CAS了。

例如:

```java
public class Test {
    static final Object obj = new Object();

    public static void m1() {
        synchronized (obj) {
            // 同步块A
            m2();
        }
    }

    public static void m2() {
        synchronized (obj) {
            // 同步块B
            m3();
        }
    }

    public static void m3() {
        synchronized (obj) {
            // 同步块C
        }
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219223917148.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201219223934280.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210202174407252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210202174448323.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

------

##### 偏向锁状态 

回忆一下对象头的结构如下:
![1583762169169](https://img-blog.csdnimg.cn/img_convert/4b3e8d91dabdf79eb0ab250835c6bbc0.png)

- `Normal：一般状态，没有加任何锁`，前面62位保存的是对象的信息，**最后2位为状态（01），倒数第三位表示是否使用偏向锁（未使用：0）**

- `Biased：偏向状态，使用偏向锁`，前面54位保存的当前线程的ID，**最后2位为状态（01），倒数第三位表示是否使用偏向锁（使用：1）**

- `Lightweight：使用轻量级锁`，前62位保存的是锁记录的指针，**最后2位为状态（00）**

- `Heavyweight：使用重量级锁`，前62位保存的是Monitor的地址指针，**最后2位为状态(10)**

[![img](https://img-blog.csdnimg.cn/img_convert/461b804dda9e6ee0737faf0519ca295c.png)](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608145101.png)



一个对象的创建过程：

- 如果开启了`偏向锁`（默认是开启的），那么对象刚创建之后，Mark Word 最后三位的值101，并且这是它的`ThreadId`，`epoch`，`age(年龄计数器)`都是0，在加锁的时候进行设置这些的值.
- **偏向锁默认是延迟的**，不会在程序启动的时候立刻生效，如果想避免延迟，可以添加虚拟机参数来禁用延迟：`-XX:BiasedLockingStartupDelay=0`来禁用延迟
- 如果没有开启偏向锁，那么对象创建后，markword 值为 0x01 即最后 3 位为 001，这时它的 `hashcode`、
`age` 都为 0，第一次用到 hashcode 时才会赋值
- **注意**：处于偏向锁的对象解锁后，线程id仍存储于对象头中



1） 测试延迟特性

2） 测试偏向锁

```java
class Dog {}
```

利用 jol 第三方工具来查看对象头信息（注意这里我扩展了 jol 让它输出更为简洁）

```java
// 添加虚拟机参数 -XX:BiasedLockingStartupDelay=0
public static void main(String[] args) throws IOException {
    Dog d = new Dog();
    ClassLayout classLayout = ClassLayout.parseInstance(d);
    new Thread(() -> {
        log.debug("synchronized 前");
        System.out.println(classLayout.toPrintableSimple(true));
        synchronized (d) {
            log.debug("synchronized 中");
            System.out.println(classLayout.toPrintableSimple(true));
        }
        log.debug("synchronized 后");
        System.out.println(classLayout.toPrintableSimple(true));
    }, "t1").start();
}
```

输出结果:

```shell
11:08:58.117 c.TestBiased [t1] - synchronized 前
00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000101
11:08:58.121 c.TestBiased [t1] - synchronized 中
00000000 00000000 00000000 00000000 00011111 11101011 11010000 00000101 //中间为加锁时设置线程ID和age
11:08:58.121 c.TestBiased [t1] - synchronized 后
00000000 00000000 00000000 00000000 00011111 11101011 11010000 00000101 //下次重入锁，仍然使用线程ID来判断
```



3）测试禁用

如果没有开启偏向锁，那么对象创建后最后三位的值为001，这时候它的hashcode，age都为0，hashcode是第一次用到hashcode时才赋值的。在上面测试代码运行时在添加 VM 参数 `-XX:-UseBiasedLocking` 禁用偏向锁（禁用偏向锁则优先使用轻量级锁），退出synchronized状态变回001。

输出结果: 最开始状态为001，然后加轻量级锁变成00，最后恢复成001

```shell
11:13:10.018 c.TestBiased [t1] - synchronized 前
00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001
11:13:10.021 c.TestBiased [t1] - synchronized 中
00000000 00000000 00000000 00000000 00100000 00010100 11110011 10001000
11:13:10.021 c.TestBiased [t1] - synchronized 后
00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001
```

4) 测试 hashCode
正常状态对象一开始是没有 hashCode 的，第一次调用才生成



##### 撤销偏向锁-hashcode方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/202012192322377.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

测试 hashCode：当调用对象的hashcode方法的时候就会**撤销这个对象的偏向锁**，**因为使用偏向锁时没有位置存hashcode的值了**

------



##### 撤销偏向锁-其他线程使用对象

> 小故事: 线程A门上刻了名字，但此时线程B也要来使用房间了，所以要将偏向锁升级为**轻量级锁**。 (线程B要在线程A使用完房间之后(执行完synchronized代码块)再来使用；否则就成了竞争获取锁对象，此时就要升级为**重量级锁**了)

> 偏向锁、轻量级锁的使用条件，都是在于多个线程没有对同一个对象进行**锁竞争**的前提下，如果有锁竞争,此时就使用重量级锁。

这里我们演示的是偏向锁撤销，变成轻量级锁的过程，那么就得满足轻量级锁的使用条件，就是没有线程对同一个对象进行锁竞争，我们使用`wait` 和 `notify` 来辅助实现

虚拟机参数`-XX:BiasedLockingStartupDelay=0`确保我们的程序最开始使用了偏向锁

```java
private static void test2() throws InterruptedException {
    Dog d = new Dog();
    Thread t1 = new Thread(() -> {
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
        }
        synchronized (TestBiased.class) {
            TestBiased.class.notify();
        }
        // 如果不用 wait/notify 使用 join 必须打开下面的注释
        // 因为：t1 线程不能结束，否则底层线程可能被 jvm 重用作为 t2 线程，底层线程 id 是一样的
        /*try {
            System.in.read();
            } catch (IOException e) {
            e.printStackTrace();
            }*/
    }, "t1");
    t1.start();
    Thread t2 = new Thread(() -> {
        synchronized (TestBiased.class) {
            try {
                //这里使用wait方法的目的是让线程1执行完了之后再执行线程2，否则就会发生锁竞争，升级为重量级锁
                TestBiased.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
        }
        log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
    }, "t2");
    t2.start();
}
```



输出结果，最开始使用的是偏向锁，但是第二个线程尝试获取对象锁时(前提是: 线程一已经释放掉锁了,也就是执行完synchroized代码块)，发现本来对象偏向的是线程一，那么偏向锁就会失效，加的就是轻量级锁，最后线程2执行完后变为无锁状态

```shell
[t1] - 00000000 00000000 00000000 00000000 00011111 01000001 00010000 00000101
[t2] - 00000000 00000000 00000000 00000000 00011111 01000001 00010000 00000101
[t2] - 00000000 00000000 00000000 00000000 00011111 10110101 11110000 01000000
[t2] - 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001
```



##### 撤销偏向锁 - 调用 wait/notify

> 调用wait方法会导致**锁膨胀**而使用重量级锁，因为wait/notify方法之后重量级锁才支持

```java
public static void main(String[] args) throws InterruptedException {
    Dog d = new Dog();
    Thread t1 = new Thread(() -> {
        log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
            try {
                d.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug(ClassLayout.parseInstance(d).toPrintableSimple(true));
        }
    }, "t1");
    t1.start();
    new Thread(() -> {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (d) {
            log.debug("notify");
            d.notify();
        }
    }, "t2").start();
}
```

输出

```shell
[t1] - 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000101
[t1] - 00000000 00000000 00000000 00000000 00011111 10110011 11111000 00000101
[t2] - notify
[t1] - 00000000 00000000 00000000 00000000 00011100 11010100 00001101 11001010
```



##### 批量重偏向

如果对象被多个线程访问，但是没有竞争 (上面撤销偏向锁就是这种情况: 一个线程执行完，另一个线程再来执行，没有竞争)，这时偏向T1的对象仍有机会重新偏向T2重偏向会重置Thread ID

当撤销偏向锁阈值超过 20 次后，jvm 会这样觉得，我是不是偏向错了呢，于是会在给这些对象加锁时重新偏向至
加锁线程。

```java
private static void test3() throws InterruptedException {
    Vector<Dog> list = new Vector<>();
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 30; i++) {
            Dog d = new Dog();
            list.add(d);
            synchronized (d) {
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple(true));
            }
        }
        synchronized (list) {
            list.notify();
        }
    }, "t1");
    t1.start();
    Thread t2 = new Thread(() -> {
        synchronized (list) {
            try {
                list.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.debug("===============> ");
        for (int i = 0; i < 30; i++) {
            Dog d = list.get(i);
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple(true));
            synchronized (d) {
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple(true));
            }
            log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintableSimple(true));
        }
    }, "t2");
    t2.start();
}
```



##### 批量撤销偏向锁

当撤销偏向锁阈值超过 40 次后，jvm 会这样觉得，自己确实偏向错了，根本就不该偏向。于是整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的。



#### 同步省略 (锁消除)

1. 线程同步的代价是相当高的，同步的后果是降低并发性和性能。
2. 在动态编译同步块的时候，JIT编译器可以借助逃逸分析来判断同步块所**使用的锁对象是否只能够被一个线程访问而没有被发布到其他线程。**
3. 如果没有，那么JIT编译器在编译这个同步块的时候就会取消对这部分代码的同步。这样就能大大提高并发性和性能。这个**取消同步的过程就叫同步省略，也叫锁消除**。

- 例如下面的智障代码，根本起不到锁的作用

```java
public void f() {
    Object hellis = new Object();
    synchronized(hellis) {
        System.out.println(hellis);
    }
}
```

- 代码中对hellis这个对象加锁，但是hellis对象的生命周期只在f()方法中，并不会被其他线程所访问到，所以在JIT编译阶段就会被优化掉，优化成：

```java
public void f() {
    Object hellis = new Object();
	System.out.println(hellis);
}
```



从字节码角度分析：字节码文件中并没有进行优化，可以看到加锁和释放锁的操作依然存在，**同步省略操作是在解释运行时发生的**

![image-20200729103650309](https://imgconvert.csdnimg.cn/aHR0cDovL2hleWdvLm9zcy1jbi1zaGFuZ2hhaS5hbGl5dW5jcy5jb20vaW1hZ2VzL2ltYWdlLTIwMjAwNzI5MTAzNjUwMzA5LnBuZw?x-oss-process=image/format,png)

------



## 7. wait notify

### 7.1 小故事

![image-20210303103310218](https://gitee.com/jchenTech/images/raw/master/img/20210303103319.png)


### 7.2 wait、notify介绍

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201220084652893.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

- Owner 线程发现条件不满足，调用 `wait` 方法，即可进入 WaitSet 变为 `WAITING` 状态
- `BLOCKED` 和 `WAITING` 的线程都处于阻塞状态，不占用 CPU 时间片
- `BLOCKED` 线程会在 Owner 线程释放锁时唤醒
- `WAITING` 线程会在 Owner 线程调用 `notify` 或 `notifyAll` 时唤醒，但唤醒后并不意味者立刻获得锁，仍需进入EntryList 重新竞争

### 7.3 API介绍

下面的三个方法都是`Object`中的方法；通过锁对象来调用

- `obj.wait()`：让获得对象锁的线程到waitSet中一直等待
- `obj.wait(long n)` ：当该等待线程没有被notify，等待时间到了之后，也会自动唤醒
- `obj.notify()`：在 object 上正在 waitSet 等待的线程中挑一个唤醒
- `obj.notifyAll()` ：让 object 上正在 waitSet 等待的线程全部唤醒



它们都是线程之间进行协作的手段，都属于Object对象的方法，必须获得此对象的锁，才能调用这些方法

演示`wait和notify`方法

```java
final static Object obj = new Object();
public static void main(String[] args) {
    new Thread(() -> {
        synchronized (obj) {
            log.debug("执行....");
            try {
                obj.wait(); // 让线程在obj上一直等待下去
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("其它代码....");
        }
    }).start();
    new Thread(() -> {
        synchronized (obj) {
            log.debug("执行....");
            try {
                obj.wait(); // 让线程在obj上一直等待下去
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("其它代码....");
        }
    }).start();
    // 主线程两秒后执行
    sleep(2);
    log.debug("唤醒 obj 上其它线程");
    synchronized (obj) {
        obj.notify(); // 唤醒obj上一个线程
        // obj.notifyAll(); // 唤醒obj上所有等待线程
    }
}
```

```shell
13:01:36.176 guizy.WaitNotifyTest [t1] - 执行...
13:01:36.178 guizy.WaitNotifyTest [t2] - 执行...
13:01:37.175 guizy.WaitNotifyTest [main] - 唤醒waitSet中的线程!
13:01:37.175 guizy.WaitNotifyTest [t2] - 其它代码...
13:01:37.175 guizy.WaitNotifyTest [t1] - 其它代码...
```



### 7.4 Sleep(long n) 和 Wait(long n)的区别 (重点)

不同点

- Sleep是Thread类的静态方法，Wait是Object的方法
- Sleep在阻塞的时候不会释放锁，而Wait在阻塞的时候会释放锁 (不释放锁的话，其他线程就无法唤醒该线程了)
- Sleep方法不需要与synchronized一起使用，而Wait方法需要与synchronized一起使用

相同点

- 阻塞状态都为`TIMED_WAITING` (限时等待)



### 7.5 wait/notify的正确使用

**Step 1 : 逐渐向下优化**

```java
@Slf4j(topic = "guizy.WaitNotifyTest")
public class WaitNotifyTest {
    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        //思考下面的解决方案好不好，为什么？
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    Sleeper.sleep(2);   // 会阻塞2s, 不会释放锁
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        Sleeper.sleep(1);
        new Thread(() -> {
            // 此时没有加锁, 所以会优先于其他人先执行
            // 这里能不能加 synchronized (room)？
            //synchronized (room) { // 如果加锁的话, 送烟人也需要等待小南睡2s的时间,此时即使送到了,小南线程也将锁释放了..
                hasCigarette = true;
                log.debug("烟到了噢！");
            //}
        }, "送烟的").start();
    }
}
```

- 不给送烟线程加synchronized输出情况

```java
10:16:32.311 guizy.WaitNotifyTest [小南] - 有烟没？[false]
10:16:32.318 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
10:16:33.318 guizy.WaitNotifyTest [送烟的] - 烟到了噢！
10:16:34.320 guizy.WaitNotifyTest [小南] - 有烟没？[true]
10:16:34.320 guizy.WaitNotifyTest [小南] - 可以开始干活了
10:16:34.320 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:34.320 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:34.320 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:34.321 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:34.321 guizy.WaitNotifyTest [其它人] - 可以开始干活了
```

- 给送烟线程加synchronized输出情况

```java
10:16:57.565 guizy.WaitNotifyTest [小南] - 有烟没？[false]
10:16:57.570 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
10:16:59.574 guizy.WaitNotifyTest [小南] - 有烟没？[false]
10:16:59.574 guizy.WaitNotifyTest [送烟的] - 烟到了噢！
10:16:59.575 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:59.575 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:59.575 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:59.575 guizy.WaitNotifyTest [其它人] - 可以开始干活了
10:16:59.576 guizy.WaitNotifyTest [其它人] - 可以开始干活了
```

- 其它干活的线程，都要一直阻塞，效率太低
- 小南线程必须睡足 2s 后才能醒来，就算烟提前送到，也无法立刻醒来
- 加了 `synchronized (room)` 后，就好比小南在里面反锁了门睡觉，烟根本没法送进门，main 没加synchronized 就好像 main 线程是翻窗户进来的
- 解决方法，使用 wait - notify 机制



**Step2:** 使用 wait - notify 机制

```java
@Slf4j(topic = "guizy.WaitNotifyTest")
public class WaitNotifyTest {
    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait(); // 此时进入到waitset等待集合, 同时会释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                // 小南进入等待状态了, 其他线程就可以获得锁了
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasCigarette = true;
                log.debug("烟到了噢！");
                room.notify();
            }
        }, "送烟的").start();
    }
}

```

```java
20:51:42.489 [小南] c.TestCorrectPosture - 有烟没？[false]
20:51:42.493 [小南] c.TestCorrectPosture - 没烟，先歇会！
20:51:42.493 [其它人] c.TestCorrectPosture - 可以开始干活了
20:51:42.493 [其它人] c.TestCorrectPosture - 可以开始干活了
20:51:42.494 [其它人] c.TestCorrectPosture - 可以开始干活了
20:51:42.494 [其它人] c.TestCorrectPosture - 可以开始干活了
20:51:42.494 [其它人] c.TestCorrectPosture - 可以开始干活了
20:51:43.490 [送烟的] c.TestCorrectPosture - 烟到了噢！
20:51:43.490 [小南] c.TestCorrectPosture - 有烟没？[true]
20:51:43.490 [小南] c.TestCorrectPosture - 可以开始干活了
```

如果此时除了小南在等待唤醒，还有一个线程也在等待唤醒呢？此时的`notify`方法会唤醒谁呢？

- 解决了其它干活的线程阻塞的问题
- 但如果有其它线程也在等待条件呢？



**Step3:** 当有其他wait线程时可能出现的问题

```java
@Slf4j(topic = "guizy.WaitNotifyTest")
public class WaitNotifyTest {
    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait(); // 此时进入到waitset等待集合, 同时会释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notify();
            }
        }, "送外卖的").start();
    }
}
```

```java
11:10:39.516 guizy.WaitNotifyTest [小南] - 有烟没？[false]
11:10:39.521 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
11:10:39.521 guizy.WaitNotifyTest [小女] - 外卖送到没？[false]
11:10:39.521 guizy.WaitNotifyTest [小女] - 没外卖，先歇会！
11:10:40.521 guizy.WaitNotifyTest [送外卖的] - 外卖到了噢！
11:10:40.521 guizy.WaitNotifyTest [小南] - 有烟没？[false]
```



问题：当外卖送到了，却唤醒了小南，此时就出现了问题

- notify 只能随机唤醒一个 WaitSet 中的线程，这时如果有其它线 程也在等待，那么就可能唤醒不了正确的线
  程，称之为【虚假唤醒】
- 解决方法，改为 notifyAll



**Step4:** 使用notifyall解决虚假唤醒

```java
new Thread(() -> {
 synchronized (room) {
   hasTakeout = true;
   log.debug("外卖到了噢！");
   room.notifyAll();
 }
}, "送外卖的").start();

```

```java
11:14:53.670 guizy.WaitNotifyTest [小南] - 有烟没？[false]
11:14:53.676 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
11:14:53.676 guizy.WaitNotifyTest [小女] - 外卖送到没？[false]
11:14:53.676 guizy.WaitNotifyTest [小女] - 没外卖，先歇会！
11:14:54.674 guizy.WaitNotifyTest [送外卖的] - 外卖到了噢！
11:14:54.674 guizy.WaitNotifyTest [小女] - 外卖送到没？[true]
11:14:54.674 guizy.WaitNotifyTest [小女] - 可以开始干活了
11:14:54.675 guizy.WaitNotifyTest [小南] - 有烟没？[false]
```

还是唤醒了小南，小南还是回去看看送来的是外卖还是烟。很麻烦，怎么解决？

- 用 notifyAll 仅解决某个线程的唤醒问题，但使用 if + wait 判断仅有一次机会，一旦条件不成立，就没有重新
  判断的机会了
- 解决方法，用 while + wait，当条件不成立，再次 wait



**Step5：** 使用`while循环`来解决`虚假唤醒`

```java
@Slf4j(topic = "guizy.WaitNotifyTest")
public class Main {
    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait(); // 此时进入到waitset等待集合, 同时会释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        Sleeper.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notifyAll();
            }
        }, "送外卖的").start();
    }
}
```

```java
11:19:25.275 guizy.WaitNotifyTest [小南] - 有烟没？[false]
11:19:25.282 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
11:19:25.282 guizy.WaitNotifyTest [小女] - 外卖送到没？[false]
11:19:25.283 guizy.WaitNotifyTest [小女] - 没外卖，先歇会！
11:19:26.287 guizy.WaitNotifyTest [送外卖的] - 外卖到了噢！
11:19:26.287 guizy.WaitNotifyTest [小女] - 外卖送到没？[true]
11:19:26.287 guizy.WaitNotifyTest [小女] - 可以开始干活了
11:19:26.288 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
```

因为改为`while`如果唤醒之后，就在while循环中执行了，不会跑到while外面去执行"有烟没…"，此时小南就不需要每次notify，就去看是不是送来的烟，如果是烟，while就为false了。



```java
synchronized(lock) {
    while(条件不成立) {
        lock.wait();
    }
    // 干活
}
//另一个线程
synchronized(lock) {
    lock.notifyAll();
}
```



------

## 8. 同步模式之保护性暂停 (join、Future的实现)

即Guarded Suspension，用在一个线程等待另一个线程的执行结果

- 有**一个结果需要从一个线程传递到另一个线程**，让他们关联同一个 GuardedObject
- 如果有结果不断从一个线程到另一个线程 那么可以使用消息队列（见生产者/消费者）
- JDK 中，join 的实现、Future 的实现，采用的就是此模式
- 因为要等待另一方的结果，因此归类到同步模式

![1594473284105](https://img-blog.csdnimg.cn/img_convert/e73412e2618ca103105cce41f4b228c9.png)

一方等待另一方的执行结果举例，线程1等待线程2下载的结果，并获取该结果：

```java
/**
 * Description: 多线程同步模式 - 一个线程需要等待另一个线程的执行结果
 */
@Slf4j(topic = "guizy.GuardeObjectTest")
public class GuardeObjectTest {
    public static void main(String[] args) {
        // 线程1等待线程2的下载结果
        GuardeObject guardeObject = new GuardeObject();
        new Thread(() -> {
            log.debug("等待结果");
            List<String> list = (List<String>) guardeObject.get();
            log.debug("结果大小:{}", list.size());
        }, "t1").start();

        new Thread(() -> {
            log.debug("执行下载");
            try {
                List<String> list = Downloader.download();
                guardeObject.complete(list);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, "t2").start();
    }
}

class GuardeObject {
    // 结果
    private Object response;

    // 获取结果
    public Object get() {
        synchronized (this) {
            // 防止虚假唤醒
            // 没有结果
            while (response == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }
    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            // 给结果变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}
```

- 线程t1 等待 线程t2的结果，可以设置超时时间，如果超过时间还没返回结果,此时就不等了。退出while循环

```java
@Slf4j(topic = "guizy.GuardeObjectTest")
public class GuardeObjectTest {
    public static void main(String[] args) {
        // 线程1等待线程2的下载结果
        GuardeObject guardeObject = new GuardeObject();
        new Thread(() -> {
            log.debug("begin");
            Object obj = guardeObject.get(2000);
            log.debug("结果是:{}", obj);
        }, "t1").start();

        new Thread(() -> {
            log.debug("begin");
            // Sleeper.sleep(1); // 在等待时间内
            Sleeper.sleep(3);
            guardeObject.complete(new Object());
        }, "t2").start();
    }
}

class GuardeObject {
    // 结果
    private Object response;

    // 获取结果
    // timeout表示等待多久. 这里假如是2s
    public Object get(long timeout) {
        synchronized (this) {
            // 假如开始时间为 15:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间, 退出循环
                if (waitTime <= 0) { 
                    break;
                }
                try {
                    // this.wait(timeout)的问题: 虚假唤醒在15:00:01的时候,此时response还null, 此时经历时间就为1s,
                    // 进入while循环的时候response还是空,此时判断1s<=timeout 2s,此时再次this.wait(2s)吗,此时已经经历了
                    // 1s,所以只要再等1s就可以了. 所以等待的时间应该是 超时时间(timeout) - 经历的时间(passedTime)
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 经历时间
                passedTime = System.currentTimeMillis() - begin; // 15:00:02
            }
            return response;
        }
    }

    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            // 给结果变量赋值
            this.response = response;
            this.notifyAll();
    }
}

// 在等待时间内的情况
16:20:41.627 guizy.GuardeObjectTest [t1] - begin
16:20:41.627 guizy.GuardeObjectTest [t2] - begin
16:20:42.633 guizy.GuardeObjectTest [t1] - 结果是:java.lang.Object@1e1d0168

// 超时的情况
16:21:24.663 guizy.GuardeObjectTest [t2] - begin
16:21:24.663 guizy.GuardeObjectTest [t1] - begin
16:21:26.667 guizy.GuardeObjectTest [t1] - 结果是:null
```

- 关于超时的增强，在`join(long millis) 的源码`中得到了体现：

```java
public final synchronized void join(long millis)
throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
    // join一个指定的时间
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

多任务版`GuardedObject`：图中 `Futures` 就好比居民楼一层的信箱（每个信箱有房间编号），左侧的 t0，t2，t4 就好比等待邮件的居民，右侧的 t1，t3，t5 就好比邮递员。

如果需要在多个类之间使用 GuardedObject 对象，作为参数传递不是很方便，因此设计一个用来解耦的中间类。 不仅能够解耦【结果等待者】和【结果生产者】，还能够同时支持多个任务的管理。和生产者消费者模式的区别就是：**这个产生结果的线程和使用结果的线程是一一对应的关系，但是生产者消费者模式并不是。**

rpc框架的调用中就使用到了这种模式。

![1594518049426](https://img-blog.csdnimg.cn/img_convert/8af156fb943083720b551f6e52ddd03c.png)

```java
/**
 * Description: 同步模式保护性暂停模式 (多任务版)
 */
@Slf4j(topic = "guizy.GuardedObjectTest")
public class GuardedObjectTest {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        Sleeper.sleep(1);
        for (Integer id : Mailboxes.getIds()) {
            new Postman(id，"内容" + id).start();
        }
    }
}

@Slf4j(topic = "guizy.People")
class People extends Thread {
    @Override
    public void run() {
        // 收信
        GuardedObject guardedObject = Mailboxes.createGuardedObject();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}

@Slf4j(topic = "guizy.Postman")
// 邮寄员类
class Postman extends Thread {
    private int id;
    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObject guardedObject = Mailboxes.getGuardedObject(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        guardedObject.complete(mail);
    }
}

// 信箱类
class Mailboxes {
    private static Map<Integer, GuardedObject> boxes = new Hashtable<>();

    private static int id = 1;

    // 产生唯一 id
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObject getGuardedObject(int id) {
        //根据id获取到box并删除对应的key和value,避免堆内存爆了
        return boxes.remove(id);
    }

    public static GuardedObject createGuardedObject() {
        GuardedObject go = new GuardedObject(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

// 用来传递信息的作用, 当多个类使用GuardedObject,就很不方便,此时需要一个设计一个解耦的中间类
class GuardedObject {
    // 标记GuardedObject
    private int id;
    // 结果
    private Object response;

    public int getId() {
        return id;
    }

    public GuardedObject(int id) {
        this.id = id;
    }

    // 获取结果
    // timeout表示等待多久. 这里假如是2s
    public Object get(long timeout) {
        synchronized (this) {
            // 假如开始时间为 15:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间, 退出循环
                if (waitTime <= 0) {
                    break;
                }
                try {
                    // this.wait(timeout)的问题: 虚假唤醒在15:00:01的时候,此时response还null, 此时经历时间就为1s,
                    // 进入while循环的时候response还是空,此时判断1s<=timeout 2s,此时再次this.wait(2s)吗,此时已经经历了
                    // 1s,所以只要再等1s就可以了. 所以等待的时间应该是 超时时间(timeout) - 经历的时间(passedTime)
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 经历时间
                passedTime = System.currentTimeMillis() - begin; // 15:00:02
            }
            return response;
        }
    }

    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            // 给结果变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}
```

## 9. 异步模式之生产者/消费者 (重点)

- 与前面的保护性暂停中的 `GuardedObject` 不同，不需要产生结果和消费结果的线程一一对应 (一个生产一个消费)
- 消费队列可以用来平衡生产和消费的线程资源
- 生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
- 消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
- JDK 中各种 [阻塞队列](https://blog.csdn.net/yanpenglei/article/details/79556591)，采用的就是这种模式

异步模式中，生产者产生消息之后消息没有被立刻消费
同步模式中，消息在产生之后被立刻消费了。

![1594524622020](https://img-blog.csdnimg.cn/img_convert/82487e714da1c44453d46a61d6dcb27b.png)

- 我们下面写的小例子是线程间通信的消息队列，要注意区别，像RabbitMQ等消息框架是进程间通信的。

```java
/**
 * Description: 异步模式之生产者/消费者
 */
@Slf4j(topic = "giuzy.ProductConsumerTest")
public class ProductConsumerTest {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);

        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id，"值" + id));
            }, "生产者" + i).start();
        }

        new Thread(() -> {
            while (true) {
                Sleeper.sleep(1);
                Message message = queue.take();
            }
        }, "消费者").start();
    }

}

// 消息队列类,在线程之间通信
@Slf4j(topic = "guizy.MessageQueue")
class MessageQueue {
    // 消息的队列集合
    private LinkedList<Message> list = new LinkedList<>();
    // 队列容量
    private int capcity;

    public MessageQueue(int capcity) {
        this.capcity = capcity;
    }

    // 获取消息
    public Message take() {
        // 检查队列是否为空
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    log.debug("队列为空, 消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列头部获取消息并返回
            Message message = list.removeFirst();
            log.debug("已消费消息 {}", message);
            list.notifyAll();
            return message;
        }
    }

    // 存入消息
    public void put(Message message) {
        synchronized (list) {
            // 检查对象是否已满
            while (list.size() == capcity) {
                try {
                    log.debug("队列已满, 生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 将消息加入队列尾部
            list.addLast(message);
            log.debug("已生产消息 {}", message);
            list.notifyAll();
        }
    }
}

final class Message {
    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}

18:52:53.440 guizy.MessageQueue [生产者1] - 已生产消息 Message{id=1, value=值1}
18:52:53.443 guizy.MessageQueue [生产者0] - 已生产消息 Message{id=0, value=值0}
18:52:53.444 guizy.MessageQueue [生产者2] - 队列已满, 生产者线程等待
18:52:54.439 guizy.MessageQueue [消费者] - 已消费消息 Message{id=1, value=值1}
18:52:54.439 guizy.MessageQueue [生产者2] - 已生产消息 Message{id=2, value=值2}
18:52:55.439 guizy.MessageQueue [消费者] - 已消费消息 Message{id=0, value=值0}
18:52:56.440 guizy.MessageQueue [消费者] - 已消费消息 Message{id=2, value=值2}
18:52:57.441 guizy.MessageQueue [消费者] - 队列为空, 消费者线程等待
```

## 10. park & unpack (重要)

### 10.1 基本使用

- `park/unpark`都是`LockSupport`类中的的方法
- **先调用`unpark`后，再调用`park`，此时`park`不会暂停线程**

```java
// 暂停当前线程
LockSupport.park();
// 恢复某个线程的运行
LockSupport.unpark(thread);
```

```java
Thread t1 = new Thread(() -> {
    log.debug("start...");
    sleep(1);
    log.debug("park...");
    LockSupport.park();
    log.debug("resume...");
},"t1");
t1.start();

sleep(2);
log.debug("unpark...");
LockSupport.unpark(t1);
```



### 10.2 特点

与 Object 的 wait & notify 相比

- wait，notify 和 notifyAll 必须配合 Object Monitor 一起使用，而 park，unpark 不必
- park & unpark 是以线程为单位来【阻塞】和【唤醒】线程，而 notify 只能随机唤醒一个等待线程，notifyAll是唤醒所有等待线程，就不那么【精确】
- park & unpark 可以先 unpark，而 wait & notify 不能先 notify



### 10.3 park、 unpark 原理

每个线程都有自己的一个 `Parker` 对象，由三部分组成 **`_counter`， `_cond`和 `_mutex`**

- 打个比喻线程就像一个旅人，Parker 就像他随身携带的背包，条件变量 `_cond`就好比背包中的帐篷。`_counter` 就好比背包中的备用干粮（0 为耗尽，1 为充足）
- 调用 `park()` 就是要看需不需要停下来歇息
  - 如果备用干粮耗尽，那么钻进帐篷歇息
  - 如果备用干粮充足，那么不需停留，继续前进
- 调用 unpark，就好比令干粮充足
  - 如果这时线程还在帐篷，就唤醒让他继续前进
  - 如果这时线程还在运行，那么下次他调用 park 时，仅是消耗掉备用干粮，不需停留继续前进
  - 因为背包空间有限，多次调用 unpark 仅会补充一份备用干粮

### 10.3 先调用park再调用upark的过程

先调用park

- 当前线程调用 `Unsafe.park()` 方法
- 检查 `_counter`，本情况为0, 这时，获得`_mutex 互斥锁`(mutex对象有个等待队列 `_cond`)
- 线程进入 `_cond` 条件变量阻塞
- 设置`_counter = 0` (没干粮了)

![1594531894163](https://img-blog.csdnimg.cn/img_convert/090e9cb2aed20d43c147ec0ea6470d5e.png)

调用unpark

- 调用`Unsafe.unpark(Thread_0)方法`，设置`_counter 为 1`
- 唤醒 `_cond` 条件变量中的 Thread_0
- Thread_0 恢复运行
- 设置 `_counter` 为 0

![1594532057205](https://img-blog.csdnimg.cn/img_convert/ca03643f837f34098def91bfadc54bd6.png)

### 10.4 先调用unpark再调用park的过程

- 调用 `Unsafe.unpark(Thread_0)`方法，设置 `_counter 为 1`
- 当前线程调用 `Unsafe.park()` 方法
- 检查 `_counter`，本情况为 1，这时线程 **无需阻塞，继续运行**
- 设置 _counter 为 0

![1594532135616](https://img-blog.csdnimg.cn/img_convert/d0d3270088a031c2066af2762e894413.png)

## 11. 线程状态转换 (重点)

![img](https://img-blog.csdnimg.cn/img_convert/64146d2ab235481979b13ec4e9608fc5.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201221214359753.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

假设有线程 Thread t

**情况1：`NEW –> RUNNABLE`**

- 调用`t.start()`方法时，由`NEW -> RUNNABLE`



**情况2：`RUNNABLE <--> WAITING`**

t线程用`synchronized(obj)`获取了对象锁后

- 调用 `obj.wait()` 方法时，t 线程进入 waitSet 中，从`RUNNABLE --> WAITING`
- 调用`obj.notify()`，`obj.notifyAll()`，`t.interrupt()`时，唤醒的线程都到entryList阻塞队列成为BLOCKED状态，在阻塞队列，和其他线程再进行竞争锁
  - **竞争锁成功**，t 线程从 `WAITING --> RUNNABLE`
  - **竞争锁失败**，t 线程从 `WAITING --> BLOCKED`



**情况3：`RUNNABLE <–> WAITING`**

- **当前线程**调用 **`t.join()`** 方法时，**当前线程**从 `RUNNABLE --> WAITING` 
  - 注意是**当前线程**在t线程对象的监视器上等待
- **t 线程**运行结束，或调用了当前线程的 `interrupt()` 时，**当前线程**从 `WAITING --> RUNNABLE`



**情况4：`RUNNABLE <–> WAITING`**

- 当前线程调用 **`LockSupport.park()`** 方法会让**当前线程**从`RUNNABLE --> WAITING`
- 调用 **`LockSupport.unpark(目标线程)`** 或调用了线程 的 `interrupt()` ，会让目标线程从 `WAITING --> RUNNABLE`



**情况5：`RUNNABLE <–> TIMED_WAITING` (带超时时间的wait)**

t 线程用 `synchronized(obj)` 获取了对象锁后

- 调用 **`obj.wait(long n)`** 方法时，t 线程从 `RUNNABLE --> TIMED_WAITING`
- t 线程等待时间超过了 n 毫秒，或调用 `obj.notify()` ， `obj.notifyAll()` ， `t.interrupt()` 时；唤醒的线程都到entryList阻塞队列成为BLOCKED状态，在阻塞队列，和其他线程再进行竞争锁
  - 竞争锁成功，t 线程从 **TIMED_WAITING --> RUNNABLE**
  - 竞争锁失败，t 线程从 **TIMED_WAITING --> BLOCKED**



**情况6：`RUNNABLE <–> TIMED_WAITING`**

- 当前线程调用 **`t.join(long n)`** 方法时，当前线程从 `RUNNABLE --> TIMED_WAITING` 
  - 注意是当前线程在t 线程对象的监视器上等待
- 当前线程等待时间超过了 n 毫秒，或 t 线程运行结束，或调用了当前线程的 `interrupt()` 时，当前线程从 `TIMED_WAITING --> RUNNABLE`



**情况7：RUNNABLE <–> TIMED_WAITING**

- 当前线程调用 `Thread.sleep(long n)` ，当前线程从 `RUNNABLE --> TIMED_WAITING`
- 当前线程等待时间超过了 n 毫秒或调用了线程的 `interrupt()` ，当前线程从 `TIMED_WAITING --> RUNNABLE`



**情况8：`RUNNABLE <–> TIMED_WAITING**`

- 当前线程调用 `LockSupport.parkNanos(long nanos)` 或 `LockSupport.parkUntil(long millis)` 时，当前线程从 `RUNNABLE --> TIMED_WAITING`
- 调用`LockSupport.unpark(目标线程)` 或调用了线程的 `interrupt()` ，或是等待超时，会让目标线程从 `TIMED_WAITING--> RUNNABLE`



**情况9：RUNNABLE <–> BLOCKED**

- t 线程用 `synchronized(obj)` 获取了对象锁时如果竞争失败，从 `RUNNABLE –> BLOCKED`
- 持 obj 锁线程的同步代码块执行完毕，会唤醒该对象上所有 BLOCKED 的线程重新竞争，如果其中 t 线程竞争 成功，从 BLOCKED –> RUNNABLE ，其它失败的线程仍然 BLOCKED



**情况10：`RUNNABLE <–> TERMINATED`**

- 当前线程所有代码运行完毕，进入 `TERMINATED`



![在这里插入图片描述](https://img-blog.csdnimg.cn/20210202223351594.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

## 12. 多把锁

一间大屋子有两个功能：`睡觉、学习，互不相干`。

现在小南要学习，小女要睡觉，但如果只用一间屋子（一个对象锁）的话，那么并发度很低

**解决方法是准备多个房间`（多个对象锁）`**

```java
@Slf4j(topic = "guizy.BigRoomTest")
public class BigRoomTest {
    public static void main(String[] args) {
        BigRoom bigRoom = new BigRoom();
        new Thread(() -> bigRoom.sleep(), "小南").start();
        new Thread(() -> bigRoom.study(), "小女").start();
    }
}

@Slf4j(topic = "guizy.BigRoom")
class BigRoom {
    public void sleep() {
        synchronized (this) {
            log.debug("sleeping 2 小时");
            Sleeper.sleep(2);
        }
    }

    public void study() {
        synchronized (this) {
            log.debug("study 1 小时");
            Sleeper.sleep(1);
        }
    }
}

// 相当于串行执行, 因为锁对象是整个屋子, 所以并发性很低
12:16:15.952 guizy.BigRoom [小南] - sleeping 2 小时
12:16:17.954 guizy.BigRoom [小女] - study 1 小时
```

改进让`小南, 小女`获取不同的锁即可

```java
@Slf4j(topic = "guizy.BigRoomTest")
public class BigRoomTest {
    private static final BigRoom sleepRoom = new BigRoom();
    private static final BigRoom studyRoom = new BigRoom();

    public static void main(String[] args) {
      // 不同对象调用
        new Thread(() -> sleepRoom.sleep(), "小南").start();
        new Thread(() -> studyRoom.study(), "小女").start();
    }
}

// 因为使用的是不同的锁对象
12:18:50.580 guizy.BigRoom [小女] - study 1 小时
12:18:50.580 guizy.BigRoom [小南] - sleeping 2 小时
```

将锁的粒度细分

- 好处，是可以增强并发度
- 坏处，如果一个线程需要同时获得多把锁，就容易发生**死锁**

## 13. 活跃性

因为某种原因，使得代码一直无法执行完毕，这样的现象叫做 **活跃性**。活跃性相关的一系列问题都可以用 **`ReentrantLock`** 进行解决。

### 13.1 死锁 (重点)

有这样的情况：一个线程需要 **同时获取多把锁**，这时就容易发生死锁

如：`线程1`获取`A对象`锁，`线程2`获取`B对象`锁；此时`线程1`又想获取`B对象`锁，`线程2`又想获取`A对象`锁；它们都等着对象释放锁，此时就称为死锁

```java
Object A = new Object();
Object B = new Object();
Thread t1 = new Thread(() -> {
    synchronized (A) {
        log.debug("lock A");
        sleep(1);
        synchronized (B) {
            log.debug("lock B");
            log.debug("操作...");
        }
    }
}, "t1");
Thread t2 = new Thread(() -> {
    synchronized (B) {
        log.debug("lock B");
        sleep(0.5);
        synchronized (A) {
            log.debug("lock A");
            log.debug("操作...");
        }
    }
}, "t2");
t1.start();
t2.start();

12:22:06.962 [t2] c.TestDeadLock - lock B
12:22:06.962 [t1] c.TestDeadLock - lock A
```

### 13.2 死锁的必要条件

- 互斥条件
  - 在一段时间内，**一种资源只能被一个进程所使用**
- 请求和保持条件
  - 进程已经拥有了至少一种资源，同时又去申请其他资源。因为其他资源被别的进程所使用，该进程进入阻塞状态，并且不释放自己已有的资源
- 不可抢占条件
  - 进程对已获得的资源在未使用完成前不能被强占，只能在进程使用完后自己释放
- 循环等待条件
  - 发生死锁时，必然存在一个进程——资源的循环链。

### 13.3 定位死锁

检测死锁可以使用 jconsole工具，或者使用 jps 定位进程 id，再用 jstack 定位死锁：

```shell
cmd > jps
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
12320 Jps
22816 KotlinCompileDaemon
33200 TestDeadLock // JVM 进程
11508 Main
28468 Launcher
```



```shell
cmd > jstack 33200
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
2018-12-29 05:51:40
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.91-b14 mixed mode):
"DestroyJavaVM" #13 prio=5 os_prio=0 tid=0x0000000003525000 nid=0x2f60 waiting on condition
[0x0000000000000000]
java.lang.Thread.State: RUNNABLE
"Thread-1" #12 prio=5 os_prio=0 tid=0x000000001eb69000 nid=0xd40 waiting for monitor entry
[0x000000001f54f000]
java.lang.Thread.State: BLOCKED (on object monitor)
at thread.TestDeadLock.lambda$main$1(TestDeadLock.java:28)
- waiting to lock <0x000000076b5bf1c0> (a java.lang.Object)
- locked <0x000000076b5bf1d0> (a java.lang.Object)
at thread.TestDeadLock$$Lambda$2/883049899.run(Unknown Source)
at java.lang.Thread.run(Thread.java:745)
"Thread-0" #11 prio=5 os_prio=0 tid=0x000000001eb68800 nid=0x1b28 waiting for monitor entry
[0x000000001f44f000]
java.lang.Thread.State: BLOCKED (on object monitor)
at thread.TestDeadLock.lambda$main$0(TestDeadLock.java:15)
- waiting to lock <0x000000076b5bf1d0> (a java.lang.Object)
- locked <0x000000076b5bf1c0> (a java.lang.Object)
at thread.TestDeadLock$$Lambda$1/495053715.run(Unknown Source)
at java.lang.Thread.run(Thread.java:745)
// 略去部分输出
Found one Java-level deadlock:
=============================
"Thread-1":
waiting to lock monitor 0x000000000361d378 (object 0x000000076b5bf1c0, a java.lang.Object),
which is held by "Thread-0"
"Thread-0":
waiting to lock monitor 0x000000000361e768 (object 0x000000076b5bf1d0, a java.lang.Object),
which is held by "Thread-1"
Java stack information for the threads listed above:
===================================================
"Thread-1":
at thread.TestDeadLock.lambda$main$1(TestDeadLock.java:28)
- waiting to lock <0x000000076b5bf1c0> (a java.lang.Object)
- locked <0x000000076b5bf1d0> (a java.lang.Object)
at thread.TestDeadLock$$Lambda$2/883049899.run(Unknown Source)
at java.lang.Thread.run(Thread.java:745)
"Thread-0":
at thread.TestDeadLock.lambda$main$0(TestDeadLock.java:15)
- waiting to lock <0x000000076b5bf1d0> (a java.lang.Object)
- locked <0x000000076b5bf1c0> (a java.lang.Object)
at thread.TestDeadLock$$Lambda$1/495053715.run(Unknown Source)
at java.lang.Thread.run(Thread.java:745)
Found 1 deadlock.
```




### 13.4 哲学家就餐问题 (重点)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201223123802724.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)
有五位哲学家，围坐在圆桌旁。

- 他们只做两件事，思考和吃饭，思考一会吃口饭，吃完饭后接着思考。
- 吃饭时要用两根筷子吃，桌上共有 5 根筷子，每位哲学家左右手边各有一根筷子。
- 如果筷子被身边的人拿着，自己就得等待

当每个哲学家即线程持有一根筷子时，他们都在等待另一个线程释放锁，因此造成了死锁。

```java
/**
 * Description: 使用synchronized加锁, 导致哲学家就餐问题, 死锁: 核心原因是因为synchronized的锁是不可打断的, 进入阻塞队列，需要一直等待别的线程释放锁
 *

 */
@Slf4j(topic = "guizy.PhilosopherEat")
public class PhilosopherEat {
    public static void main(String[] args) {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c5, c1).start();
    }
}

@Slf4j(topic = "guizy.Philosopher")
class Philosopher extends Thread {
    final Chopstick left;
    final Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true) {
            // 尝试获取左手筷子
            synchronized (left) {
                // 尝试获取右手筷子
                synchronized (right) {
                    eat();
                }
            }
        }
    }

    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(0.5);
    }
}

class Chopstick{
    String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}


15:04:55.346 guizy.Philosopher [苏格拉底] - eating...
15:04:55.346 guizy.Philosopher [亚里士多德] - eating...
15:04:55.850 guizy.Philosopher [亚里士多德] - eating...
15:04:55.850 guizy.Philosopher [苏格拉底] - eating...
15:04:56.351 guizy.Philosopher [亚里士多德] - eating...
15:04:56.852 guizy.Philosopher [亚里士多德] - eating...
//卡在这里，不继续打印了
```

通过`jps, jstack 进程id`查看死锁原因
`Found one Java-level deadlock:` 发现了一个Java级别的死锁

```java
Found one Java-level deadlock:
=============================
"阿基米德":
  waiting to lock monitor 0x000000001ae2a358 (object 0x00000000d6ea7420, a com.guizy.reentrantlock.Chopstick),
  which is held by "苏格拉底"
"苏格拉底":
  waiting to lock monitor 0x0000000017fb3518 (object 0x00000000d6ea7430, a com.guizy.reentrantlock.Chopstick),
  which is held by "柏拉图"
"柏???图":
  waiting to lock monitor 0x0000000017fb3468 (object 0x00000000d6ea7440, a com.guizy.reentrantlock.Chopstick),
  which is held by "亚里士多德"
"亚里士多德":
  waiting to lock monitor 0x0000000017fb0bd8 (object 0x00000000d6ea7450, a com.guizy.reentrantlock.Chopstick),
  which is held by "赫拉克利特"
"赫拉克利特":
  waiting to lock monitor 0x0000000017fb0c88 (object 0x00000000d6ea7460, a com.guizy.reentrantlock.Chopstick),
  which is held by "阿基米德"

Java stack information for the threads listed above:
===================================================
"阿基米德":
        at com.guizy.reentrantlock.Philosopher.run(PhilosopherEat.java:47)
        - waiting to lock <0x00000000d6ea7420> (a com.guizy.reentrantlock.Chopstick)
        - locked <0x00000000d6ea7460> (a com.guizy.reentrantlock.Chopstick)
"苏格拉底":
        at com.guizy.reentrantlock.Philosopher.run(PhilosopherEat.java:47)
        - waiting to lock <0x00000000d6ea7430> (a com.guizy.reentrantlock.Chopstick)
        - locked <0x00000000d6ea7420> (a com.guizy.reentrantlock.Chopstick)
"柏拉图":
        at com.guizy.reentrantlock.Philosopher.run(PhilosopherEat.java:47)
        - waiting to lock <0x00000000d6ea7440> (a com.guizy.reentrantlock.Chopstick)
        - locked <0x00000000d6ea7430> (a com.guizy.reentrantlock.Chopstick)
"亚里士多德":
        at com.guizy.reentrantlock.Philosopher.run(PhilosopherEat.java:47)
        - waiting to lock <0x00000000d6ea7450> (a com.guizy.reentrantlock.Chopstick)
        - locked <0x00000000d6ea7440> (a com.guizy.reentrantlock.Chopstick)
"赫拉克利特":
        at com.guizy.reentrantlock.Philosopher.run(PhilosopherEat.java:47)
        - waiting to lock <0x00000000d6ea7460> (a com.guizy.reentrantlock.Chopstick)
        - locked <0x00000000d6ea7450> (a com.guizy.reentrantlock.Chopstick)

Found 1 deadlock.
```



### 13.5 活锁

活锁出现在两个线程互相改变对方的结束条件，最后谁也无法结束，例如

```java
public class TestLiveLock {
    static volatile int count = 10;
    static final Object lock = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                sleep(0.2);
                count--;
                log.debug("count: {}", count);
            }
        }, "t1").start();
        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                sleep(0.2);
                count++;
                log.debug("count: {}", count);
            }
        }, "t2").start();
    }
}
```

如果想避免活锁，那么在线程执行时，中途给予 **不同的间隔时间**, 让某个线程先结束即可。

死锁与活锁的区别

- 死锁是因为线程互相持有对象想要的锁，并且都不释放，最后导致**线程阻塞**，**停止运行**的现象。
- 活锁是因为线程间修改了对方的结束条件，而导致代码**一直在运行**，却一直**运行不完**的现象。

### 13.6 饥饿

很多教程中把饥饿定义为，一个线程由于优先级太低，始终得不到 CPU 调度执行，也不能够结束，饥饿的情况不易演示，讲读写锁时会涉及饥饿问题。

下面我讲一下我遇到的一个线程饥饿的例子，先来看看使用顺序加锁的方式解决之前的死锁问题

![image-20210303164223058](C:\Users\jchen\AppData\Roaming\Typora\typora-user-images\image-20210303164223058.png)

顺序加锁的解决方案

![image-20210303164156324](https://gitee.com/jchenTech/images/raw/master/img/20210303164200.png)



- 在线程使用锁对象时，采用**固定加锁的顺序**，可以使用Hash值的大小来确定加锁的先后
- 尽可能缩减加锁的范围，等到操作共享变量的时候才加锁
- 使用可释放的定时锁 (一段时间申请不到锁的权限了，直接释放掉)

但是这样的作法会导致饥饿的问题。



## 14. ReentrantLock (重点)

**`ReentrantLock`** 的特点 (synchronized不具备的)

- **可中断**
  - `lock.lockInterruptibly()` : 可以被其他线程打断的中断锁
- **可以设置超时时间**
  - `lock.tryLock(时间)` : 尝试获取锁对象, 如果超过了设置的时间, 还没有获取到锁, 此时就退出阻塞队列, 并释放掉自己拥有的锁
- **可以设置为公平锁**
  - (先到先得) 默认是非公平, true为公平 `new ReentrantLock(true)`
- **支持多个条件变量( 有多个waitset)**
  - (可避免虚假唤醒) - `lock.newCondition()`创建条件变量对象，通过条件变量对象调用 await/signal方法，等待/唤醒

与synchronized一样，都支持可重入



**基本语法**

```java
// 获取锁
reentrantLock.lock();
try {
    // 临界区
} finally {
    // 释放锁
    reentrantLock.unlock();
}
```



### 14.1 可重入

- 可重入锁是指同一个线程如果首次获得了这把锁，那么因为它是这把锁的拥有者，因此 有权利再次获取这把锁
- 如果是不可重入锁，那么第二次获得锁时，自己也会被锁挡住

```java
static ReentrantLock lock = new ReentrantLock();
public static void main(String[] args) {
    method1();
}
public static void method1() {
    lock.lock();
    try {
        log.debug("execute method1");
        method2();
    } finally {
        lock.unlock();
    }
}
public static void method2() {
    lock.lock();
    try {
        log.debug("execute method2");
        method3();
    } finally {
        lock.unlock();
    }
}
public static void method3() {
    lock.lock();
    try {
        log.debug("execute method3");
    } finally {
        lock.unlock();
    }
}

17:59:11.862 [main] c.TestReentrant - execute method1
17:59:11.865 [main] c.TestReentrant - execute method2
17:59:11.865 [main] c.TestReentrant - execute method3
```

### 14.2 可中断

> `synchronized` 和 `reentrantlock.lock()` 的锁，是不可被打断的；也就是说别的线程已经获得了锁，我的线程就需要一直等待下去，不能中断。
>
> 可被中断的锁，通过`lock.lockInterruptibly()`获取的锁对象，可以通过调用阻塞线程的interrupt()方法。
>
> 可中断的锁，在一定程度上可以被动的减少死锁的概率，之所以被动，是因为我们需要手动调用阻塞线程的interrupt方法；



测试使用`lock.lockInterruptibly()`可以从阻塞队列中打断

```java
/**
 * Description: ReentrantLock, 演示RenntrantLock中的可打断锁方法 lock.lockInterruptibly();
 */
@Slf4j(topic = "guizy.ReentrantTest")
public class ReentrantTest {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            log.debug("t1线程启动...");
            try {
                // lockInterruptibly()是一个可打断的锁, 如果有锁竞争在进入阻塞队列后,可以通过interrupt进行打断
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("等锁的过程中被打断"); //没有获得锁就被打断跑出的异常
                return;
            }
            try {
                log.debug("t1线程获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        // 主线程获得锁(此锁不可打断)
        lock.lock();
        log.debug("main线程获得了锁");
        // 启动t1线程
        t1.start();
        try {
            Sleeper.sleep(1);
            t1.interrupt();            //打断t1线程
            log.debug("执行打断");
        } finally {
            lock.unlock();
        }
    }
}

14:18:09.145 guizy.ReentrantTest [main] - main线程获得了锁
14:18:09.148 guizy.ReentrantTest [t1] - t1线程启动...
14:18:10.149 guizy.ReentrantTest [main] - 执行打断
14:18:10.149 guizy.ReentrantTest [t1] - 等锁的过程中被打断
java.lang.InterruptedException
  at java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireInterruptibly(AbstractQueuedSynchronizer.java:898)
  at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireInterruptibly(AbstractQueuedSynchronizer.java:1222)
  at java.util.concurrent.locks.ReentrantLock.lockInterruptibly(ReentrantLock.java:335)
  at com.guizy.reentrantlock.ReentrantTest.lambda$main$0(ReentrantTest.java:25)
  at java.lang.Thread.run(Thread.java:748)
```

`lock.lock()`不可以从阻塞队列中打断, 一直等待别的线程释放锁



### 14.3 锁超时

> 防止无限制等待, 减少死锁

- 使用 **`lock.tryLock()`** 方法会返回获取锁是否成功。如果成功则返回true，反之则返回false。
- 并且`tryLock方法`可以设置**指定等待时间**，参数为：**`tryLock(long timeout, TimeUnit unit)`** , 其中timeout为最长等待时间，TimeUnit为时间单位

> 获取锁的过程中, 如果超过等待时间, 或者被打断, 就直接从阻塞队列移除, 此时获取锁就失败了, 不会一直阻塞着 ! (可以用来实现死锁问题)

- **不设置等待时间, 立即失败**

```java
/**
 * Description: ReentrantLock, 演示RenntrantLock中的tryLock(), 获取锁立即失败
 */
@Slf4j(topic = "guizy.ReentrantTest")
public class ReentrantTest {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            // 此时肯定获取失败, 因为主线程已经获得了锁对象
            if (!lock.tryLock()) {
                log.debug("获取立刻失败，返回");
                return;
            }
            try {
                log.debug("获得到锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        log.debug("获得到锁");
        t1.start();
        // 主线程2s之后才释放锁
        Sleeper.sleep(2);
        log.debug("释放了锁");
        lock.unlock();
    }
}

14:52:19.726 guizy.WaitNotifyTest [main] - 获得到锁
14:52:19.728 guizy.WaitNotifyTest [t1] - 尝试获得锁
14:52:19.728 guizy.WaitNotifyTest [t1] - 获取立刻失败，返回
14:52:21.728 guizy.WaitNotifyTest [main] - 释放了锁
```

**设置等待时间，超过等待时间还没有获得锁，失败，从阻塞队列移除该线程**

```java
/**
 * Description: ReentrantLock, 演示RenntrantLock中的tryLock(long mills), 超过锁设置的等待时间,就从阻塞队列移除
 */
@Slf4j(topic = "guizy.ReentrantTest")
public class ReentrantTest {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("尝试获得锁");
            try {
                // 设置等待时间, 超过等待时间 / 被打断, 都会获取锁失败; 退出阻塞队列
                if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                    log.debug("获取锁超时，返回");
                    return;
                }
            } catch (InterruptedException e) {
                log.debug("被打断了, 获取锁失败, 返回");
                e.printStackTrace();
                return;
            }
            try {
                log.debug("获得到锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        log.debug("获得到锁");
        t1.start();
//        t1.interrupt();
        // 主线程2s之后才释放锁
        Sleeper.sleep(2);
        log.debug("main线程释放了锁");
        lock.unlock();
    }
}

// 超时的打印
14:55:56.647 guizy.WaitNotifyTest [main] - 获得到锁
14:55:56.651 guizy.WaitNotifyTest [t1] - 尝试获得锁
14:55:57.652 guizy.WaitNotifyTest [t1] - 获取锁超时，返回
14:55:58.652 guizy.WaitNotifyTest [main] - main线程释放了锁

// 中断的打印
14:56:41.258 guizy.WaitNotifyTest [main] - 获得到锁
14:56:41.260 guizy.WaitNotifyTest [main] - main线程释放了锁
14:56:41.261 guizy.WaitNotifyTest [t1] - 尝试获得锁
14:56:41.261 guizy.WaitNotifyTest [t1] - 被打断了, 获取锁失败, 返回
java.lang.InterruptedException
```

通过`lock.tryLock()`来解决哲学家就餐问题 (重点)

`lock.tryLock(时间)` : 尝试获取锁对象，如果超过了设置的时间，还没有获取到锁，此时就退出阻塞队列，并释放掉自己拥有的锁

```java
/**
 * Description: 使用了ReentrantLock锁, 该类中有一个tryLock()方法, 在指定时间内获取不到锁对象, 就从阻塞队列移除,不用一直等待。当获取了左手边的筷子之后, 尝试获取右手边的筷子, 如果该筷子被其他哲学家占用, 获取失败, 此时就先把自己左手边的筷子,给释放掉. 这样就避免了死锁问题
 *
 */
@Slf4j(topic = "guizy.PhilosopherEat")
public class PhilosopherEat {
    public static void main(String[] args) {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c5, c1).start();
    }
}

@Slf4j(topic = "guizy.Philosopher")
class Philosopher extends Thread {
    final Chopstick left;
    final Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        while (true) {
            // 获得了左手边筷子 (针对五个哲学家, 它们刚开始肯定都可获得左筷子)
            if (left.tryLock()) {
                try {
                  // 此时发现它的right筷子被占用了, 使用tryLock(), 
                  // 尝试获取失败, 此时它就会将自己左筷子也释放掉
                    // 临界区代码
                    if (right.tryLock()) { //尝试获取右手边筷子, 如果获取失败, 则会释放左边的筷子
                        try {
                            eat();
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
        }
    }

    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(0.5);
    }
}

// 继承ReentrantLock, 让筷子类称为锁
class Chopstick extends ReentrantLock {
    String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}

15:16:01.793 guizy.Philosopher [亚里士多德] - eating...
15:16:01.795 guizy.Philosopher [苏格拉底] - eating...
15:16:02.293 guizy.Philosopher [亚里士多德] - eating...
15:16:02.295 guizy.Philosopher [苏格拉底] - eating...
15:16:02.794 guizy.Philosopher [赫拉克利特] - eating...
15:16:02.796 guizy.Philosopher [苏格拉底] - eating...
15:16:03.294 guizy.Philosopher [赫拉克利特] - eating...
15:16:03.296 guizy.Philosopher [柏拉图] - eating...
15:16:03.795 guizy.Philosopher [赫拉克利特] - eating...
15:16:03.797 guizy.Philosopher [苏格拉底] - eating...
15:16:04.295 guizy.Philosopher [亚里士多德] - eating...
15:16:04.297 guizy.Philosopher [苏格拉底] - eating...
15:16:04.796 guizy.Philosopher [亚里士多德] - eating...
15:16:04.798 guizy.Philosopher [阿基米德] - eating...
15:16:05.296 guizy.Philosopher [柏拉图] - eating...
15:16:05.299 guizy.Philosopher [赫拉克利特] - eating...
```

### 14.4 公平锁

- ReentrantLock默认是非公平锁，可以指定为公平锁。

- 在线程获取锁失败，进入阻塞队列时，**先进入**的会在锁被释放后**先获得**锁。这样的获取方式就是**公平**的。一般不设置`ReentrantLock`为公平的, 会降低并发度

- `Synchronized`底层的`Monitor锁`就是不公平的，和谁先进入阻塞队列是没有关系的。

  ```java
  //默认是不公平锁，需要在创建时指定为公平锁
  ReentrantLock lock = new ReentrantLock(true);
  ```

什么是公平锁? 什么是非公平锁?

公平锁 (new ReentrantLock(true))

- 公平锁, 可以把竞争的线程放在一个先进先出的阻塞队列上
- 只要持有锁的线程执行完了, 唤醒阻塞队列中的下一个线程获取锁即可；此时先进入阻塞队列的线程先获取到锁

非公平锁 (synchronized, new ReentrantLock())

- 非公平锁，当阻塞队列中已经有等待的线程A了，此时后到的线程B，先去尝试看能否获得到锁对象。如果获取成功，此时就不需要进入阻塞队列了。这样以来后来的线程B就先获得锁了

> 所以公平和非公平的区别 : **线程执行同步代码块时, 是否回去尝试获取锁**, 如果会尝试获取锁，那就是非公平的，如果不会尝试获取锁，直接进入阻塞队列，再等待被唤醒，那就是公平的
>
> - 如果不进如队列呢? 线程一直尝试获取锁不就行了?
>   - 一直尝试获取锁, 在synchronized轻量级锁升级为重量级锁时, 做的一个优化, 叫做**`自旋锁`**， 一般很消耗资源，cpu一直空转，最后获取锁也失败，所以不推荐使用。在jdk6对于自旋锁有一个机制，在重试获得锁指定次数就失败等等

```java
ReentrantLock lock = new ReentrantLock(true);
lock.lock();
for (int i = 0; i < 500; i++) {
    new Thread(() -> {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " running...");
        } finally {
            lock.unlock();
        }
    }, "t" + i).start();
}
// 1s 之后去争抢锁
Thread.sleep(1000);
new Thread(() -> {
    System.out.println(Thread.currentThread().getName() + " start...");
    lock.lock();
    try {
        System.out.println(Thread.currentThread().getName() + " running...");
    } finally {
        lock.unlock();
    }
}, "强行插入").start();
lock.unlock();

t465 running...
t464 running...
t477 running...
t442 running...
t468 running...
t493 running...
t482 running...
t485 running...
t481 running...
强行插入 running...
```



### 14.5 条件变量 

**`Synchronized`** 中也有条件变量，就是Monitor监视器中的 waitSet等待集合，当条件不满足时进入 waitSet 等待

**`ReentrantLock`** 的条件变量比 synchronized 强大之处在于，它是 **支持多个条件变量。**

- 这就好比synchronized 是那些不满足条件的线程都在**一间**休息室等通知; **`(此时会造成虚假唤醒)`**
- 而 ReentrantLock 支持**多间**休息室，有专门等烟的休息室、专门等早餐的休息室、唤醒时也是按休息室来唤醒; **`(可以避免虚假唤醒)`**

使用要点：

- await 前需要获得锁
- await 执行后，会释放锁，进入 `conditionObject` (条件变量)中等待
- await 的线程被唤醒（或打断、或超时）取重新竞争 lock 锁
- 竞争 lock 锁成功后，从 await 后继续执行
- signal 方法用来唤醒条件变量(等待室)汇总的某一个等待的线程
- signalAll方法，唤醒条件变量(休息室)中的所有线程

```java
/**
 * Description: ReentrantLock可以设置多个条件变量(多个休息室), 相对于synchronized底层monitor锁中waitSet
 */
@Slf4j(topic = "guizy.ConditionVariable")
public class ConditionVariable {
    private static boolean hasCigarette = false;
    private static boolean hasTakeout = false;
    private static final ReentrantLock lock = new ReentrantLock();
    // 等待烟的休息室
    static Condition waitCigaretteSet = lock.newCondition();
    // 等外卖的休息室
    static Condition waitTakeoutSet = lock.newCondition();

    public static void main(String[] args) {

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        // 此时小南进入到 等烟的休息室
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("烟来咯, 可以开始干活了");
            } finally {
                lock.unlock();
            }
        }, "小南").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        // 此时小女进入到 等外卖的休息室
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖来咯, 可以开始干活了");
            } finally {
                lock.unlock();
            }
        }, "小女").start();

        Sleeper.sleep(1);
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("送外卖的来咯~");
                hasTakeout = true;
                // 唤醒等外卖的小女线程
                waitTakeoutSet.signal();
            } finally {
                lock.unlock();
            }
        }, "送外卖的").start();

        Sleeper.sleep(1);
        new Thread(() -> {
            lock.lock();
            try {
                log.debug("送烟的来咯~");
                hasCigarette = true;
                // 唤醒等烟的小南线程
                waitCigaretteSet.signal();
            } finally {
                lock.unlock();
            }
        }, "送烟的").start();
    }
}

15:08:58.231 guizy.WaitNotifyTest [小南] - 有烟没？[false]
15:08:58.234 guizy.WaitNotifyTest [小南] - 没烟，先歇会！
15:08:58.235 guizy.WaitNotifyTest [小女] - 外卖送到没？[false]
15:08:58.235 guizy.WaitNotifyTest [小女] - 没外卖，先歇会！
15:08:59.232 guizy.WaitNotifyTest [送外卖的] - 送外卖的来咯~
15:08:59.233 guizy.WaitNotifyTest [小女] - 外卖来咯, 可以开始干活了
15:09:00.233 guizy.WaitNotifyTest [送烟的] - 送烟的来咯~
15:09:00.234 guizy.WaitNotifyTest [小南] - 烟来咯, 可以开始干活了
```

## 15. 同步模式之顺序控制 (案例)

- 假如有两个线程，线程A打印1，线程B打印2。
- 要求: **程序先打印2，再打印1**

### 15.1 固定运行顺序

#### Wait/Notify版

```java
/**
 * Description: 使用wait/notify来实现顺序打印 2, 1
 */
@Slf4j(topic = "guizy.SyncPrintWaitTest")
public class SyncPrintWaitTest {

    public static final Object lock = new Object();
    // t2线程释放执行过
    public static boolean t2Runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!t2Runned) {
                    try {
                      // 进入等待(waitset), 会释放锁
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("2");
                t2Runned = true;
                lock.notify();
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
```

#### await/signal版

```java
/**
 * Description: 使用ReentrantLock的await/sinal 来实现顺序打印 2, 1
 */
@Slf4j(topic = "guizy.SyncPrintWaitTest")
public class SyncPrintWaitTest {

    public static final ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();
    // t2线程释放执行过
    public static boolean t2Runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                // 临界区
                while (!t2Runned) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("1");
            } finally {
                lock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                log.debug("2");
                t2Runned = true;
                condition.signal();
            } finally {
                lock.unlock();
            }
        }, "t2");

        t1.start();
        t2.start();
    }
}
```

#### park/unpark版

```java
/**
 * Description: 使用LockSupport中的park,unpark来实现, 顺序打印 2, 1
 */
@Slf4j(topic = "guizy.SyncPrintWaitTest")
public class SyncPrintWaitTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("1");
        }, "t1");
        t1.start();

        new Thread(() -> {
            log.debug("2");
            LockSupport.unpark(t1);
        }, "t2").start();
    }
}

16:10:28.592 guizy.SyncPrintWaitTest [t2] - 2
16:10:28.595 guizy.SyncPrintWaitTest [t1] - 1
```

### 15.2 交替输出

需求：线程1 输出 a 5次，线程2 输出 b 5次，线程3 输出 c 5次。现在要求输出 abcabcabcabcabcabc

#### wait/notify版

```java
/**
 * Description: 使用wait/notify来实现三个线程交替打印abcabcabcabcabc
 */
@Slf4j(topic = "guizy.TestWaitNotify")
public class TestWaitNotify {
    public static void main(String[] args) {
        WaitNotify waitNotify = new WaitNotify(1, 5);

        new Thread(() -> {
            waitNotify.print("a", 1, 2);

        }, "a线程").start();

        new Thread(() -> {
            waitNotify.print("b", 2, 3);

        }, "b线程").start();

        new Thread(() -> {
            waitNotify.print("c", 3, 1);

        }, "c线程").start();
    }
}

@Slf4j(topic = "guizy.WaitNotify")
@Data
@AllArgsConstructor
class WaitNotify {

    private int flag;
    
    // 循环次数
    private int loopNumber;

    /*
        输出内容    等待标记    下一个标记
        a           1          2
        b           2          3
        c           3          1
     */
    public void print(String str, int waitFlag, int nextFlag) {
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this) {
                while (waitFlag != this.flag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                this.flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}
```

#### await/signal版

```java
/**
 * Description: 使用await/signal来实现三个线程交替打印abcabcabcabcabc
 */
@Slf4j(topic = "guizy.TestWaitNotify")
public class TestAwaitSignal {
    public static void main(String[] args) throws InterruptedException {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a_condition = awaitSignal.newCondition();
        Condition b_condition = awaitSignal.newCondition();
        Condition c_condition = awaitSignal.newCondition();

        new Thread(() -> {
            awaitSignal.print("a", a_condition, b_condition);
        }, "a").start();

        new Thread(() -> {
            awaitSignal.print("b", b_condition, c_condition);
        }, "b").start();

        new Thread(() -> {
            awaitSignal.print("c", c_condition, a_condition);
        }, "c").start();

        Thread.sleep(1000);
        System.out.println("==========开始=========");
        awaitSignal.lock();
        try {
            a_condition.signal();  //首先唤醒a线程
        } finally {
            awaitSignal.unlock();
        }
    }
}

class AwaitSignal extends ReentrantLock {
    private final int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String str, Condition condition, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            lock();
            try {
                try {
                    condition.await();
                    //System.out.print("i:==="+i);
                    System.out.print(str);
                    next.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                unlock();
            }
        }
    }
}
```

#### park/unpark版

```java
/**
 * Description: 使用park/unpark来实现三个线程交替打印abcabcabcabcabc
 */
@Slf4j(topic = "guizy.TestWaitNotify")
public class TestParkUnpark {
    static Thread a;
    static Thread b;
    static Thread c;

    public static void main(String[] args) {
        ParkUnpark parkUnpark = new ParkUnpark(5);

        a = new Thread(() -> {
            parkUnpark.print("a", b);
        }, "a");

        b = new Thread(() -> {
            parkUnpark.print("b", c);
        }, "b");

        c = new Thread(() -> {
            parkUnpark.print("c", a);
        }, "c");

        a.start();
        b.start();
        c.start();

        LockSupport.unpark(a);

    }
}

class ParkUnpark {
    private final int loopNumber;

    public ParkUnpark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String str, Thread nextThread) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(nextThread);
        }
    }
}
```

## 16. 本章小结

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201223172500153.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20201223172527523.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)