## 1 概述

在Java语言中，所有类似“ABC”的字面值，都是String类的实例；String类位于java.lang包下，是Java语言的核心类，提供了字符串的比较、查找、截取、大小写转换等操作；Java语言为“+”连接符（字符串连接符）以及对象转换为字符串提供了特殊的支持，字符串对象可以使用“+”连接其他对象。String类的部分源码如下

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    /** The value is used for character storage. */
    private final char value[];

    /** Cache the hash code for the string */
    private int hash; // Default to 0
    ...
}

```

从上面可以看出：

1）String类被**`final`**关键字修饰，意味着String类不能被继承，并且它的成员方法都默认为final方法；字符串一旦创建就不能再修改。

2）String类实现了**`Serializable`**、**`CharSequence`**、 **`Comparable`**接口。

* 实现了Serializable接口：表示字符串是支持序列化的。
* 实现了Comparable接口：表示String可以比较大小

3）String实例的值是通过字符数组实现字符串存储的。String内部定义了 `final char[] value` 用于存储字符串数据。

4） 通过字面量的方式（区别于new给一个字符串赋值，此时的字符串值声明在字符串常量池中)。

5）字符串常量池中是不会存储相同内容(使用String类的equals()比较，返回true；如果用==，比较的是地址)的字符串的。

## 2 字符串常量池

在Java的内存分配中，总共3种常量池，分别是**Class常量池**、**运行时常量池**、**字符串常量池**。

字符串的分配和其他对象分配一样，是需要消耗高昂的时间和空间的，而且字符串使用的非常多。JVM为了提高性能和减少内存的开销，在实例化字符串的时候进行了一些优化：使用字符串常量池。每当创建字符串常量时，JVM会首先检查字符串常量池，如果该字符串已经存在常量池中，那么就直接返回常量池中的实例引用。如果字符串不存在常量池中，就会实例化该字符串并且将其放到常量池中。由于String字符串的不可变性，**常量池中一定不存在两个相同的字符串**。

```java
/**
 * 字符串常量池中的字符串只存在一份！
 * 运行结果为true
 */
String s1 = "hello world!";
String s2 = "hello world!";
System.out.println(s1 == s2);
```

### 2.1 内存区域

在HotSpot VM中字符串常量池是通过一个StringTable类实现的，它是一个Hash表，默认值大小长度是1009；这个StringTable在每个HotSpot VM的实例中只有一份，被所有的类共享；字符串常量由一个一个字符组成，放在了StringTable上。要注意的是，如果放进String Pool的String非常多，就会造成Hash冲突严重，从而导致链表会很长，而链表长了后直接会造成的影响就是当调用String.intern时性能会大幅下降（因为要一个一个找）。

* 在JDK6及之前版本，运行时常量池（字符串常量池也在里面）是放在Perm Gen区(也就是方法区)中的，此时方法区的实现是永久带，StringTable的长度是固定的1009；

* 在JDK7版本中，字符串常量池被单独从方法区移到了堆中，运行时常量池剩下的还在永久带，StringTable的长度可以通过**`-XX:StringTableSize=66666`**参数指定。至于JDK7为什么把常量池移动到堆上实现，原因可能是由于方法区的内存空间太小且不方便扩展，而堆的内存空间比较大且扩展方便。
* **在JDK8(JDK1.8)中永久带更名为元空间（方法区的新的实现），但是字符串常量池还在堆中，运行时常量池在元空间（方法区）**

<img src="https://gitee.com/jchenTech/images/raw/master/img/20201214162904.png" alt="image-20201214154046543" style="zoom: 50%;" />



### 2.2 存放的内容

在JDK6及之前版本中，String Pool里放的都是字符串常量；在JDK7.0中，由于String.intern()发生了改变，因此String Pool中也可以存放放于堆内的字符串对象的引用。

```java
/**
 * 运行结果为true false
 */
String s1 = "AB";
String s2 = "AB";
String s3 = new String("AB");
System.out.println(s1 == s2);
System.out.println(s1 == s3);
```

由于常量池中不存在两个相同的对象，所以s1和s2都是指向JVM字符串常量池中的"AB"对象。new关键字一定会产生一个对象，并且这个对象存储在堆中。所以`String s3 = new String(“AB”);`产生了两个对象：保存在栈中的s3和保存堆中的String对象。

<img src="https://gitee.com/jchenTech/images/raw/master/img/20201214162905.png" alt="String类 (1)" style="zoom:50%;" />

下面我们再对字符串的不可变性和实例化做更加详细的解释和总结。

## 3 String特性

### 3.1 String的不可变性

1）当对字符串重新赋值时，需要重写指定内存区域赋值，不能使用原有的value进行赋值。

2）当对现有的字符串进行连接操作时，也需要重新指定内存区域赋值，不能使用原有的value进行赋值。

3）当调用String的replace()方法修改指定字符或字符串时，也需要重新指定内存区域赋值，不能使用原有的value进行赋值。

```java
String s1 = "abc";//字面量的定义方式
String s2 = "abc";
s1 = "hello";

