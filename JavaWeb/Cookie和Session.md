## 1 会话技术

1. 会话：一次会话中包含多次请求和响应
   注：一次会话表示浏览器第一次给服务器发送请求，会话建立，直到有一方断开为止
2. 功能：在一次会话的多次请求间共享数据
3. 方式：
   (1) 客户端会话技术：Cookie
   (2) 服务器端会话技术：Session

## 2 Cookie会话

### 2.1 什么是Cookie

1. Cookie 翻译过来是饼干的意思。
2. Cookie 是服务器通知客户端保存键值对的一种技术。
3. 客户端有了Cookie 后，每次请求都发送给服务器。
4. 每个Cookie 的大小不能超过4kb

### 2.2 如何创建Cookie

![image-20201109214540284](https://gitee.com/jchenTech/images/raw/master/img/20201109214540.png)

src目录下创建CookieServlet程序：

```java
public class CookieServlet extends BaseServlet{
    protected void createCookie(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1 创建 Cookie 对象
        Cookie cookie = new Cookie("key4", "value4");
        //2 通知客户端保存 Cookie
        resp.addCookie(cookie);
        //1 创建 Cookie 对象
        Cookie cookie1 = new Cookie("key5", "value5");
        //2 通知客户端保存 Cookie
        resp.addCookie(cookie1);
        resp.getWriter().write("Cookie 创建成功");
    }
}
```

配置好xml文件后，在浏览器中输入地址：http://localhost:8080/13_cookie_session/cookie.html，运行结果为：

![image-20201109220312623](https://gitee.com/jchenTech/images/raw/master/img/20201109220312.png)

### 2.3 服务器如何获取Cookie

服务器获取客户端的 Cookie 只需要一行代码：`req.getCookies():Cookie[]`

![image-20201109220716213](https://gitee.com/jchenTech/images/raw/master/img/20201109220716.png)

Cookie的工具类，查找指定的Cookie对象：

```java
public class CookieUtils {
    /**
     * 查找指定名称的Cookie对象
     * @param name
     * @param cookies
     * @return
     */
    public static Cookie findCookie(String name , Cookie[] cookies){
        if (name == null || cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
```

CookieServlet 程序中的代码：

```java
protected void getCookie(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Cookie[] cookies = req.getCookies();
    for (Cookie cookie : cookies) {
        // getName方法返回Cookie的key（名）
        // getValue方法返回Cookie的value值
        resp.getWriter().write("Cookie[" + cookie.getName() + "=" + cookie.getValue() + "] <br/>");
    }

    Cookie iWantCookie = CookieUtils.findCookie("key1", cookies);

    //        for (Cookie cookie : cookies) {
    //            if ("key2".equals(cookie.getName())) {
    //                iWantCookie = cookie;
    //                break;
    //            }
    //        }
    // 如果不等于null，说明赋过值，也就是找到了需要的Cookie
    if (iWantCookie != null) {
        resp.getWriter().write("找到了需要的Cookie");
    }
}
```

浏览器运行结果：

![image-20201110162345972](https://gitee.com/jchenTech/images/raw/master/img/20201110162353.png)



### 2.4 Cookie值的修改

方案一：

1. 先创建一个要修改的同名（指的就是 key）的 Cookie 对象
2. 在构造器，同时赋于新的 Cookie 值。
3. 调用 `response.addCookie(Cookie)`;

```java
protected void updateCookie(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //        方案一：
    //        1. 先创建一个要修改的同名（指的就是 key）的 Cookie 对象
    //        2. 在构造器，同时赋于新的 Cookie 值。
    Cookie cookie = new Cookie("key1", "newValue");

    //        3. 调用 `response.addCookie(Cookie)`;
    resp.addCookie(cookie);
    resp.getWriter().write("key1的Cookie已经修改好");
}
```

方案二：

1. 先查找到需要修改的 Cookie 对象
2. 调用 `setValue()`方法赋于新的 Cookie 值。
3. 调用 `response.addCookie()`通知客户端保存修改

```java
protected void updateCookie(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        方案二：
//        1. 先查找到需要修改的 Cookie 对象
        Cookie cookie = CookieUtils.findCookie("key2", req.getCookies());
        if (cookie != null) {
//        2. 调用 `setValue()`方法赋于新的 Cookie 值。
            cookie.setValue("newValue2");
//        3. 调用response.addCookie()通知客户端保存修改
            resp.addCookie(cookie);
	}
}
```

浏览器运行结果：

![image-20201110163748277](https://gitee.com/jchenTech/images/raw/master/img/20201110163748.png)

### 2.5 浏览器查看Cookie

![image-20201110164219220](https://gitee.com/jchenTech/images/raw/master/img/20201110164219.png)

### 2.6 Cookie生命控制

Cookie 的生命控制指的是如何管理Cookie什么时候被销毁（删除）
`setMaxAge()`：

* 正数，表示在指定的秒数后过期
* 负数，表示浏览器一关，Cookie 就会被删除（默认值是-1）
* 零，表示马上删除 Cookie

```java

/**
     * 设置存活1个小时的Cooie
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
protected void life3600(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    Cookie cookie = new Cookie("life3600", "life3600");
    cookie.setMaxAge(60 * 60); // 设置Cookie一小时之后被删除。无效
    resp.addCookie(cookie);
    resp.getWriter().write("已经创建了一个存活一小时的Cookie");

}

/**
     * 马上删除一个Cookie
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
protected void deleteNow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 先找到你要删除的Cookie对象
    Cookie cookie = CookieUtils.findCookie("key4", req.getCookies());
    if (cookie != null) {
        // 调用setMaxAge(0);
        cookie.setMaxAge(0); // 表示马上删除，都不需要等待浏览器关闭
        // 调用response.addCookie(cookie);
        resp.addCookie(cookie);

        resp.getWriter().write("key4的Cookie已经被删除");
    }

}

/**
     * 默认的会话级别的Cookie
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
protected void defaultLife(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Cookie cookie = new Cookie("defaultLife", "defaultLife");
    cookie.setMaxAge(-1);
    resp.addCookie(cookie);
}
```

### 2.7 Cookie有效路径Path的设置

Cookie 的 `path` 属性可以有效的过滤哪些 Cookie 可以发送给服务器。哪些不发。
`path` 属性是通过请求的地址来进行有效的过滤。

```
CookieA path=/工程路径
CookieB path=/工程路径/abc

请求地址如下：
1.http://ip:port/工程路径/a.html
CookieA 发送
CookieB 不发送

2.http://ip:port/工程路径/abc/a.html
CookieA 发送
CookieB 发送
```

创建testPath方法：

```java
protected void testPath(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Cookie cookie = new Cookie("path1", "path1");
    // getContextPath() ===>>>>  得到工程路径
    cookie.setPath( req.getContextPath() + "/abc" ); // ===>>>>  /工程路径/abc
    resp.addCookie(cookie);
    resp.getWriter().write("创建了一个带有Path路径的Cookie");
}
```

### 2.8 免输入用户名登陆

免输入用户名登陆示例图：

![image-20201110170622668](https://gitee.com/jchenTech/images/raw/master/img/20201110170622.png)



login.jsp页面：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="http://localhost:8080/13_cookie_session/loginServlet" method="get">
        用户名：<input type="text" name="username" value="${cookie.username.value}"> <br>
        密码：<input type="password" name="password"> <br>
        <input type="submit" value="登录">
    </form>
</body>
</html>

```

LoginServlet程序：

```java
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if ("cjj057".equals(username) && "123456".equals(password)) {
            //登录 成功
            Cookie cookie = new Cookie("username", username);
            cookie.setMaxAge(60 * 60 * 24 * 7);//当前Cookie一周内有效
            resp.addCookie(cookie);
            System.out.println("登录 成功");
        } else {
//            登录 失败
            System.out.println("登录 失败");
        }

    }
}
```

## 3 Session会话

### 3.1 什么是Session会话

1. Session 就一个接口（HttpSession）。
2. Session 就是会话。它是用来维护一个客户端和服务器之间关联的一种技术。
3. 每个客户端都有自己的一个 Session 会话。
4. Session 会话中，我们经常用来保存用户登录之后的信息。

### 3.2 如何创建Session和获取(id 号, 是否为新)

如何创建和获取 Session。它们的 API 是一样的： 

`request.getSession()`：

* 第一次调用：创建 Session 会话
* 之后调用：获取前面创建好的 Session 会话对象。

`isNew()`：判断到底是不是刚创建出来的（新的）

* true 表示刚创建
* false 表示获取之前创建

每个会话都有一个身份证号。也就是 ID 值。而且这个 ID 是唯一的。`getId()` 得到 Session 的会话 id 值。

```java
protected void createOrGetSession(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 创建和获取Session会话对象
    HttpSession session = req.getSession();
    // 判断 当前Session会话，是否是新创建出来的
    boolean isNew = session.isNew();
    // 获取Session会话的唯一标识 id
    String id = session.getId();

    resp.getWriter().write("得到的Session，它的id是：" + id + " <br /> ");
    resp.getWriter().write("这个Session是否是新创建的：" + isNew + " <br /> ");
}
```

### 3.3 Session域数据的存取

```java
/**
 * 往Session中保存数据
 * @param req
 * @param resp
 * @throws ServletException
 * @throws IOException
 */
protected void setAttribute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getSession().setAttribute("key1", "value1");
    resp.getWriter().write("已经往Session中保存了数据");

}

/**
 * 获取Session域中的数据
 * @param req
 * @param resp
 * @throws ServletException
 * @throws IOException
 */
protected void getAttribute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Object attribute = req.getSession().getAttribute("key1");
    resp.getWriter().write("从Session中获取出key1的数据是：" + attribute);
}
```

### 3.4 Session生命周期控制

1. `public void setMaxInactiveInterval(int interval)` 设置 Session 的超时时间（以秒为单位），超过指定的时长，Session就会被销毁。
   * 值为正数的时候，设定 Session 的超时时长。
   * 负数表示永不超时（极少使用）

2. `public int getMaxInactiveInterval()`获取 Session 的超时时间
   `public void invalidate()` 让当前 Session 会话马上超时无效。

3. Session 默认的超时时长是多少！
   Session 默认的超时时间长为 30 分钟。因为在Tomcat服务器的配置文件web.xml中默认有以下的配置，它就表示配置了当前Tomcat服务器下所有的Session超时配置默认时长为：30 分钟。

   ```xml
   <session-config>
   	<session-timeout>30</session-timeout>
   </session-config>
   ```

   

如果说。你希望你的 web 工程，默认的 Session 的超时时长为其他时长。你可以在你自己的 web.xml 配置文件中做以上相同的配置。就可以修改你的 web 工程所有 Seession 的默认超时时长。

```xml
<!-- 表示当前 web 工程。创建出来 的所有 Session 默认是 20 分钟 超时时长 -->
<session-config>
<session-timeout>20</session-timeout>
</session-config>
```

如果你想只修改个别 Session 的超时时长。就可以使用上面的 API。`setMaxInactiveInterval(int interval)`来进行单独的设置。



Session 超时的概念介绍：

<img src="https://gitee.com/jchenTech/images/raw/master/img/20201110175017.png" alt="image-20201110175017041" style="zoom: 80%;" />

示例代码：

```java
protected void life3(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
    // 先获取 Session 对象
    HttpSession session = req.getSession();
    // 设置当前 Session3 秒后超时
    session.setMaxInactiveInterval(3);
    resp.getWriter().write(" 当前 Session 已经设置为 3 秒后超时");
}
```

Session 马上被超时示例：

```java
protected void deleteNow(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 先获取 Session 对象
    HttpSession session = req.getSession();
    // 让 Session 会话马上超时
    session.invalidate();
    resp.getWriter().write("Session 已经设置为超时（无效）");
}
```

### 3.5 浏览器和Session之间的关联的技术内幕

Session技术，底层其实是基于Cookie技术来实现的。

![image-20201110184315771](https://gitee.com/jchenTech/images/raw/master/img/20201110184316.png)

