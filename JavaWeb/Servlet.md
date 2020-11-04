## 1 Servlet技术

### 1.1 Servlet介绍

1. Servlet时JavaEE规范（接口）之一
2. Servlet是JavaWeb三大组件之一，三大组件分别是Servlet，Filter过滤器和Listener过滤器
3. Servlet是运行在服务器上的一个Java程序，**可以接受客户端发来的请求，并响应数据给客户端**

### 1.2 手动实现Servlet程序

1. 编写一个类实现Servlet接口
2. 实现service方法处理请求并响应数据
3. 在WEB-INF文件夹中的web.xml文件中配置Servlet程序的访问地址

HelloServlet程序代码：

```java
package com.atguigu.servlet;

import javax.servlet.*;
import java.io.IOException;

public class HelloServlet implements Servlet {
    /**
     * service 方法是专门用来处理请求和响应的
     * @param servletRequest
     * @param servletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("Hello Servlet 被访问了");
    }
```

web.xml中的配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- servlet标签给Tomcat配置Servlet程序 -->
    <servlet>
        <!-- servlet-name标签Servlet程序起一个别名（一般是类名） -->
        <servlet-name>HelloServlet</servlet-name>
        <!--servlet-class是Servlet程序的全类名-->
        <servlet-class>com.atguigu.servlet.HelloServlet</servlet-class>
    </servlet>

    <!--servlet-mapping标签给servlet程序配置访问地址-->
    <servlet-mapping>
        <!--servlet-name标签的作用是告诉服务器，我当前配置的地址给那个servlet程序使用-->
        <servlet-name>HelloServlet</servlet-name>
        <!--
            url-pattern标签配置访问地址                                     <br/>
               / 斜杠在服务器解析的时候，表示地址为：http://ip:port/工程路径          <br/>
               /hello 表示地址为：http://ip:port/工程路径/hello              <br/>
        -->
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>

</web-app>
```

运行结果： 点击绿色按钮开启Tomcat服务器之后，会自动打开默认的地址http://localhost:8080/06_servlet， 在地址栏继续输入`/hello`，会执行指定类的service方法，控制台输出：Hello Servlet 被访问了

### 1.3 常见错误

1. url-pattern中配置的路径没有以斜杠打头
   ![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201102191949.png)

2. servlet-name中的两个映射值不一致
   ![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201102192129.png)

3. servlet-class标签的全类名配置错误

   ![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201102192212.png)

### 1.4 Servlet程序的访问原理

![Servlet程序访问原理](https://gitee.com/jchenTech/images/raw/master/img/20201102192419.jpg)

### 1.5 Servlet的声明周期

Servlet程序被访问以后按以下顺序执行：

1. 执行Servlet程序的构造方法
2. 执行init方法
3. 执行service方法
4. 执行destroy方法

其中1和2是在初次访问并创建Servlet程序时会执行(每次启动服务只执行一次)，第3步每次刷新 (访问)都会执行，第4步点击停止时会执行一次

### 1.6 GET和POST请求的不同处理

在HelloServlet类中的service方法：

```java
public class HelloServlet implements Servlet {
    /**
     * service 方法是专门用来处理请求和响应的
     * @param servletRequest
     * @param servletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("3 service === Hello Servlet 被访问了");
        //类型转换（因为他有getMethod()方法）
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String method = httpServletRequest.getMethod();

        if ("GET".equals(method)) {
            doGet();
        } else if ("POST".equals(method)) {
            doPost();
        }
    }

    /*
     * 做get请求的操作
     */
    public void doGet() {
        System.out.println("get请求");
        System.out.println("get请求");
    }

    /*
     * 做post请求的操作
     */
    public void doPost() {
        System.out.println("post请求");
        System.out.println("post请求");
    }
}
```

web目录下创建a.html页面：

```html
<body>
    <form action="http://localhost:8080/06_servlet/hello" method="post">
        <input type="submit">
    </form>
