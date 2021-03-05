## 1. 线程与进程

### 1.1 进程与进程

> 进程: 资源分配的最小单位
>
> - 进程是线程的容器, 一个进程中包含多个线程, 真正执行任务的是线程

> 线程: 资源调度的最小单位

#### 进程

- `程序`由`指令`和`数据`组成，但是这些 **指令要运行，数据要读写**，就必须将指令加载到cpu，数据加载至内存。在指令运行过程中还需要用到磁盘，网络等设备，`进程就是用来加载指令,管理内存,管理IO的`
- 当一个指令被运行，从磁盘加载这个程序的代码到内存，这时候就开启了一个进程，进程是系统进行资源分配和调度的基本单位。
- `进程`就可以视为`程序`的一个`实例`，大部分程序都可以运行多个实例进程（例如记事本，浏览器等），部分只可以运行一个实例进程（例如360安全卫士）

#### 线程

- 一个进程之内可以分为`多个线程`。
- `一个线程`就是`一个指令流`，将指令流中的一条条指令以一定的顺序交给 CPU 执行，所以真正占用CPU运行的是线程
- Java 中，**线程作为资源的最小调度单位，进程作为资源分配的最小单位。** 在 windows 中进程是不活动的，只是作为线程的容器。

#### 二者对比

- **进程**基本上`相互独立的`，而`线程存在于进程内，是进程的一个子集`
- 进程拥有共享的资源，如内存空间等，供其内部的线程共享; 进程间通信较为复杂
  同一台计算机的进程通信称为 IPC（Inter-process communication）
- `不同计算机之间的进程通信，需要通过网络，并遵守共同的协议，例如 HTTP`
- **线程通信相对简单，因为它们共享进程内的内存，一个例子是多个线程可以访问同一个共享变量**
- 线程更`轻量`，线程上下文切换成本一般上要比进程上下文切换低

### 1.2 并行与并发

> **并发:** 在单核CPU下, 一定是`并发执行`的, 也就是在同一个时间段内一起执行. 实际还是串行执行, CPU的时间片切换非常快, 给人一种同时运行的感觉。

> **并行:** 在多核CPU下, 能真正意义上实现`并行执行`, 在同一个时刻, 多个线程同时执行; 比如说2核cpu, 同时执行4个线程. 理论上同时可以有2个线程是并行执行的. 此时还是存在`并发`, 因为2个cpu也会同时切换不同的线程执行任务罢了

#### 并发 (concurrent)

