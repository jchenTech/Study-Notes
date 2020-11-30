+++
title = "JavaWeb连载12 | JSON和AJAX"
categories = ["JavaWeb"]
date = "2020-11-11T14:00:00+08:00"
tags = ["JSON", "AJAX"]

+++

## 1 什么是JSON？

JSON (JavaScript Object Notation) 是一种轻量级的数据交换格式。易于人阅读和编写。同时也易于机器解析和生成。JSON采用完全独立于语言的文本格式，而且很多语言都提供了对 json 的支持（包括 C, C++, C#, Java, JavaScript, Perl, Python等）。 这样就使得 JSON 成为理想的数据交换格式。

json 是一种轻量级的数据交换格式。**轻量级**指的是跟 xml 做比较。数据交换指的是客户端和服务器之间业务数据的传递格式。

### 1.1 JSON在JavaScript中的使用

#### 1.1.1 JSON的定义

JSON是由键值对组成，并且由花括号（大括号）包围。每个键由引号引起来，键和值之间使用冒号进行分隔，多组键值对之间进行逗号进行分隔。

JSON定义示例：

```json
var jsonObj = {
    "key1":12,
    "key2":"abc",
    "key3":true,
    "key4":[11,"arr",false],
    "key5":{
        "key5_1" : 551,
        "key5_2" : "key5_2_value"
    },
    "key6":[{
        "key6_1_1":6611,
        "key6_1_2":"key6_1_2_value"
    },{
        "key6_2_1":6621,
        "key6_2_2":"key6_2_2_value"
    }]
};
```

#### 1.1.2 JSON的访问

* json 本身是一个对象。
* json 中的 key 我们可以理解为是对象中的一个属性。
* json 中的 key 访问就跟访问对象的属性一样： json 对象.key



json 访问示例：

```json
alert(typeof(jsonObj));//Object jason就是一个对象

//json的访问
alert(jsonObj.key1);//12
alert(jsonObj.key2);//abc
alert(jsonObj.key3);//true
alert(jsonObj.key4);//得到数组[11,"arr",false]
//数组值的遍历
for(var i = 0; i < jsonObj.key4.length; i++) {
	alert(jsonObj.key4[i]);
}

alert( jsonObj.key6 );// 得到json数组

// 取出来每一个元素都是json对象
var jsonItem = jsonObj.key6[0];
// alert( jsonItem.key6_1_1 ); //6611
alert( jsonItem.key6_1_2 ); //key6_1_2_value
```

#### 1.1.3 JSON的两个常用方法

JSON的存在有两种形式。

1. 对象的形式存在，我们叫它JSON对象。
2. 字符串的形式存在，我们叫它JSON字符串。

一般我们要操作JSON中的数据的时候，需要 JSON对象的格式。
一般我们要在客户端和服务器之间进行数据交换的时候，使用 JSON字符串。

1. `JSON.stringify()` 把 JSON对象转换成为 JSON字符串
2. `JSON.parse()` 把 JSON字符串转换成为 JSON对象

示例代码：

```json
// json对象转字符串
var jsonObjString = JSON.stringify(jsonObj);
alert(jsonObjString);

// json字符串转json对象
var jsonObj2 = JSON.parse(jsonObjString);
alert(jsonObj2.key1);
alert(jsonObj2.key2);
```

### 1.2 JSON在Java中的使用

1. 需要先导入jar包 `Gson`
2. 创建`Gson`实例，调用 (1) `toJson()`方法：将参数转换成JSON字符串
   (2) `fromJson()`方法：将参数JSON字符串还原

#### 1.2.1 JavaBean和JSON的互转

```java
//    1.2.1、javaBean和json的互转
@Test
public void test1(){
    Person person = new Person(1,"国哥好帅!");
    // 创建Gson对象实例
    Gson gson = new Gson();
    // toJson方法可以把java对象转换成为json字符串
    String personJsonString = gson.toJson(person);
    System.out.println(personJsonString);
    // fromJson把json字符串转换回Java对象
    // 第一个参数是json字符串
    // 第二个参数是转换回去的Java对象类型
    Person person1 = gson.fromJson(personJsonString, Person.class);
    System.out.println(person1);
}
```

