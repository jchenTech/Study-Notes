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

## 5 HttpServletRequest类

### 5.1 作用

每次只要有请求进入Tomcat服务器，Tomcat服务器就会把请求发来的HTTP协议信息解析好封装到Request对象中，然后传递到service方法中(调用doGet或doPost方法)供编程人员使用，编程人员通过HttpServletRequest对象，可以获取到请求的所有信息

### 5.2 HttpServletRequest常用方法

* getRequestURI()：获取请求的资源路径
* getRequestURL()：获取请求的绝对路径
* getRemoteHost()：获取客户端的ip地址
* getHeader()：获取请求头
* getParameter()：获取请求的参数
* getParameterValues()：获取请求的参数(多个值时使用)
* getMethod()：获取请求的方式(GET或POST)
* setAttribute(key, value)：设置域数据
* getAttribute(key)：获取域数据
* getRequestDispatcher()：获取请求转发对象

常用API示例代码：

```java
public class RequestAPIServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        i. getRequestURI() 获取请求的资源路径
        System.out.println("URI = " + req.getRequestURI());
//        ii. getRequestURL() 获取请求的统一资源定位符（绝对路径）
        System.out.println("URL = " + req.getRequestURL());
//        iii. getRemoteHost() 获取客户端的ip 地址
        /*
         * 在IDEA中使用localhost访问时，得到的客户端ip地址是 127.0.0.1
         * 在IDEA中使用127.0.0.1访问时，得到的客户端ip地址是 127.0.0.1
         * 在IDEA中使用真实ip访问时，得到的客户端ip地址是真实的ip地址
         */
        System.out.println("ip = " + req.getRemoteHost());
//        iv. getHeader() 获取请求头
        System.out.println("请求头Uer-Agent = " + req.getHeader("User-Agent"));
//        vii. getMethod() 获取请求的方式GET 或POST
        System.out.println("请求的方式 = " + req.getMethod());
    }
}
```

### 5.3 如何请求参数

表单：

```html
<body>
    <form action="http://localhost:8080/07_servlet/parameterServlet" method="post">
        用户名：<input type="text" name="username"><br/>
        密码： <input type="password" name="password"><br/>
        兴趣爱好：<input type="checkbox" name="hobby" value="cpp">C++
        <input type="checkbox" name="hobby" value="java">Java
        <input type="checkbox" name="hobby" value="js">JavaScript
        <input type="submit">
    </form>
</body>
```

ParameterServlet代码：

```java
public class ParameterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //doPost方法会出现中文请求乱码问题
        //需要在获取任何参数之前修改字符编码集，而不仅仅获取中文参数时才修改：
        req.setCharacterEncoding("UTF-8");

        //获取请求参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String[] hobby = req.getParameterValues("hobby");

        System.out.println("用户名：" + username);
        System.out.println("密码：" + password);
        System.out.println("兴趣爱好：" + Arrays.asList(hobby));
    }
}

```

### 5.4 请求的转发

请求转发指的是服务器收到请求之后，从一个资源跳转到另一个资源的操作，如图所示：