System.out.println(s1 == s2);//比较s1和s2的地址值

System.out.println(s1);//hello
System.out.println(s2);//abc

System.out.println("*****************");

String s3 = "abc";
s3 += "def";
System.out.println(s3);//abcdef
System.out.println(s2);//abc

System.out.println("*****************");

String s4 = "abc";
String s5 = s4.replace('a', 'm');
System.out.println(s4);//abc
System.out.println(s5);//mbc
```

### 3.2 字符串实例化方式

字符串可以通过两种方式进行实例化：

* 通过字面量定义的方式

* 通过new + 构造器的方式

```java
//通过字面量定义的方式：此时的s1和s2的数据javaEE声明在堆空间的字符串常量池中。
String s1 = "javaEE";
String s2 = "javaEE";
//通过new + 构造器的方式:此时的s3和s4保存的地址值，是数据在堆空间中开辟空间以后对应的地址值。
String s3 = new String("javaEE");
String s4 = new String("javaEE");

System.out.println(s1 == s2);//true
System.out.println(s1 == s3);//false
System.out.println(s1 == s4);//false
System.out.println(s3 == s4);//false
```

### 3.3 字符串拼接方式赋值

1）常量与常量的拼接结果在常量池。且常量池中不会存在相同内容的常量。

2）只要其中一个是变量，结果就在堆中。

3）如果拼接的结果调用intern()方法，返回值就在常量池中

```java
String s1 = "javaEE";
String s2 = "hadoop";

String s3 = "javaEEhadoop";
String s4 = "javaEE" + "hadoop";
String s5 = s1 + "hadoop";
String s6 = "javaEE" + s2;
String s7 = s1 + s2;

System.out.println(s3 == s4);//true
System.out.println(s3 == s5);//false
System.out.println(s3 == s6);//false
System.out.println(s3 == s7);//false
System.out.println(s5 == s6);//false
System.out.println(s5 == s7);//false
System.out.println(s6 == s7);//false

String s8 = s6.intern();//返回值得到的s8使用的常量值中已经存在的“javaEEhadoop”
System.out.println(s3 == s8);//true
****************************
String s1 = "javaEEhadoop";
String s2 = "javaEE";
String s3 = s2 + "hadoop";
System.out.println(s1 == s3);//false

final String s4 = "javaEE";//s4:常量
String s5 = s4 + "hadoop";
System.out.println(s1 == s5);//true
```

### 3.4 intern方法

直接使用双引号声明出来的String对象会直接存储在字符串常量池中，如果不是用双引号声明的String对象，可以使用String提供的intern方法。intern 方法是一个native方法，intern方法会从字符串常量池中查询当前字符串是否存在，如果存在，就直接返回当前字符串；如果不存在就会将当前字符串放入常量池中，之后再返回。

JDK1.7的改动：

1. 将String常量池 从 Perm 区移动到了 Java Heap区
2. String.intern() 方法时，如果存在堆中的对象，会直接保存对象的引用，而不会重新创建对象。

```java
/**
 * Returns a canonical representation for the string object.
 * <p>
 * A pool of strings, initially empty, is maintained privately by the
 * class {@code String}.
 * <p>
 * When the intern method is invoked, if the pool already contains a
 * string equal to this {@code String} object as determined by
 * the {@link #equals(Object)} method, then the string from the pool is
 * returned. Otherwise, this {@code String} object is added to the
 * pool and a reference to this {@code String} object is returned.
 * <p>
 * It follows that for any two strings {@code s} and {@code t},
 * {@code s.intern() == t.intern()} is {@code true}
 * if and only if {@code s.equals(t)} is {@code true}.
 * <p>
 * All literal strings and string-valued constant expressions are
 * interned. String literals are defined in section 3.10.5 of the
 * <cite>The Java&trade; Language Specification</cite>.
 *
 * @return  a string that has the same contents as this string, but is
 *          guaranteed to be from a pool of unique strings.
 */
