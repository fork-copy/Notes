# hadoop 学习笔记

1. 如何编译源代码
要求

* Unix System
* JDK 1.7+
* Maven 3.0 or later
* Findbugs 1.3.9 (if running findbugs)
* ProtocolBuffer 2.5.0
* CMake 2.6 or newer (if compiling native code), must be 3.0 or newer on Mac
* Zlib devel (if compiling native code)
* openssl devel ( if compiling native hadoop-pipes and to get the best HDFS encryption performance )
* Linux FUSE (Filesystem in Userspace) version 2.6 or above ( if compiling fuse_dfs )
* Internet connection for first build (to fetch all Maven and Hadoop dependencies)

下载源代码：

准备工作：
安装JDK
安装Maven
安装Protoc
下载地址：
 http://pan.baidu.com/s/1pJlZubT

 解压，切换到根目录，安装命令如下：
 ```
 ./configure
 make && make install
 ```
 默认情况下，protoco是安装在`/usr/local/bin', `/usr/local/man'下，但也可以指定安装到某个路径下面，例如，我要把protobuf安装在`/home/yang/Soft/protobuf/`目录下，则使用配置项`prefix`，如下：

 ```
 ./configure --prefix=/home/yang/Soft/protobuf/
 make && make install

 ```

配置环境变量：
```
[yang@master bin]$ vim ~/.bashrc 
export PATH=$PATH:/home/yang/Soft/protobuf/bin

[yang@master bin]$ source ~/.bashrc
[yang@master ~]$ protoc --version
libprotoc 2.5.0

```

切换到源码根目录：
```
mvn package -Pdist,native,docs -DskipTests -Dtar
```

安装Cmake

```
export  PATH=$PATH:/home/yang/GitHub/json2avro/:/home/yang/Soft/protobuf/bin:/home/yang/Soft/cmake/cmake-3.6.3-Linux-x86_64/bin

```
安装findbugs
下载地址：https://sourceforge.net/projects/findbugs/files/findbugs/1.3.9/findbugs-1.3.9.tar.gz/download

安装zlib
```
yum install zlib-devel.x86_64
```

安装openssl-devel
```
yum install openssl-devel.x86_64
```

# Exception

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-antrun-plugin:1.7:run (make) on project hadoop-common: An Ant BuildException has occured: Execute failed: java.io.IOException: Cannot run program "cmake" (in directory "/home/yang/GitHub/hadoop-2.7.3-src/hadoop-common-project/hadoop-common/target/native"): error=2, No such file or directory
```
解决方法：
安装Cmake

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-antrun-plugin:1.7:run (site) on project hadoop-common: An Ant BuildException has occured: stylesheet /home/yang/GitHub/hadoop-2.7.3-src/hadoop-common-project/hadoop-common/${env.FINDBUGS_HOME}/src/xsl/default.xsl doesn't exist.
[ERROR] around Ant part ...<xslt in="/home/yang/GitHub/hadoop-2.7.3-src/hadoop-common-project/hadoop-common/target/findbugsXml.xml" style="${env.FINDBUGS_HOME}/src/xsl/default.xsl" out="/home/yang/GitHub/hadoop-2.7.3-src/hadoop-common-project/hadoop-common/target/site/findbugs.html"/>... @ 43:261 in /home/yang/GitHub/hadoop-2.7.3-src/hadoop-common-project/hadoop-common/target/antrun/build-main.xml
```
编辑`.bashrc`文件，设置`FINDBUGS_HOME`
```
export FINDBUGS_HOME=/home/yang/Soft/findbugs-1.3.9
```

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-antrun-plugin:1.7:run (make) on project hadoop-pipes: An Ant BuildException has occured: exec returned: 1
[ERROR] around Ant part ...<exec failonerror="true" dir="/home/yang/GitHub/hadoop-2.7.3-src/hadoop-tools/hadoop-pipes/target/native" executable="cmake">... @ 5:128 in /home/yang/GitHub/hadoop-2.7.3-src/hadoop-tools/hadoop-pipes/target/antrun/build-main.xml
```
解决方法：
安装openssl