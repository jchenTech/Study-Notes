## 1 软件版本

- 物理宿主机系统：`Windows 10` 专业版
- 虚拟机软件：`VMware Workstation Pro 16 `版本
- `CentOS`操作系统`ISO`镜像：`CentOS 7.9 64位`

## 2 安装Linux操作系统

**1、创建新的虚拟机**

![image-20201129174357778](https://gitee.com/jchenTech/images/raw/master/img/20201129174357.png)

**2、选择虚拟机兼容性**

![image-20201129174508586](https://gitee.com/jchenTech/images/raw/master/img/20201129174508.png)

默认即可

**3、加载Linux系统ISO镜像**

这里选择稍后安装操作系统（需要在虚拟机安装完成之后，删除不需要的硬件，所以稍后安装操作系统）

![image-20201129174724189](https://gitee.com/jchenTech/images/raw/master/img/20201129174724.png)

![image-20201129174901788](https://gitee.com/jchenTech/images/raw/master/img/20201129174901.png)

**4、虚拟机命名并存储**

![image-20201129175108041](https://gitee.com/jchenTech/images/raw/master/img/20201129175108.png)

**5、自定义虚拟机配置**

![image-20201129175132040](https://gitee.com/jchenTech/images/raw/master/img/20201129175132.png)

![image-20201129175156505](https://gitee.com/jchenTech/images/raw/master/img/20201129175156.png)

网络连接类型的选择，网络连接类型一共有桥接、NAT、仅主机和不联网四种。

* 桥接：选择桥接模式的话虚拟机和宿主机在网络上就是平级的关系，相当于连接在同一交换机上。
* NAT：NAT模式就是虚拟机要联网得先通过宿主机才能和外面进行通信。
* 仅主机：虚拟机与宿主机直接连起来

桥接与NAT模式访问互联网过程，如下图所示

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129175256.png)

这里选择桥接网络即可：

![image-20201129175338841](https://gitee.com/jchenTech/images/raw/master/img/20201129175338.png)

后面I/O控制器，磁盘类型默认即可

![image-20201129175400225](https://gitee.com/jchenTech/images/raw/master/img/20201129175400.png)

![image-20201129175521339](https://gitee.com/jchenTech/images/raw/master/img/20201129175521.png)

![image-20201129175636938](https://gitee.com/jchenTech/images/raw/master/img/20201129175636.png)

**6、安装设置**

![image-20201129175727035](https://gitee.com/jchenTech/images/raw/master/img/20201129175727.png)

删除不需要的硬件：编辑虚拟机设置--删-USB控制器、声卡、打印机（可以使虚拟器启动的快一点）

![image-20201129180248590](https://gitee.com/jchenTech/images/raw/master/img/20201129180248.png)

**7、安装CentOS**

![image-20201129180431455](https://gitee.com/jchenTech/images/raw/master/img/20201129180431.png)

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129180629.png)

**8、选择安装语言**

![image-20201129180753520](https://gitee.com/jchenTech/images/raw/master/img/20201129180753.png)

**9、进行安装设置**

设置时间

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129180958.png)

选择需要安装的软件，这里选择的带图形界面的Server with GUI

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129182217.jpeg)

选择安装位置，在这里可以进行磁盘划分:

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129182314.png)

这里我选择自动分区。



设置root密码：

![img](https://gitee.com/jchenTech/images/raw/master/img/20201129182449.png)

![image-20201129182609968](https://gitee.com/jchenTech/images/raw/master/img/20201129182610.png)

设置完成后安装然后重启，安装成功。

![image-20201129225540943](https://gitee.com/jchenTech/images/raw/master/img/20201129225541.png)

## 3 系统装好了，但还有几个问题

**问题一：** 虚拟机内Linux系统与外网无法连通

![image-20201129201900156](https://gitee.com/jchenTech/images/raw/master/img/20201129201907.png)

**问题二：** 虚拟机内Linux系统与外部宿主机无法连通

比如我这里的物理宿主机的IP地址为：`10.11.74.141`

![image-20201129202436572](https://gitee.com/jchenTech/images/raw/master/img/20201129202436.png)

**问题三：** 虚拟机内Linux系统节点与节点之间无法连通（如果装了多个Linux节点的话）



## 4 网络配置（重要！）

我们可以使用桥接模式和NAT模式进行网络连接，但是在实验桥接模式的时候，我最终都无法联网并ping通宿主机，采用了网上很多方法都不行，这个情况非常奇怪，后面成功的时候再来补充吧。这里我采用NAT模式进行网络配置。

**1、VMware Station设置**

首先，在VMware Satation中先设置虚拟机的网络适配器为NAT模式。

![img](https://gitee.com/jchenTech/images/raw/master/img/20201130130537.jpeg)

然后进行网络适配器的设置，这个必须需要是VMware Station，如果只是VMware Player就不能更改，同时必须具有系统管理员权限。

选中VMnet8，**取消勾选**使用本地DHCP服务将IP地址分配给虚拟机，查看DHCP确保未启用，点击NAT设置：

![image-20201129205928732](https://gitee.com/jchenTech/images/raw/master/img/20201129205928.png)

查看网关IP，并记住 `192.168.92.2` ，用于网络配置文件设置

![image-20201129210014475](https://gitee.com/jchenTech/images/raw/master/img/20201129210014.png)



**2、为虚拟机配置固定静态IP**

`vi /etc/sysconfig/network-scripts/ifcfg-ens32`

``` shell
TYPE="Ethernet"
PROXY_METHOD="none"
BROWSER_ONLY="no"
BOOTPROTO="static" # 这里改成静态IP地址，方便管理
DEFROUTE="yes"
IPV4_FAILURE_FATAL="no"
IPV6INIT="yes"
IPV6_AUTOCONF="yes"
IPV6_DEFROUTE="yes"
IPV6_FAILURE_FATAL="no"
IPV6_ADDR_GEN_MODE="stable-privacy"
NAME="ens32"
UUID="822b132e-d532-4590-8006-6d270c59f347"
DEVICE="ens32"
ONBOOT="yes" #改为yes
IPADDR=192.168.92.111 #IP地址
NETMASK=255.255.255.0 #子网掩码
GATEWAY=192.168.92.2 #这是上一步查看的默认网关
DNS1=8.8.8.8
DNS2=114.114.114.114
```

参数详情如下：

* `BOOTPROTO`：static，使用静态IP地址，方便管理
* `IPADDR`（IP地址）：这里是写你自己的IP，例如我的子网IP是192.168.92.0，你的是192.168.x.0，那么你这里就写192.168.x.y，其中y一定不要是0,1,2,3这几个比较小的数字，因为这些末尾数字比较小的具有特殊含义，也不要写255，那个是广播地址。
* `NETMASK`（子网掩码）：255.255.255.0，通常来说都是这个，这个是C类IP地址的通用网络掩码，这个与你的VMware中网络编辑器里NAT网络的一致即可，通常来说用255.255.255.0。
* `GATEWAY`（网关）：这里写自己的网关，我的网关为192.168.92.2，这个是VMware默认采用子网段中尾数为2的为网关。
* `DNS`(域名解析服务)：8.8.8.8,114.114.114.114。就用这两个域名解析服务，其中8.8.8.8为谷歌提供的，114.114.114.114是国内移bai动、电信和联通通用的DNS。
* `ONBOOT`：yes，保证重启服务时，网卡会重启。



再次ping百度和宿主机时：

![image-20201130131321938](https://gitee.com/jchenTech/images/raw/master/img/20201130131321.png)

现在我们就发现已经能够正常联网了。