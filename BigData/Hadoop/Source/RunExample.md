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
创建输入目录：
使用以下命令创建目录`input`：
```
[yang@node3 root]$ hadoop fs -mkdir input
```
编辑输入文件：
```
[yang@node3 Documents]$ vim test.txt
[yang@node3 Documents]$ more test.txt
This is a test file
[yang@node3 Documents]$
```
上传输入文件到HDFS输入目录下：
```
[yang@node3 Documents]$ hadoop fs -put test.txt input/

```

运行程序：
使用以下命令运行单词统计程序：
```
[yang@node3 hadoop-mapreduce]$ hadoop jar hadoop-mapreduce-examples.jar wordcount input/ output/
```
控制台输出如下：
```
16/12/11 20:27:33 INFO impl.TimelineClientImpl: Timeline service address: http://node2:8188/ws/v1/timeline/
16/12/11 20:27:33 INFO client.RMProxy: Connecting to ResourceManager at node2/172.16.41.55:8050
16/12/11 20:27:33 INFO client.AHSProxy: Connecting to Application History server at node2/172.16.41.55:10200
16/12/11 20:27:34 INFO input.FileInputFormat: Total input paths to process : 1
16/12/11 20:27:34 INFO mapreduce.JobSubmitter: number of splits:1
16/12/11 20:27:34 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1481443742421_0001
16/12/11 20:27:35 INFO impl.YarnClientImpl: Submitted application application_1481443742421_0001
16/12/11 20:27:35 INFO mapreduce.Job: The url to track the job: http://node2:8088/proxy/application_1481443742421_0001/
16/12/11 20:27:35 INFO mapreduce.Job: Running job: job_1481443742421_0001
16/12/11 20:27:44 INFO mapreduce.Job: Job job_1481443742421_0001 running in uber mode : false
16/12/11 20:27:44 INFO mapreduce.Job:  map 0% reduce 0%
16/12/11 20:27:47 INFO mapreduce.Job:  map 100% reduce 0%
16/12/11 20:27:54 INFO mapreduce.Job:  map 100% reduce 100%
16/12/11 20:27:55 INFO mapreduce.Job: Job job_1481443742421_0001 completed successfully
16/12/11 20:27:55 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=56
		FILE: Number of bytes written=282095
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=127
		HDFS: Number of bytes written=30
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=3168
		Total time spent by all reduces in occupied slots (ms)=9630
		Total time spent by all map tasks (ms)=1584
		Total time spent by all reduce tasks (ms)=4815
		Total vcore-milliseconds taken by all map tasks=1584
		Total vcore-milliseconds taken by all reduce tasks=4815
		Total megabyte-milliseconds taken by all map tasks=2433024
		Total megabyte-milliseconds taken by all reduce tasks=9861120
	Map-Reduce Framework
		Map input records=1
		Map output records=5
		Map output bytes=40
		Map output materialized bytes=56
		Input split bytes=107
		Combine input records=5
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=56
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=72
		CPU time spent (ms)=1200
		Physical memory (bytes) snapshot=1321771008
		Virtual memory (bytes) snapshot=6979145728
		Total committed heap usage (bytes)=1215299584
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters
		Bytes Read=20
	File Output Format Counters
		Bytes Written=30
[yang@node3 hadoop-mapreduce]$
```

查看结果：
使用以下命令查看输出结果：
```
[yang@node3 hadoop-mapreduce]$ hadoop fs -cat output/par*
This	1
a	1
file	1
is	1
test	1
```
这个就是统计出来的结果，每个单词对应出现的次数。当然，不是所有的输出都是这么查看的，因为这里是统计词频，所以可以使用直接查看文件内容的方式。

这里用到了`hadoop`命令，我们一起来看下hadoop命令的用法：
```
[yang@node3 hadoop-mapreduce]$ hadoop
Usage: hadoop [--config confdir] [COMMAND | CLASSNAME]
  CLASSNAME            run the class named CLASSNAME
 or
  where COMMAND is one of:
  fs                   run a generic filesystem user client
  version              print the version
  jar <jar>            run a jar file
                       note: please use "yarn jar" to launch
                             YARN applications, not this command.
  checknative [-a|-h]  check native hadoop and compression libraries availability
  distcp <srcurl> <desturl> copy file or directories recursively
  envvars              display computed Hadoop environment variables
  archive -archiveName NAME -p <parent path> <src>* <dest> create a hadoop archive
  classpath            prints the class path needed to get the
  credential           interact with credential providers
                       Hadoop jar and the required libraries
  daemonlog            get/set the log level for each daemon
  trace                view and modify Hadoop tracing settings

Most commands print help when invoked w/o parameters.
```
*注意*：在终端运行这个命令时，需要之前配置过环境变量，即需要将`hadoop`命令所在的bin目录配置到环境变量`PATH`里,或者直接把`hadoop`命令链接到当前`PATH`下。

我安装的是HDP 2.5，在安装的时候，自动帮我配置到`/usr/bin/`下去了，通过以下可以看到，它把`hadoop-client/bin/`下的`hadoop`命令链接到了`/usr/bin/`。

```
[yang@node3 hadoop-mapreduce]$ ls -al /usr/bin/ | grep hadoop
lrwxrwxrwx.   1 root root         41 Nov 15 09:52 hadoop -> /usr/hdp/current/hadoop-client/bin/hadoop
lrwxrwxrwx.   1 root root         44 Nov 15 09:52 hdfs -> /usr/hdp/current/hadoop-hdfs-client/bin/hdfs
lrwxrwxrwx.   1 root root         51 Nov 15 09:52 mapred -> /usr/hdp/current/hadoop-mapreduce-client/bin/mapred
lrwxrwxrwx.   1 root root         44 Nov 15 09:52 yarn -> /usr/hdp/current/hadoop-yarn-client/bin/yarn
```
接下来，我们看下这个hadoop命令的源代码；
```
[yang@node3 hadoop-mapreduce]$ cd /usr/hdp/current/hadoop-client/bin/
[yang@node3 bin]$ ls
hadoop  hadoop.distro  hadoop-fuse-dfs  hdfs  mapred  rcc  yarn

```
这里，hadoop是一个shell脚本：
```
#!/bin/bash

export HADOOP_HOME=${HADOOP_HOME:-/usr/hdp/2.5.0.0-1245/hadoop}
export HADOOP_MAPRED_HOME=${HADOOP_MAPRED_HOME:-/usr/hdp/2.5.0.0-1245/hadoop-mapreduce}
export HADOOP_YARN_HOME=${HADOOP_YARN_HOME:-/usr/hdp/2.5.0.0-1245/hadoop-yarn}
export HADOOP_LIBEXEC_DIR=${HADOOP_HOME}/libexec
export HDP_VERSION=${HDP_VERSION:-2.5.0.0-1245}
export HADOOP_OPTS="${HADOOP_OPTS} -Dhdp.version=${HDP_VERSION}"

exec /usr/hdp/2.5.0.0-1245//hadoop/bin/hadoop.distro "$@"

```
