# 运行例子

1. 准备工作
2. 运行

# 准备工作
运行环境：

JAVA版本：
```
[root@node3 hadoop]# java -version
openjdk version "1.8.0_111"
OpenJDK Runtime Environment (build 1.8.0_111-b15)
OpenJDK 64-Bit Server VM (build 25.111-b15, mixed mode)
```
Hadoop版本：
```
[root@node3 hadoop]# hadoop version
Hadoop 2.7.3.2.5.0.0-1245
Subversion git@github.com:hortonworks/hadoop.git -r cb6e514b14fb60e9995e5ad9543315cd404b4e59
Compiled by jenkins on 2016-08-26T00:55Z
Compiled with protoc 2.5.0
From source with checksum eba8ae32a1d8bb736a829d9dc18dddc2
This command was run using /usr/hdp/2.5.0.0-1245/hadoop/hadoop-common-2.7.3.2.5.0.0-1245.jar
```

在运行一个Hadoop程序之前，我们需要明白三件事情：
- hadoop jar包：我们需要一个Hadoop jar包，即我们要做什么事情，要实现什么功能，例如，我们这里想实现统计某个文档里单词的个数，那么我们就需要写一个MR程序来实现这个功能，然后打包成jar包。
- 输入目录：有了jar包之后，我们还需要指定输入目录，即告诉程序，我们的输入目录在哪，我要需要对哪个文件进行统计单词。
- 输出目录：有了输入目录，有了jar程序，接下来需要告诉hadoop，程序的输出目录在哪。

hadoop jar包：

这里为了方便，直接使用hadoop自带的实例，这个例子的jar包在`/usr/hdp/2.5.0.0-1245/hadoop-mapreduce`目录下（其中2.5.0.0-1245是版本号，如果你的hdp不是2.5版本，那么这个目录是不一样的，但类似于这个目录），如下：
```
[root@node3 hadoop-mapreduce]# ls | grep example
hadoop-mapreduce-examples-2.7.3.2.5.0.0-1245.jar
hadoop-mapreduce-examples.jar
[root@node3 hadoop-mapreduce]# pwd
/usr/hdp/2.5.0.0-1245/hadoop-mapreduce
```
