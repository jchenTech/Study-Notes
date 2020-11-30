## 1 JSP的介绍与创建

1. JSP的全称是Java Server Pages，即Java的服务器页面
2. JSP的主要作用是代替Servlet程序回传HTML页面的数据
3. web目录(或其他)右击 --> new --> JSP/JSPX --> 输入文件名 --> 选择JSP file创建



Servlet程序回传HTML页面数据（非常繁琐）：

```java
public class PrintHtml extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //通过响应的回传流回传html页面数据
        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter writer = resp.getWriter();
        writer.write("<!DOCTYPE html>\r\n");
        writer.write("  <html lang=\"en\">\r\n");
        writer.write("  <head>\r\n");
        writer.write("      <meta charset=\"UTF-8\">\r\n");
        writer.write("      <title>Title</title>\r\n");
        writer.write("  </head>\r\n");
        writer.write(" <body>\r\n");
        writer.write("    这是html页面数据 \r\n");
        writer.write("  </body>\r\n");
        writer.write("</html>\r\n");
        writer.write("\r\n");

    }
}
```

## 2 JSP的本质

JSP页面本质上是一个Servlet程序，第一次访问JSP页面时(运行Tomcat服务器后在浏览器地址栏输入路径)，Tomcat服务器会将此JSP页面翻译成为一个Java源文件，并对其进行编译成为.class字节码文件(一个.java，一个.class)，当打开.java文件时发现其中的内容是：

