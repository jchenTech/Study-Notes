# jQuery介绍

1. 什么是jQuery ?
   jQuery，顾名思义，也就是JavaScript 和查询（Query），它就是辅助JavaScript 开发的js 类库。
2. jQuery 核心思想！！！
   它的核心思想是write less, do more(写得更少,做得更多)，所以它实现了很多浏览器的兼容问题。
3. jQuery 流行程度
   jQuery 现在已经成为最流行的JavaScript 库，在世界前10000 个访问最多的网站中，有超过55%在使用
   jQuery。
4. jQuery 好处！！！
   jQuery 是免费、开源的，jQuery 的语法设计可以使开发更加便捷，例如操作文档对象、选择DOM 元素、制作动画效果、事件处理、使用Ajax以及其他功能

# jQuery初体验

需求：使用jQuery 给一个按钮绑定单击事件?

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript" src="../script/jquery-1.7.2.js"></script>
<script type="text/javascript">

	//使用$()代替window.onload
	$(function(){
		//使用选择器获取按钮对象，随后绑定单击响应函数
		$("#btnId").click(function(){
			//弹出Hello
			alert('Hello');
		});
	});

</script>


</head>
<body>

	<button id="btnId">SayHello</button>

</body>
</html>	
```

常见问题？

1. 使用jQuery 一定要引入jQuery 库吗？ 答案： 是，必须
2. jQuery 中的$到底是什么？ 答案： 它是一个函数
3. 怎么为按钮添加点击响应函数的？ 答案：
   * 使用jQuery 查询到标签对象
   * 使用标签对象.click( function(){} );

# jQuery核心函数

`$` 是jQuery 的核心函数，能完成jQuery的很多功能。`$()`就是调用$这个函数

1. 传入参数为[ 函数] 时：
   表示页面加载完成之后。相当于window.onload = function(){}
2. 传入参数为[ HTML 字符串] 时：
   会对我们创建这个html 标签对象
3. 传入参数为[ 选择器字符串] 时：
   $(“#id 属性值”);    id 选择器，根据id 查询标签对象
   $(“标签名”);           标签名选择器，根据指定的标签名查询标签对象
   $(“.class 属性值”); 类型选择器，可以根据class 属性查询标签对象
4. 传入参数为[ DOM 对象] 时：
   会把这个dom 对象转换为jQuery 对象

# jQuery对象和dom对象区分

## 什么是jQuery对象，什么是dom对象

Dom 对象

1. 通过getElementById()查询出来的标签对象是Dom对象

2. 通过getElementsByName()查询出来的标签对象是Dom对象

3. 通过getElementsByTagName()查询出来的标签对象是Dom对象

4. 通过createElement() 方法创建的对象，是Dom对象
   DOM 对象Alert 出来的效果是：[object HTML标签名Element]

jQuery 对象

1. 通过JQuery 提供的API 创建的对象，是JQuery 对象
2. 通过JQuery 包装的Dom 对象，也是JQuery 对象
3. 通过JQuery 提供的API 查询到的对象，是JQuery 对象
      jQuery 对象Alert 出来的效果是：[object Object]

## 问题：jQuery对象的本质是什么？

jQuery对象是dom对象的数组+jQuery提供的一系列功能函数。

## jQuery对象和Dom对象使用区别

jQuery对象不能使用DOM对象的属性和方法
DOM对象也不能使用jQuery对象的属性和方法

## Dom对象和jQuery对象互转

1. dom 对象转化为jQuery 对象（*重点）
   * 先有DOM 对象
   * $( DOM 对象) 就可以转换成为jQuery 对象

2. jQuery 对象转为dom 对象（*重点）
   * 先有jQuery 对象
   * jQuery 对象[下标]取出相应的DOM 对象

![Dom对象和jQuery对象互转](https://raw.githubusercontent.com/jchenTech/images/main/img/20201024165641.jpg)

# jQuery选择器（重点）

## 基本选择器（重点）

* #ID 选择器：根据id 查找标签对象
* .class 选择器：根据class 查找标签对象
* element 选择器：根据标签名查找标签对象

* *选择器：表示任意的，所有的元素
* selector1，selector2 组合选择器：合并选择器1，选择器2 的结果并返回



`p.myClass`
表示标签名必须是p 标签，而且class 类型还要是myClass

## 层级选择器（重点）

* ancestor descendant 后代选择器：在给定的祖先元素下匹配所有的后代元素
* parent > child 子元素选择器：在给定的父元素下匹配所有的子元素
* prev + next 相邻元素选择器：匹配所有紧接在prev 元素后的next 元素
* prev ~ sibings 之后的兄弟元素选择器：匹配prev 元素之后的所有siblings 元素