#### 1.2.2 List和JSON的互转

```java
//    1.2.2、List 和json的互转
@Test
public void test2() {
    List<Person> personList = new ArrayList<>();

    personList.add(new Person(1, "国哥"));
    personList.add(new Person(2, "康师傅"));

    Gson gson = new Gson();

    // 把List转换为json字符串
    String personListJsonString = gson.toJson(personList);
    System.out.println(personListJsonString);

    List<Person> list = gson.fromJson(personListJsonString, new PersonListType().getType());
    System.out.println(list);
    Person person = list.get(0);
    System.out.println(person);
}
```

#### 1.2.3 Map和JSON的互转

```java
//    1.2.3、map 和json的互转
@Test
public void test3(){
    Map<Integer,Person> personMap = new HashMap<>();

    personMap.put(1, new Person(1, "国哥好帅"));
    personMap.put(2, new Person(2, "康师傅也好帅"));

    Gson gson = new Gson();
    // 把 map 集合转换成为 json字符串
    String personMapJsonString = gson.toJson(personMap);
    System.out.println(personMapJsonString);

    //        Map<Integer,Person> personMap2 = gson.fromJson(personMapJsonString, new PersonMapType().getType());
    Map<Integer,Person> personMap2 = gson.fromJson(personMapJsonString, new TypeToken<HashMap<Integer,Person>>(){}.getType());

    System.out.println(personMap2);
    Person p = personMap2.get(1);
    System.out.println(p);

}
```

## 2 AJAX请求

### 2.1 什么是AJAX请求

> **AJAX的概念：AJAX (异步的JavaScript和XML) 是一种浏览器异步发起请求，局部更新页面的技术。通过在后台与服务器进行少量数据交换，Ajax可以使网页实现异步更新。这意味着可以在不重新加载整个网页的情况下，对网页的某部分进行更新。传统的网页(不使用Ajax)如果需要更 新内容，必须重载整个网页页面。**

异步与同步的概念：