![image-20201108163241183](https://gitee.com/jchenTech/images/raw/master/img/20201108163241.png)

我们跟踪原代码发现，HttpJspBase类直接继承于HttpServlet类，即JSP翻译出来的Java类间接继承于HttpServlet类，证明JSP页面是一个Servlet程序

## 3 JSP的三种语法

### 3.1 JSP头部的page指令

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
```

JSP头部的page指令可以修改JSP页面中的一些重要属性或行为
(以下属性均写在page指令中，默认page指令中没有出现的属性都采用默认值)：

1. contentType属性：表示JSP返回的数据类型是什么，即response.setContentType()的参数值
2. language属性：表示JSP翻译之后是什么语言文件(目前只支持Java)
3. pageEncoding属性：表示当前JSP文件本身的字符集(可在IDEA右下角看到)
4. import属性：表示导包(导类)，与Java一致
5. autoFlush属性：设置当out输出流缓冲区满了之后是否自动刷新缓冲区，默认值是true
6. buffer属性：设置out缓冲区的大小，默认是8kb
   注意：out缓冲区满了之后不能自动刷新的话会报错
7. errorPage属性：设置当JSP页面运行出错时自动跳转到的页面(错误信息页面)的路径，这个 路径一般都是以斜杠打头，表示请求的地址是http://ip:port/工程路径/，对应代码web目录
8. isErrorPage属性：设置当前JSP页面是否是错误信息页面，默认是false，如果是true可以 获取错误信息
9. session属性：设置访问当前JSP页面时是否会创建HttpSession对象，默认值是true
10.  extends属性：设置JSP页面翻译出来的Java类默认继承谁

注意：以上默认值除非有特殊需要，否则不建议修改

### 3.2 JSP中的常用脚本

**声明脚本（极少使用）：**

格式：`<%! 声明Java代码 %>`
作用：可以给JSP翻译出来的Java类定义属性、方法、静态代码块、内部类等
特点：不会在浏览器的页面上显示出来，仅存在于翻译后的Java类中

代码演示：声明脚本的使用(此JSP文件在web目录下，名为First.jsp)

```jsp
<%--练习：
    --%>

<%--1、声明类属性--%>
<%!
    private Integer id;
private String name;
private static Map<String,Object> map;
%>
<%--2、声明static静态代码块--%>
<%!
    static {
    map = new HashMap<String,Object>();
    map.put("key1", "value1");
    map.put("key2", "value2");
    map.put("key3", "value3");
}
%>
<%--3、声明类方法--%>
<%!
    public int abc(){
    return 12;
}
%>
<%--4、声明内部类--%>
<%!
    public static class A {
        private Integer id = 12;
        private String abc = "abc";
    }
%>
```

翻译后的java源文件：

![image-20201108170450434](https://gitee.com/jchenTech/images/raw/master/img/20201108170450.png)



 **表达式脚本**（常用）：

格式：`<%=表达式 %>`
作用：在浏览器的JSP页面上输出数据(只有此脚本可以在浏览器的页面上输出数据)
特点：

1. 所有的表达式脚本都会被翻译到对应的Java类的_`jspService()`方法中，故表达式脚本可以 直接使用_jspService()方法参数中的对象
2.  表达式脚本都会被编译后的Java类中的out.print()方法输出到浏览器页面上
3. 表达式脚本中的表达式不能以分号结束

代码演示：表达式脚本的使用

```jsp
<%--练习：
1.输出整型
2.输出浮点型
3.输出字符串
4.输出对象    --%>

<%=12 %> <br>
<%=12.12 %> <br>
<%="我是字符串" %> <br>
<%=map%> <br>
<%=request.getParameter("username")%>
```

启动Tomcat服务器后浏览器的运行结果：

![image-20201108171045374](https://gitee.com/jchenTech/images/raw/master/img/20201108171045.png)

翻译对照：

![image-20201108171337796](https://gitee.com/jchenTech/images/raw/master/img/20201108171337.png)



**代码脚本：**

格式：`<% Java语句 %>`
作用：在JSP页面中可以编写需要的Java代码
特点：

1. 代码脚本翻译后都在jspService方法中，故代码脚本可以直接使用此方法参数中的对象
2. 可以由多个代码脚本块组合完成一个完整的Java语句
3. 代码脚本还可以和表达式脚本一起组合使用，在JSP页面上输出数据

代码演示：代码脚本的使用

```jsp
<%--练习：--%>
<%--1.代码脚本----if 语句--%>
<%
int i = 13 ;
if (i == 12) {
    %>
<h1>国哥好帅</h1>
<%
} else {
    %>
<h1>国哥又骗人了！</h1>
<%
}
%>
<br>
<%--2.代码脚本----for 循环语句--%>
<table border="1" cellspacing="0">
    <%
    for (int j = 0; j < 10; j++) {
        %>
    <tr>
        <td>第 <%=j + 1%>行</td>
    </tr>
    <%
    }
    %>
</table>
<%--3.翻译后java文件中_jspService方法内的代码都可以写--%>
<%
String username = request.getParameter("username");
System.out.println("用户名的请求参数值是：" + username);
%>
```

翻译对照：

![image-20201108171711007](https://gitee.com/jchenTech/images/raw/master/img/20201108171711.png)

### 3.3 JSP中的三种注释

1. HTML注释：`<!--HTML注释-->`
   HTML注释会被翻译到JSP文件对应的Java类的_jspService方法中，以out.write()输出到客户端，write方法会自动识别标签，执行标签对应的功能，不会在浏览器的页面上输出注释
2. Java注释：`(1) //单行注释 (2) /*多行注释*/`
   Java注释要写在声明脚本和代码脚本中才被认为是Java注释，会被翻译到JSP文件对应的Java类的jspService方法中，在对应的Java类中也是注释
3. JSP注释：`<%- -这是JSP注释- -%>`
   JSP注释中的内容不会在JSP文件翻译后的Java类中出现，即注释中的内容没有任何功能

## 4 JSP中的九大内置对象

![image-20201108182619637](https://gitee.com/jchenTech/images/raw/master/img/20201108182619.png)

1. request：请求对象
2. response：响应对象
3. pageContext：JSP的上下文对象
4. session：会话对象
5. application：ServletContext对象
6. config：ServletConfig对象
7. out：JSP输出流对象
8. page：指向当前JSP的对象
9. exception：异常对象

## 5 JSP四大域对象

| 域对象      | 对应的类           | 作用范围                                                   |
| ----------- | ------------------ | ---------------------------------------------------------- |
| pageContext | PageContextImpl    | 当前jsp页面范围内有效                                      |
| request     | HttpServletRequest | 一次请求内有效                                             |
| session     | HttpSession        | 一个会话范围内有效（打开浏览器访问服务器，直到关闭浏览器） |
| application | ServletContext     | 整个web 工程范围内都有效（只要web 工程不停止，数据都在）   |

域对象是指可以像Map一样存取数据的对象，四个域对象功能一样，只是对数据的存取范围不同。

注意：若四个域对象在使用时范围都可满足要求，则使用的优先顺序是(范围从小到大)：
pageContext --> request --> session --> application



代码演示1：四个域对象存取数据的范围的不同(在web目录下创建scope.jsp)

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>scope.jsp页面</h1>
    <%
        // 往四个域中都分别保存了数据
        pageContext.setAttribute("key", "pageContext");
        request.setAttribute("key", "request");
        session.setAttribute("key", "session");
        application.setAttribute("key", "application");
    %>
    pageContext域是否有值：<%=pageContext.getAttribute("key")%> <br>
    request域是否有值：<%=request.getAttribute("key")%> <br>
    session域是否有值：<%=session.getAttribute("key")%> <br>
    application域是否有值：<%=application.getAttribute("key")%> <br>
    <%
        request.getRequestDispatcher("/scope2.jsp").forward(request,response);
    %>
</body>
</html>

```

代码演示2：在web目录下创建scope2.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>scope2.jsp页面</h1>
    pageContext域是否有值：<%=pageContext.getAttribute("key")%> <br>
    request域是否有值：<%=request.getAttribute("key")%> <br>
    session域是否有值：<%=session.getAttribute("key")%> <br>
    application域是否有值：<%=application.getAttribute("key")%> <br>
</body>
</html>

```

## 6 out输出与response.getWriter输出的区别

1. 相同点：response表示响应，用于给客户端(浏览器)返回内容
   out同样也是用于给客户端(浏览器)输出内容
2. 不同点：
   ![img](https://gitee.com/jchenTech/images/raw/master/img/20201108191223)
3. 注意：由于官方的代码中翻译后的Java代码底层都是使用out进行输出，故一般都使用out进行 输出，out又分为`write`方法和`print`方法：
   1. `out.print()`：会将任何内容转换成字符串后调用write方法输出
   2. `out.write()`：输出字符串没有问题，但输出int型时会将int转换成char输出，导致输出的并非是想要的数字而是数字对应的ASCII码
      结论：JSP页面的代码脚本中任何要输出在浏览器的内容均使用out.print()方法

## 7 JSP常用标签

### 7.1 静态包含

1. 使用场景：

![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108193317)



2. 使用方法：`<%@include file=""%>`
   其中file属性设置要包含的JSP页面，以/打头，代表http://ip:port/工程路径/，对应web目录

```jsp
<%@ include file="/include/footer.jsp"%>
```

3. 静态包含的特点：
   * 静态包含不会将被包含的JSP页面翻译成.java.class文件
   * 静态包含是把被包含的页面的代码拷贝到body.jsp对应的Java文件的对应位置执行输出

### 7.2 动态包含

1. 使用方法：
   `<jsp:include page=""></jsp:include>`
   其中page属性设置要包含的JSP页面，与静态包含一致

2. 动态包含的特点：
   * 动态包含将被包含的JSP页面翻译成.java.class文件
   * 动态包含还可以传递参数
   * 动态包含底层使用如下代码调用被包含的JSP页面执行输出：
     `org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "/foot.jsp", out, false);`

```jsp
<jsp:include page="/include/footer.jsp">
    <jsp:param name="username" value="bbj"/>
    <jsp:param name="password" value="root"/>
</jsp:include>
```

3. 动态包含底层原理：

   ![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108194142)



### 7.3 JSP标签-转发

`<jsp:forward page=""></jsp:forward>` 是请求转发标签，它的功能就是请求转发
page 属性设置请求转发的路径

示例说明：

```jsp
<jsp:forward page="/scope2.jsp"></jsp:forward>
<jsp:forward page="/scope2.jsp"></jsp:forward>
```

## 8 Listener监听器

### 8.1 监听器介绍

1. Listener监听器是JavaWeb的三大组件之一
2. Listener监听器是JavaEE的规范(接口)
3.  Listener监听器的作用是监听某件事物的变化，然后通过回调函数反馈给程序做一些处理

### 8.2 ServletContextListener监听器

`ServletContextListener`监听器可以监听`ServletContext`对象的创建和销毁(web工程启动时创建，停止时销毁)，监听到创建和销毁之后都会调用`ServletContextListener`监听器的方法进行反馈：

```java
public interface ServletContextListener extends EventListener {
    //在ServletContext对象创建之后调用
    public void contextInitialized(ServletContextEvent sce);
    //在ServletContext对象销毁之后调用
    public void contextDestroyed(ServletContextEvent sce);
}	
```

### 8.3 ServlerContextListener监听器的使用步骤

1. 编写一个类实现ServletContextListener接口
2. 重写两个方法
3. 在web.xml文件中配置监听器

代码演示1：创建一个类

```java
public class MyServletContextListenerImpl implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContext对象被创建了");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContext对象被销毁了");
    }
}
```

代码演示2：在web.xml中配置

```xml
<listener>
    <listener-class>com.atguigu.listener.MyServletContextListenerImpl</listener-class>
</listener>
```

运行结果：
Tomcat服务器启动之后控制台输出ServletContext对象创建
Tomcat服务器停止之后控制台输出ServletContext对象销毁