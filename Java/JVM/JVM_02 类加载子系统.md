## JVM架构图



![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7cd31581d15?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



## 1. 类加载子系统作用

- 类加载子系统负责从文件系统或者网络中加载Class文件，class文件在文件开头有特定的文件标识；
- ClassLoader只负责class文件的加载，至于它是否可以运行，则由Execution Engine决定
- 加载的类信息存放于一块成为方法区的内存空间。除了类信息之外，方法区还会存放运行时常量池信息，可能还包括字符串字面量和数字常量（这部分常量信息是Class文件中常量池部分的内存映射）



### 1.1 类加载器ClassLoader角色

![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7d7217f0c1c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 1.2 加载

- 通过一个类的全限定名获取定义此类的二进制字节流；
- 将这个字节流所代表的的静态存储结构转化为方法区的运行时数据；
- 在内存中生成一个代表这个类的java.lang.Class对象，作为方法区这个类的各种数据的访问入口



![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7daeca85a52?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 1.3 链接

#### 1.3.1 验证 

- 目的在于确保Class文件的字节流中包含信息符合当前虚拟机要求，保证被加载类的正确性，不会危害虚拟机自身安全。
- 主要包括四种验证，文件格式验证，源数据验证，字节码验证，符号引用验证。

#### 1.3.2 准备

- 为类变量分配内存并且设置该类变量的默认初始值，即零值；
- **这里不包含用final修饰的static，因为final在编译的时候就会分配了，准备阶段会显式初始化；**
- **这里不会为实例变量分配初始化**，类变量会分配在方法区中，而实例变量是会随着对象一起分配到java堆中。

#### 1.3.3 解析

- 将常量池内的符号引用转换为直接引用的过程。 
- 事实上，解析操作往往会伴随着jvm在执行完初始化之后再执行
- 符号引用就是一组符号来描述所引用的目标。符号引用的字面量形式明确定义在《java虚拟机规范》的class文件格式中。直接引用就是直接指向目标的指针、相对偏移量或一个间接定位到目标的句柄
- 解析动作主要针对类或接口、字段、类方法、接口方法、方法类型等。对应常量池中的CONSTANT_Class_info/CONSTANT_Fieldref_info、CONSTANT_Methodref_info等。

### 1.4 初始化

- **初始化阶段就是执行类构造器方法 `<clinit>()` 的过程。**

- 此方法不需要定义，是javac编译器自动收集类中的所有类变量的赋值动作和静态代码块中的语句合并而来。（类变量为static修饰的变量）

  **我们注意到如果没有静态变量c，那么字节码文件中就不会有clinit方法**
  
  ![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7ee5e625699?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

  ![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7f508f951e6?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

- 构造器方法中指令按语句在源文件中出现的顺序执行

  ![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec7fc6f50f714?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

- **`<clinit>()`不同于类的构造器。**（关联：构造器是虚拟机视角下的init()）

- 若该类具有父类，jvm会保证子类的clinit()执行前，父类的clinit()已经执行完毕

  ![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec833466b3af3?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

- 虚拟机必须保证一个类的clinit()方法在多线程下被同步加锁。

  ![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8415e75466b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

------

## 2. 类加载器分类

- JVM支持两种类型的加载器，分别为**引导类加载器（BootStrap ClassLoader）**和 **自定义类加载器（User-Defined ClassLoader）**
- 从概念上来讲，自定义类加载器一般指的是程序中由开发人员自定义的一类类加载器，但是java虚拟机规范却没有这么定义，而是**将所有派生于抽象类ClassLoader的类加载器都划分为自定义类加载器**。
- 无论类加载器的类型如何划分，在程序中我们最常见的类加载器始终只有三个，如下所示：



![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec88cffd157f0?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 2.1 自定义类与核心类库的加载器

- 对于用户自定义类来说：使用系统类加载器AppClassLoader进行加载
- java核心类库都是使用引导类加载器BootStrapClassLoader加载的

```java
/**
 * ClassLoader加载
 */
public class ClassLoaderTest {
    public static void main(String[] args) {
        //获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);//sun.misc.Launcher$AppClassLoader@18b4aac2

        //获取其上层  扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader);//sun.misc.Launcher$ExtClassLoader@610455d6

        //获取其上层 获取不到引导类加载器
        ClassLoader bootStrapClassLoader = extClassLoader.getParent();
        System.out.println(bootStrapClassLoader);//null

        //对于用户自定义类来说：使用系统类加载器进行加载
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);//sun.misc.Launcher$AppClassLoader@18b4aac2

        //String 类使用引导类加载器进行加载的  -->java核心类库都是使用引导类加载器加载的
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println(classLoader1);//null

    }
}
```

### 2.2 虚拟机自带的加载器

- ①启动类加载器（引导类加载器，BootStrap ClassLoader）

  - 这个类加载使用**C/C++语言实现的**，嵌套在JVM内部
  - 它用来加载java的核心库（JAVA_HOME/jre/lib/rt.jar/resources.jar或sun.boot.class.path路径下的内容），用于提供JVM自身需要的类
  - 并不继承自java.lang.ClassLoader,没有父加载器
  - 加载拓展类和应用程序类加载器，并指定为他们的父加载器
  - 处于安全考虑，BootStrap启动类加载器只加载包名为java、javax、sun等开头的类

- ②拓展类加载器（Extension ClassLoader）

  - java语言编写 ，由sun.misc.Launcher$ExtClassLoader实现。
  - 派生于ClassLoader类
  - 父类加载器为启动类加载器
  - 从java.ext.dirs系统属性所指定的目录中加载类库，或从JDK的安装目录的jre/lib/ext子目录（扩展目录）下加载类库。**如果用户创建的JAR放在此目录下，也会由拓展类加载器自动加载**

- ③应用程序类加载器（系统类加载器，AppClassLoader）

  - java语言编写， 由sun.misc.Launcher$AppClassLoader实现。
  - 派生于ClassLoader类
  - 父类加载器为拓展类加载器
  - 它负责加载环境变量classpath或系统属性 java.class.path指定路径下的类库
  - **该类加载器是程序中默认的类加载器**，一般来说，java应用的类都是由它来完成加载
  - 通过ClassLoader#getSystemClassLoader()方法可以获取到该类加载器

  ##### 代码演示

```java
/**
 * 虚拟机自带加载器
 */
public class ClassLoaderTest1 {
    public static void main(String[] args) {
        System.out.println("********启动类加载器*********");
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        //获取BootStrapClassLoader能够加载的api路径
        for (URL e:urls){
            System.out.println(e.toExternalForm());
        }

        //从上面的路径中随意选择一个类 看看他的类加载器是什么
        //Provider位于 /jdk1.8.0_171.jdk/Contents/Home/jre/lib/jsse.jar 下，引导类加载器加载它
        ClassLoader classLoader = Provider.class.getClassLoader();
        System.out.println(classLoader);//null

        System.out.println("********拓展类加载器********");
        String extDirs = System.getProperty("java.ext.dirs");
        for (String path : extDirs.split(";")){
            System.out.println(path);
        }
        //从上面的路径中随意选择一个类 看看他的类加载器是什么:拓展类加载器
        ClassLoader classLoader1 = CurveDB.class.getClassLoader();
        System.out.println(classLoader1);//sun.misc.Launcher$ExtClassLoader@4dc63996
    }
}
复制代码
```

### 2.3 用户自定义类加载器

**为什么**

- 隔离加载类
- 修改类加载的方式
- 拓展加载源
- 防止源码泄漏



用户自定义类加载器实现步骤:

1. 开发人员可以通过继承抽象类 java.lang .ClassLoader 类的方式，实现自己的类加载器，以满足一些特殊的需求
2. 在JDK1.2之前， 在自定义类加载器时，总会去继承 ClassLoader 类并重写 loadClass() 方法，从而实现自定义的类加载类，但是在JDK1.2之后已不再建议用户去覆盖loadClass()方法，而是建议把自定义的类加载逻辑写在findClass()方法中
3. 在编写自定义类加载器时，如果没有太过于复杂的需求，可以直接继承URLClassLoader类，这样就可以避免自己去编写findClass()方法及其获取字节码流的方式，使自定义类加载器编写更加简洁。



## 3. ClassLoader的常用方法及获取方法

### 3.1 ClassLoader类

它是一个抽象类，其后所有的类加载器都继承自ClassLoader（不包括启动类加载器）

| 方法名称                                             | 描述                                                         |
| ---------------------------------------------------- | :----------------------------------------------------------- |
| getParent（）                                        | 返回该类加载器的超类加载器                                   |
| loadClass（String name）                             | 加载名称为name的类，返回结果为java.lang.Class类的实例        |
| findClass（String name）                             | 查找名称为name的类，返回结果为java.lang.Class类的实例        |
| findLoadedClass（String name）                       | 查找名称为name的已经被加载过的类，返回结果为java.lang.Class类的实例 |
| defineClass（String name，byte[] b,int off,int len） | 把字节数组b中的内容转换为一个Java类 ，返回结果为java.lang.Class类的实例 |
| resolveClass（Class<?> c）                           | 连接指定的一个java类                                         |

### 3.2 ClassLoader继承关系

**拓展类加载器和系统类加载器间接继承于ClassLoader抽象类**

![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8a4eaae0b43?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 3.3 获取ClassLoader的途径

![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8b36f5d4187?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)





![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8b6c081f466?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



## 4. 双亲委派机制

Java虚拟机对class文件采用的是按需加载的方式，也就是说当需要使用该类时才会将她的class文件加载到内存生成的class对象。而且加载某个类的class文件时，java虚拟机采用的是双亲微拍模式，即把请求交由父类处理，它是一种任务委派 模式

### 4.1 双亲委派机制工作原理



![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8cbbe16af0c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



**例** ：如图，虽然我们自定义了一个java.lang包下的String尝试覆盖核心类库中的String，但是由于双亲委派机制，启动加载器会加载java核心类库的String类（BootStrap启动类加载器只加载包名为java、javax、sun等开头的类），而核心类库中的String并没有main方法

![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8d5358f0991?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 4.2 双亲委派机制的优势

- 避免类的重复加载
- 保护程序安全，防止核心API被随意篡改
  - 自定义类：java.lang.String
  - 自定义类：java.lang.MeDsh（java.lang包需要访问权限，阻止我们用包名自定义类）



![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8ddb41c5559?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)





![img](https://user-gold-cdn.xitu.io/2020/3/18/170ec8e47d7e861b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



## 5. 沙箱安全机制

**自定义String类，但是在加载自定义String类的时候会率先使用引导类加载器加载，而引导类加载器在加载过程中会先加载jdk自带的文件（rt.jar包中的java\lang\String.class），报错信息说没有main方法就是因为加载的是rt.jar包中的String类。这样可以保证对java核心源代码的保护，这就是沙箱安全机制。**

**类比举例：** 我们在读写U盘信息时可以用[360沙箱](https://jingyan.baidu.com/article/6c67b1d65dfa6d2787bb1eae.html)，防止U盘内的病毒等对沙箱外的系统构成污染

## 6. 其他

- 在jvm中表示两个class对象是否为同一个类存在的两个必要条件
  - 类的完整类名必须一致，包括包名
  - 加载这个类的ClassLoader（指ClassLoader实例对象）必须相同
- 换句话说，在jvm中，即使这两个类对象（class对象）来源同一个Class文件，被同一个虚拟机所加载，但只要加载它们的ClassLoader实例对象不同，那么这两个类对象也是不相等的.

### 6.1 对类加载器的引用

JVM必须知道一个类型是有启动类加载器加载的还是由用户类加载器加载的。如果一个类型由用户类加载器加载的，那么jvm会**将这个类加载器的一个引用作为类型信息的会议部分保存在方法区中**。当解析一个类型到另一个类型的引用的时候，JVM需要保证两个类型的加载器是相同的。

### 6.2 类的主动使用和被动使用

**java程序对类的使用方式分为：主动使用和被动使用**

- 主动使用，分为七种情况
  - 创建类的实例
  - 访问某各类或接口的静态变量，或者对静态变量赋值
  - 调用类的静态方法
  - 反射 比如Class.forName(com.dsh.jvm.xxx)
  - 初始化一个类的子类
  - java虚拟机启动时被标明为启动类的类
  - JDK 7 开始提供的动态语言支持：
     java.lang.invoke.MethodHandle实例的解析结果REF_getStatic、REF_putStatic、REF_invokeStatic句柄对应的类没有初始化，则初始化
- 除了以上七种情况，其他使用java类的方式都被看作是对类的被动使用，都不会导致类的初始化。
