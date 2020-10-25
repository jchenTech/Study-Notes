[toc]

# File类的使用

## File类的理解

1. File类的一个对象，代表一个文件或一个文件目录(俗称：文件夹)

2. File类声明在java.io包下

3. File类中涉及到关于文件或文件目录的创建、删除、重命名、修改时间、文件大小等方法，

   并未涉及到写入或读取文件内容的操作。如果需要读取或写入文件内容，必须使用IO流来完成。

4. 后续File类的对象常会作为参数传递到流的构造器中，指明读取或写入的"终点".

## File的实例化

### 常用的构造器

* File(String filePath)
* File(String parentPath,String childPath)
* File(File parentFile,String childPath)

### 路径的分类

相对路径：相较于某个路径下，指明的路径。
绝对路径：包含盘符在内的文件或文件目录的路径

在IDEA中，main方法中的相对路径为project下，test中的相对路径在module下

### 路径分隔符

windows和DOS系统默认使用“\”来表示
UNIX和URL使用“/”来表示

## File类的常用方法

### File类的获取功能

* public String getAbsolutePath() 获取绝对路径
* public String getPath 获取 路径
* public String getName 获取名称
* public String getParent() 获取上层文件目录路径 。 若无 返回 null
* public long length 获取 文件 长度 即：字节数 。 不能获取目录的长度 。
* public long lastModified 获取 最后一次的修改时间 毫秒值

* public String[] list 获取 指定目录下的所有文件或者 文件 目录 的 名称数组
* public File[] listFiles 获取 指定目录下的所有文件或者 文件 目录 的 File 数组

### File类的创建功能

* public boolean createNewFile 创建文件 。 若 文件 存在 则不创建 返回 false
* public boolean mkdir 创建文件 目录 。 如果 此文件目录存在 就不创建了。如果此文件目录的上层目录不存在 也不创建 。
* public boolean mkdirs 创建文件 目录 。 如果 上层 文件 目录 不 存在 一并 创建

### File类的删除功能

* public boolean delete 删除文件或者文件夹
**注意事项：Java中的删除不走回收站 。要删除一个 文件 目录 请注意该 文件 目录 内 不能包含文件或者 文件 目录**

# IO流概述

## 流的分类

1. 操作数据单位：字节流、字符流
2. 数据的流向：输入流、输出流
3. 流的角色：节点流、处理流

![image-20201012153942602](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191926.png)

## 流的体系结构

![image-20201012154229947](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191927.png)

说明：红框对应的是IO流中的4个抽象基类

蓝框的流需要重点关注

## 重点说明的几个流结构

