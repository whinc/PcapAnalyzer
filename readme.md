## PcapAnalyzer

...


## 安装运行

下载源码，源码不包含具体IDE的配置文件，可以用Intellij IDEA或Eclipse导入然后构建。

该项目依赖于 JnetPcap 项目，JNetPcap 是基于开源的 libpcap 库（C语言编写）的Java封装。

在 Windows 上运行：

* 安装 Winpcap
* 运行时指定 JNetPcap 动态链接库搜索路径

在 Linux/Mac 上运行：

* 安装 libpcap
* 运行时指定 libpcap 和 JNetPcap 共享库的搜索目录

下面是运行命令：
```
java -cp .;<path to jnetpcap.jar> -Djava.library.path=<path to share library> com/whinc/Main
```

## 存在的问题

* 相同两个主机之间多次会话会被认为是一个网络流，解决办法：三次握手开始作为一个网络流的开始，四次握手时作为一个网络流的结束。发现三次握手时应新建立一个网络流。结束的网络流不能再添加数据包进来。



