## 1. spring概述

### 1.1 Spring是什么

**Spring是分层的 Java SE/EE应用 full-stack 轻量级开源框架，以 IoC（Inverse Of Control：反转控制）和 AOP（Aspect Oriented Programming：面向切面编程）为内核。**

提供了展现层 SpringMVC和持久层 Spring JDBCTemplate以及业务层事务管理等众多的企业级应用技术，还能整合开源世界众多著名的第三方框架和类库，逐渐成为使用最多的Java EE 企业应用开源框架。

官网 : http://spring.io/

Spring中文文档地址：https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference

官方下载地址 : https://repo.spring.io/libs-release-local/org/springframework/spring/

GitHub : https://github.com/spring-projects

### 1.2 Spring的优势

* Spring是一个开源免费的框架，容器 。
* Spring是一个轻量级的框架，非侵入式的。
* **控制反转 IoC，面向切面 Aop**
* 对事物的支持，对框架的支持

一句话概括：

**Spring是一个轻量级的控制反转(IoC)和面向切面(AOP)的容器（框架）。**

### 1.3 Spring的体系结构

![](E:\Study-Notes\SSM框架\Spring\img\图片29.png)

Spring 框架是一个分层架构，由 7 个定义良好的模块组成。Spring 模块构建在核心容器之上，核心容器定义了创建、配置和管理 bean 的方式 。

![spring七大模块](E:\Study-Notes\SSM框架\Spring\img\spring七大模块.jpg)

组成 Spring 框架的每个模块（或组件）都可以单独存在，或者与其他一个或多个模块联合实现。每个模块的功能如下：

- **核心容器**：核心容器提供 Spring 框架的基本功能。核心容器的主要组件是 BeanFactory，它是工厂模式的实现。BeanFactory 使用*控制反转*（IOC） 模式将应用程序的配置和依赖性规范与实际的应用程序代码分开。
- **Spring Context上下文**：Spring 上下文是一个配置文件，向 Spring 框架提供上下文信息。Spring 上下文包括企业服务，例如 JNDI、EJB、电子邮件、国际化、校验和调度功能。
- **Spring AOP**：通过配置管理特性，Spring AOP 模块直接将面向切面的编程功能，集成到了 Spring 框架中。所以，可以很容易地使 Spring 框架管理任何支持 AOP的对象。Spring AOP 模块为基于 Spring 的应用程序中的对象提供了事务管理服务。通过使用 Spring AOP，不用依赖组件，就可以将声明性事务管理集成到应用程序中。
- **Spring DAO**：JDBC DAO 抽象层提供了有意义的异常层次结构，可用该结构来管理异常处理和不同数据库供应商抛出的错误消息。异常层次结构简化了错误处理，并且极大地降低了需要编写的异常代码数量（例如打开和关闭连接）。Spring DAO 的面向 JDBC 的异常遵从通用的 DAO 异常层次结构。
- **Spring ORM**：Spring 框架插入了若干个 ORM 框架，从而提供了 ORM 的对象关系工具，其中包括 JDO、Hibernate 和 iBatis SQL Map。所有这些都遵从 Spring 的通用事务和 DAO 异常层次结构。
- **Spring Web 模块**：Web 上下文模块建立在应用程序上下文模块之上，为基于 Web 的应用程序提供了上下文。所以，Spring 框架支持与 Jakarta Struts 的集成。Web 模块还简化了处理多部分请求以及将请求参数绑定到域对象的工作。
- **Spring MVC 框架**：MVC 框架是一个全功能的构建 Web 应用程序的 MVC 实现。通过策略接口，MVC 框架变成为高度可配置的，MVC 容纳了大量视图技术，其中包括 JSP、Velocity、Tiles、iText 和 POI。

### 1.4 扩展

**Spring Boot与Spring Cloud**

- Spring Boot 是 Spring 的一套快速配置脚手架，可以基于Spring Boot 快速开发单个微服务;
- Spring Cloud是基于Spring Boot实现的；
- Spring Boot专注于快速、方便集成的单个微服务个体，Spring Cloud关注全局的服务治理框架；
- Spring Boot使用了约束优于配置的理念，很多集成方案已经帮你选择好了，能不配置就不配置，Spring Cloud很大的一部分是基于Spring Boot来实现，Spring Boot可以离开Spring Cloud独立使用开发项目，但是Spring Cloud离不开Spring Boot，属于依赖的关系。
- SpringBoot在SpringClound中起到了承上启下的作用，如果你要学习SpringCloud必须要学习SpringBoot。