![123](https://gitee.com/jchenTech/images/raw/master/img/20201111115559)

### 2.2 JavaScript原生AJAX请求

原生的 Ajax 请求：

1. 我们首先要创建 `XMLHttpRequest` 对象
2. 调用 `open` 方法设置请求参数
3. 调用 `send` 方法发送请求
4. 在 `send` 方法前绑定 `onreadystatechange` 事件，处理请求完成后的操作。


1） 创建一个 html 页面，发起请求。代码如下：

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="Expires" content="0" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
		<script type="text/javascript">
			function ajaxRequest() {
// 				1、我们首先要创建XMLHttpRequest 
				var xmlhttprequest = new XMLHttpRequest();
// 				2、调用open方法设置请求参数
				xmlhttprequest.open("GET","http://localhost:8080/16_json_ajax_i18n/ajaxServlet?action=javaScriptAjax",true);
// 				4、在send方法前绑定onreadystatechange事件，处理请求完成后的操作。
				xmlhttprequest.onreadystatechange = function(){
					if (xmlhttprequest.readyState == 4 && xmlhttprequest.status == 200) {
						alert("收到服务器返回的数据：" + xmlhttprequest.responseText);
						var jsonObj = JSON.parse(xmlhttprequest.responseText);
						// 把响应的数据显示在页面上
						document.getElementById("div01").innerHTML = "编号：" + jsonObj.id + " , 姓名：" + jsonObj.name;
					}
				}
// 				3、调用send方法发送请求
				xmlhttprequest.send();
				alert("我是最后一行的代码");
			}
		</script>
	</head>
	<body>	
		<button onclick="ajaxRequest()">ajax request</button>
		<div id="div01">
		</div>
	</body>
</html>
```

2 ） 创建一个 AjaxServlet 程序接收请求

```java
public class AjaxServlet  extends BaseServlet{
    protected void javaScriptAjax(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Ajax请求过来了");
        Person person = new Person(1, "国哥");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // json格式的字符串
        Gson gson = new Gson();
        String personJsonString = gson.toJson(person);

        resp.getWriter().write(personJsonString);
    }
}
```

3 ）在 web.xml 文件中的配置：

```xml
<servlet>
    <servlet-name>AjaxServlet</servlet-name>
    <servlet-class>com.atguigu.servlet.AjaxServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>AjaxServlet</servlet-name>
    <url-pattern>/ajaxServlet</url-pattern>
</servlet-mapping>
```



### 2.3 jQuery中的AJAX请求

四个Ajax请求方法：

1. `$.ajax` 方法
2. `$.get` 方法
3. `$.post` 方法
4. `$.getJSON` 方法

一个表单序列化方法： `serialize()`表单序列化方法



**$.ajax请求参数：**

* `url` 表示请求的地址
* `type` 表示请求的类型 GET 或 POST 请求
* `data` 表示发送给服务器的数据
  格式有两种：
  1. `name=value&name=value`
  2. `{key:value}`
* `success` 请求成功，响应的回调函数
* `dataType` 响应的数据类型
  常用的数据类型有：
  1. `text` 表示纯文本
  2. `xml` 表示 xml 数据
  3. `json` 表示 json 对象

```javascript
// ajax请求
$("#ajaxBtn").click(function(){
    $.ajax({
        url:"http://localhost:8080/16_json_ajax_i18n/ajaxServlet",
        // data:"action=jQueryAjax",
        data:{action:"jQueryAjax"},
        type:"GET",
        success:function (data) {
            // alert("服务器返回的数据是：" + data);
            // var jsonObj = JSON.parse(data);
            $("#msg").html(" ajax 编号：" + data.id + " , 姓名：" + data.name);
        },
        dataType : "json"
    });
});
```



**`$.get`请求与`$.post`请求：**

* `url`:请求的 URL 地址
* `data`:待发送 Key/value 参数。
* `callback`:载入成功时回调函数。
* `type`:返回内容格式，xml, html, script, json, text。

```javascript
// ajax--get请求
$("#getBtn").click(function(){

   $.get("http://localhost:8080/16_json_ajax_i18n/ajaxServlet","action=jQueryGet",function (data) {
      $("#msg").html(" get 编号：" + data.id + " , 姓名：" + data.name);
   },"json");
   
});

// ajax--post请求
$("#postBtn").click(function(){
   // post请求
   $.post("http://localhost:8080/16_json_ajax_i18n/ajaxServlet","action=jQueryPost",function (data) {
      $("#msg").html(" post 编号：" + data.id + " , 姓名：" + data.name);
   },"json");
   
});
```



**JQuery 的`$.getJSON`：**

* url:待载入页面的 URL 地址
* data:待发送 Key/value 参数。
* callback:载入成功时回调函数

```javascript
// ajax--getJson请求
$("#getJSONBtn").click(function(){
   $.getJSON("http://localhost:8080/16_json_ajax_i18n/ajaxServlet","action=jQueryGetJSON",function (data) {
      $("#msg").html(" getJSON 编号：" + data.id + " , 姓名：" + data.name);
   });
});
```



**表单的序列化：**

`serialize()` 方法可以把一个 form 表单中所有的表单项。都以字符串 `name=value&name=value` 的形式进行拼接，省去我们很多不必要的工作。

```javascript
// ajax请求
$("#submit").click(function(){
   // 把参数序列化
   $.getJSON("http://localhost:8080/16_json_ajax_i18n/ajaxServlet","action=jQuerySerialize&" + $("#form01").serialize(),function (data) {
      $("#msg").html(" Serialize 编号：" + data.id + " , 姓名：" + data.name);
   });
});
```