public native String intern();
```

### 3.5 小结

String类是我们使用频率最高的类之一，也是面试官经常考察的题目，下面是一个小测验。

```java
public static void main(String[] args) {
    String s1 = "AB";
    String s2 = new String("AB");
    String s3 = "A";
    String s4 = "B";
    String s5 = "A" + "B";
    String s6 = s3 + s4;
    System.out.println(s1 == s2);//false
    System.out.println(s1 == s5);//true
    System.out.println(s1 == s6);//false
    System.out.println(s1 == s6.intern());//true
    System.out.println(s2 == s2.intern());//false
}
```

解析：真正理解此题目需要清楚以下三点
1）直接使用双引号声明出来的String对象会直接存储在常量池中；
2）String对象的intern方法会得到字符串对象在常量池中对应的引用，如果常量池中没有对应的字符串，则该字符串将被添加到常量池中，然后返回常量池中字符串的引用；
3） 字符串的+操作其本质是创建了StringBuilder对象进行append操作，然后将拼接后的StringBuilder对象用toString方法处理成String对象，这一点可以用javap -c命令获得class文件对应的JVM字节码指令就可以看出来。

![这里写图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201214162949.png)

## 4 String、StringBuffer和StringBuilder

### 4.1 继承结构

![这里写图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201214162954.png)

### 4.2 内存解析

以StringBuffer为例：

```java
String str = new String();//char[] value = new char[0];
String str1 = new String("abc");//char[] value = new char[]{'a','b','c'};

StringBuffer sb1 = new StringBuffer();//char[] value = new char[16];底层创建了一个长度是16的数组。
System.out.println(sb1.length());//
sb1.append('a');//value[0] = 'a';
sb1.append('b');//value[1] = 'b';

StringBuffer sb2 = new StringBuffer("abc");//char[] value = new char["abc".length() + 16];

//问题1. System.out.println(sb2.length());//3
//问题2. 扩容问题:如果要添加的数据底层数组盛不下了，那就需要扩容底层的数组。默认情况下，扩容为原来容量的2倍 + 2，同时将原数组中的元素复制到新的数组中。指导意义：开发中建议大家使用：StringBuffer(int capacity) 或 StringBuilder(int capacity)
```

### 4.3 主要区别

1）String是不可变字符序列，StringBuilder和StringBuffer是可变字符序列。

2）执行速度StringBuilder > StringBuffer > String。

3）StringBuilder是非线程安全的，StringBuffer是线程安全的。

### 4.4 常用方法

* 增：append(xxx)
* 删：delete(int start,int end)
* 改：setCharAt(int n ,char ch) / replace(int start, int end, String str)
* 查：charAt(int n )
* 插：insert(int offset, xxx)
* 长度：length();
* 遍历：for() + charAt() / toString()

## 5 String常用方法

基本方法

| 方法                                           | 描述                                                         |
| :--------------------------------------------- | :----------------------------------------------------------- |
| int length()                                   | 返回字符串的长度： return value.length                       |
| char charAt(int index)                         | 返回某索引处的字符return value[index]                        |
| boolean isEmpty()                              | 判断是否是空字符串：return value.length == 0                 |
| String toLowerCase()                           | 使用默认语言环境，将 String 中的所字符转换为小写             |
| String toUpperCase()                           | 使用默认语言环境，将 String 中的所字符转换为大写             |
| String trim()                                  | 返回字符串的副本，忽略前导空白和尾部空白                     |
| boolean equals(Object obj)                     | 比较字符串的内容是否相同                                     |
| boolean equalsIgnoreCase(String anotherString) | 与equals方法类似，忽略大小写                                 |
| String concat(String str)                      | 将指定字符串连接到此字符串的结尾。 等价于用“+”               |
| int compareTo(String anotherString)            | 比较两个字符串的大小                                         |
| String substring(int beginIndex)               | 返回一个新的字符串，它是此字符串的从beginIndex开始截取到最后的一个子字符串。 |
| String substring(int beginIndex, int endIndex) | 返回一个新字符串，它是此字符串从beginIndex开始截取到endIndex(不包含)的一个子字符串。 |
| boolean endsWith(String suffix)                | 测试此字符串是否以指定的后缀结束                             |
| boolean startsWith(String prefix)              | 测试此字符串是否以指定的前缀开始                             |
| boolean startsWith(String prefix, int toffset) | 测试此字符串从指定索引开始的子字符串是否以指定前缀开始       |
| boolean contains(CharSequence s)               | 当且仅当此字符串包含指定的 char 值序列时，返回 true          |
| int indexOf(String str)                        | 返回指定子字符串在此字符串中第一次出现处的索引               |
| int indexOf(String str, int fromIndex)         | 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始 |
| int lastIndexOf(String str)                    | 返回指定子字符串在此字符串中最右边出现处的索引               |
| int lastIndexOf(String str, int fromIndex)     | 返回指定子字符串在此字符串中最后一次出现处的索引，从指定的索引开始反向搜索 |

替换：

| 方法                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| String replace(char oldChar, char newChar)                   | 返回一个新的字符串，它是通过用 newChar 替换此字符串中出现的所 oldChar 得到的。 |
| String replace(CharSequence target, CharSequence replacement) | 使用指定的字面值替换序列替换此字符串所匹配字面值目标序列的子字符串。 |
| String replaceAll(String regex, String replacement)          | 使用给定的 replacement 替换此字符串所匹配给定的正则表达式的子字符串。 |
| String replaceFirst(String regex, String replacement)        | 使用给定的 replacement 替换此字符串匹配给定的正则表达式的第一个子字符串。 |

匹配与切片：

| 方法                                    | 描述                                                         |
| :-------------------------------------- | :----------------------------------------------------------- |
| boolean matches(String regex)           | 告知此字符串是否匹配给定的正则表达式                         |
| String[] split(String regex)            | 根据给定正则表达式的匹配拆分此字符串。                       |
| String[] split(String regex, int limit) | 根据匹配给定的正则表达式来拆分此字符串，最多不超过limit个，如果超过了，剩下的全部都放到最后一个元素中。 |

## 6 String与其他结构的转换

### 6.1 与基本数据类型、包装类间转换

```java
package 包装类;
/**
 *8种基本数据类型对应一个类，此类即为包装类
 * 基本数据类型、包装类、String之间的转换
 * 1.基本数据类型转成包装类（装箱）：
 *  ->通过构造器 ：Integer i = new Integer(11)
 *  ->通过字符串参数：Float f = new Float("12.1f")
 *  ->自动装箱  
 * 2.基本数据类型转换成String类
 *  ->String类的：valueof(2.1f)
 *  ->2.1+" "
 * 3.包装类转换成基本数据类型（拆箱）：
 *  ->调用包装类的方法：xxxValue()
 *  ->自动拆箱
 * 4.包装类转换成String类
 *  ->包装类对象的toString方法
 *  ->调用包装类的toString(形参)
 * 5.String类转换成基本数据类型
 *  ->调用相应包装类：parseXxx(String)静态方法
 *  ->通过包装类的构造器：Integer i = new Integer(11)
 * 6.String类转换成包装类
 *  ->通过字符串参数：Float f = new Float("12.1f")
 * 
 */
