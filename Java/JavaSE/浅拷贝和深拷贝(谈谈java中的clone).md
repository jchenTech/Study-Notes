> clone顾名思义就是复制， 在Java语言中， clone方法被对象调用，所以会复制对象。所谓的复制对象，首先要分配一个和源对象同样大小的空间，在这个空间中创建一个新的对象。那么在java语言中，有几种方式可以创建对象呢？
> 1. 使用new操作符创建一个对象
> 2. 使用clone方法复制一个对象
> 那么这两种方式有什么相同和不同呢？ new操作符的本意是分配内存。程序执行到new操作符时， 首先去看new操作符后面的类型，因为知道了类型，才能知道要分配多大的内存空间。分配完内存之后，再调用构造函数，填充对象的各个域，这一步叫做对象的初始化，构造方法返回后，一个对象创建完毕，可以把他的引用（地址）发布到外部，在外部就可以使用这个引用操纵这个对象。而clone在第一步是和new相似的， 都是分配内存，调用clone方法时，分配的内存和源对象（即调用clone方法的对象）相同，然后再使用原对象中对应的各个域，填充新对象的域， 填充完成之后，clone方法返回，一个新的相同的对象被创建，同样可以把这个新对象的引用发布到外部。【详解Java中的clone方法】
>

## 1. 引用的拷贝

```java
//引用拷贝
private static void copyReferenceObject(){
    Person p = new Person(23, "zhang");
    Person p1 = p;
    System.out.println(p);
    System.out.println(p1);
}
```

这里打印的结果：
`com.yaolong.clone.Person@3654919e`
`com.yaolong.clone.Person@3654919e`

可以看到，打印的结果是一样的，也就是说，二者的引用是同一个对象，并没有创建出一个新的对象。因此要区分引用拷贝和对象拷贝的区别，下面要介绍的就是对象拷贝。

## 2. 浅拷贝

> 浅拷贝是按位拷贝对象，它会创建一个新对象，这个对象有着原始对象属性值的一份精确拷贝。如果属性是基本类型，拷贝的就是基本类型的值；如果属性是内存地址（引用类型），拷贝的就是内存地址 ，因此如果其中一个对象改变了这个地址，就会影响到另一个对象。

实现对象拷贝的类，必须实现Cloneable接口，并覆写clone()方法。
Persion类：

```java
package com.yaolong.clone;

public class Person implements Cloneable{

    //private Integer age;
    private int age;//阿里规范中规定pojo类中的属性强制使用包装类型，这里只是测试

    private String name;

    public Person(Integer age, String name) {
        super();
        this.age = age;
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

```

 

```java
    //对象拷贝
    private static void copyRealObject() throws CloneNotSupportedException{
        Person p = new Person(23, "zhang");
        Person p1 = (Person) p.clone();      
        System.out.println(p);
        System.out.println(p1);
    }
```

这里打印的结果：
`com.yaolong.clone.Person@28084850`
`com.yaolong.clone.Person@37c390b8`

可以看出，二者的对象地址不一样，因此实现了拷贝。



但是还是有个问题，就是Person类中有一个String类型的引用对象name，它真的也被拷贝过去了吗，还是说依然是引用的是同一个name对象呢，在上面的代码基础上，我们继续打印：

```java
System.out.println("pName："+p.getName().hashCode());
System.out.println("p1Name："+p1.getName().hashCode());
```
这里打印的结果：
`pName：115864556`
`p1Name：115864556`

可见，二者的name属性依然是指向同一个对象。上面故意将age属性改为int基本类型，因为基本数据类型是不存在引用问题。这实际上就是典型的浅拷贝。





由上可知，从Object中继承过来的clone默认实现的是浅拷贝。

## 3. 深拷贝

> 深拷贝会拷贝所有的属性,并拷贝属性指向的动态分配的内存。当对象和它所引用的对象一起拷贝时即发生深拷贝。深拷贝相比于浅拷贝速度较慢并且花销较大。

现在为了要在clone对象时进行深拷贝， 那么就要Clonable接口，覆盖并实现clone方法，除了调用父类中的clone方法得到新的对象， 还要将该类中的引用变量也clone出来。如果只是用Object中默认的clone方法，是浅拷贝的。

```java
static class Body implements Cloneable{
    public Head head;

    public Body() {}

    public Body(Head head) {this.head = head;}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
static class Head /*implements Cloneable*/{
    public  Face face;

    public Head() {}
    public Head(Face face){this.face = face;}

} 
public static void main(String[] args) throws CloneNotSupportedException {

    Body body = new Body(new Head());

    Body body1 = (Body) body.clone();

    System.out.println("body == body1 : " + (body == body1) );

    System.out.println("body.head == body1.head : " +  (body.head == body1.head));


}
```

在以上代码中， 有两个主要的类， 分别为Body和Face， 在Body类中， 组合了一个Face对象。当对Body对象进行clone时， 它组合的Face对象只进行浅拷贝。
打印结果可以验证该结论：
`body == body1 : false`
`body.head == body1.head : true`

如果要使Body对象在clone时进行深拷贝， 那么就要在Body的clone方法中，将源对象引用的Head对象也clone一份。

```java
static class Body implements Cloneable{
    public Head head;
    public Body() {}
    public Body(Head head) {this.head = head;}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Body newBody =  (Body) super.clone();
        newBody.head = (Head) head.clone();
        return newBody;
    }

}
static class Head implements Cloneable{
    public  Face face;

    public Head() {}
    public Head(Face face){this.face = face;}
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
} 
public static void main(String[] args) throws CloneNotSupportedException {

    Body body = new Body(new Head());

    Body body1 = (Body) body.clone();

    System.out.println("body == body1 : " + (body == body1) );

    System.out.println("body.head == body1.head : " +  (body.head == body1.head));


}
```

