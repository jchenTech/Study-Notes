## 1 文件的上传(重点)

2. form标签的`encType`属性值必须为`multipart/form-data`
3. 在form标签中使用`input type=file`添加上传的文件
4. 在form标签中使用`input type=submit`提交到服务器
5. 编写Servlet程序接收、处理上传的文件

注意：`encType = multipart/fourm-data`表示提交的数据以多段(每一个表单项表示一个数据段)的形式进行拼接，然后以二进制流的形式发送给服务器

代码演示(1)：在web目录下创建upload.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="http://localhost:8080/09_EL_JSTL/uploadServlet" method="post" enctype="multipart/form-data">
        用户名：<input type="text" name="username" /> <br>
        头像：<input type="file" name="photo" /> <br>
        <input type="submit" value="上传">
    </form>
</body>
</html>

```

代码演示(2)：创建Servlet程序UploadServlet.java

```java
public class UploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("上传成功！");
    }
}
```

代码演示(3)：在web.xml中编写servlet标签

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
    <servlet>
        <servlet-name>UploadServlet</servlet-name>
        <servlet-class>com.atguigu.servlet.UploadServlet</servlet-class>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>UploadServlet</servlet-name>
        <url-pattern>/uploadServlet</url-pattern>
    </servlet-mapping>
</web-app>
```

运行结果：

![image-20201109191521025](https://gitee.com/jchenTech/images/raw/master/img/20201109191521.png)

点击上传后，控制台输出：上传成功了！



![1](https://gitee.com/jchenTech/images/raw/master/img/20201109192449.jpg)
注意：谷歌浏览器中上传的文件的数据显示的是空行，但服务器可以接收到数据

## 2 服务器对上传的数据进行解析

 首先导入两个jar包 (fileupload包依赖io包)：
![image-20201109192719665](https://gitee.com/jchenTech/images/raw/master/img/20201109192719.png)



两个jar包中常用的类 (导入的jar包是commons的)：

1. `ServletFileUpload`类，用于解析上传的数据
   * `public static final boolean isMultipartContent(HttpServletRequest request)`
     如果上传的数据是多段的形式，返回true，只有多段的数据才是文件上传的
   * `public ServletFileUpload()`
     空参构造器
   * `public ServletFileUpload(FileItemFactory fileItemFactory)`
     参数为工厂实现类的构造器
   * `public List parseRequest(HttpServletRequest request)`
     解析上传的数据，返回包含每一个表单项的List集合
2. `FileItem`类，表示每一个表单项
   * `public boolean isFormField()`
     如果当前表单项是普通表单项，返回true，文件类型返回false
   * `public String getFieldName()`
     获取当前表单项的name属性值
   * `public String getString()`
     获取当前表单项的value属性值，参数为”UTF-8”可解决乱码问题
   * `public String getName()`
     获取上传的文件名
   * `public void write(File file)`
     将上传的文件写到参数File所指向的硬盘位置



代码演示(1)：Servlet程序UploadServlet.java

```java
public class UploadServlet extends HttpServlet {
    /*
     * 用来处理上传的数据
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1 先判断上传的数据是否是多段数据（只有多段的数据才是文件上传的）
        if (ServletFileUpload.isMultipartContent(req)) {
            //创建FileItemFactory工厂实现类
            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            //创建用于解析上传数据的工具类ServletFileUpload类
            ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
            //解析上传的数据，得到每一个表单项FileItem
            try {
                List<FileItem> list = servletFileUpload.parseRequest(req);
                //循环判断：每一个表单项是普通类型还是上传的文件
                for (FileItem fileItem : list) {
                    if (fileItem.isFormField()) {
                        //普通表单项
                        System.out.println("表单项的name属性值：" + fileItem.getFieldName());
                        System.out.println("表单项的value属性值：" + fileItem.getString("UTF-8"));

                    } else {
                        //上传的文件
                        System.out.println("表单项的name属性值：" + fileItem.getFieldName());
                        System.out.println("上传的文件名：" + fileItem.getName());

                        fileItem.write(new File("e:\\" + fileItem.getName()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```


浏览器中上传文件后控制台运行结果：
![image-20201109194818604](https://gitee.com/jchenTech/images/raw/master/img/20201109194818.png)

在E盘中出现了我们上传的文件
注意：上传的不仅可以是图片，还可以是其他格式

## 3 文件下载的过程

1. 获取要下载的文件名
2. 获取要下载的文件内容
3. 将下载的文件内容回传给客户端
4. 在回传前，通过响应头告诉客户端返回的数据类型
5. 还要告诉客户端收到的数据是用于下载使用（使用响应头）

## 4 文件下载过程详解

```java
public class Download extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        1. 获取要下载的文件名
        String downloadFilename = "2.jpg";
//        2. 获取要下载的文件内容
        ServletContext servletContext = getServletContext();
        //获取要下载的文件类型
        String mimeType = servletContext.getMimeType("/file/" + downloadFilename);
        System.out.println("下载的文件类型：" + mimeType);
//        4. 在回传前，通过响应头告诉客户端返回的数据类型
        resp.setContentType(mimeType);
//        5. 还要告诉客户端收到的数据是用于下载使用（使用响应头）
        //Content-Disposition响应头，表示收到的数据怎么处理
        //attachment表示附件，表示下载使用
        //filename=表示指定下载的文件名
        resp.setHeader("Content-Disposition", "attachment; fileName=" + downloadFilename);

        // /斜杠被服务器解析表示地址为：hhtp://ip:port/工程名/映射到代码的web目录
        InputStream resourceAsStream = servletContext.getResourceAsStream("/file/" + downloadFilename);
        OutputStream outputStream = resp.getOutputStream();
//        3. 将下载的文件内容回传给客户端
        //读取输入流中全部的数据，复制给输出流去输出
        IOUtils.copy(resourceAsStream, outputStream);


    }
}
```

## 5 中文名下载文件的乱码问题

原因：
`response.setHeader(“Content-Disposition”, “attachment; fileName=中文名.jpg”);`
如果下载的文件是中文名，会发现下载的文件无法正常显示汉字，原因是响应头中不能有汉字

解决： 

### 5.1 URLEncoder

如果客户端浏览器是IE浏览器或者是谷歌浏览器。需要使用`URLEncoder`类先对中文名进行UTF-8编码，因为IE浏览器和谷歌浏览器收到含有编码的字符串后会以UTF-8字符集进行解码显示，代码如下：

```java
//把中文名进行UTF-8编码操作
String str = "attachment; filename=" + URLEncoder.encode("中文名.jpg", "UTF-8");
//把编码后的字符串设置到响应头中
resp.setHeader("Content-Disposition", str);
```

### 5.2 BASE64编解码

如果客户端浏览器是火狐浏览器。那么我们需要对中文名进行BASE64的编码操作。

代码演示：BASE64编解码的操作

```java
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
//jdk8之后不再支持上述两个类
public class Base64Test {
    public static void main(String[] args) throws Exception {
        String content = "这是需要Base64编码的内容";
        System.out.println("初始内容：" + content);
        // 创建一个Base64编码器
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 执行Base64编码操作，encode()参数是字节数组
        String encodedString = base64Encoder.encode(content.getBytes("UTF-8"));
        System.out.println("编码后的结果：" + encodedString );
        // 创建Base64解码器
        BASE64Decoder base64Decoder = new BASE64Decoder();
        // 解码操作
        byte[] bytes = base64Decoder.decodeBuffer(encodedString);
        //以utf-8编码，以utf-8解码
        String str = new String(bytes, "UTF-8");
        System.out.println("解码后的结果：" + str);
    }
}
```