![请求转发](https://gitee.com/jchenTech/images/raw/master/img/20201104175527.png)

在src下创建Servlet1，并在web.xml中配置相应的数据：

```java
public class Servlet1 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求的参数(查看办事的材料)
        String username = req.getParameter("username");
        System.out.println("在Servlet1(柜台1)中查看参数(材料):" + username);

        //给材料盖章，并传递到Servlet2（柜台2）去查看
        req.setAttribute("key1","柜台1的章");

        //问路：获得通向Servlet2的路径(请求转发对象)
        //请求转发参数必须以斜杠打头，斜杠代表http://localhost:8080/工程名/，对应IDEA代码的web目录
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/servlet2");

        //通过得到的路径走向Servlet2(柜台2)
        requestDispatcher.forward(req, resp);
    }
}
```

在src下创建Servlet2，并配置xml：

```java
public class Servlet2 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求的参数（办事的材料）查看
        String username = req.getParameter("username");
        System.out.println("在Servlet2（柜台2）中查看参数（材料）：" + username);

        // 查看柜台1 是否有盖章
        Object key1 = req.getAttribute("key1");
        System.out.println("柜台1 是否有章：" + key1);

        // 处理自己的业务
        System.out.println("Servlet2 处理自己的业务");
    }
}
```

运行结果：
(在浏览器的地址栏中输入：http://localhost:8080/07_servlet/servlet1?username=cjj057)

```
在Servlet1(柜台1)中查看参数(材料):cjj057
在Servlet2（柜台2）中查看参数（材料）：cjj057
柜台1 是否有章：柜台1的章
Servlet2 处理自己的业务
```

可以得出地址栏的内容不发生变化，但页面自动跳转(访问)
到了请求转发对象Servlet2中，即显示http://localhost:8080/07_servlet/servlet2的页面

### 5.5 base标签的作用

1. 在web目录下创建a文件夹下创建b文件夹下创建c.html

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
       这是a下的b下的c.html页面<br/>
       <a href="../../index.html">跳回首页</a><br/>
   </body>
   </html>
   ```

2. 在web目录下创建index.html

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
       这是Web下的index.html <br/>
       <a href="a/b/c.html">a/b/c.html</a><br/>
   </body>
   </html>
   ```

   两个页面可以来回跳转，分析：当在c.html页面准备点击进行跳转时浏览器的地址栏是http://localhost:8080/07_servlet/a/b/c.html，跳转到index.html页面时的a标签路径是…/…/index.html，所有相对路径在跳转时都会参照当前浏览器地址栏中的地址来进行跳转，路径正确，跳转成功。

3. 创建ForwardC类，并在xml中进行配置：

   ```java
   public class ForwardC extends HttpServlet {
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           System.out.println("经过了ForwardC程序");
   
           RequestDispatcher requestDispatcher = req.getRequestDispatcher("/a/b/c.html");
           requestDispatcher.forward(req, resp);
       }
   }
   ```

4. 在web目录下index.html中改为:

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
       这是Web下的index.html <br/>
       <a href="a/b/c.html">a/b/c.html</a><br/>
       <a href="http://localhost:8080/07_servlet/forwardC">请求转发：a/b/c.html</a>
   </body>
   </html>
   ```

   此时：

   <img src="https://gitee.com/jchenTech/images/raw/master/img/20201104192750.png" alt="index页面" style="zoom:92%;" />

   ![跳回首页](https://gitee.com/jchenTech/images/raw/master/img/20201104192807.png)

   <img src="https://gitee.com/jchenTech/images/raw/master/img/20201104192830.png" alt="image-20201104192830016" style="zoom: 67%;" />

   点击之后无法跳转，原因是，要跳转的地址是http://localhost:8080/07_servlet/forwardC，而不是http://localhost:8080/07_servlet/a/b/c.html，在跳转抵消之后为http://localhost:8080/index.html，这是错误的路径，因此跳转失败。解决方案如下：

base标签可以设置当前页面中所有相对路径跳转时参照指定的路径来进行跳转，在href属性中设置指定路径：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <!--base标签设置页面相对路径工作是参照的地址
            href属性就是参数的地址值-->
    <base href="http://localhost:8080/07_servlet/a/b/c.html">
</head>
<body>
    这是a下的b下的c.html页面<br/>
    <a href="../../index.html">跳回首页</a><br/>
</body>
</html>
```

![base标签](https://gitee.com/jchenTech/images/raw/master/img/20201104202502.jpg)

### 5.6 Web中的相对路径和绝对路径

在javaWeb 中，路径分为相对路径和绝对路径两种：

1. 相对路径是：
   * . 表示当前目录
   * .. 表示上一级目录
   * 资源名表示当前目录/资源名

2. 绝对路径：
   `http://ip:port/工程路径/资源路径`
   在实际开发中，路径都使用绝对路径，而不简单的使用相对路径。
   * 绝对路径
   * base+相对

### 5.7 Web中/斜杠的不同意义

1. `/`若被浏览器解析，得到的地址是：http://ip:port/

   `<a href="/">斜杠</a>`

2. `/`若被服务器解析，得到的地址是：http://ip:port/工程路径

   * `<url-pattern>/servlet1</url-pattern>`
   * `servletContext.getRealPath("/");`
   * `request.getRequestDispatcher("/");`

3. 特殊情况：`response.sendRedirect("/");` 把斜杠发送到浏览器解析，得到http://ip:port/

## 6 HttpServletResponse类

### 6.1 HttpServletResponse的作用

`HttpServletResponse` 类和`HttpServletRequest` 类一样。每次请求进来，Tomcat 服务器都会创建一个`Response` 对象传递给Servlet 程序去使用。`HttpServletRequest` 表示请求过来的信息，`HttpServletResponse` 表示所有响应的信息，我们如果需要设置返回给客户端的信息，都可以通过`HttpServletResponse` 对象来进行设置

### 6.2 两个输出流的说明该

* 字节流 getOutputStream();   常用于下载（传递）二进制数据
* 字符流 getWriter();                   常用于回传字符串（常用）

注：同一个HttpServletResponse对象两个流不可同时使用，只可二选一，否则报错：

![两个输出流](https://gitee.com/jchenTech/images/raw/master/img/20201104200437.png)

### 6.3 如何往客户端回传数据

要求： 往客户端回传字符串数据。

```java
public class ResponseIOServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.write("response's content!!!");
    }
}
```

### 6.4 响应的乱码解决

解决响应中文乱码方案一（不推荐使用）：

```java
//设置服务器字符集为UTF-8
resp.setCharacterEncoding("UTF-8");
// 通过响应头，设置浏览器也是用UTF-8字符集
resp.setHeader("Content-Type", "text/html; charset=UTF-8");
```

解决响应中文乱码方案二（推荐）：

```java
// 它会同时设置服务器和客户端都使用UTF-8 字符集，还设置了响应头
// 此方法一定要在获取流对象之前调用才有效
resp.setContentType("text/html; charset=UTF-8");
```

### 6.5 请求重定向

请求重定向，是指客户端给服务器发请求，然后服务器告诉客户端说。我给你一些地址。你去新地址访问。叫请求重定向（因为之前的地址可能已经被废弃）。

请求重定向的第一种方案：

```java
// 设置响应状态码302 ，表示重定向，（已搬迁）
resp.setStatus(302);
// 设置响应头，说明新的地址在哪里
resp.setHeader("Location", "http://localhost:8080");
```

请求重定向的第二种方案（推荐使用）：

```java
resp.sendRedirect("http://localhost:8080");
```

![请求重定向](https://gitee.com/jchenTech/images/raw/master/img/20201104203925.png)