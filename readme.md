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