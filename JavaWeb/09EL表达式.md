## 1 EL表达式简介

1. EL表达式全称：Expression Language，即表达式语言
2. EL表达式作用：代替JSP页面中表达式脚本进行数据的输出
3. EL表达式比JSP的表达式脚本简洁很多
4. EL表达式的格式是：`${表达式}` ，注：EL表达式写在jsp页面中，表达式一般是域对象的key

代码演示：在web目录下创建a.jsp

```jsp
<body>
    <%
        request.setAttribute("key","值");
    %>
    表达式脚本输出key的值是：<%=request.getAttribute("key1")==null?"":request.getAttribute("key1")%><br/>
    EL表达式输出key的值是：${key1}
</body>
```

EL表达式在输出null 值的时候，输出的是空串。jsp 表达式脚本输出null 值的时候，输出的是null 字符串。

## 2 EL表达式搜索域数据的顺序

EL表达式主要是输出域对象中的数据，当四个域对象都有同一个key的值时，EL表达式会按照四个域对象的范围从小到大进行搜索，找到就输出，与四个域对象声明的先后顺序无关

代码演示：在web目录下创建Test.jsp

```html
<body>
    <%
        //往四个域中都保存了相同的key的数据。
        request.setAttribute("key", "request");
        session.setAttribute("key", "session");
        application.setAttribute("key", "application");
        pageContext.setAttribute("key", "pageContext");
    %>
    ${ key }
</body>
```

运行结果：

![image-20201108204005933](https://gitee.com/jchenTech/images/raw/master/img/20201108204006.png)

## 3 EL表达式输出Java类的属性

代码演示：创建Person类

```java
public class Person {
    //输出Person类中普通属性，数组属性，list集合属性和map集合属性
    private String name;
    private String[] phones;
    private List<String> cities;
    private Map<String, Object> map;
    //注意：没有声明age属性
    public int getAge() {
        return 18;
    }
    //以及全参、空参构造器，各属性的getter/setter方法
}
123456789101112
```

代码演示：在web目录下创建c.jsp

```jsp
<body>
<%
    Person person = new Person();
    person.setName("我爱中国！");
    person.setPhones(new String[]{"18610541354","18688886666","18699998888"});

    List<String> cities = new ArrayList<String>();
    cities.add("北京");
    cities.add("上海");
    cities.add("深圳");
    person.setCities(cities);

    Map<String,Object> map = new HashMap<>();
    map.put("key1","value1");
    map.put("key2","value2");
    map.put("key3","value3");
    person.setMap(map);

    pageContext.setAttribute("p", person);
%>

    输出Person：${ p }<br/>
    输出Person的name属性：${p.name} <br>
    输出Person的pnones数组属性值：${p.phones[2]} <br>
    输出Person的cities集合中的元素值：${p.cities} <br>
    输出Person的List集合中个别元素值：${p.cities[2]} <br>
    输出Person的Map集合: ${p.map} <br>
    输出Person的Map集合中某个key的值: ${p.map.key3} <br>
    输出Person的age属性：${p.age} <br>


</body>
```

注意，这里面输出Java类对象属性时是调用的对象属性对应的get方法，而不是调用属性，因此`${p.age}`调用的是age对应的`getAge()`方法



运行结果：
![image-20201108204813927](https://gitee.com/jchenTech/images/raw/master/img/20201108204814.png)

## 4 EL表达式的运算

语法：${运算表达式}，EL表达式支持以下运算符：

### 4.1 关系运算

![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108215416)

### 4.2 逻辑运算

![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108214648)

### 4.3 算数运算

![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108214647)

### 4.4 empty运算

empty运算可以判断一个数据是否为空，若为空，输出true，不为空，输出false
以下几种情况为空(在原本的key之前加empty关键字)：

1. 值为null 值的时候，为空
2. 值为空串的时候，为空
3. 值是Object 类型数组，长度为零的时候
4. list 集合，元素个数为零
5. map 集合，元素个数为零

### 4.5 三元运算

表达式 1？表达式 2：表达式 3
如果表达式1为真返回表达式2的值，如果表达式1为假返回表达式3的值

```jsp
<body>
    <%
        //1、值为null值时
        request.setAttribute("emptyNull", null);
        //2、值为空串时
        request.setAttribute("emptyStr", "");
        //3、值是Object类型数组，长度为零的时候
        request.setAttribute("emptyArr", new Object[]{});
        //4、list集合，元素个数为零
        List<String> list = new ArrayList<>();
        request.setAttribute("emptyList", list);
        //5、map集合，元素个数为零
        Map<String,Object> map = new HashMap<String, Object>();
        request.setAttribute("emptyMap", map);
        //6、其他类型数组长度为0
        request.setAttribute("emptyIntArr", new int[]{});
    %>
    ${ empty emptyNull } <br/>
    ${ empty emptyStr } <br/>
    ${ empty emptyArr } <br/>
    ${ empty emptyList } <br/>
    ${ empty emptyMap } <br/>
    ${ empty emptyIntArr} <br/>
    <%-- 三元运算   --%>
    ${ 12 != 12 ? "相等":"不相等" }
</body>
```

运行结果：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108214640)

### 4.6 "."点运算和"[]"中括号运算

点运算可以输出Bean对象的某个属性的值(getXxx或isXxx方法返回的值)
中括号运算可以输出有序集合中某个元素的值

注：中括号运算可以输出Map集合中key里含有特殊字符的key的值

```jsp
<body>
    <%
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("a.a.a", "aaaValue");
        map.put("b+b+b", "bbbValue");
        map.put("c-c-c", "cccValue");
        request.setAttribute("map", map);
    %>
    <%--特殊的key需要去掉最开始的"."并使用中括号和单引号(双引号)包起来--%>
    ${ map['a.a.a'] } <br> <%--如果不加中括号则相当于三个.运算--%> //错误的是 ${map.a.a.a}
    ${ map["b+b+b"] } <br> <%--如果不加中括号则相当于三个+运算--%>
    ${ map['c-c-c'] } <br> <%--如果不加中括号则相当于三个-运算--%>
</body>
12345678910111213
```

运行结果：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108212049)