## 2 IoC理论推导

### 2.1 IoC基础

新建一个空白的maven项目，尝试在Service业务层调用DAO层的方法，我们先用我们原来的方式写一段代码。

1、先写一个UserDao接口及其实现类（DAO层）

```java
public interface UserDao {
    public void getUser();
}

public class UserDaoImpl implements UserDao {
    @Override
    public void getUser() {
        System.out.println("获取用户数据");
    }
}
```

2、然后去写UserService的接口及其实现类

```java
public interface UserService {
    public void getUser();
}

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    @Override
    public void getUser() {
        userDao.getUser();
    }
}
```

3、测试一下

```java
@Test
public void test(){
    //用户实际调用的是业务层，dao层他们不需要接触
    UserService service = new UserServiceImpl();
    service.getUser();
}
```

这是我们原来的方式，开始大家也都是这么去写的对吧。那我们现在修改一下，把Userdao的实现类增加一个。

```java
public class UserDaoMySqlImpl implements UserDao {
    @Override
    public void getUser() {
        System.out.println("MySql获取用户数据");
    }
}
```

紧接着我们要去使用MySql的话，我们就需要去service实现类里面修改对应的实现

```
public class UserServiceImpl implements UserService {
   private UserDao userDao = new UserDaoMySqlImpl();

   @Override
   public void getUser() {
       userDao.getUser();
  }
}
```

在假设, 我们再增加一个Userdao的实现类。

```
public class UserDaoOracleImpl implements UserDao {
   @Override
   public void getUser() {
       System.out.println("Oracle获取用户数据");
  }
}
```

那么我们要使用Oracle，又需要去service实现类里面修改对应的实现。假设我们的这种需求非常大，这种方式就根本不适用了, 甚至反人类对吧，每次变动，都需要修改大量代码。这种设计的耦合性太高了，牵一发而动全身。

**那我们如何去解决呢 ?** 

我们可以在需要用到他的地方不去实现它，而是留出一个接口，利用set，我们去代码里修改下。

```java
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    // 利用set实现
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void getUser() {
        userDao.getUser();
    }
}
```

现在去我们的测试类里，进行测试 ;

```java
@Test
public void test(){
    UserServiceImpl service = new UserServiceImpl();
    service.setUserDao( new UserDaoMySqlImpl() );
    service.getUser();
    //那我们现在又想用Oracle去实现呢
    service.setUserDao( new UserDaoOracleImpl() );
    service.getUser();
}
```

大家发现了区别没有 ? 可能很多人说没啥区别。但是其实他们已经发生了根本性的变化，很多地方都不一样了。仔细去思考一下，以前所有东西都是由程序去进行控制创建，而现在是由我们自行控制创建对象，把主动权交给了调用者。程序不用去管怎么创建,怎么实现了。它只负责提供一个接口。

这种思想，从本质上解决了问题，我们程序员不再去管理对象的创建了，更多的去关注业务的实现。耦合性大大降低。这也就是IOC的原型 !

### 2.2 IoC本质

**控制反转IoC(Inversion of Control)，是一种设计思想，DI(依赖注入)是实现IoC的一种方法**，也有人认为DI只是IoC的另一种说法。没有IoC的程序中，我们使用面向对象编程，对象的创建与对象间的依赖关系完全硬编码在程序中，对象的创建由程序自己控制，控制反转后将对象的创建转移给第三方，个人认为所谓控制反转就是：获得依赖对象的方式反转了。