</body>
```

运行结果：服务器启动之后，在浏览器的地址栏中的后缀加上a.html，即可访问此页面，点击提交标签，即可跳转到http://localhost:8080/06_servlet/a.html，执行service方法，控制台输出：POST方式

### 1.7 继承HttpServlet类实现Servlet程序

在实际的项目开发中，都是使用继承HttpServlet类实现Servlet程序的方式，步骤如下：

1. 编写一个类继承HttpServlet类
2. 根据需求重写doGet或doPost方法，由service方法根据表单的method属性值调用二者之一
3. 到web.xml中配置Servlet程序的访问地址

Servlet程序的代码：

```java
public class HelloServlet2 extends HttpServlet {
    /*
     * doGet() 在get请求的时候调用
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HelloServlet2的doGet方法");
    }

    /*
     * doPost() 在post请求的时候调用
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("HelloServlet2的doPost方法");
    }
}

```

web.xml中的代码：

```xml
<servlet>
    <servlet-name>HelloServlet2</servlet-name>
    <servlet-class>com.atguigu.servlet.HelloServlet2</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>HelloServlet2</servlet-name>
    <url-pattern>/hello2</url-pattern>
</servlet-mapping>
```

### 1.8 使用IDEA创建Servlet程序

<img src="https://gitee.com/jchenTech/images/raw/master/img/20201102215143.png" alt="image-20201102215142908" style="zoom: 67%;" />

![image-20201102215238055](https://gitee.com/jchenTech/images/raw/master/img/20201102215238.png)

创建之后，会在包下创建此类(类名为全类名中的类名)，此类继承于HttpServlet类，其中有doGet和doPost方法(无函数体)，并自动的在web.xml 文件中补充新的servlet标签，但无servlet-mapping标签，需自己补充。

### 1.9 Servlet接口的继承体系

![继承体系](https://gitee.com/jchenTech/images/raw/master/img/20201102215745.jpg)

​                           

## 2 ServletConfig类

从名字来看，得知此接口中是Servlet程序的配置信息：

1. Servlet程序和ServletConfig对象都是由Tomcat负责创建，编程人员负责使用

2. Servlet程序默认是第一次访问时创建，ServletConfig是每个Servlet程序创建时就创建一个ServletConfig对象，二者相互对应，某个Servlet程序只可以获得他对应的ServletConfig对象，无法获得别的 Servlet程序的ServletConfig对象



ServletConfig类的三大作用：

1. 可以获取Servlet程序的别名servlet-name(即web.xml的内容)
2. 可以获取web.xml的初始化参数的值
3. 可以获取ServletContext对象

servlet程序：

```java
@Override
public void init(ServletConfig servletConfig) throws ServletException {
    System.out.println("2 init初始化");

    //ServletConfig类三大好处
    //1. 可以获取Servlet程序的别名servlet-name(即web.xml的内容)
    System.out.println("HelloServlet程序的别名为" + servletConfig.getServletName());
    //2. 可以获取web.xml的初始化参数的值
    System.out.println("初始化参数username的值是" + servletConfig.getInitParameter("username"));
    System.out.println("初始化参数url的值是" + servletConfig.getInitParameter("url"));
    //3. 可以获取ServletContext对象
    System.out.println(servletConfig.getServletContext());

}
```

web.xml中的配置：

```xml
<!-- servlet标签给Tomcat配置Servlet程序 -->
    <servlet>
        <!-- servlet-name标签Servlet程序起一个别名（一般是类名） -->
        <servlet-name>HelloServlet</servlet-name>
        <!--servlet-class是Servlet程序的全类名-->
        <servlet-class>com.atguigu.servlet.HelloServlet</servlet-class>
        <!--init-parm是初始化参数-->
        <init-param>
            <!--是参数名-->
            <param-name>username</param-name>
            <!--是参数值-->
            <param-value>root</param-value>
        </init-param>
        <init-param>
            <!--是参数名-->
            <param-name>url</param-name>
            <!--是参数值-->
            <param-value>jdbc:mysql://localhost:3306/test</param-value>
        </init-param>
    </servlet>
```

**注意：重写init方法时，必须要在函数体内写：`super.init(config);`**
**原因：父类GenericServlet中的init方法将参数config保存起来，子类若不调用则无法保存**

![image-20201102223847571](https://gitee.com/jchenTech/images/raw/master/img/20201102223847.png)

## 3 ServletContext类

### 3.1 什么是ServletContext

1. ServletContext是一个接口，表示Servlet上下文对象
2. 一个web工程只有一个ServletContext对象实例
3. ServletContext是在web工程启动时创建，在web工程停止时销毁
4. ServletContext对象是一个域对象
   域对象：像Map一样存取数据的对象称为域对象，域指的是存取数据的操作范围，**ServletContext的域是整个web工程**

|        | 存数据         | 取数据         | 删除数据          |
| ------ | -------------- | -------------- | ----------------- |
| Map    | put()          | get()          | remove()          |
| 域对象 | setAttribute() | getAttribute() | removeAttribute() |

### 3.2 ServletContext接口的四个作用：

1. 获取web.xml中配置的上下文参数标签中的值context-param
2. 获取当前工程的路径，格式：/工程路径，也就是Edit Configurations中Deployment中的 Application context的内容(即地址中8080之后，具体的打开的页面之前的内容)
3.  获取工程部署后在硬盘上的绝对路径
4. 像Map一样存取数据

ServletContext演示代码：

```java
public class ContextServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        1. 获取web.xml中配置的上下文参数标签中的值context-param
        ServletContext context = getServletConfig().getServletContext();
        String username = context.getInitParameter("username");
        String password = context.getInitParameter("password");
        System.out.println("context-param参数username的值是:" + username);
        System.out.println("context-param参数password的值是：" + password);
//        2. 获取当前工程的路径，格式：/工程路径，也就是Edit Configurations中Deployment中的 Application context的内容(即地址中8080之后，具体的打开的页面之前的内容)
        System.out.println("当前工程路径：" + context.getContextPath());
//        3. 获取工程部署后在硬盘上的绝对路径
        /*
         * /斜杠被服务器解析地址为：http://ip:port/工程名/  映射到idea代码的web目录<br/>
         */
        System.out.println("工程部署的路径：" + context.getRealPath("/"));
        System.out.println("工程下css目录的绝对路径是" + context.getRealPath("/css"));
        System.out.println("工程下imgs目录1.png的绝对路径是" + context.getRealPath("/imgs/1.png"));

    }
}
```

web.xml配置：

```xml
<!--context-param时上下文参数（它属于整个工程）-->
<context-param>
    <param-name>username</param-name>
    <param-value>context</param-value>