打印结果为： `body == body1 : false`
`body.head == body1.head : false`

由此可见， body和body1内的head引用指向了不同的Head对象， 也就是说在clone Body对象的同时， 也拷贝了它所引用的Head对象， 进行了深拷贝。



## 4. 真的是深拷贝吗

由上一节的内容可以得出如下结论：如果想要深拷贝一个对象， 这个对象必须要实现Cloneable接口，实现clone方法，并且在clone方法内部，把该对象引用的其他对象也要clone一份 ， 这就要求这个被引用的对象必须也要实现Cloneable接口并且实现clone方法。

那么，按照上面的结论， Body类组合了Head类， 而Head类组合了Face类，要想深拷贝Body类，必须在Body类的clone方法中将Head类也要拷贝一份，但是在拷贝Head类时，默认执行的是浅拷贝，也就是说Head中组合的Face对象并不会被拷贝。验证代码如下：（这里本来只给出Face类的代码就可以了， 但是为了阅读起来具有连贯性，避免丢失上下文信息， 还是给出整个程序，整个程序也非常简短）

```java
static class Body implements Cloneable{
    public Head head;
    public Body() {}
    public Body(Head head) {this.head = head;}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Body newBody =  (Body) super.clone();
        newBody.head = (Head) head.clone();
        return newBody;
    }

}

static class Head implements Cloneable{
    public  Face face;

    public Head() {}
    public Head(Face face){this.face = face;}
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
} 

static class Face{}

public static void main(String[] args) throws CloneNotSupportedException {

    Body body = new Body(new Head(new Face()));

    Body body1 = (Body) body.clone();

    System.out.println("body == body1 : " + (body == body1) );

    System.out.println("body.head == body1.head : " +  (body.head == body1.head));

    System.out.println("body.head.face == body1.head.face : " +  (body.head.face == body1.head.face));


}
```

打印结果为：
`body == body1 : false`
`body.head == body1.head : false`
`body.head.face == body1.head.face : true`


那么，对Body对象来说，算是这算是深拷贝吗？其实应该算是深拷贝，因为对Body对象内所引用的其他对象（目前只有Head）都进行了拷贝，也就是说两个独立的Body对象内的head引用已经指向了独立的两个Head对象。但是，这对于两个Head对象来说，他们指向了同一个Face对象，这就说明，两个Body对象还是有一定的联系，并没有完全的独立。这应该说是一种不彻底的深拷贝。

## 5. 如何进行彻底的深拷贝

对于上面的例子来说，怎样才能保证两个Body对象完全独立呢？只要在拷贝Head对象的时候，也将Face对象拷贝一份就可以了。这需要让Face类也实现Cloneable接口，实现clone方法，并且在在Head对象的clone方法中，拷贝它所引用的Face对象。修改的部分代码如下：

```java
static class Head implements Cloneable{
    public  Face face;

    public Head() {}
    public Head(Face face){this.face = face;}
    @Override
    protected Object clone() throws CloneNotSupportedException {
        //return super.clone();
        Head newHead = (Head) super.clone();
        newHead.face = (Face) this.face.clone();
        return newHead;
    }
} 

static class Face implements Cloneable{
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

再次运行上面的示例，得到的运行结果如下： `body == body1 : false`
`body.head == body1.head : false`
`body.head.face == body1.head.face : false`

这说名两个Body已经完全独立了，他们间接引用的face对象已经被拷贝，也就是引用了独立的Face对象。



依此类推，如果Face对象还引用了其他的对象， 比如说Mouth，如果不经过处理，Body对象拷贝之后还是会通过一级一级的引用，引用到同一个Mouth对象。同理， 如果要让Body在引用链上完全独立， 只能显式的让Mouth对象也被拷贝。 到此，可以得到如下结论：如果在拷贝一个对象时，要想让这个拷贝的对象和源对象完全彼此独立，那么在引用链上的每一级对象都要被显式的拷贝。所以创建彻底的深拷贝是非常麻烦的，尤其是在引用关系非常复杂的情况下， 或者在引用链的某一级上引用了一个第三方的对象， 而这个对象没有实现clone方法， 那么在它之后的所有引用的对象都是被共享的。 举例来说，如果被Head引用的Face类是第三方库中的类，并且没有实现Cloneable接口，那么在Face之后的所有对象都会被拷贝前后的两个Body对象共同引用。假设Face对象内部组合了Mouth对象，并且Mouth对象内部组合了Tooth对象。

clone在平时项目的开发中可能用的不是很频繁，但是区分深拷贝和浅拷贝会让我们对java内存结构和运行方式有更深的了解。至于彻底深拷贝，几乎是不可能实现的，原因已经在上一节中进行了说明。深拷贝和彻底深拷贝，在创建不可变对象时，可能对程序有着微妙的影响，可能会决定我们创建的不可变对象是不是真的不可变。clone的一个重要的应用也是用于不可变对象的创建。

> alibaba的规范手册
>

【强制】关于基本数据类型与包装数据类型的使用标准如下：

1. 所有的 POJO 类属性必须使用包装数据类型。
2.  RPC 方法的返回值和参数必须使用包装数据类型。
3. 所有的局部变量推荐使用基本数据类型。
   说明： POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何NPE 问题，或者入库检查，都由使用者来保证。
   正例： 数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。反例： 某业务的交易报表上显示成交总额涨跌情况，即正负 x%， x 为基本数据类型，调用的RPC 服务，调用不成功时，返回的是默认值，页面显示： 0%，这是不合理的，应该显示成中划线-。所以包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出。

【推荐】慎用 Object 的 clone 方法来拷贝对象。
说明： 对象的 clone 方法默认是浅拷贝，若想实现深拷贝需要重写 clone 方法实现属性对象的拷贝。





