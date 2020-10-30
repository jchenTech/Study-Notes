# 1 JavaWeb的概念

1. JavaWeb是指所有通过Java语言编写的可以通过浏览器访问的程序的总称，
   JavaWeb是基于请求和响应来开发的

2. 请求(Request)：指客户端给服务器发送数据

3. 响应(Response)：指服务器给客户端回传数据

4. 请求和响应的关系：

   ![请求与响应](C:\Users\lenovo\Desktop\1.png)

# 2 Web资源的分类

静态资源：html，css，js，txt，mp4，jpg等
动态资源：jsp页面，Servlet程序

# 3 Tomcat概念

由Apache组织提供的一种Web服务器，提供对jsp和Servlet的支持，它是一种轻量级的javaweb容器(服务器)，也是目前应用最广泛的Javaweb服务器(免费)

# 4 Tomcat服务器和Servlet版本的对应关系

![20200804133524551](C:\Users\lenovo\Desktop\20200804133524551.png)

# 5 Tomcat的使用

1. 将所需的zip压缩包解压到需要安装的目录

2. 解压之后的目录介绍

   * bin：存放Tomcat服务器的可执行程序
   * conf：存放Tomcat服务器的配置文件
   * lib：存放Tomcat服务器的jar包
   * logs：存放Tomcat服务器运行时输出的日志信息
   * temp：存放Tomcat服务器运行时产生的临时数据
   * webapps：存放部署的Web工程
   * work：存放Tomcat运行时jsp翻译为Servlet的源码和Session钝化的目录

3. 配置JAVA_HOME环境变量

   若不配置，会导致启动服务器失败：双击bin目录下的startup.bat文件会出现一个小黑窗 口一闪而过，此时代表未启动成功，需要配置JAVA_HOME环境变量

4. 启动Tomcat服务器

   * 找到Tomcat目录下的bin目录下的startup.bat文件，双击即可启动Tomcat服务器

   * 测试Tomcat服务器启动成功与否(以下三选一即可):

     在浏览器的地址栏中输入以下地址(此时访问到的是Tomcat目录下的webapps目录中)：

     1. http://localhost:8080
     2. http://127.0.0.1:8080
     3. http://真实ip:8080

5. 另一种启动Tomcat服务器的方式
   * 打开命令行
   * cd到Tomcat安装目录下的bin目录下
   * 敲入启动命令：`catalina run`

6. Tomcat服务器的停止

   以下方式三选一：

   * 点击已经启动Tomcat服务器的命令行窗口的x关闭按钮
   * 把Tomcat服务器窗口设置为当前窗口，然后按快捷键ctrl + c
   * 双击Tomcat安装目录下的bin目录下的shutdown.bat文件

7. 修改Tomcat的端口号

   Tomcat的默认端口号是8080，修改方法如下：

   * 找到Tomcat目录下的conf目录，打开server.xml配置文件
   * 找到Connector标签，修改port属性值为想要的端口号
   * 修改完端口号需重启Tomcat服务器方可生效

8. 部署web工程到Tomcat中

   * 第一种部署方法：将web工程的目录拷贝到Tomcat的webapps目录下即可
     步骤如下：

     1. 在webapps目录下创建一个book工程(文件夹)：

     2. 将工程拷贝到book文件夹中

     3. 访问Tomcat下的web工程

        在浏览器的地址栏中输入：http://localhost:8080/工程名/文件名
        如：http://localhost:8080/book/index.html，即可打开索引页

   * 第二种部署方法：创建配置文件
     步骤如下：

     1. 在`:/apache-tomcat-8.0.50/conf/Catalina/localhost`目录下创建任意名字的xml文件

     2. 此xml文件(UTF-8格式)中的内容为：
        `<Context path="/test" docBase="E:\book"/>`

        其中：

        * `Context`代表一个工程的上下文
        * `path`代表工程的访问路径，即：/此文件名
        * `docBase`代表要访问的工程目录在哪里(可以访问磁盘中的任何目录)
          注意：默认打开的是工程的index.html文件（如果目录路径有中文字符好像无法成功？）

9. 访问方式的区别

   1. 拖动html页面到浏览器：

      * 浏览器中的地址栏为：`file:///E:/Java%E8%AF%BE%E7%A8%8B/JavaWeb/%E8%B5%84%E6%96%99/05-XML%20&%20Tomcat/%E8%B5%84%E6%96%99/apache-tomcat-8.0.50/webapps/book/index.html` 这里的字符是因为路径中有中文字符，因此解析成了这串复杂的字符

      * 使用的是`file://`协议：浏览器直接读取file协议后面的路径，解析展示在浏览器上即可

   2. 在浏览器的地址栏中输入http协议：

   ![访问原因](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030140745.jpg)

   

10. ROOT的工程的访问，以及默认index.html 页面的访问

    * 当我们在浏览器地址栏中输入访问地址如下：
      `http://ip:port/` ====>>>> 没有工程名的时候，默认访问的是ROOT 工程。
    * 当我们在浏览器地址栏中输入的访问地址如下：
      `http://ip:port/工程名/` ====>>>> 没有资源名，默认访问index.html 页面

# 6 IDEA整合Tomcat服务器

1. IDEA中创建动态web工程

   ![2020080413392171](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030143439.png)

2. 在IDEA中部署工程到Tomcat中运行
   
   * 每次创建web工程时，会伴随着一个Tomcat实例一起创建，如下：
     ![20200804133930593](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030143449.png)

   * 为了防止多个web工程的Tomcat实例混淆，一般将Tomcat实例重命名为web工程的名字， 点击上图的Edit Configurations，如下：
     ![在这里插入图片描述](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030145418.png)
   
   * 确认Tomcat实例中有要部署运行的web工程，如下：
   
     ![在这里插入图片描述](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030145352.平、)

3. 在IDEA中运行、停止Tomcat实例
   * 启动Tomcat实例：
     ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200804134026228.png)
   * Debug方式启动Tomcat实例：
     ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020080413403187.png)
   *  停止运行Tomcat实例：
     ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200804134043115.png)
   * 重启Tomcat运行实例：
     ![重启](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030152906.jpg)

4. 配置资源热部署
   热部署就是正在运行状态的应用，修改了他的源码之后，在不重新启动的情况下能够自动把增量内容编译并部署到服务器上，使得修改立即生效。热部署为了解决的问题有两个， 一是在开发的时候，修改代码后不需要重启应用就能看到效果，大大提升开发效率；二是生产上运行的程序，可以在不停止运行的情况下进行升级，不影响用户使用。

   ![资源热部署](https://raw.githubusercontent.com/jchenTech/images/main/img/20201030152815.jpg)

