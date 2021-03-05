## 1 JavaScipt介绍

Javascript 语言诞生主要是完成页面的数据验证。因此它运行在客户端，需要运行浏览器来解析执行JavaScript 代码。
JS 是Netscape 网景公司的产品，最早取名为LiveScript;为了吸引更多java程序员。更名为JavaScript。
JS 是弱类型，Java 是强类型。
特点：

1. 交互性（它可以做的就是信息的动态交互）
2. 安全性（不允许直接访问本地硬盘）
3. 跨平台性（只要是可以解释JS 的浏览器都可以执行，和平台无关）

## 2 JavaScript 和HTML代码的结合方式

1. 只需要在head标签中，或者在body标签中， 使用script标签来书写JavaScript 代码

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
       <script type="text/javascript">
           // alert是JavaScript语言提供的一个警告框函数。
           // 它可以接收任意类型的参数，这个参数就是警告框的提示信息
           alert("hello javaScript!");
       </script>
   </head>
   <body>
   
   </body>
   </html>
   ```

2. 使用script标签引入单独的JavaScript 代码文件

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
       <!--
           现在需要使用script引入外部的js文件来执行
               src 属性专门用来引入js文件路径（可以是相对路径，也可以是绝对路径）
   
           script标签可以用来定义js代码，也可以用来引入js文件
           但是，两个功能二选一使用。不能同时使用两个功能
       -->
       <script type="text/javascript" src="1.js"></script>
       <script type="text/javascript">
           alert("国哥现在可以帅了");
       </script>
   </head>
   <body>
       
   </body>
   </html>
   ```

## 3 变量

什么是变量？变量是可以存放某些值的内存的命名。
JavaScript 的变量类型：

* 数值类型： number

* 字符串类型： string

* 对象类型： object

* 布尔类型： boolean

* 函数类型： function

  

JavaScript 里特殊的值：

* undefined 未定义，所有js 变量未赋于初始值的时候，默认值都是undefined.

* null 空值

* NaN 全称是：Not a Number。非数字。非数值。

  

JS 中的定义变量格式：

* var 变量名;

* var 变量名= 值;

## 4 关系（比较）运算

等于： == 等于是简单的做字面值的比较
全等于： === 除了做字面值的比较之外，还会比较两个变量的数据类型

## 5 逻辑运算

且运算： &&
或运算： ||
取反运算： !

在JavaScript 语言中，所有的变量，都可以做为一个boolean 类型的变量去使用。
0 、null、undefined、“”(空串) 都认为是false；

```javascript
/*
&& 且运算。
有两种情况：
第一种：当表达式全为真的时候。返回最后一个表达式的值。
第二种：当表达式中，有一个为假的时候。返回第一个为假的表达式的值
|| 或运算
第一种情况：当表达式全为假时，返回最后一个表达式的值
第二种情况：只要有一个表达式为真。就会把回第一个为真的表达式的值
并且&& 与运算和||或运算有短路。
短路就是说，当这个&&或||运算有结果了之后。后面的表达式不再执行
*/
var a = "abc";
var b = true;
var d = false;
var c = null;
```

## 6 数组

JS中数组的定义：
格式：

```javascript
var 数组名= []; // 空数组
var 数组名= [1 , ’abc’ , true]; // 定义数组同时赋值元素
```

## 7 函数（重点）

### 7.1 函数的两种定义方式

1. 使用function关键字来定义函数。
   使用的格式如下:

   ```javascript
   function 函数名(形参列表){
   函数体
   }
   ```

   在JavaScript 语言中，如何定义带有返回值的函数？
   只需要在函数体内直接使用return 语句返回值即可！

   ```html
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
       <script type="text/javascript">
           // 定义一个无参函数
           function fun(){
               alert("无参函数fun()被调用了");
           }
           // 函数调用===才会执行
           // fun();
   
           function fun2(a ,b) {
               alert("有参函数fun2()被调用了 a=>" + a + ",b=>"+b);
           }
   
           // fun2(12,"abc");
   
           // 定义带有返回值的函数
           function sum(num1,num2) {
               var result = num1 + num2;
               return result;
           }
   
           alert( sum(100,50) );
   
       </script>
   </head>
   ```

2. var 函数名= function(形参列表) { 函数体}

   ```html
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
       <script type="text/javascript">
           var fun = function () {
               alert("无参函数");
           }
           // fun();
   
           var fun2 = function (a,b) {
               alert("有参函数a=" + a + ",b=" + b);
           }
           // fun2(1,2);
   
           var fun3 = function (num1,num2) {
               return num1 + num2;
           }
   
           alert( fun3(100,200) );
       </script>
   
   </head>
   ```

   注：在Java 中函数允许重载。但是在JS 中函数的重载会直接覆盖掉上一次的定义

### 7.2 函数的arguments隐形参数（只在function 函数内）

就是在function 函数中不需要定义，但却可以直接用来获取所有参数的变量。我们管它叫隐形参数。
隐形参数特别像java 基础的可变长参数一样。
`public void fun( Object ... args );`
可变长参数其实是一个数组。那么js中的隐形参数也跟java 的可变长参数一样。操作类似数组。

```html
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <script type="text/javascript">
        function fun(a) {
            alert( arguments.length );//可看参数个数

            alert( arguments[0] );
            alert( arguments[1] );
            alert( arguments[2] );

            alert("a = " + a);

            for (var i = 0; i < arguments.length; i++){
                alert( arguments[i] );
            }

            alert("无参函数fun()");
        }
        // fun(1,"ad",true);

        // 需求：要求 编写 一个函数。用于计算所有参数相加的和并返回
        function sum(num1,num2) {
            var result = 0;
            for (var i = 0; i < arguments.length; i++) {
                if (typeof(arguments[i]) == "number") {
                    result += arguments[i];
                }
            }
            return result;
        }

        alert( sum(1,2,3,4,"abc",5,6,7,8,9) );


    </script>
</head>
```

