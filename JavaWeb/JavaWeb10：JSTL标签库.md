## 1 JSTL标签库介绍

1. JSTL标签库全称是JSP Standard Tag Library，是一个不断完善的开放源代码的JSP标签库
2. EL表达式主要是为了替换JSP中的表达式脚本，JSTL标签库是为了替换代码脚本
3. JSTL由五个不同功能的标签库组成：
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200821221629419.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80OTM0MzE5MA==,size_16,color_FFFFFF,t_70#pic_center)

## 2 JSTL标签库的使用步骤

1. 先导入JSTL标签库的jar包
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200821221649470.png#pic_center)

2. 在jsp页面中使用taglib指令引入标签库(可自动导包)

   ```
   CORE 标签库
   <%@ taglib prefix=“c” uri=“http://java.sun.com/jsp/jstl/core” %>
   XML 标签库
   <%@ taglib prefix=“x” uri=“http://java.sun.com/jsp/jstl/xml” %>
   FMT 标签库
   <%@ taglib prefix=“fmt” uri=“http://java.sun.com/jsp/jstl/fmt” %>
   SQL 标签库
   <%@ taglib prefix=“sql” uri=“http://java.sun.com/jsp/jstl/sql” %>
   FUNCTIONS 标签库
   <%@ taglib prefix=“fn” uri=“http://java.sun.com/jsp/jstl/functions” %>
   ```

## 3 core核心库的使用

### 3.1 `<c:set/>`标签

作用：可以往域中保存数据

1. scope属性设置保存到哪个域，值如下：
   * page 表示pageContext域(默认值)
   * request表示Request域
   * session表示Session域
   * application表示ServletContext域
2. var属性设置key是多少
3. value属性设置value是多少

代码演示：在web目录下创建core.jsp

```jsp
<body>
    保存之前：${ sessionScope.name } <br>
	<c:set scope="session" var="name" value="jchen"/>
	保存之后：${ sessionScope.name } <br>
</body> 
```



### 3.2 `<c:if/>`标签

作用：做if判断
test属性设置判断的条件，属性值使用EL表达式
注意：如果test属性值为真则执行if开始标签和结束标签中的语句，为假则不执行

代码演示：在web目录下创建core.jsp

```jsp
<body>
    <c:if test="${ 1 == 1 }">
        test属性值为真，我执行
    </c:if>
    <c:if test="${ 1 != 1 }">
        test属性值为假，我不执行
    </c:if>
</body>
```

运行结果：
![image-20201109164254640](https://gitee.com/jchenTech/images/raw/master/img/20201109164254.png)



### 3.3 `<c:choose>`、`<c:when>`、`<c:otherwise>`标签

作用：多路判断，类似switch-case结构

1. `choose`标签表示开始多路判断

2. `when`标签表示每一种判断情况
   `test`属性设置判断的条件，属性值使用EL表达式
   注意：若`test`属性值为真，则执行`when`起始和结束标签中的语句

3.  `otherwise`标签表示其余情况，若满足，则执行起始和结束标签中的语句
   注意：

   1. 使用三种标签时标签里不能使用html注释，要使用jsp注释

   2. when标签的父标签一定是choose标签
   3. when标签是从上向下依次判断的，一旦有满足的就不会再判断剩余when标签

```jsp
<body>
    <%
        request.setAttribute("grade", 55);
    %>
    <c:choose>
        <c:when test="${ requestScope.grade > 90 }">
            <div>绩点4.0</div>
        </c:when>
        <c:when test="${ requestScope.grade > 80 }">
            <h2>绩点3.0</h2>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${ requestScope.grade > 70 }">
                    <h2>绩点2.0</h2>
                </c:when>
                <c:otherwise>
                    绩点1.0或无
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</body>
```

运行结果：
![image-20201109164938117](https://gitee.com/jchenTech/images/raw/master/img/20201109164938.png)



### 3.4 `<c:forEach/>`标签

作用：遍历输出(与foreach循环一样，自动的遍历到下一个数据)

① 遍历1到10

代码演示：在web目录下创建foreach.jsp

```jsp
<body>
    <%--
        begin属性设置开始的索引
        end属性设置结束的索引
        var属性表示循环的变量(当前正在遍历到的数据)，可任意取名
        循环的是foreach起始和结束标签中的内容
    --%>
    <c:forEach begin="1" end="10" var="i">
        第${i}行 
    </c:forEach>
</body>
1234567891011
```

运行结果：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201109172852)



② 遍历数组

代码演示：在web目录下创建foreach.jsp

```jsp
<body>
    <%--
        items表示遍历的数据源
        var表示当前遍历到的数据，可任意取名
    --%>
    <%
        request.setAttribute("arr", new String[]{"周杰伦","昆凌","方文山"});
    %>
    <c:forEach items="${ requestScope.arr }" var="item">
        ${ item } <br>
    </c:forEach>
</body>
```

运行结果：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201109172853)



③ 遍历Map集合

```jsp
<body>
    <%
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        request.setAttribute("map", map);
    %>
    <c:forEach items="${ requestScope.map }" var="entry">
        ${entry.key} 等于 ${entry.value} <br>
        <%--  ${entry}会将所有数据以 key=value 格式输出  --%>
    </c:forEach>
</body>
```

运行结果：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201109172854)

④ 遍历List集合，集合中存放Student类

代码演示1：创建Student类

```java
public class Student {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String phone;
    //以及全参、空参构造器，get、set方法、toString方法
}
```

代码演示2：在web目录下创建foreach.jsp

```jsp
<body>
    <%
        List<Student> studentList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            studentList.add(new Student(i,"username"+i ,"pass"+i,18+i,"phone"+i));
        }
        request.setAttribute("stus", studentList);
    %>
    <%--
        items 表示遍历的数据源
        var 表示遍历到的数据
        begin表示遍历的开始索引值(起始为0)，不写begin代表从第一个开始
        end 表示结束的索引值，不写end代表遍历到最后一个
        step 属性表示遍历的步长值，默认是1
        varStatus 属性表示当前遍历到的数据的状态
    --%>
    <c:forEach items="${requestScope.stus}" var="stu" begin="2"
                        end="7" step="2" varStatus="status">
            ${stu.id} <br>
            ${stu.username} <br>
            ${stu.password} <br>
            ${stu.age} <br>
            ${stu.phone} <br>
            ${status.step} <br> <%--还可获取更多状态，见下图--%>
    </c:forEach>
    <%--运行结果：从3输出到8，每隔两个输出，即只有3、5、7--%>
</body>
```

varStatus属性可以获得的状态：
![在这里插入图片描述](https://gitee.com/jchenTech/images/raw/master/img/20201109172402)