</context-param>
<!--context-param时上下文参数（它属于整个工程）-->
<context-param>
    <param-name>password</param-name>
    <param-value>root</param-value>
</context-param>
```



ServletContext 像Map 一样存取数据：

```java
public class ContextServlet1 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取ServletContext对象
        ServletContext context = getServletContext();

        System.out.println("保存之前：Context1获取key1的值是：" + context.getAttribute("key1"));

        context.setAttribute("key1", "value1");
        System.out.println("Context1中获取域数据key1的值是：" + context.getAttribute("key1"));
        System.out.println("Context1中获取域数据key1的值是：" + context.getAttribute("key1"));
        System.out.println("Context1中获取域数据key1的值是：" + context.getAttribute("key1"));
        System.out.println("Context1中获取域数据key1的值是：" + context.getAttribute("key1"));

    }
}
```

ContextServlet2代码：

```java
public class ContextServlet2 extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        System.out.println(context);
        System.out.println("Context2中获取域数据key1的值是：" + context.getAttribute("key1"));
    }
}
```

## 4 HTTP协议

### 4.1 什么是HTTP协议

所谓协议指的是双方或多方相互约定好，都要遵守的规则，而HTTP协议指的是客户端和服务器之间通信时发送的数据需要遵守的规则，HTTP协议中的数据又称为报文。

### 4.2 请求的HTTP协议格式

客户端给服务器发送数据叫请求；

服务器给客户端回传数据叫响应。



请求分为GET请求和POST请求两种：

1. GET请求

   * 请求行：
     * 请求的方式       GET
     * 请求参的资源路径[+?+请求参数]
     * 请求的协议的版本号        HTTP/1.1

   * 请求头：
     key: value 组成不同的键值对，表示不同的含义
     ![HTTP协议](https://gitee.com/jchenTech/images/raw/master/img/20201104150433.jpg)

2. POST请求

   * 请求行：

     * 请求的方式       GET
     * 请求参的资源路径[+?+请求参数]
     * 请求的协议的版本号        HTTP/1.1

   * 请求头：

     * key: value不同的请求头有不同的含义
     * 空行

   * 请求体：
     就是发送给服务器的数据

     ![POST请求HTTP协议](https://gitee.com/jchenTech/images/raw/master/img/20201104151500.jpg)

3. 常用请求头的说明：
   `Accept`: 表示客户端可以接收的数据类型
   `Accpet-Languege`: 表示客户端可以接收的语言类型
   `User-Agent`: 表示客户端浏览器的信息
   `Host`： 表示请求时的服务器ip 和端口号

4. 那些是GET请求，哪些是POST请求
   GET请求：

   * form 标签method=get
   * a 标签
   * link 标签引入css
   * Script 标签引入js 文件
   * img 标签引入图片
   * iframe 引入html 页面
   * 在浏览器地址栏中输入地址后敲回车

   POST请求：

   * form标签 method= post

### 4.3 响应的HTTP协议

1. 响应行
   (1) 响应的协议和版本号
   (2) 响应状态码
   (3) 响应状态描述符
2. 响应头
   (1) key : value 不同的响应头，有其不同含义
   空行
3. 响应体---->>> 就是回传给客户端的数据
   ![响应的HTTP协议](https://gitee.com/jchenTech/images/raw/master/img/20201104153953.jpg)

### 4.4 常见的响应码

* `200` 表示请求成功
* `302` 表示请求重定向
* `404` 表示服务器收到请求，但是请求的数据不存在（请求地址错误）
* `500` 表示服务器收到请求，但是服务器内部错误（代码错误）

### 4.5 MIME类型说明

MIME是HTTP协议中的数据类型，MIME 的英文全称是"Multipurpose Internet Mail Extensions" 多功能Internet 邮件扩充服务。格式是：大类型/小类型，并与某一种文件的扩展名相对应：

![MIME类型说明](https://gitee.com/jchenTech/images/raw/master/img/20201104154747.png)

### 4.6 谷歌浏览器查看HTTP协议

首先点击F12

![image-20201104155157841](https://gitee.com/jchenTech/images/raw/master/img/20201104155158.png)

注意点：

1. 到目前为止除了form标签中method=post之外，其余均为GET请求
2. 标签不一定与标签相邻，只要根据能对应上即可
3. 默认地址值与工程路径是两个概念，上述只是将默认地址值修改为工程路径，即上述斜杠 等代表访问到的是工程路径：http://localhost:8080/工程名，而非默认路径