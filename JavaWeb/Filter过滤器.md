## 1 什么是Filter过滤器

1. Filter 过滤器它是 JavaWeb 的三大组件之一。三大组件分别是：Servlet 程序、Listener 监听器、Filter 过滤器
2. Filter 过滤器它是 JavaEE 的规范。也就是接口
3. Filter 过滤器它的作用是： 拦截请求，过滤响应。
   拦截请求常见的应用场景有：
   * 权限检查
   * 日记操作
   * 事务管理
   * ……等等

## 2 Filter初体验

要求：在你的 web 工程下，有一个 `admin` 目录。这个 `admin` 目录下的所有资源（html 页面、jpg 图片、jsp 文件、等等）都必须是用户登录之后才允许访问。

思考：根据之前我们学过内容。我们知道，用户登录之后都会把用户登录的信息保存到 Session 域中。所以要检查用户是否登录，可以判断 Session 中否包含有用户登录的信息即可！！！

```jsp
<%
    Object user = session.getAttribute("user");
    //如果等于null，说明还没有登录
    if (user == null) {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
        return;
    }
%>
```

该方法具有局限性：当内容只有是jsp页面时才能采用当前方法

Filter 的工作流程图：

![image-20201110191714473](https://gitee.com/jchenTech/images/raw/master/img/20201110191714.png)



**Filter 过滤器的使用步骤：**

1. 编写一个类去实现 `Filter` 接口
2. 实现过滤方法 `doFilter()`
3. 到 `web.xml` 中去配置 Filter 的拦截路径

Filter 的代码：

```java
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    System.out.println("3、Filter的doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)方法 ");

    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    HttpSession session = httpServletRequest.getSession();
    Object user = session.getAttribute("user");
    // 如果等于 null，说明还没有登录
    if (user == null) {
        servletRequest.getRequestDispatcher("/login.jsp").forward(servletRequest, servletResponse);
        return;
    }else {
        // 让程序继续往下访问用户的目标资源
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
```

web.xml中的配置：

```xml
<!--filter 标签用于配置一个 Filter 过滤器-->
<filter>
    <!--给 filter 起一个别名-->
    <filter-name>AdminFilter</filter-name>
    <!--配置 filter 的全类名-->
    <filter-class>com.atguigu.filter.AdminFilter</filter-class>

</filter>
<!--filter-mapping 配置 Filter 过滤器的拦截路径-->
<filter-mapping>
    <!--filter-name 表示当前的拦截路径给哪个 filter 使用-->
    <filter-name>AdminFilter</filter-name>
    <!--url-pattern 配置拦截路径
            /表示请求地址为:http://ip:port/工程路径/ 映射到IDEA的web目录
            /admin/* 表示请求地址为:http://ip:port/工程路径/admin/* -->
    <url-pattern>/admin/*</url-pattern>
</filter-mapping>
```

### 2.1 完整的用户登录

login.jsp 页面 == 登录表单

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    这是登陆界面，login.jsp页面
    <form action="http://localhost:8080/15_filter/loginServlet" method="get">
        用户名:<input type="text" name="username"> <br/>
        密码:<input type="text" name="password"> <br/>
        <input type="submit">
    </form>
</body>
</html>
```

LoginServlet程序：

```java
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if ("cjj057".equals(username) && "123456".equals(password)) {
            req.getSession().setAttribute("user", username);
            resp.getWriter().write(" 登陆成功 ！ ");
        }else {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
```

## 3 Filter的生命周期

Filter 的生命周期包含几个方法

1. 构造器方法
2. `init` 初始化方法
   第 1，2 步，在 web 工程启动的时候执行（Filter 已经创建）
3. `doFilter` 过滤方法
   第 3 步，每次拦截到请求，就会执行
4. `destroy` 销毁
   第 4 步，停止 web 工程的时候，就会执行（停止 web 工程，也会销毁 Filter 过滤器）

## 4 FilterConfig类

`FilterConfig` 类见名知义，它是 Filter 过滤器的配置文件类。Tomcat 每次创建 Filter 的时候，也会同时创建一个 `FilterConfig` 类，这里包含了 Filter 配置文件的配置信息。

`FilterConfig` 类的作用是获取 filter 过滤器的配置内容：

1. 获取 Filter 的名称 `filter-name` 的内容
2. 获取在 Filter 中配置的 `init-param` 初始化参数
3. 获取 `ServletContext` 对象

Servlet代码：

```java
@Override
public void init(FilterConfig filterConfig) throws ServletException {
    System.out.println("2、Filter的init(FilterConfig filterConfig)初始化方法 ");

    //         1、获取 Filter 的名称 filter-name 的内容
    System.out.println("filter-name -> " + filterConfig.getFilterName());
    //         2、获取在 Filter 中配置的 init-param 初始化参数
    System.out.println("初始化参数username的值是 -> " + filterConfig.getInitParameter("username"));
    System.out.println("初始化参数 url 的值是 -> " + filterConfig.getInitParameter("url"));
    //         3、获取 ServletContext 对象
    System.out.println(filterConfig.getServletContext());
}
```

web.xml中的配置：

```xml
<filter>
    <!--给 filter 起一个别名-->
    <filter-name>AdminFilter</filter-name>
    <!--配置 filter 的全类名-->
    <filter-class>com.atguigu.filter.AdminFilter</filter-class>

    <init-param>
        <param-name>username</param-name>
        <param-value>root</param-value>
    </init-param>
    <init-param>
        <param-name>url</param-name>
        <param-value>jdbc:mysql://localhost3306/test</param-value>
    </init-param>
</filter>
```

## 5 FilterChain过滤器链

`FilterChain` 就是过滤器链（多个过滤器如何一起工作）

![image-20201110204223706](https://gitee.com/jchenTech/images/raw/master/img/20201110204223.png)

注意：

1. 上述两个Filter拦截的资源路径相同，代表一定会执行两个Filter过滤器的doFilter方法， 但因为手动注释不一定执行其中的chain.doFilter方法
2. 如果两个Filter拦截资源不同，且拦截资源符合Filter1，不符合Filter2，则会执行Filter1 的doFilter方法，且执行其中的chain.doFilter方法时，不会去执行Filter2的doFilter方法， 直接去访问资源，之后执行Filter1的后置代码(在chain.doFilter之后的均是后置代码)
3. 如果请求的资源不符合过滤器1和2的拦截路径，两个doFilter方法都不执行
4. 前置代码、chain.doFilter方法、后置代码都在doFilter方法中

## 6 Filter的拦截路径

1. 精确匹配

   ```xml
   <url-pattern>/target.jsp</url-pattern>
   ```

   以上配置的路径，表示请求地址必须为：`http://ip:port/工程路径/target.jsp`

2. 目录匹配

   ```xml
   <url-pattern>/admin/*</url-pattern>
   ```

   以上配置的路径，表示请求地址必须为：`http://ip:port/工程路径/admin/*`

3. 后缀名匹配

   ```xml
   <url-pattern>*.html</url-pattern>
   ```

   以上配置的路径，表示请求地址必须以.html 结尾才会拦截到

   ```xml
   <url-pattern>*.do</url-pattern>
   ```

   以上配置的路径，表示请求地址必须以.do 结尾才会拦截到

   ```
   <url-pattern>*.action</url-pattern>
   ```

   以上配置的路径，表示请求地址必须以.action 结尾才会拦截到

   

**Filter 过滤器它只关心请求的地址是否匹配，不关心请求的资源是否存在！！！**