import org.junit.Test;


public class TestWrapper {

    //基本数据类型和包装类之间的转换
    @Test//单元测试
    public void test1(){
        int i = 10;//基本数据类型
        float f = 10.1f;
        Integer i1 = new Integer(i);//包装类
        Float f1 = new Float(f);
        String str = "123";//字符串
        //1.基本数据类型转成包装类（装箱）：
        Float f2 = new Float(1.0);//参数可以是包装类对应的基本数据类型
        Float f3 = new Float("1.0");//也可以是字符串类型，但其实体（其值）必须是对应的基本数据类型
        System.out.println("基本数据类型转成包装类："+f2);
        System.out.println("基本数据类型转成包装类："+f3);
        //2.基本数据类型转换成String类
        String str1 = String.valueOf(f);
        String str2 = f+" ";
        System.out.println("基本数据类型转换成String:"+str1);
        System.out.println("基本数据类型转换成String:"+str2);
        //3.包装类转换成基本数据类型（拆箱）：
        int i2 = i1.intValue();
        int i3 = i1;//自动拆箱
        System.out.println("包装类转换成基本数据类型"+i2);
        System.out.println("包装类转换成基本数据类型"+i3);
        //4.包装类转换成String类
        String str3 = f1.toString();
        String str4 = Float.toString(f1);
        System.out.println("包装类转换成String类"+str3);
        System.out.println("包装类转换成String类"+str4);
        //5.String类转换成基本数据类型
        int i4 = Integer.parseInt(str);
        int i5 = Integer.valueOf(str);
        System.out.println("String类转换成基本数据类型"+i4);
        System.out.println("String类转换成基本数据类型"+i5);
        //6.String类转换成包装类
        Integer i6 = new Integer(str);
        System.out.println("String类转换成包装类"+i6);
    }

}
```

### 6.2 与字符数组间转换

* String --> char[]：调用String的toCharArray()
* char[] --> String：调用String的构造器

```java
@Test
public void test2(){
    String str1 = "abc123";  //题目： a21cb3

    //1.String --> char[]
    char[] charArray = str1.toCharArray();
    
    //2.char[] --> String
    char[] arr = new char[]{'h','e','l','l','o'};
    String str2 = new String(arr);
    System.out.println(str2);
}
```

### 6.3 与StringBuffer、StringBuilder之间的转换

* String --> StringBuffer、StringBuilder：调用StringBuffer、StringBuilder构造器
* StringBuffer、StringBuilder -->String：1）调用String构造器；2）StringBuffer、StringBuilder的toString()

## 参考文章

* [深入理解Java String类](https://blog.csdn.net/ifwinds/article/details/80849184)
* [JDK8的JVM内存结构，元空间替代永久代成为方法区及常量池的变化](https://www.cnblogs.com/shen-qian/p/11277085.html)
* [基本数据类型、包装类、String之间的转换](https://www.cnblogs.com/tengpengfei/p/10454038.html)