## 8 JS中的自定义对象（扩展内容）

1. Object 形式的自定义对象
   对象的定义：

   ```javascript
   var 变量名= new Object(); // 对象实例（空对象）
   变量名.属性名= 值; // 定义一个属性
   变量名.函数名= function(){} // 定义一个函数
   ```

   对象的访问：
   `变量名.属性/ 函数名();`



2. {}花括号形式的自定义对象

   对象的定义：

   ```javascript
   var 变量名= { // 空对象
   属性名：值, // 定义一个属性
   属性名：值, // 定义一个属性
   函数名：function(){} // 定义一个函数
   };
   ```

   对象的访问：
   `变量名.属性/ 函数名();`

   ```html
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
       <script type="text/javascript">
           // 对象的定义：
           // var 变量名 = {			// 空对象
           //     属性名：值,			// 定义一个属性
           //     属性名：值,			// 定义一个属性
           //     函数名：function(){}	// 定义一个函数
           // };
           var obj = {
               name:"国哥",
               age:18,
                   fun : function () {
                   alert("姓名：" + this.name + " , 年龄：" + this.age);
               }
           };
   
           // 对象的访问：
           //     变量名.属性 / 函数名();
           alert(obj.name);
           obj.fun();
       </script>
   </head>
   ```

## 9 JS中的事件

什么是事件？事件是电脑输入设备与页面进行交互的响应。我们称之为事件。

常用的事件：

* onload 加载完成事件： 页面加载完成之后，常用于做页面js 代码初始化操作
* onclick 单击事件： 常用于按钮的点击响应操作。
* onblur 失去焦点事件： 常用用于输入框失去焦点后验证其输入内容是否合法。
* onchange 内容发生改变事件： 常用于下拉列表和输入框内容发生改变后操作
* onsubmit 表单提交事件： 常用于表单提交前，验证所有表单项是否合法。



什么是事件的注册（绑定）？

其实就是告诉浏览器，当事件响应后要执行哪些操作代码，叫事件注册或事件绑定。


事件的注册又分为静态注册和动态注册两种：

* 静态注册事件：通过html 标签的事件属性直接赋于事件响应后的代码，这种方式我们叫静态注册。

* 动态注册事件：是指先通过js 代码得到标签的dom 对象，然后再通过dom 对象.事件名= function(){} 这种形式赋于事件响应后的代码，叫动态注册。
  


动态注册基本步骤：

1. 获取标签对象

2. 标签对象.事件名= fucntion(){}

## 10 DOM模型

DOM 全称是Document Object Model 文档对象模型

大白话，就是把文档中的标签，属性，文本，转换成为对象来管理。那么它们是如何实现把标签，属性，文本转换成为对象来管理呢。这就是我们马上要学习的重点。

### 10.1 Document对象(重点)

![Document对象](https://raw.githubusercontent.com/jchenTech/images/main/img/20201024142804.jpg)

Document 对象的理解：

* 第一点：Document 它管理了所有的HTML 文档内容。
* 第二点：document 它是一种树结构的文档。有层级关系。
* 第三点：它让我们把所有的标签都对象化
* 第四点：我们可以通过document 访问所有的标签对象。



有一个人有年龄：18 岁，性别：女，名字：张某某。我们要把这个人的信息对象化怎么办?

```java
class Person {
    private int age;
    private String sex;
    private String name;
}
```

那么html 标签要对象化怎么办？

```html
<body>
	<div id="div01">div01</div>
</body>
```


模拟对象化，相当于：

```java
class Dom{
    private String id; // id 属性
    private String tagName; //表示标签名
    private Dom parentNode; //父亲
    private List<Dom> children; // 孩子结点
    private String innerHTML; // 起始标签和结束标签中间的内容
}
```

### 10.2 Document 对象中的方法介绍（重点）

`document.getElementById(elementId)`
通过标签的id 属性查找标签dom 对象，elementId 是标签的id 属性值

`document.getElementsByName(elementName)`
通过标签的name 属性查找标签dom 对象，elementName 标签的name 属性值

`document.getElementsByTagName(tagname)`
通过标签名查找标签dom 对象。tagname 是标签名

`document.createElement( tagName)`
方法，通过给定的标签名，创建一个标签对象。tagName 是要创建的标签名



注：document 对象的三个查询方法，

如果有id 属性，优先使用`getElementById` 方法来进行查询
如果没有id 属性，则优先使用`getElementsByName` 方法来进行查询
如果id 属性和name 属性都没有最后再按标签名查`getElementsByTagName`
以上三个方法，一定要在页面加载完成之后执行，才能查询到标签对象。

### 10.3 节点常用属性和方法

节点就是标签对象

方法：

通过具体的元素节点调用

1. getElementsByTagName()
   方法，获取当前节点的指定标签名孩子节点
2. appendChild( oChildNode )
   方法，可以添加一个子节点，oChildNode 是要添加的孩子节点

属性：

1. childNodes
   属性，获取当前节点的所有子节点
2. firstChild
   属性，获取当前节点的第一个子节点
3. lastChild
   属性，获取当前节点的最后一个子节点
4. parentNode
   属性，获取当前节点的父节点
5. nextSibling
   属性，获取当前节点的下一个节点
6. previousSibling
   属性，获取当前节点的上一个节点
7. className
   用于获取或设置标签的class 属性值
8. innerHTML
   属性，表示获取/设置起始标签和结束标签中的内容
9. innerText
   属性，表示获取/设置起始标签和结束标签中的文本