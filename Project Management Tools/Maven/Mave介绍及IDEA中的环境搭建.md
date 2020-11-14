## 1. Maven介绍

### 1.1 什么是Maven

#### 1.1.1 Maven的定义

一个对 Maven 比较正式的定义是这么说的：Maven 是一个项目管理工具，它包含了一个项目对象模型 (POM：Project Object Model)，一组标准集合，一个项目生命周期(Project Lifecycle)，一个依赖管理系统(Dependency Management System)，和用来运行定义在生命周期阶段(phase)中插件(plugin)目标(goal)的逻辑。



#### 1.1.2 Maven能解决什么问题

可以用更通俗的方式来说明。我们知道，项目开发不仅仅是写写代码而已，期间会伴随着各种必不可少的事情要做，下面列举几个感受一下：

1、我们需要引用各种jar包，尤其是比较大的工程，引用的 jar 包往往有几十个乃至上百个， 每用到一种jar包，都需要手动引入工程目录，而且经常遇到各种让人抓狂的 jar 包冲突，版本冲突。
2、我们辛辛苦苦写好了Java 文件，可是只懂0和1的白痴电脑却完全读不懂，需要将它编译成二进制字节码。好歹现在这项工作可以由各种集成开发工具帮我们完成，Eclipse、IDEA 等都可以将代码即时编译。当然，如果你嫌生命漫长，何不铺张，也可以用记事本来敲代码，然后用 javac 命令一个个地去编译，逗电脑玩。

3、世界上没有不存在 bug 的代码，计算机喜欢 bug 就和人们总是喜欢美女帅哥一样。为了追求美为了减少 bug，因此写完了代码，我们还要写一些单元测试，然后一个个的运行来检验代码质量。

4、再优雅的代码也是要出来卖的。我们后面还需要把代码与各种配置文件、资源整合到一起，定型打包，如果是 web 项目，还需要将之发布到服务器，供人蹂躏。



试想，如果现在有一种工具，可以把你从上面的繁琐工作中解放出来，能帮你构建工程，管理 jar包，编译代码，还能帮你自动运行单元测试，打包，生成报表，甚至能帮你部署项目，生成Web站点，你会心动吗？Maven就可以解决上面所提到的这些问题。

#### 1.1.3 Maven的优势

我们通过Web阶段项目，要能够将项目运行起来，就必须将该项目所依赖的一些 jar 包添加到工程中，否则项目就不能运行。试想如果具有相同架构的项目有十个，那么我们就需要将这一份 jar包复制到十个不同的工程中。我们一起来看一个 CRM项目的工程大小。

使用传统 Web 项目构建的 CRM 项目如下：