- `微观串行, 宏观并行`
- 在`单核 cpu`下，`线程`实际还是`串行执行`的。操作系统中有一个组件叫做**任务调度器**，将 cpu 的时间片（windows下时间片最小约为 15 毫秒）分给不同的程序使用，只是由于`cpu 在线程间（时间片很短）的切换非常快`，给人的 **感觉是同时运行**的 。一般会将这种`线程轮流使用 CPU`的做法称为`并发（concurrent）`
- 将`线程轮流使用cput`称为`并发(concurrent)`
  ![1583408729416](https://img-blog.csdnimg.cn/img_convert/466a3192acf5930dba4f58d28a99936a.png)

#### 并行

- `多核 cpu`下，每个核（core） 都可以调度运行线程，这时候线程可以是`并行`的，不同的线程同时使用不同的cpu在执行。
  ![1583408812725](https://img-blog.csdnimg.cn/img_convert/0c812484e0998d533c6212d80d059ff0.png)

#### 二者对比

- 引用 Rob Pike 的一段描述：
  - `并发（concurrent）`: 是同一时间应对（dealing with）多件事情的能力
  - `并行（parallel）`: 是同一时间动手做（doing）多件事情的能力

例子

- 家庭主妇做饭、打扫卫生、给孩子喂奶，她**一个人轮流交替做这多件事**，这时就是`并发`
- 家庭主妇雇了个保姆，她们一起这些事，这时`既有并发，也有并行`（这时会产生竞争，例如锅只有一口，一个人用锅时，另一个人就得等待）
- 雇了3个保姆，一个专做饭、一个专打扫卫生、一个专喂奶，**互不干扰**，这时是 `并行`

### 1.3 应用

#### 应用1：异步调用

同步与异步：以`调用方`的角度讲

- 如果`需要等待结果返回才能继续运行`的话就是`同步`
- 如果`不需要等待`就是`异步`

设计：

- 多线程可以让方法执行变为`异步`的（即不要巴巴干等着）比如说读取磁盘文件时，假设读取操作花费了 5 秒钟，如果没有线程调度机制，这5秒cpu什么都做不了，其它代码都得暂停

结论：

- 比如在项目中，视频文件需要转换格式等操作比较`费时`，这时`开一个新线程处理视频转换`，**避免阻塞**主线程
- tomcat 的异步 servlet 也是类似的目的，`让用户线程处理耗时较长的操作，避免阻塞 tomcat 的工作线程`
- UI 程序中，开线程进行其他操作，避免阻塞 UI 线程



#### 应用2：提高效率

充分利用多核 cpu 的优势，提高运行效率。想象下面的场景，执行 3 个计算，最后将计算结果汇总。

```
计算 1 花费 10 ms
计算 2 花费 11 ms
计算 3 花费 9 ms
汇总需要 1 ms
```

- 如果是串行执行，那么总共花费的时间是 10 + 11 + 9 + 1 = 31ms
- 但如果是四核 cpu，各个核心分别使用线程 1 执行计算 1，线程 2 执行计算 2，线程 3 执行计算 3，那么 3 个
  线程是并行的，花费时间只取决于最长的那个线程运行的时间，即 11ms 最后加上汇总时间只会花费 12ms

结论：

- 单核 cpu 下，多线程不能实际提高程序运行效率，只是为了能够在不同的任务之间切换，不同线程轮流使用 cpu ，不至于一个线程总占用 cpu，别的线程没法干活
- 多核 cpu 可以并行跑多个线程，但能否提高程序运行效率还是要分情况的
  - 有些任务，经过精心设计，将任务拆分，并行执行，当然可以提高程序的运行效率。但不是所有计算任 务都能拆分（参考后文的【阿姆达尔定律】）
  - 也不是所有任务都需要拆分，任务的目的如果不同，谈拆分和效率没啥意义
- IO 操作不占用 cpu，只是我们一般拷贝文件使用的是【阻塞 IO】，这时相当于线程虽然不用 cpu，但需要一 直等待 IO 结束，没能充分利用线程。所以才有后面的【非阻塞 IO】和【异步 IO】优化

## 2. Java线程

### 2.1 创建和运行线程（重要）

#### 方法一：直接使用Thread

```java
// 构造方法的参数是给线程指定名字，推荐
Thread t1 = new Thread("t1") {//使用匿名内部类
    @Override
    // run 方法内实现了要执行的任务
    public void run() {
        log.debug("hello");
    }
};

//启动线程
t1.start();
```



#### 方法二：使用Runnable配合Thread (推荐)

把【线程】和【任务】（要执行的代码）分开

- `Thread` ：代表线程
- `Runnable` ：可运行的任务（线程要执行的代码）

```java
// 创建任务对象
Runnable task2 = new Runnable() {
    @Override
    public void run() {
        log.debug("hello");
    }
};

// 参数1 是任务对象; 参数2 是线程名字，推荐
Thread t2 = new Thread(task2, "t2");
t2.start();
```



- **当一个接口带有`@FunctionalInterface注解`时，表示是一个函数式接口，因此Runable接口是可以使用lambda来简化操作的**
- 所以方法二中的代码可以被简化为

```java
// 创建任务对象
Runnable task2 = () -> log.debug("hello");

// 参数1 是任务对象; 参数2 是线程名字，推荐
Thread t2 = new Thread(task2, "t2");
t2.start();
```



##### * 原理之 Thread 与 Runnable 的关系

- 分析 `Thread` 的源码，理清它与 `Runnable` 的关系

**小结**

- 方法1 是把线程和任务合并在了一起，方法2 是把线程和任务分开了
- **用 Runnable 更容易与线程池等高级 API 配合 **
- **用 Runnable 让任务类脱离了 Thread 继承体系，更灵活**

#### 方法三：使用FutureTask与Thread结合

**使用FutureTask可以接收 Callable 类型的参数，用来处理有返回结果的情况（Runnable的run方法没有返回值）**

```java
// 创建任务对象
FutureTask<Integer> task3 = new FutureTask<>(() -> {
    log.debug("hello");
    return 100;
});

// 参数1 是任务对象; 参数2 是线程名字，推荐
Thread t3 = new Thread(task3, "t3");
t3.start();

// 主线程阻塞，同步等待 task 执行完毕的结果
Integer result = task3.get();
log.debug("结果是:{}", result);
```



#### 方法四：使用线程池来创建线程

```java
/**
 * 创建线程的方式四：使用线程池
 *
 * 好处：
 * 1.提高响应速度（减少了创建新线程的时间）
 * 2.降低资源消耗（重复利用线程池中线程，不需要每次都创建）
 * 3.便于线程管理
 *      corePoolSize：核心池的大小
 *      maximumPoolSize：最大线程数
 *      keepAliveTime：线程没有任务时最多保持多长时间后会终止
 *
 *
 * 面试题：创建多线程有几种方式？四种！
 */

class NumberThread implements Runnable{

    @Override
    public void run() {
        for(int i = 0;i <= 100;i++){
            if(i % 2 == 0){
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

class NumberThread1 implements Runnable{

    @Override
    public void run() {
        for(int i = 0;i <= 100;i++){
            if(i % 2 != 0){
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

public class ThreadPool {

    public static void main(String[] args) {
        //1. 提供指定线程数量的线程池
        ExecutorService service = Executors.newFixedThreadPool(10);
        ThreadPoolExecutor service1 = (ThreadPoolExecutor) service;
        //设置线程池的属性
//        System.out.println(service.getClass());
//        service1.setCorePoolSize(15);
//        service1.setKeepAliveTime();


        //2.执行指定的线程的操作。需要提供实现Runnable接口或Callable接口实现类的对象
        service.execute(new NumberThread());//适合使用于Runnable
        service.execute(new NumberThread1());//适合使用于Runnable

//        service.submit(Callable callable);//适合使用于Callable
        //3.关闭连接池
        service.shutdown();
    }
}
```

#### 总结

- 使用 **继承方式的好处是方便传参**，你可以在子类里面添加成员变量，通过set方法设置参数或者通过构造函数进行传递，而如果`使用Runnable方式`，则只能使用主线程里面被声明为final的变量。**不好的地方是Java不支持多继承**，如果继承了Thread类，那么子类不能再继承其他类，而Runable则没有这个限制。**`前两种方式都没办法拿到任务的返回结果，但是Futuretask方式可以`**
- 开发中一般使用`线程池`的方式

### 2.2 查看进程线程的方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201218110254483.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

### 2.3 线程运行原理 (重点)

#### 虚拟机栈与栈帧

- `虚拟机栈`描述的是`Java方法执行的内存模型`：每个方法被执行的时候都会同时创建一个`栈帧(stack frame)`用于存储`局部变量表、操作数栈、动态链接、方法出口`等信息，是属于**线程私有的**。当Java中使用多线程时，每个线程都会维护它自己的栈帧！每个线程只能有一个活动栈帧(在栈顶)，对应着当前正在执行的那个方法

例子：

```java
public class TestFrames {
    public static void main(String[] args) {
        method1(10);
    }
    public static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.println(m);
    }
    public static void method2() {
		Object n = new Object();
        return n;
    }
}
```

![image-20210301110521333](C:\Users\jchen\AppData\Roaming\Typora\typora-user-images\image-20210301110521333.png)

该图展示的是单个线程的运行时数据区的状态，当有多线程时，由于虚拟机栈是线程私有的，因此每个线程都会维护自己的虚拟机栈，每个线程调用一个方法时，就会创建一个对应于该方法的栈帧。



#### 线程上下文切换（Thread Context Switch)

> 因为以下一些原因导致 cpu 不再执行当前的线程，转而执行另一个线程

- **线程的 cpu 时间片用完**(每个线程轮流执行，看前面并行的概念)
- **垃圾回收**
- **有更高优先级的线程需要运行**
- 线程自己调用了 `sleep`、`yield`、`wait`、`join`、`park`、`synchronized`、`lock` 等方法

当`Thread Context Switch`发生时，需要由操作系统`保存当前线程的状态`，并`恢复另一个线程的状态`，Java 中对应的概念就是**程序计数器（Program Counter Register）**，它的作用是`记住下一条 jvm 指令的执行地址，是线程私有的`

- `线程的状态`包括程序计数器、虚拟机栈中每个栈帧的信息，如局部变量、操作数栈、返回地址等
- Context Switch 频繁发生会`影响性能`



### 2.4 Thread的常见方法

![7f246.png)](https://img-blog.csdnimg.cn/20201218104837363.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20201218104857415.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

![image-20210301100805722](https://gitee.com/jchenTech/images/raw/master/img/20210301100815.png)

### 2.5 调用start 与 run方法的区别

#### 调用start()方法

```java
public static void main(String[] args) {
    Thread thread = new Thread(){
      @Override
      public void run(){
          log.debug("我是一个新建的线程正在运行中");
          FileReader.read(fileName);
      }
    };
    thread.setName("新建线程");
    thread.start();
    log.debug("主线程");
}
```

- 输出：程序在`t1 线程运行`， `run()`方法里面内容的调用是异步的代码

```properties
11:59:40.711 [main] DEBUG com.concurrent.test.Test4 - 主线程
11:59:40.711 [新建线程] DEBUG com.concurrent.test.Test4 - 我是一个新建的线程正在运行中
11:59:40.732 [新建线程] DEBUG com.concurrent.test.FileReader - read [test] start ...
11:59:40.735 [新建线程] DEBUG com.concurrent.test.FileReader - read [test] end ... cost: 3 ms
```

#### 调用run()方法

- 将上面代码的`thread.start();`改为 `thread.run();`输出结果如下：程序仍在 main 线程运行， `run()`方法里面内容的调用还是同步的

```properties
12:03:46.711 [main] DEBUG com.concurrent.test.Test4 - 我是一个新建的线程正在运行中
12:03:46.727 [main] DEBUG com.concurrent.test.FileReader - read [test] start ...
12:03:46.729 [main] DEBUG com.concurrent.test.FileReader - read [test] end ... cost: 2 ms
12:03:46.730 [main] DEBUG com.concurrent.test.Test4 - 主线程
```

#### 小结

- 直接调用 `run()` 是在主线程中执行了 `run()`，**没有启动新的线程**
- 使用 `start()` 是**启动新的线程**，通过新的线程间接执行 `run()`方法中的代码

### 2.6 sleep 与 yield

#### sleep方法

1. 调用 `sleep()` 会让当前线程从 `Running(运行状态)` 进入 `Timed Waiting 状态（阻塞）`

2. 其它线程可以使用`interrupt 方法打断正在睡眠的线程`，那么被打断的线程这时就会抛出 `InterruptedException异常`**【注意：这里打断的是正在休眠的线程，而不是其它状态的线程】**

3. 睡眠结束后的线程未必会立刻得到执行 (需要分配到cpu时间片)

4. 建议用 `TimeUnit` 的 `sleep()` 代替 Thread 的 `sleep()`来获得更好的可读性

   ```java
   //可以控制睡眠时间单位，可读性更好
   TimeUnit.SECONDS.sleep(1)
   //sleep的时间单位默认为毫秒ms
   Thread.sleep(1000)
   ```

   

#### yield方法

1. 调用 yield 会让当前线程从`Running` 进入 `Runnable 就绪状态`，然后调度执行其它线程
2. **具体的实现依赖于操作系统的任务调度器**(就是可能没有其它的线程正在执行，虽然调用了yield方法，但是也没有用)

#### 两者对比

- yield使cpu调用其它线程，`但是cpu可能会再分配时间片给该线程`；`而sleep需要等过了休眠时间之后才有可能被分配cpu时间片`

#### 线程优先级

- 线程`优先级`会`提示（hint）调度器优先调度该线程`，但它仅仅是一个提示，调度器可以忽略它, 如果 cpu 比较忙，那么优先级高的线程会获得更多的时间片，但 cpu 闲时，优先级几乎没作用

```java
thread1.setPriority(Thread.MAX_PRIORITY); //设置为优先级最高。最大为10，最小为1，默认为5
```

###  2.7 join方法详解

#### 为什么要使用join 

- 在`主线程`中调用`t1.join`，则`主线程`会`等待t1线程执行完之后`再`继续执行`

```java
private static void test1() throws InterruptedException {
    log.debug("开始");
    Thread t1 = new Thread(() -> {
        log.debug("开始");
        sleep(1);
        log.debug("结束");
        r = 10;
    },"t1");
    t1.start();
    // t1.join(); 
    // 这里如果不加t1.join(), 此时主线程不会等待t1线程给r赋值, 主线程直接就输出r=0结束了
    // 如果加上t1.join(), 此时主线程会等待到t1线程执行完才会继续执行.(同步), 此时r=10;
    log.debug("结果为:{}", r);
    log.debug("结束");
}
```

#### 应用之同步

以调用方角度来讲，如果：

- 需要等待结果返回，才能继续运行就是同步
- 不需要等待结果返回，就能继续运行就是异步

> 下图, 因为开辟了t1线程. 此时程序中有两个线程; main线程和t1线程; 此时在main线程中调用`t1.join`, 所以main线程只能`阻塞`等待t1线程执行完. `t1线程在1s后将r=10`, t1线程执行完, 此时main线程才会接着执行
> ![1583483843354](https://img-blog.csdnimg.cn/img_convert/52ed0d839807dfa6768787cfe659fb9e.png)

问，下面代码 cost 大约多少秒？

```java
static int r1 = 0;
static int r2 = 0;
public static void main(String[] args) throws InterruptedException {
    test2();
}
private static void test2() throws InterruptedException {
    Thread t1 = new Thread(() -> {
        sleep(1);
        r1 = 10;
    });
    Thread t2 = new Thread(() -> {
        sleep(2);
        r2 = 20;
    });
    long start = System.currentTimeMillis();
    t1.start();
    t2.start();
    
    t1.join();
    t2.join();
    long end = System.currentTimeMillis();
    log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
}
```

分析如下

- 第一个 `join`：等待 t1 时，主线程停止但是 t2 并没有停止，而在运行，因此 t2 sleep了1秒
- 第二个 `join`：1s 后，执行到此，t2 也已经运行了 1s，因此也只需再等待 1s

如果颠倒两个 join 呢？最终也是只进行了2s。输出：

```properties
20:45:43.239 [main] c.TestJoin - r1: 10 r2: 20 cost: 2005
```

![image-20210301134858181](https://gitee.com/jchenTech/images/raw/master/img/20210301134932.png)

#### 有时效的 join

等够时间

```java
static int r1 = 0;
static int r2 = 0;
public static void main(String[] args) throws InterruptedException {
    test3();
}
public static void test3() throws InterruptedException {
    Thread t1 = new Thread(() -> {
        sleep(1);
        r1 = 10;
    });
    long start = System.currentTimeMillis();
    t1.start();
    // 线程执行结束会导致 join 结束
    t1.join(1500);
    long end = System.currentTimeMillis();
    log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
}
```

输出：

```properties
20:48:01.320 [main] c.TestJoin - r1: 10 r2: 0 cost: 1010
```



没等够时间：

```java
static int r1 = 0;
static int r2 = 0;
public static void main(String[] args) throws InterruptedException {
    test3();
}
public static void test3() throws InterruptedException {
    Thread t1 = new Thread(() -> {
        sleep(2);
        r1 = 10;
    });
    long start = System.currentTimeMillis();
    t1.start();
    // 超过时间之后会导致 join 结束，此时线程还处于 sleep 状态
    t1.join(1500);
    long end = System.currentTimeMillis();
    log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
}
```

输出：

```properties
20:52:15.623 [main] c.TestJoin - r1: 0 r2: 0 cost: 1502
```



### 2.8 interrupt 方法详解

#### 打断 sleep，wait，join

该方法用于打断 `sleep，wait，join`的线程, 在阻塞期间cpu不会分配给时间片

- 先了解一些`interrupt()方法`的相关知识：[博客地址](https://www.cnblogs.com/noteless/p/10372826.html#0)
- 如果`一个线程在在运行中被打断`，**打断标记会被置为true**
- 如果是打断`因sleep wait join方法而被阻塞的线程`，会将**打断标记置为false**

`sleep，wait，join`的线程，这几个方法都会让线程进入`阻塞状态`，以 sleep 为例

```java
public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println("sleep...");
            try {
                Thread.sleep(5000); // wait, join
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        Thread.sleep(1000);
        System.out.println("iterrupt..");
        t1.interrupt();
        System.out.println(t1.isInterrupted()); // 如果是打断sleep,wait,join的线程, 即使打断了, 标记也为false
    }
}
```

输出：

```properties
sleep...
iterrupt..
打断标记为:false
java.lang.InterruptedException: sleep interrupted
at java.lang.Thread.sleep(Native Method)
at com.guizy.ThreadPrintDemo.lambda$main$0(ThreadPrintDemo.java:14)
at java.lang.Thread.run(Thread.java:748)

Process finished with exit code 0
```



#### 打断正常运行的线程

- 打断正常运行的线程, 线程并不会暂停，只是调用方法`Thread.currentThread().isInterrupted();`的返回值为true，可以判断`Thread.currentThread().isInterrupted();`的值来手动停止线程

```java
public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        while(true) {
            boolean interrupted = Thread.currentThread().isInterrupted();
            if(interrupted) {
                System.out.println("被打断了, 退出循环");
                break;
            }
        }
    }, "t1");
    t1.start();
    Thread.sleep(1000);
    System.out.println("interrupt");
    t1.interrupt();
    System.out.println("打断标记为: "+t1.isInterrupted());
}
```

```properties
interrupt
被打断了, 退出循环
打断标记为: true

Process finished with exit code 0
```



#### 终止模式之两阶段终止模式

> 当我们在执行线程一时，想要终止线程二，这是就需要使用interrupt方法来优雅的停止线程二。

- Two Phase Termination，就是考虑在一个线程T1中如何优雅地终止另一个线程T2？这里的优雅指的是给T2线程一个处理其他事情的机会（如释放锁）。

错误思路：

- 使用线程对象的 `stop()` 方法停止线程
  - **stop 方法会真正杀死线程，如果这时线程锁住了共享资源，那么当它被杀死后就再也没有机会释放锁，其他线程将永远无法获取锁**
- 使用 `System.exit(int)` 方法停止线程
  - 目的仅是停止一个线程，但是这种做法会让整个程序都停止



- 如下所示：那么线程的`isInterrupted()`方法可以`取得线程的打断标记`
  - 如果线程在睡眠`sleep`期间被打断，**打断标记是不会变的**，为`false`，但是`sleep`期间被打断会抛出异常，我们据此**手动设置**打断标记为`true`；
  - 如果是在`程序正常运行期间被打断`的，那么打断标记就被自动设置为`true`。处理好这两种情况那我们就可以放心地来料理后事啦！

下图①就是正常运行打断, ②是在睡眠中被打断
![1583496991915](https://img-blog.csdnimg.cn/img_convert/889b421e38b1d734bb96cbf20feb4664.png)

代码实现如下：

```java
public class Test7 {
	public static void main(String[] args) throws InterruptedException {
		Monitor monitor = new Monitor();
		monitor.start();
		Thread.sleep(3500);
		monitor.stop();
	}
}

class Monitor {

	Thread monitor;

	/**
	 * 启动监控器线程
	 */
	public void start() {
		//设置监控器线程，用于监控线程状态
		monitor = new Thread() {
			@Override
			public void run() {
				//开始不停的监控
				while (true) {
                    //判断当前线程是否被打断了
					if(Thread.currentThread().isInterrupted()) {
						System.out.println("处理后续任务");
                        //终止线程执行
						break;
					}
					System.out.println("监控器运行中...");
					try {
						//线程休眠
						Thread.sleep(1000);
                        log.debug("执行监控记录");
					} catch (InterruptedException e) {
						e.printStackTrace();
						//如果是在休眠的时候被打断，不会将打断标记设置为true，这时要重新设置打断标记
						Thread.currentThread().interrupt();
					}
				}
			}
		};
		monitor.start();
	}

	/**
	 * 	用于停止监控器线程
	 */
	public void stop() {
		//打断线程
		monitor.interrupt();
	}
}
```



#### 打断 park 线程

打断 park 线程, 不会清空打断状态

```java
private static void test3() throws InterruptedException {
    Thread t1 = new Thread(() -> {
        log.debug("park...");
        LockSupport.park();//让当前线程停下来
        log.debug("unpark...");
        log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
    }, "t1");
    t1.start();
    sleep(0.5);
    t1.interrupt();
}
```

输出：

```properties
21:11:52.795 [t1] c.TestInterrupt - park...
21:11:53.295 [t1] c.TestInterrupt - unpark...
21:11:53.295 [t1] c.TestInterrupt - 打断状态：true
```



如果打断标记已经是 true, 则 park 会失效

```java
private static void test4() {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
            log.debug("park...");
            LockSupport.park();
            log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
        }
    });
    t1.start();
    sleep(1);
    t1.interrupt();
}
```

输出

```properties
21:13:48.783 [Thread-0] c.TestInterrupt - park...
21:13:49.809 [Thread-0] c.TestInterrupt - 打断状态：true
21:13:49.812 [Thread-0] c.TestInterrupt - park...
21:13:49.813 [Thread-0] c.TestInterrupt - 打断状态：true
21:13:49.813 [Thread-0] c.TestInterrupt - park...
21:13:49.813 [Thread-0] c.TestInterrupt - 打断状态：true
21:13:49.813 [Thread-0] c.TestInterrupt - park...
21:13:49.813 [Thread-0] c.TestInterrupt - 打断状态：true
21:13:49.813 [Thread-0] c.TestInterrupt - park...
21:13:49.813 [Thread-0] c.TestInterrupt - 打断状态：true
```

> 提示
> 可以使用 `Thread.interrupted()` 清除打断状态



### 2.9 sleep，yiled，wait，join 对比及其他不推荐方法

> 补充：
>
> - sleep，join，yield，interrupted是Thread类中的方法
> - wait/notify是object中的方法
> - sleep 不释放锁、释放cpu
> - join 释放锁、抢占cpu
> - yiled 不释放锁、释放cpu
> - wait 释放锁、释放cpu

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201218122903649.png)



### 2.10 守护线程

- 默认情况下，当`Java进程`中有`多个线程`在执行时，**只有当所有线程都执行完毕后，Java进程才会结束**。有一种特殊的线程叫做`守护线程`，**只要其他非守护线程结束了，即使守护线程没有执行完毕，也会强制结束**。

例：

```java
log.debug("开始运行...");
Thread t1 = new Thread(() -> {
    log.debug("开始运行...");
    sleep(2);
    log.debug("运行结束...");
}, "daemon");

// 设置该线程为守护线程
t1.setDaemon(true);
t1.start();
sleep(1);
log.debug("运行结束...");
```

输出

```properties
08:26:38.123 [main] c.TestDaemon - 开始运行...
08:26:38.213 [daemon] c.TestDaemon - 开始运行...
08:26:39.215 [main] c.TestDaemon - 运行结束...
```



> 注意:
>
> - `垃圾回收器线程`就是一种守护线程 
> - Tomcat 中的 `Acceptor 和 Poller 线程`都是守护线程，所以 Tomcat 接收到 shutdown 命令后，不会等待它们处理完当前请求



### 2.11 五种状态

- 在`操作系统`的层面上
  ![1583507073055](https://img-blog.csdnimg.cn/img_convert/b1754631b4dfc3b98fd5375100a3fe34.png)

1. **`初始状态`**，仅仅是在语言层面上**创建了线程对象**，即`Thead thread = new Thead();`，还未与操作系统线程关联 

2. **`可运行状态`**，也称`就绪状态`，指该线程已经被创建，与操作系统相关联，**等待cpu给它分配时间片就可运行**

3. `运行状态`，指线程获取了CPU时间片，正在运行
   1. 当CPU时间片用完，线程会转换至【可运行状态】，等待 CPU再次分配时间片，会导致我们前面讲到的上下文切换

4. `阻塞状态`

   1. 如果调用了阻塞API，如BIO读写文件，那么线程实际上不会用到CPU，不会分配CPU时间片，会导致上下文切换，进入【阻塞状态】
   2. 等待BIO操作完毕，会由操作系统唤醒阻塞的线程，转换至【可运行状态】
   3. **与【可运行状态】的区别是，只要操作系统一直不唤醒线程，调度器就一直不会考虑调度它们，CPU就一直不会分配时间片**

5. **`终止状态`**，**表示线程已经执行完毕，生命周期已经结束，不会再转换为其它状态**

### 2.12 六种状态

- 这是从 Java API 层面来描述的
- 根据`Thread.State 枚举，分为六种状态`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210129171228140.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3OTg5OTgw,size_16,color_FFFFFF,t_70)

> **`新建状态`、`运行状态`(就绪状态, 运行中状态)、`阻塞状态`、`等待状态`、`定时等待状态`、`终止状态`**

- **`NEW (新建状态)`** 线程刚被创建，但是还没有调用 start() 方法
- **`RUNNABLE (运行状态)`** 当调用了 `start() 方法之后`，注意，Java API 层面的`RUNNABLE 状态涵盖了操作系统层面的 【就绪状态】、【运行中状态】和【阻塞状态】`（由于 BIO 导致的线程阻塞，在 Java 里无法区分，仍然认为 是可运行）
- **`BLOCKED (阻塞状态)` ， `WAITING (等待状态)` ， `TIMED_WAITING(定时等待状态)`** 都是 Java API 层面对【阻塞状态】的细分，如 **sleep** 就为 **TIMED_WAITING**， **join **为 **WAITING **状态。后面会在状态转换一节详述。
- **`TERMINATED (结束状态)`** 当线程代码运行结束

```java
@Slf4j(topic = "c.TestState")
public class TestState {
    public static void main(String[] args) throws IOException {
        Thread t1 = new Thread("t1") {	// new 状态
            @Override
            public void run() {
                log.debug("running...");
            }
        };

        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                while(true) { // runnable 状态

                }
            }
        };
        t2.start();

        Thread t3 = new Thread("t3") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t3.start();

        Thread t4 = new Thread("t4") {
            @Override
            public void run() {
                synchronized (TestState.class) {
                    try {
                        Thread.sleep(1000000); // timed_waiting 显示阻塞状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t4.start();

        Thread t5 = new Thread("t5") {
            @Override
            public void run() {
                try {
                    t2.join(); // waiting 状态
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }  
            }
        };
        t5.start();

        Thread t6 = new Thread("t6") {
            @Override
            public void run() {
                synchronized (TestState.class) { // blocked 状态
                    try {
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t6.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("t1 state {}", t1.getState());
        log.debug("t2 state {}", t2.getState());
        log.debug("t3 state {}", t3.getState());
        log.debug("t4 state {}", t4.getState());
        log.debug("t5 state {}", t5.getState());
        log.debug("t6 state {}", t6.getState());
    }
}
```



### 2.13 本章小结

本章的重点在于掌握

- 线程创建
- 线程重要 api，如 start，run，sleep，join，interrupt 等
- 线程状态



应用方面

- 异步调用：主线程执行期间，其它线程异步执行耗时操作
- 提高效率：并行计算，缩短运算时间
- 同步等待：join
- 统筹规划：合理使用线程，得到最优效果



原理方面

- 线程运行流程：栈、栈帧、上下文切换、程序计数器
- Thread 两种创建方式的源码
- 模式方面
- 终止模式之两阶段终止