| 抽象基类     | 节点流                                        | 缓冲流                                                     |
| ------------ | --------------------------------------------- | ---------------------------------------------------------- |
| InputStream  | FileInputStream   (read(byte[] buffer))       | BufferedInputStream (read(byte[] buffer))                  |
| OutputStream | FileOutputStream  (write(byte[] buffer,0,len) | BufferedOutputStream (write(byte[] buffer,0,len) / flush() |
| Reader       | FileReader (read(char[] cbuf))                | BufferedReader (read(char[] cbuf) / readLine())            |
| Writer       | FileWriter (write(char[] cbuf,0,len)          | BufferedWriter (write(char[] cbuf,0,len) / flush()         |

## 输入输出的标准化过程

### 输入过程

1. 创建File类的对象，指明读取的数据的来源。（要求此文件一定要存在）
2. 创建相应的输入流，将File类的对象作为参数，传入流的构造器中
3. 具体的读入过程：创建相应的byte[] 或 char[]。
4. 关闭流资源

说明：程序中出现的异常需要使用try-catch-finally处理。


### 输出过程

1. 创建File类的对象，指明写出的数据的位置。（不要求此文件一定要存在）
2. 创建相应的输出流，将File类的对象作为参数，传入流的构造器中
3. 具体的写出过程：write(char[]/byte[] buffer,0,len)
4. 关闭流资源

说明：程序中出现的异常需要使用try-catch-finally处理。

# 节点流（或文件流）

## FileReader/FileWriter的使用

### FileReader使用

将文件内容读到程序中并输出到控制台。

说明：

1. read()的理解：返回读入的一个字符。如果达到文件末尾，返回-1
2. 异常的处理：为了保证流资源一定可以执行关闭操作。需要使用try-catch-finally处理
3. 读入的文件一定要存在，否则就会报FileNotFoundException。            

```Java
  @Test
    public void testFileReader1()  {
        FileReader fr = null;
        try {
            //1.File类的实例化
            File file = new File("hello.txt");
            
            //2.FileReader流的实例化
            fr = new FileReader(file);

            //3.读入的操作
            //read(char[] cbuf):返回每次读入cbuf数组中的字符的个数。如果达到文件末尾，返回-1
            char[] cbuf = new char[5];
            int len;
            while((len = fr.read(cbuf)) != -1){
                String str = new String(cbuf,0,len);
                System.out.print(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fr != null){
                //4.资源的关闭
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

### FileWriter的使用

从内存中写出数据到硬盘的文件里。

说明：

1. 输出操作，对应的File可以不存在的。并不会报异常
2. File对应的硬盘中的文件如果不存在，在输出的过程中，会自动创建此文件。
   File对应的硬盘中的文件如果存在：
           如果流使用的构造器是：FileWriter(file,false) / FileWriter(file):对原有文件的覆盖
           如果流使用的构造器是：FileWriter(file,true):不会对原有文件覆盖，而是在原有文件基础上追加内容

```Java
@Test
public void testFileWriter() {
    FileWriter fw = null;
    try {
        //1.提供File类的对象，指明写出到的文件
        File file = new File("hello1.txt");

        //2.提供FileWriter的对象，用于数据的写出
        fw = new FileWriter(file);

        //3.写出的操作
        fw.write("I have a dream!");
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (fw != null) {
            try {
                //4.流资源的关闭
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 文本文件的复制

```Java
@Test
    public void testFileReaderFileWriter() {
        FileReader fr = null;
        FileWriter fw = null;
        try {
            //1.创建File类的对象，指明读入和写出的文件
            File srcFile = new File("hello.txt");
            File destFile = new File("hello2.txt");

            //2.创建输入流和输出流的对象
            fr = new FileReader(srcFile);
            fw = new FileWriter(destFile);

            //3.数据的读入和写出操作
            char[] cbuf = new char[5];
            int len;//记录每次读入到cbuf数组中的字符的个数
            while((len = fr.read(cbuf)) != -1){
                //每次写出len个字符
                fw.write(cbuf,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭流资源
            try {
                if(fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## FileInputStream/FileOutputStream的使用

1. 对于文本文件(.txt,.java,.c,.cpp)，使用字符流处理

2. 对于非文本文件(.jpg,.mp3,.mp4,.avi,.doc,.ppt,...)，使用字节流处理

实现对图片的复制操作：

```Java
@Test
public void testFileInputOutputStream()  {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try {
        //1.造文件
        File srcFile = new File("爱情与友情.jpg");
        File destFile = new File("爱情与友情2.jpg");

        //2.造流
        fis = new FileInputStream(srcFile);
        fos = new FileOutputStream(destFile);

        //3.复制的过程
        byte[] buffer = new byte[5];
        int len;
        while((len = fis.read(buffer)) != -1){
            fos.write(buffer,0,len);
        }

    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if(fos != null){
            //4.关闭流
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(fis != null){
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
```

# 缓冲流

## 缓冲流涉及到的类

* BufferedInputStream
* BufferedOutputStream
* BufferedReader
* BufferedWriter

## 作用

作用：提供流的读取、写入的速度
提高读写速度的原因：内部提供了一个缓冲区。默认情况下是8kb

![image-20201012175215297](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191928.png)

## 典型代码

### 使用BufferedInputStream和BufferedOutputStream:处理非文本文件

```Java
@Test
    public void BufferedStreamTest() throws FileNotFoundException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            //1.造文件
            File srcFile = new File("爱情与友情.jpg");
            File destFile = new File("爱情与友情3.jpg");
            //2.造流
            //2.1 造节点流
            FileInputStream fis = new FileInputStream((srcFile));
            FileOutputStream fos = new FileOutputStream(destFile);
            //2.2 造缓冲流
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);

            //3.复制的细节：读取、写入
            byte[] buffer = new byte[10];
            int len;
            while((len = bis.read(buffer)) != -1){
                bos.write(buffer,0,len);

//                bos.flush();//刷新缓冲区

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.资源关闭
            //要求：先关闭外层的流，再关闭内层的流
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //说明：关闭外层流的同时，内层流也会自动的进行关闭。关于内层流的关闭，我们可以省略.
//        fos.close();
//        fis.close();
        }
}
```

###  使用BufferedReader和BufferedWriter：处理文本文件

与上面代码类似

# 转换流

## 转换流涉及的类

转换流：属于字符流

* InputStreamReader：将一个字节的输入流转换为字符的输入流
  解码：字节、字节数组  --->字符数组、字符串

* OutputStreamWriter：将一个字符的输出流转换为字节的输出流
  编码：字符数组、字符串 ---> 字节、字节数组

说明：编码决定了解码的方式

## 作用

提供字节流与字符流之间的转换

![image-20201012175641157](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191929.png)

## 转换流实现

综合使用InputStreamReader和OutputStreamWriter



```Java
@Test
    public void test2() throws Exception {
        //1.造文件、造流
        File file1 = new File("dbcp.txt");
        File file2 = new File("dbcp_gbk.txt");

        FileInputStream fis = new FileInputStream(file1);
        FileOutputStream fos = new FileOutputStream(file2);

        InputStreamReader isr = new InputStreamReader(fis,"utf-8");
        OutputStreamWriter osw = new OutputStreamWriter(fos,"gbk");

        //2.读写过程
        char[] cbuf = new char[20];
        int len;
        while((len = isr.read(cbuf)) != -1){
            osw.write(cbuf,0,len);
        }

        //3.关闭资源
        isr.close();
        osw.close();

    }
```

## 常见的编码表

* ASCII：美国标准信息交换码。
  用一个字节的7位可以表示。
* ISO8859-1：拉丁码表。欧洲码表
  用一个字节的8位表示。
* GB2312：中国的中文编码表。最多两个字节编码所有字符
* GBK：中国的中文编码表升级，融合了更多的中文文字符号。最多两个字节编码
* Unicode：国际标准码，融合了目前人类使用的所字符。为每个字符分配唯一的字符码。所有的文字都用两个字节来表示。
* UTF-8：变长的编码方式，可用1-4个字节来表示一个字符。

# 其他的流

## 标准输入输出流

System.in:标准的输入流，默认从键盘输入
System.out:标准的输出流，默认从控制台输出

## 打印流

PrintStream 和PrintWriter
说明：

* 提供了一系列重载的print()和println()方法，用于多种数据类型的输出
* System.out返回的是PrintStream的实例

## 数据流

DataInputStream 和 DataOutputStream
作用：用于读取或写出基本数据类型的变量或字符串

# 对象流

ObjectInputStream 和 ObjectOutputStream

## 作用
ObjectOutputStream:内存中的对象--->存储中的文件、通过网络传输出去：序列化过程
ObjectInputStream:存储中的文件、通过网络接收过来 --->内存中的对象：反序列化过程

## 对象的序列化机制

**对象序列化机制**允许把内存中的Java对象转换成平台无关的二进制流，从而允许把这种二进制流持久地保存在磁盘上，或通过网络将这种二进制流传输到另一个网络节点。//当其它程序获取了这种二进制流，就可以恢复成原来的Java对象。

## 序列化代码实现

```Java
@Test
public void testObjectOutputStream(){
    ObjectOutputStream oos = null;

    try {
        //1.
        oos = new ObjectOutputStream(new FileOutputStream("object.dat"));
        //2.
        oos.writeObject(new String("我爱北京天安门"));
        oos.flush();//刷新操作

         oos.writeObject(new Person("王铭",23));
        oos.flush();

        oos.writeObject(new Person("张学良",23,1001,new Account(5000)));
        oos.flush();

    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if(oos != null){
            //3.
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
```

## 反序列化代码实现

```Java
@Test
public void testObjectInputStream(){
    ObjectInputStream ois = null;
    try {
        ois = new ObjectInputStream(new FileInputStream("object.dat"));

        Object obj = ois.readObject();
        String str = (String) obj;

        Person p = (Person) ois.readObject();
        Person p1 = (Person) ois.readObject();

        System.out.println(str);
        System.out.println(p);
        System.out.println(p1);

    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } finally {
        if(ois != null){
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
```

自定义类需要满足如下的要求，方可序列化
* 1.需要实现接口：Serializable
* 2.当前类提供一个全局常量：serialVersionUID
* 3.除了当前Person类需要实现Serializable接口之外，还必须保证其内部所有属性也必须是可序列化的。（默认情况下，基本数据类型可序列化）

# 随机存取文件流

## 概述

1. RandomAccessFile直接继承于java.lang.Object类，实现了DataInput和DataOutput接口。
2. RandomAccessFile既可以作为一个输入流，又可以作为一个输出流。
3. 如果RandomAccessFile作为输出流时，写出到的文件如果不存在，则在执行过程中自动创建。
如果写出到的文件存在，则会对原文件内容进行覆盖。（默认情况下，从头覆盖）。
4. 可以通过相关的操作，实现RandomAccessFile“插入”数据的效果。seek(int pos)。

## 文件读写代码实现

```Java
@Test
public void test1() {
    RandomAccessFile raf1 = null;
    RandomAccessFile raf2 = null;
    try {
        //1.
        raf1 = new RandomAccessFile(new File("爱情与友情.jpg"),"r");
        raf2 = new RandomAccessFile(new File("爱情与友情1.jpg"),"rw");
        //2.
        byte[] buffer = new byte[1024];
        int len;
        while((len = raf1.read(buffer)) != -1){
            raf2.write(buffer,0,len);
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        //3.
        if(raf1 != null){
            try {
                raf1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(raf2 != null){
            try {
                raf2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

使用RandomAccessFile实现数据的插入效果:

```Java
@Test
public void test3() throws IOException {
    RandomAccessFile raf1 = new RandomAccessFile("hello.txt","rw");

    raf1.seek(3);//将指针调到角标为3的位置
    //保存指针3后面的所数据到StringBuilder中
    StringBuilder builder = new StringBuilder((int) new File("hello.txt").length());
    byte[] buffer = new byte[20];
    int len;
    while((len = raf1.read(buffer)) != -1){
        builder.append(new String(buffer,0,len)) ;
    }
    //调回指针，写入“xyz”
    raf1.seek(3);
    raf1.write("xyz".getBytes());

    //将StringBuilder中的数据写入到文件中
    raf1.write(builder.toString().getBytes());

    raf1.close();
    //思考：将StringBuilder替换为ByteArrayOutputStream
}

```

# NIO

## NIO使用说明

* Java NIO (New IO，Non-Blocking IO)是从Java 1.4版本开始引入的一套新的IO API，可以替代标准的Java IO API。

* NIO与原来的IO同样的作用和目的，但是使用的方式完全不同，NIO支持面向缓冲区的(IO是面向流的)、基于通道的IO操作。NIO将以更加高效的方式进行文件的读写操作。
* 随着 JDK 7 的发布，Java对NIO进行了极大的扩展，增强了对文件处理和文件系统特性的支持，以至于我们称他们为 NIO.2。
* Java API 中提供了两套 NIO 一套是针对标准输入输出 NIO 另一套就是网
  络编程 NIO

## Path的使用

Path可以用于替换原有的File类。

原来的File类使用方法：

```Java
import java.io.File;
File file = new File("index.html");
```

再jdk7后我们可以使用：

```Java
import java.nio.file.Path;
import java.nio.file.Paths;
Path path = Paths.get("index.html");
```

常用方法

![image-20201012203436094](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191930.png)

## Files工具类

用于操作文件或文件目录的工具类

常用方法：

![image-20201012203609036](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191931.png)

![image-20201012203612124](https://raw.githubusercontent.com/jchenTech/images/main/img/20201025191932.png)