![图片](https://mmbiz.qpic.cn/mmbiz_png/S1VArPjWIfDntnQwPXxFloxPibue2iajINBXkGxT1BOcyaibDrQya4njcXbYjFS0YuG0EThKPnG0vRfcvUWHgKKew/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

![图片](https://mmbiz.qpic.cn/mmbiz_png/S1VArPjWIfDntnQwPXxFloxPibue2iajINXgcnPhm8TC5KQ42VHQxibt2VxupEKNjmQBKCJQicXHEkT1HBLK4oibW8Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**IoC是Spring框架的核心内容**，使用多种方式完美的实现了IoC，可以使用XML配置，也可以使用注解，新版本的Spring也可以零配置实现IoC。

Spring容器在初始化时先读取配置文件，根据配置文件或元数据创建与组织对象存入容器中，程序使用时再从Ioc容器中取出需要的对象。

![SpringIoC](E:\Study-Notes\SSM框架\Spring\img\SpringIoC.png)

采用XML方式配置Bean的时候，Bean的定义信息是和实现分离的，而采用注解的方式可以把两者合为一体，Bean的定义信息直接以注解的形式定义在实现类中，从而达到了零配置的目的。

**控制反转是一种通过描述（XML或注解）并通过第三方去生产或获取特定对象的方式。在Spring中实现控制反转的是IoC容器，其实现方法是依赖注入（Dependency Injection,DI）。**

## 3 Spring快速入门

### 3.1 Spring程序步骤

> Spring程序开发步骤为:
> 1. 导入 Spring 开发的基本包坐标
> 2. 编写 Dao 接口和实现类
> 3. 创建 Spring 核心配置文件
> 4. 在 Spring 配置文件中配置 UserDaoImpl
> 5. 使用 Spring 的 API 获得 Bean 实例

这里我们通过编写一个Hello实体类来感受spring程序的过程：

1、导入 Spring 开发的基本包坐标

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.1.10.RELEASE</version>
</dependency>
```

2、编写一个Hello实体类

```java
public class Hello {
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void show(){
        System.out.println("Hello,"+ name );
    }
}
```

2、编写我们的 spring 文件，这里我们命名为beans.xml（最标准的是命名为：applicationContext.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--bean就是java对象，由Spring创建和管理-->
    <bean id="hello" class="com.jchen.pojo.Hello">
        <property name="name" value="Spring"/>
    </bean>

</beans>
```

3、我们可以去进行测试了

```java
@Test
public void test(){
    //解析beans.xml文件，生成管理相应的Bean对象
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    //getBean : 参数即为spring配置文件中bean的id。
    Hello hello = (Hello) context.getBean("hello");
    hello.show();
}
```

### 3.2 问题思考

**Hello 对象是谁创建的 ?**  

答：hello 对象是由Spring创建的



**Hello 对象的属性是怎么设置的 ?** 

答：hello 对象的属性是由Spring容器设置的。这个过程就叫控制反转 :

- 控制 : 谁来控制对象的创建，传统应用程序的对象是由程序本身控制创建的，使用Spring后，对象是由Spring来创建的
- 反转 : 程序本身不创建对象，而变成被动的接收对象。

依赖注入 : 就是利用set方法来进行注入的.

 **IOC是一种编程思想，由主动的编程变成被动的接收**

可以通过`newClassPathXmlApplicationContext`去浏览一下底层源码。



我们在案例一中， 新增一个Spring配置文件beans.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="MysqlImpl" class="com.jchen.dao.impl.UserDaoMySqlImpl"/>
    <bean id="OracleImpl" class="com.jchen.dao.impl.UserDaoOracleImpl"/>

    <bean id="ServiceImpl" class="com.kuang.service.impl.UserServiceImpl">
        <!--注意: 这里的name并不是属性，而是set方法后面的那部分，首字母小写-->
        <!--引用另外一个bean，不是用value 3而是用 ref-->
        <property name="userDao" ref="OracleImpl"/>
    </bean>

</beans>
```

测试！

```java
@Test
public void test2(){
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    UserServiceImpl serviceImpl = (UserServiceImpl) context.getBean("ServiceImpl");
    serviceImpl.getUser();
}
```

OK，到了现在，我们彻底不用再程序中去改动了，要实现不同的操作，只需要在xml配置文件中进行修改，所谓的IoC，一句话搞定 : 对象由Spring 来创建，管理，装配 ! 



### 2.3 IoC对象创建方式

**通过无参构造方法来创建：**

1、User.java

```java
public class User {

    private String name;

    public User() {
        System.out.println("user无参构造方法");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void show(){
        System.out.println("name="+ name );
    }

}
```

2、beans.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="user" class="com.jchen.pojo.User">
        <property name="name" value="jchen"/>
    </bean>

</beans>
```

3、测试类

```java
@Test
public void test(){
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    //在执行getBean的时候, user已经创建好了，通过无参构造
    User user = (User) context.getBean("user");
    //调用对象的方法。
    user.show();
}
```

结果可以发现，在spring核心配置文件被加载时，User对象已经通过无参构造初始化了！



**通过有参构造方法来创建：**

1、创建一个UserT.java

```java
public class UserT {

    private String name;

    public UserT(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void show(){
        System.out.println("name="+ name );
    }

}
```

2、beans.xml 有三种方式编写

```xml
<!-- 第一种根据index参数下标设置 -->
<bean id="userT" class="com.jchen.pojo.UserT">
    <!-- index指构造方法，下标从0开始 -->
    <constructor-arg index="0" value="jchen"/>
</bean>
<!-- 第二种根据参数名字设置 -->
<bean id="userT" class="com.jchen.pojo.UserT">
    <!-- name指参数名 -->
    <constructor-arg name="name" value="jchen"/>
</bean>
<!-- 第三种根据参数类型设置 (不推荐使用)-->
<bean id="userT" class="com.jchen.pojo.UserT">
    <constructor-arg type="java.lang.String" value="jchen"/>
</bean>
```

3、测试

```java
@Test
public void testT(){
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    UserT user = (UserT) context.getBean("userT");
    user.show();
}
```

结论：在配置文件加载的时候。其中管理的对象都已经初始化了！

### 2.4 Spring配置

* **别名**

alias 设置别名，为bean设置别名，可以设置多个别名

```xml
<!--设置别名：在获取Bean的时候可以使用别名获取-->
<alias name="userT" alias="userNew"/>
```

* **Bean的配置**

```xml
<!--bean就是java对象,由Spring创建和管理-->

<!--
   id 是bean的标识符,要唯一,如果没有配置id,name就是默认标识符
   如果配置id,又配置了name,那么name是别名
   name可以设置多个别名,可以用逗号,分号,空格隔开
   如果不配置id和name,可以根据applicationContext.getBean(.class)获取对象;

	class是bean的全限定名=包名+类名
-->
<bean id="hello" name="hello2 h2,h3;h4" class="com.kuang.pojo.Hello">
    <property name="name" value="Spring"/>
</bean>
```

* **import**

团队的合作通过import来实现。

```xml
<import resource="{path}/beans.xml"/>
<import resource="{path}/beans2.xml"/>
<import resource="{path}/beans3.xml"/>
```

## 4 依赖注入DI

### 4.1 概念

依赖注入（Dependency Injection）：它是 Spring 框架核心 IOC 的具体实现。

在编写程序时，通过控制反转，把对象的创建交给了 Spring，但是代码中不可能出现没有依赖的情况。

IOC 解耦只是降低他们的依赖关系，但不会消除。例如：业务层仍会调用持久层的方法。那这种业务层和持久层的依赖关系，在使用 Spring 之后，就让 Spring 来维护了。简单的说，就是坐等框架把持久层对象传入业务层，而不用我们自己去获取

- 依赖注入（Dependency Injection,DI）。
- 依赖 : 指Bean对象的创建依赖于容器，Bean对象的依赖资源。
- 注入 : 指Bean对象所依赖的资源，由容器来设置和装配 。

### 4.2 构造器注入

正如我们在之前的案例已经讲过的：

```xml
<!-- 第一种根据index参数下标设置 -->
<bean id="userT" class="com.jchen.pojo.UserT">
    <!-- index指构造方法，下标从0开始 -->
    <constructor-arg index="0" value="jchen"/>
</bean>
<!-- 第二种根据参数名字设置 -->
<bean id="userT" class="com.jchen.pojo.UserT">
    <!-- name指参数名 -->
    <constructor-arg name="name" value="jchen"/>
</bean>
<!-- 第三种根据参数类型设置 (不推荐使用)-->
<bean id="userT" class="com.jchen.pojo.UserT">
    <constructor-arg type="java.lang.String" value="jchen"/>
</bean>
```

### 4.3 Set 注入 （重点）

要求被注入的属性，必须有set方法，set方法的方法名由set + 属性首字母大写，如果属性是boolean类型，没有set方法，是 is。

注入数据的三种数据类型 ：

* 普通数据类型
* 引用数据类型
* 集合数据类型

1、编写pojo类 : Address和Student类

```java
public class Address {
    private String address;
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
```

```java
package com.jchen.pojo;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Student {

    private String name;
    private Address address;
    private String[] books;
    private List<String> hobbys;
    private Map<String,String> card;
    private Set<String> games;
    private String wife;
    private Properties info;

    //....
    //各个属性的get和set方法
    //show()或toString()方法
}
```

2、配置spring文件：

```xml
<bean id="address" class="com.jchen.pojo.Address">
    <property name="address" value="武汉"/>
</bean>

<bean id="student" class="com.jchen.pojo.Student">
    <!--第一种，普通值注入，value-->
    <property name="name" value="jchen"/>

    <!--第二种，Bean注入，ref-->
    <property name="address" ref="address"/>

    <!--数组注入-->
    <property name="books">
        <array>
            <value>红楼梦</value>
            <value>西游记</value>
            <value>水浒传</value>
            <value>三国演义</value>
        </array>
    </property>

    <!--List注入-->
    <property name="hobbies">
        <list>
            <value>听歌</value>
            <value>敲代码</value>
            <value>看电影</value>
        </list>
    </property>

    <!--Map注入-->
    <property name="card">
        <map>
            <entry key="身份证" value="12345648943138"/>
            <entry key="银行卡" value="5467452345453"/>
        </map>
    </property>

    <!--Set注入-->
    <property name="games">
        <set>
            <value>LOL</value>
            <value>COC</value>
            <value>BOB</value>
        </set>
    </property>
    
    <!--null注入-->
    <property name="wife">
        <null/>
    </property>

    <!--properties注入-->
    <property name="info">
        <props>
            <prop key="driver">20190525</prop>
            <prop key="url">男</prop>
            <prop key="username">小明</prop>
            <prop key="password">123456</prop>
        </props>
    </property>
</bean>
```

3、编写测试类：

```java
public class MyTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Student student = (Student) context.getBean("student");
        student.show();
    }
}
```

测试结果：

![image-20201213183402336](C:\Users\jchen\AppData\Roaming\Typora\typora-user-images\image-20201213183402336.png)

### 4.4 p命名和c命名注入

User.java ：【注意：这里没有有参构造器！】

```java
public class User {
    private String name;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }
}
```

1、P命名空间注入 : 需要在头文件中加入约束文件

```xml
<!--导入约束--> : xmlns:p="http://www.springframework.org/schema/p"

<!--P(属性: properties)命名空间，属性依然要设置set方法-->
<bean id="user" class="com.jchen.pojo.User" p:name="陈建君" p:age="18"/>
```

2、c 命名空间注入 : 需要在头文件中加入约束文件

```xml
<!--导入约束--> : xmlns:c="http://www.springframework.org/schema/c"
<!--C(构造: Constructor)命名空间，属性依然要设置set方法-->
<bean id="user" class="com.jchen.pojo.User" c:name="陈建君" c:age="18"/>
```

发现问题：爆红了，刚才我们没有写有参构造！

解决：把有参构造器加上，这里也能知道，c 就是所谓的构造器注入！

## 5 Bean作用域

| Scope                                                        | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [singleton](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/core.html#beans-factory-scopes-singleton) | (默认)将每个 Spring IoC 容器的单个 bean 定义范围限定为单个对象实例。 |
| [prototype](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/core.html#beans-factory-scopes-prototype) | 将单个 bean 定义的作用域限定为任意数量的对象实例。即，每次调用getBean()时，相当于执行new XxxBean()。 |
| [request](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/core.html#beans-factory-scopes-request) | 将单个 bean 定义的范围限定为单个 HTTP 请求的生命周期。也就是说，每个 HTTP 请求都有一个在单个 bean 定义后面创建的 bean 实例。仅在可感知网络的 Spring `ApplicationContext`中有效。 |
| [session](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/core.html#beans-factory-scopes-session) | 将单个 bean 定义的范围限定为 HTTP `Session`的生命周期。仅在可感知网络的 Spring `ApplicationContext`上下文中有效。 |
| [application](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/core.html#beans-factory-scopes-application) | 将单个 bean 定义的范围限定为`ServletContext`的生命周期。仅在可感知网络的 Spring `ApplicationContext`上下文中有效。 |
| [websocket](https://www.docs4dev.com/docs/zh/spring-framework/5.1.3.RELEASE/reference/web.html#websocket-stomp-websocket-scope) | 将单个 bean 定义的范围限定为`WebSocket`的生命周期。仅在可感知网络的 Spring `ApplicationContext`上下文中有效。 |

几种作用域中，request、session作用域仅在基于web的应用中使用（不必关心你所采用的是什么web应用框架），只能用在基于web的Spring ApplicationContext环境。

###  4.1 Singleton单例范围

当一个bean的作用域为Singleton，那么Spring IoC容器中只会存在一个共享的bean实例，并且所有对bean的请求，只要id与该bean定义相匹配，则只会返回bean的同一实例。**Singleton是单例类型，就是在创建起容器时就同时自动创建了一个bean的对象，不管你是否使用，他都存在了，每次获取到的对象都是同一个对象**。

![singleton](https://www.docs4dev.com/images/spring-framework/5.1.3.RELEASE/singleton.png)

注意，Singleton作用域是Spring中的缺省作用域。如果要在XML中将bean定义成singleton，可以这样配置：

```xml
<bean id="ServiceImpl" class="cn.csdn.service.ServiceImpl" scope="singleton">
```

测试：

```java
@Test
public void test03(){
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    User user = (User) context.getBean("user");
    User user2 = (User) context.getBean("user");
    System.out.println(user==user2);
    //结果返回true，创建的为同一对象
}
```

### 4.2 Prototype原型范围

当一个bean的作用域为Prototype，表示一个bean定义对应多个对象实例。Prototype作用域的bean会导致在每次对该bean请求（将其注入到另一个bean中，或者以程序的方式调用容器的getBean()方法）时都会创建一个新的bean实例。**Prototype是原型类型，它在我们创建容器的时候并没有实例化，而是当我们获取bean的时候才会去创建一个对象，而且我们每次获取到的对象都不是同一个对象**。

![prototype](https://www.docs4dev.com/images/spring-framework/5.1.3.RELEASE/prototype.png)

根据经验，对有状态的bean应该使用prototype作用域，而对无状态的bean则应该使用singleton作用域。在XML中将bean定义成prototype，可以这样配置：

```xml
<bean id="account" class="com.foo.DefaultAccount" scope="prototype"/>  
或者
<bean id="account" class="com.foo.DefaultAccount" singleton="false"/>
```

### 4.3 Request

当一个bean的作用域为Request，表示在一次HTTP请求中，一个bean定义对应一个实例；即每个HTTP请求都会有各自的bean实例，它们依据某个bean定义创建而成。该作用域仅在基于web的Spring ApplicationContext情形下有效。考虑下面bean定义：

```xml
<bean id="loginAction" class=cn.csdn.LoginAction" scope="request"/>
```

针对每次HTTP请求，Spring容器会根据loginAction bean的定义创建一个全新的LoginAction bean实例，且该loginAction bean实例仅在当前HTTP request内有效，因此可以根据需要放心的更改所建实例的内部状态，而其他请求中根据loginAction bean定义创建的实例，将不会看到这些特定于某个请求的状态变化。当处理请求结束，request作用域的bean实例将被销毁。

### 4.4 Session

当一个bean的作用域为Session，表示在一个HTTP Session中，一个bean定义对应一个实例。该作用域仅在基于web的Spring ApplicationContext情形下有效。考虑下面bean定义：

```xml
<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>
```

针对某个HTTP Session，Spring容器会根据userPreferences bean定义创建一个全新的userPreferences bean实例，且该userPreferences bean仅在当前HTTP Session内有效。与request作用域一样，可以根据需要放心的更改所创建实例的内部状态，而别的HTTP Session中根据userPreferences创建的实例，将不会看到这些特定于某个HTTP Session的状态变化。当HTTP Session最终被废弃的时候，在该HTTP Session作用域内的bean也会被废弃掉。