![image-20201113114345837](https://gitee.com/jchenTech/images/raw/master/img/20201113114352.png)

原因主要是因为上面的 WEB 程序要运行，我们必须将项目运行所需的 Jar 包复制到工程目录中，从而导致了工程很大。同样的项目，如果我们使用 Maven 工程来构建，会发现总体上工程的大小会少很多。如下图:

![image-20201113114432041](https://gitee.com/jchenTech/images/raw/master/img/20201113114443.png)

小结：可以初步推断它里面一定没有 jar 包，继续思考，没有 jar 包的项目怎么可能运行呢？

### 1.2 Maven的两个经典作用

#### 1.2.1 Maven 的依赖管理

Maven 的一个核心特性就是依赖管理。当我们涉及到多模块的项目（包含成百个模块或者子项目），管理依赖就变成一项困难的任务。Maven展示出了它对处理这种情形的高度控制。
传统的WEB项目中，我们必须将工程所依赖的 jar 包复制到工程中，导致了工程的变得很大。那么maven工程是如何使得工程变得很少呢？

分析如下：

![image-20201113114735555](https://gitee.com/jchenTech/images/raw/master/img/20201113230925.png)

通过分析发现：maven 工程中不直接将 jar 包导入到工程中，而是通过在 `pom.xml` 文件中添加所需 jar包的坐标，这样就很好的避免了 jar 直接引入进来，在需要用到 jar 包的时候，只要查找 `pom.xml` 文件，再通过 `pom.xml` 文件中的坐标，到一个专门用于“存放 jar 包的仓库”(maven 仓库)中根据坐标从而找到这些 jar 包，再把这些 jar 包拿去运行。

那么问题来了
第一：“存放 jar 包的仓库”长什么样？
第二：通过读取 pom.xml 文件中的坐标，再到仓库中找到 jar 包，会不会很慢？从而导致这种方式不可行！



第一个问题：存放 jar 包的仓库长什么样，这一点我们后期会分析仓库的分类，也会带大家去看我们的本地的仓库长什么样。
第二个问题：通过 pom.xml 文件配置要引入的 jar 包的坐标，再读取坐标并到仓库中加载 jar 包，这样我们就可以直接使用 jar 包了，为了解决这个过程中速度慢的问题，maven中也有索引的概念，通过建立索引，可以大大提高加载 jar 包的速度，使得我们认为 jar 包基本跟放在本地的工程文件中再读取出来的速度是一样的。这个过程就好比我们查阅字典时，为了能够加快查找到内容，书前面的目录就好比是索引，有了这个目录我们就可以方便找到内容了，一样的在 maven 仓库中有了索引我们就可以认为可以快速找到 jar 包。



#### 1.2.2 项目的一键构建

我们的项目，往往都要经历编译、测试、运行、打包、安装 ，部署等一系列过程。

* 什么是构建？
  指的是项目从编译、测试、运行、打包、安装 ，部署整个过程都交给 maven 进行管理，这个过程称为构建。

* 一键构建？
  指的是整个构建过程，使用 maven 一个命令可以轻松完成整个工作。

Maven 规范化构建流程如下：

![image-20201113115655468](https://gitee.com/jchenTech/images/raw/master/img/20201113230926.png)

我们一起来看 Hello-Maven 工程的一键运行的过程。通过 `tomcat:run` 的这个命令，我们发现现在的工程编译，测试，运行都变得非常简单。



## 2. Maven的使用

### 2.1 Maven的安装

在此我选择的是Maven较为稳定的版本3.6.1版本。将Maven下载后，将Maven解压到一个没有中文没有空格的路径下，比如 D:\software\maven 下面。解压后目录结构如下：

![image-20201113155649125](https://gitee.com/jchenTech/images/raw/master/img/20201113230927.png)

* **bin**: 存放了 maven 的命令，比如我们前面用到的 `mvn tomcat:run`
* **boot**: 存放了一些 maven 本身的引导程序，如类加载器等
* **conf**: 存放了 maven 的一些配置文件，如 setting.xml 文件
* **lib**: 存放了 maven 本身运行所需的一些 jar 包



接下来配置Maven的环境变量，注意这个目录就是之前你解压 maven 的压缩文件包在的目录，最好不要有中文和空格。

![image-20201113155729523](https://gitee.com/jchenTech/images/raw/master/img/20201113230928.png)

然后将`%MAVEN_HOME%\bin`添加到`Path`中：

![image-20201113155615739](https://gitee.com/jchenTech/images/raw/master/img/20201113230929.png)



至此我们的 maven 软件就可以使用了，前提是你的电脑上之前已经安装并配置好了 JDK。通过 `mvn -v` 命令检查 maven 是否安装成功，看到 maven 的版本为 3.6.1 及 java 版本为 1.8 即为安装成功。找开 cmd 命令，输入 `mvn –v` 命令，如下图：

![image-20201113155818398](https://gitee.com/jchenTech/images/raw/master/img/20201113230930.png)



### 2.2 Maven仓库

maven 的工作需要从仓库下载一些 jar 包，如下图所示，本地的项目A和项目B等都会通过 maven软件从远程仓库（可以理解为互联网上的仓库）下载 jar 包并存在本地仓库，本地仓库就是本地文件夹，当第二次需要此 jar 包时则不再从远程仓库下载，因为本地仓库已经存在了，可以将本地仓库理解为缓存，有了本地仓库就不用每次从远程仓库下载了。

下图描述了 maven 中仓库的类型：

![image-20201113160419017](https://gitee.com/jchenTech/images/raw/master/img/20201113230931.png)

* **本地仓库** ：用来存储从远程仓库或中央仓库下载的插件和 jar 包，项目使用一些插件或 jar 包，优先从本地仓库查找默认本地仓库位置在 `${user.dir}/.m2/repository`，`${user.dir}`表示 windows 用户目录。后面我们会更改本地仓库位置。
* **远程仓库**：如果本地需要插件或者 jar 包，本地仓库没有，默认去远程仓库下载。
  远程仓库可以在互联网内也可以在局域网内。
* **中央仓库** ：在 maven 软件中内置一个远程仓库地址 http://repo1.maven.org/maven2 ，它是中央仓库，服务于整个互联网，它是由 Maven 团队自己维护，里面存储了非常全的 jar 包，它包含了世界上大部分流行的开源项目构件。

#### 2.2.1 配置本地仓库和阿里云镜像

下面配置本地仓库和阿里云镜像，打开 `apache-maven-3.6.1\conf\settings.xml` ，配置本地仓库：

```xml
<!-- localRepository
   | The path to the local repository maven will use to store artifacts.
   |
   | Default: ${user.home}/.m2/repository
  <localRepository>/path/to/local/repo</localRepository>
  -->
 <localRepository>D:\development\Maven\apache-maven-3.6.1\repository</localRepository>
```

为了下载 jar 包方便，在 Maven 的核心配置文件 settings.xml 文件的`<mirrors></mirrors>`标签里面配置阿里云镜像：

```xml
<mirrors>
    <!-- mirror
     | Specifies a repository mirror site to use instead of a given repository. The repository that
     | this mirror serves has an ID that matches the mirrorOf element of this mirror. IDs are used
     | for inheritance and direct lookup purposes, and must be unique across the set of mirrors.
     |
    <mirror>
      <id>mirrorId</id>
      <mirrorOf>repositoryId</mirrorOf>
      <name>Human Readable Name for this Mirror.</name>
      <url>http://my.repository.com/repo/path</url>
    </mirror>
     -->

    <mirror>
        <id>alimaven</id>
        <name>aliyun maven</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public/ </url>
        <mirrorOf>central</mirrorOf>
    </mirror>

</mirrors>
```

#### 2.2.2 全局setting与用户setting

maven 仓库地址、私服等配置信息需要在 setting.xml 文件中配置，分为全局配置和用户配置。

在 maven 安装目录下的有 `conf/setting.xml` 文件，此 `setting.xml` 文件用于 maven 的所有project项目，它作为maven的全局配置。如需要个性配置则需要在用户配置中设置，用户配置的 `setting.xml` 文件默认的位置在：`${user.dir}/.m2/settings.xml` 目录中,`${user.dir}` 指 windows 中的用户目录。

maven 会先找用户配置，如果找到则以用户配置文件为准，否则使用全局配置文件。

![image-20201113161115468](https://gitee.com/jchenTech/images/raw/master/img/20201113230932.png)

### 2.3 Maven工程的认识

#### 2.3.1 Maven 工程的目录结构

![image-20201113161241813](https://gitee.com/jchenTech/images/raw/master/img/20201113230933.png)

作为一个 maven 工程，它的 `src` 目录和 `pom.xml` 是必备的。进入 `src` 目录后，我们发现它里面的目录结构如下：

![image-20201113161425310](https://gitee.com/jchenTech/images/raw/master/img/20201113230934.png)

`src/main/java` —— 存放项目的.java 文件
`src/main/resources` —— 存放项目资源文件，如 spring, hibernate 配置文件
`src/test/java` —— 存放所有单元测试.java 文件，如 JUnit 测试类
`src/test/resources` —— 测试资源文件
`target` —— 项目输出位置，编译后的 class 文件会输出到此目录
`pom.xml`——maven 项目核心配置文件

注意：如果是普通的 java 项目，那么就没有 webapp 目录。

#### 2.3.2 Maven 工程的运行

## 3. Maven常用命令

我们可以在 cmd 中通过一系列的 maven 命令来对我们的 maven-helloworld 工程进行编译、测试、运行、打包、安装、部署。

### 3.1 compile

compile 是 maven 工程的编译命令，作用是将 `src/main/java` 下的文件编译为 class 文件输出到 target 目录下。

cmd 进入命令状态，执行 mvn compile，如下图提示成功：

![image-20201113164554964](https://gitee.com/jchenTech/images/raw/master/img/20201113230935.png)

查看 target 目录，class 文件已生成，编译完成。

![image-20201113164635813](https://gitee.com/jchenTech/images/raw/master/img/20201113230936.png)

### 3.2 test

`test` 是 maven 工程的测试命令 `mvn test`，会执行 `src/test/java` 下的单元测试类。cmd 执行 `mvn test` 执行 `src/test/java` 下单元测试类，下图为测试结果，运行 1 个测试用例，全部成功。

![image-20201113164814672](https://gitee.com/jchenTech/images/raw/master/img/20201113230937.png)

### 3.3 clean

`clean` 是 maven 工程的清理命令，执行 `clean` 会删除 target 目录及内容。

### 3.4 package

`package` 是 maven 工程的打包命令，对于 java 工程执行 `package` 打成 jar 包，对于 web 工程打成 war 包。

### 3.5 install

`install` 是 maven 工程的安装命令，执行 `install` 将 maven 打成 jar 包或 war 包发布到本地仓库。

### 3.6 Maven 指令的生命周期

maven 对项目构建过程分为三套相互独立的生命周期，请注意这里说的是“三套”，而且“相互独立”，这三套生命周期分别是：

* **Clean Lifecycle** 在进行真正的构建之前进行一些清理工作。
* **Default Lifecycle** 构建的核心部分，编译，测试，打包，部署等等。
* **Site Lifecycle** 生成项目报告，站点，发布站点。

### 3.7 Maven的概念模型

Maven 包含了一个项目对象模型 (Project Object Model)，一组标准集合，一个项目生命周期(ProjectLifecycle)，一个依赖管理系统(Dependency Management System)，和用来运行定义在生命周期阶段(phase)中插件(plugin)目标(goal)的逻辑。

![image-20201113165204625](https://gitee.com/jchenTech/images/raw/master/img/20201113230938.png)

* 项目对象模型 (Project Object Model)
一个 maven 工程都有一个 pom.xml 文件，通过 pom.xml 文件定义项目的坐标、项目依赖、项目信息、插件目标等。

* 依赖管理系统(Dependency Management System)
  通过 maven 的依赖管理对项目所依赖的 jar 包进行统一管理。
  比如：项目依赖 junit4.9，通过在 pom.xml 中定义 junit4.9 的依赖即使用 junit4.9，如下所示是 junit4.9的依赖定义：

  ```xml
  <!-- 依赖关系 -->
  <dependencies>
  	<!-- 此项目运行使用 junit，所以此项目依赖 junit -->
  	<dependency>
  	<!-- junit 的项目名称 -->
  	<groupId>junit</groupId>
  	<!-- junit 的模块名称 -->
  	<artifactId>junit</artifactId>
  	<!-- junit 版本 -->
  	<version>4.9</version>
  	<!-- 依赖范围：单元测试时使用 junit -->
  	<scope>test</scope>
  </dependency>
  ```

* 一个项目生命周期(Project Lifecycle)
  使用 maven 完成项目的构建，项目构建包括：清理、编译、测试、部署等过程，maven 将这些过程规范为一个生命周期，如下所示是生命周期的各各阶段：
  ![image-20201113165540364](https://gitee.com/jchenTech/images/raw/master/img/20201113230939.png)

  maven 通过执行一些简单命令即可实现上边生命周期的各各过程，比如执行 `mvn compile` 执行编译、执行 `mvn clean` 执行清理。

* 一组标准集合
  maven 将整个项目管理过程定义一组标准，比如：通过 maven 构建工程有标准的目录结构，有标准的生命周期阶段、依赖管理有标准的坐标定义等。
  
* 插件(plugin)目标(goal)
  maven 管理项目生命周期过程都是基于插件完成的。

## 4. idea开发Maven项目

在实战的环境中，我们都会使用流行的工具来开发项目。

### 4.1 idea的maven设置

![image-20201113172248920](https://gitee.com/jchenTech/images/raw/master/img/20201113230940.png)

![image-20201113210055852](https://gitee.com/jchenTech/images/raw/master/img/20201113230941.png)

### 4.2 在idea中创建一个maven的web工程

此处我们选择使用骨架来创建maven工程，后面建议不使用骨架创建。

![image-20201113172750562](https://gitee.com/jchenTech/images/raw/master/img/20201113230942.png)

不使用骨架创建maven工程时，他会自己创建好目录结构，不需要我们再去配置

![image-20201113212635163](https://gitee.com/jchenTech/images/raw/master/img/20201113230943.png)



点击 Next 填写项目信息

![image-20201113173233355](https://gitee.com/jchenTech/images/raw/master/img/20201113230944.png)

点击Next，此处不做改动：

![image-20201113173600743](https://gitee.com/jchenTech/images/raw/master/img/20201113230945.png)

点击Finish后开始创建工程，耐心等待，直到出现如下界面：

![image-20201113173836766](https://gitee.com/jchenTech/images/raw/master/img/20201113230946.png)

手动添加 `src/main/java` 目录，如下图右键 main文件夹——>New——>Directory

![image-20201113174031539](https://gitee.com/jchenTech/images/raw/master/img/20201113230947.png)

创建一个新的文件夹命名为 java

![image-20201113174102109](https://gitee.com/jchenTech/images/raw/master/img/20201113230948.png)

点击 OK 后，在新的文件夹 java 上右键——>Make Directory as——>Sources Root

![image-20201113210342552](https://gitee.com/jchenTech/images/raw/master/img/20201113230949.png)

#### 4.2.1 创建一个 Servlet

`src/java/main` 创建了一个 Servlet，但报错

![image-20201113210638099](https://gitee.com/jchenTech/images/raw/master/img/20201113230950.png)

要解决问题，就是要将 `servlet-api-xxx.jar` 包放进来，作为 maven 工程应当添加 servlet 的坐标，从而导入它的 jar

#### 4.2.2 在pom.xml文件添加坐标

直接打开 hello_maven 工程的 `pom.xml` 文件，再添加坐标

![image-20201113210901124](https://gitee.com/jchenTech/images/raw/master/img/20201113230951.png)

添加 jar 包的坐标时，还可以指定这个 jar 包将来的作用范围。
每个 maven 工程都需要定义本工程的坐标，坐标是 maven 对 jar 包的身份定义，比如：入门程序的坐标定义如下：

```xml
<!--项目名称，定义为组织名+项目名，类似包名-->
<groupId>com.jchen</groupId>
<!-- 模块名称 -->
<artifactId>hello_maven</artifactId>
<!-- 当前项目版本号，snapshot 为快照版本即非正式版本，release 为正式发布版本 -->
<version>1.0-SNAPSHOT</version>
<!-- 打包类型
jar：执行 package 会打成 jar 包
war：执行 package 会打成 war 包
pom ：用于 maven 工程的继承，通常父工程设置为 pom-->
<packaging>war</packaging>
```

#### 4.2.3 坐标的来源方式

添加依赖需要指定依赖 jar 包的坐标，但是很多情况我们是不知道 jar 包的的坐标，可以通过如下方式查询：

网站搜索示例：http://search.maven.org/和https://mvnrepository.com/

![image-20201113211559135](https://gitee.com/jchenTech/images/raw/master/img/20201113230952.png)

### 4.3 依赖范围

A 依赖 B，需要在 A 的 `pom.xml` 文件中添加 B 的坐标，添加坐标时需要指定依赖范围，依赖范围包括：

* **compile**：编译范围，指 A 在编译时依赖 B，此范围为默认依赖范围。编译范围的依赖会用在编译、测试、运行，由于运行时需要所以编译范围的依赖会被打包。
* **provided**：provided 依赖只有在当 JDK 或者一个容器已提供该依赖之后才使用， provided 依赖在编译和测试时需要，在运行时不需要，比如：servlet api 被 tomcat 容器提供。
* **runtime**：runtime 依赖在运行和测试系统的时候需要，但在编译的时候不需要。比如：jdbc的驱动包。由于运行时需要所以 runtime 范围的依赖会被打包。
* **test**：test 范围依赖 在编译和运行时都不需要，它们只有在测试编译和测试运行阶段可用，比如：junit。由于运行时不需要所以 test 范围依赖不会被打包。
* **system**：system 范围依赖与 provided 类似，但是你必须显式的提供一个对于本地系统中 JAR文件的路径，需要指定 systemPath 磁盘路径，system 依赖不推荐使用。

![image-20201113211935564](https://gitee.com/jchenTech/images/raw/master/img/20201113230953.png)



在 maven-web 工程中测试各 scop。测试总结：

* 默认引入 的 jar 包 ------- compile【默认范围 可以不写】（编译、测试、运行都有效 ）
* servlet-api 、jsp-api ------- provided（编译、测试有效， 运行时无效防止和 tomcat 下 jar 冲突）
* jdbc 驱动 jar 包 ---- runtime（测试、运行 有效 ）
* junit ----- test （测试有效）
  依赖范围由强到弱的顺序是：compile>provided>runtime>test

### 4.4 项目中添加的坐标

![image-20201113213305190](https://gitee.com/jchenTech/images/raw/master/img/20201113230954.png)

### 4.5 设置jdk编译版本

本教程使用jdk1.8，需要设置编译版本为 1.8，这里需要使用 maven 的插件来设置，在 `pom.xml` 中加入：

![image-20201113213654128](https://gitee.com/jchenTech/images/raw/master/img/20201113230955.png)

### 4.6 编写Servlet

在 `src/main/java` 中创建 `ServletTest`

![image-20201113213928249](https://gitee.com/jchenTech/images/raw/master/img/20201113230956.png)

### 4.7 编写JSP文件

![image-20201113214033590](https://gitee.com/jchenTech/images/raw/master/img/20201113230957.png)

### 4.8 在web.xml中配置servlet访问路径

```xml
<web-app>
  <display-name>Archetype Created Web Application</display-name>

  <servlet>
    <servlet-name>servletTest</servlet-name>
    <servlet-class>com.jchen.servlet.ServletTest</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>servletTest</servlet-name>
    <url-pattern>/maven</url-pattern>
  </servlet-mapping>
</web-app>
```

### 4.9 添加tomcat7插件

在 pom 文件中添加如下内容

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/</path>
    </configuration>
</plugin>
```

配置Configuration：

![image-20201113225551994](https://gitee.com/jchenTech/images/raw/master/img/20201113230958.png)

配置完成后，在绿色三角形run的旁边的下拉框里就有刚刚配置的项目名称了

### 4.10 运行结果

此时我们可以看到，在运行时，看到ServletTest不是一个Servlet：

![image-20201113225940552](https://gitee.com/jchenTech/images/raw/master/img/20201113230959.png)

这是因为我们在maven中导入的servlet的jar包和本地的tomcat中的servlet的jar包冲突了。

此时我们需要设置一下依赖范围：

在pom.xml文件中将serlet的jar包的`<scope>`设置为provided，此时再运行时，可以看到结果为：

![image-20201113230244103](https://gitee.com/jchenTech/images/raw/master/img/20201113231000.png)

## 5. 总结

### 5.1 maven仓库

1、maven 仓库的类型有哪些？
2、maven 工程查找仓库的流程是什么？
3、本地仓库如何配置？



### 5.2 常用的 maven 命令

常用 的 maven 命令包括：

* **compile**：编译
* **clean**：清理
* **test**：测试
* **package**：打包
* **install**：安装



### 5.3 坐标定义

在 pom.xml 中定义坐标，内容包括：groupId、artifactId、version，详细内容如下：

```xml
<!--项目名称，定义为组织名+项目名，类似包名-->
<groupId>com.jchen.maven</groupId>
<!-- 模块名称 -->
<artifactId>maven-first</artifactId>
<!-- 当前项目版本号，snapshot 为快照版本即非正式版本，release 为正式发布版本 -->
<version>0.0.1-SNAPSHOT</version>
<!-- 打包类型
jar：执行 package 会打成 jar 包
war：执行 package 会打成 war 包
pom ：用于 maven 工程的继承，通常父工程设置为 pom-->
<packaging>war</packaging>

```

### 5.4 pom基本配置

pom.xml 是 Maven 项目的核心配置文件，位于每个工程的根目录，基本配置如下：

* `<project>` ：文件的根节点 .
* `<modelversion>` ： pom.xml 使用的对象模型版本
* `<groupId>` ：项目名称，一般写项目的域名
*  `<artifactId> `：模块名称，子项目名或模块名称
* `<version>` ：产品的版本号 .
*  `<packaging> `：打包类型，一般有 jar、war、pom 等
* `<name>`：项目的显示名，常用于 Maven 生成的文档。
*  `<description> `：项目描述，常用于 Maven 生成的文档
* `<dependencies>` ：项目依赖构件配置，配置项目依赖构件的坐标
*  `<build> `：项目构建配置，配置编译、运行插件等。