## 5 EL表达式的11个隐含对象

![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201108212126)

EL表达式中的11个隐含对象是EL表达式自己定义的，可以直接使用

### 5.1  pageScope、requestScope、sessionScope、applicationScope对象的使用

代码演示：在web目录下创建scope.jsp

```html
<body>
    <%
        pageContext.setAttribute("key1", "pageContext1");
        pageContext.setAttribute("key2", "pageContext2");
        request.setAttribute("key2", "request");
        session.setAttribute("key2", "session");
        application.setAttribute("key2", "application");
    %>
    <%--  获取特定域中的属性  --%>
    ${ pageScope.key1 } <br>
    ${ applicationScope.key2 }
    <%--  若直接获取key1或key2依然按照之前范围从小到大检索，无法获取指定域  --%>
</body>
```

运行结果：
![image-20201108213521870](https://gitee.com/jchenTech/images/raw/master/img/20201108213521.png)

### 5.2 pageContext对象的使用

代码示例：在web目录下创建pageContext.jsp

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%-- 先通过pageContext对象获取request、session对象，再获取以下内容 --%>
    <%--
        获取请求的协议：request.getScheme()
        获取请求的服务器ip或域名：request.getServerName()
        获取请求的服务器端口号：request.getServerPort()
        获取当前工程路径：request.getContextPath()
        获取请求的方式：request.getMethod()
        获取客户端的ip地址：request.getRemoteHost()
        获取会话的唯一标识：session.getId()
    --%>
1.协议： ${ pageContext.request.scheme }<br>
2.服务器ip：${ pageContext.request.serverName }<br>
3.服务器端口：${ pageContext.request.serverPort }<br>
4.获取工程路径：${ pageContext.request.contextPath }<br>
5.获取请求方法：${ pageContext.request.method }<br>
6.获取客户端ip地址：${ pageContext.request.remoteHost }<br>
7.获取会话的id编号：${ pageContext.session.id}<br>
</body>
</html>
```

运行结果：

![image-20201108213605532](https://gitee.com/jchenTech/images/raw/master/img/20201108213605.png)

### 5.3 其他隐含对象的使用

代码示例：在web目录下创建other_el_obj.jsp

```jsp
<body>
    输出请求参数username的值：${ param.username } <br>
    输出请求参数password的值：${ param.password } <br>

    输出请求参数username的值：${ paramValues.username[0] } <br>
    输出请求参数hobby的值：${ paramValues.hobby[0] } <br>
    输出请求参数hobby的值：${ paramValues.hobby[1] } <br>
    <hr>
    输出请求头【User-Agent】的值：${ header['User-Agent'] } <br>
    输出请求头【Connection】的值：${ header.Connection } <br>
    输出请求头【User-Agent】的值：${ headerValues['User-Agent'][0] } <br>
    <hr>
    获取Cookie的名称：${ cookie.JSESSIONID.name } <br>
    获取Cookie的值：${ cookie.JSESSIONID.value } <br>
    <hr>

    输出&lt;Context-param&gt;username的值：${ initParam.username } <br>
    输出&lt;Context-param&gt;url的值：${ initParam.url } <br>

</body>
```

请求地址：
http://localhost:8080/09_EL_JSTL/other_el_obj.jsp?username=cjj057&password=666666&hobby=java&hobby=cpp

运行结果：

![image-20201108215152396](https://gitee.com/jchenTech/images/raw/master/img/20201108215152.png)

