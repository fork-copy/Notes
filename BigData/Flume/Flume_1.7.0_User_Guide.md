# Flume 1.7.0 用户指南


- Introduction
  - Overview
  - System Requirements
  - Architecture
    - Data flow model
    - Complex flows
    - Reliability
    - Recoverability
- Setup
  - Setting up an agent
    - Configuring individual components
    - Wiring the pieces together
    - Starting an agent
    - A simple example
    - Logging raw data
    - Zookeeper based Configuration
    - Installing third-party plugins

  - Data ingestion
    - RPC
    - Executing commands
    - Network streams
  - Setting multi-agent flow
  - Consolidation
  - Multiplexing the flow
- Configuration
  - Defining the flow
  - Configuring individual components
  - Adding multiple flows in an agent
  - Configuring a multi agent flow
  - Fan out flow
  - Flume Sources
Avro Source
Thrift Source
Exec Source
JMS Source
Converter
Spooling Directory Source
Event Deserializers
LINE
AVRO
BlobDeserializer
Taildir Source
Twitter 1% firehose Source (experimental)
Kafka Source
NetCat Source
Sequence Generator Source
Syslog Sources
Syslog TCP Source
Multiport Syslog TCP Source
Syslog UDP Source
HTTP Source
JSONHandler
BlobHandler
Stress Source
Legacy Sources
Avro Legacy Source
Thrift Legacy Source
Custom Source
Scribe Source
  - Flume Sinks
HDFS Sink
Hive Sink
Logger Sink
Avro Sink
Thrift Sink
IRC Sink
File Roll Sink
Null Sink
HBaseSinks
HBaseSink
AsyncHBaseSink
MorphlineSolrSink
ElasticSearchSink
Kite Dataset Sink
Kafka Sink
Custom Sink
  - Flume Channels
Memory Channel
JDBC Channel
Kafka Channel
File Channel
Spillable Memory Channel
Pseudo Transaction Channel
Custom Channel
  - Flume Channel Selectors
Replicating Channel Selector (default)
Multiplexing Channel Selector
Custom Channel Selector
  - Flume Sink Processors
Default Sink Processor
Failover Sink Processor
Load balancing Sink Processor
Custom Sink Processor
  - Event Serializers
Body Text Serializer
“Flume Event” Avro Event Serializer
Avro Event Serializer
  - Flume Interceptors
Timestamp Interceptor
Host Interceptor
Static Interceptor
UUID Interceptor
Morphline Interceptor
Search and Replace Interceptor
Regex Filtering Interceptor
Regex Extractor Interceptor
Example 1:
Example 2:
  - Flume Properties
Property: flume.called.from.service
- Log4J Appender
- Load Balancing Log4J Appender
- Security
- Monitoring
JMX Reporting
Ganglia Reporting
JSON Reporting
Custom Reporting
Reporting metrics from custom components
- Tools
File Channel Integrity Tool
Event Validator Tool
- Topology Design Considerations
Is Flume a good fit for your problem?
Flow reliability in Flume
Flume topology design
Sizing a Flume deployment
- Troubleshooting
Handling agent failures
Compatibility
HDFS
AVRO
Additional version requirements
Tracing
More Sample Configs
- Component Summary
- Alias Conventions

## 概述
Apache Flume是一个分布式的，可靠的，且可用的系统，它可以用来有效地从许多不同的数据源收集，聚合和移动大量的日志数据到一个集中的数据中心进行存储。

Apache Flume的使用不仅限于日志数据的聚合。因为数据源是可定制的，Flume可以用来传输大量的事件型数据，包括但不限于，如网络流量数据，社交媒体产生的数据，邮件信息和任何可能的数据源。

Apache Flume是Apache软件基金会的顶级项目。

## 系统要求
1. Java运行环境 － Java 1.7及更高版本
2. 内存 － 满足sources, Channels和sinks的配置所需要的内存
3. 磁盘空间 － 满足channels和sinks的配置所需要的磁盘空间
4. 目录权限 － 对于agent所使用的目录要有读/写权限

## 架构

### 数据流模型
Flume event被定义为一个数据流单元，它有一个字节负载和一个可选的字符串属性。一个Flume agent是一个JVM进程，agent中的组件将事件流（event flow)从一个外部源传到下一个目的地。
![](http://flume.apache.org/_images/UserGuide_image00.png)

Flume srouce消费从外部源（如web server）传递过来的events。外部源以目标Flume source能识别的格式发送events给Flume。例如，Avro Flume source可以用于接收来自Avro客户端或其他流中的Flume agent的Avro sink发送的Avro events。与此类似的流是，使用Thrift FLume Source接收来自Thrift sink或Thrift Rpc 客户端或来自Flume thrift protocol产生的任意语言写的Thrift 客户端。当Flume source接收到一个event，它存储event到一个或多个channel。这个channel被动存储event，并保留此event，直到它被一个Flume sink消费。例如－file channel，它是存储在本地文件系统。sink把event从channel移除，并把它放到一个外部的库，如HDFS（经过Flume HDFS sink)，或把此event转发给流中的下一个Flume agent的Flume source。agent中的source和sink异步地进行event staged。

### 复杂流
Flume允许用户来建立多跳的流，即event经过多个agent之后才到达最终目的。它也可以“扇入”和“扇出”流（即像扇子一样的流，例如，扇入可以是多个agent，流向同一个agent。扇出也类似，一个agent的event流向多个agent），前面路由和容灾路由。

### 可靠性
events是阶段性的停留在每个agent的channel中。然后events被传递到下一个agent或最终库（如HDFS）。只有在events被存储在下一个agent的channel中或到达了最终目的，events才会在当前channel中删除。这就是在单跳信息传递中，Flume是如何提供端到端可靠的流。

Flume使用事务型方式来确保可靠的events传输。source和sink都被压缩在一个由channel提供的事务中。这确保events集合可以可靠地从流中的一个点传递到另一个点。这“多跳流“的情况下，来自前一跳的sink和来自下一跳的source，它们都是以事务的方式运行，以此确保数据能够安全的存储在下一跳的channel中。

### 可恢复性
events暂存在channel中，channel可以从出错中恢复。Flume提供了一个持久的文件channel，这个文件channel是本地文件系统的一个备份。也有memory channel，它简单地存储events在一个内存队列中，它是很快的，但是，当agent进程死了的时候，在memory channel中的任何event都会丢失。

# 设置
## 设置agent
Flume agent的配置是存储在一个本地的配置文件中。它是一个遵循Java属性文件格式的文本文件。在相同的配置文件中，可以指定一个或多个agents配置。配置文件包括agent中的每个source，sink和channel的属性，及是如何组织在一起形成数据流的。

### 配置组件
流中的每个组件（source, sink, channel)都有名字，类型和指定类型和安装的属性集合。例如，一个Avro source需要一个主机名和一个端口号来接收数据。一个memory channel有最大队列大小（capacity),一个HDFS sink需要知道文件系统的URI，创建文件的路径，文件rotation的频率（“hdfs.rollInterval“）等。组件的所有这些属性应该配置在Flume agent主机上的配置文件中。

### Wiring the pieces together
agent需要知道哪些组件要加载及它们如何连接以构建成流。通过列出在agent中的每个sources, sinks和channels的名字，然后对每个sink和source指定channel连接，从而构成流。例如，一个agent events流,通过一个名为“file-channel1“的文件channel，从一个名为“avroWeb“的Avro source到名为“hdfs-cluster1“的HDFS sink。配置文件将会包含这些组件的名字，且file-channel是作为avroWeb source和hdfs-cluster1 sink的共享channel。

### 启动一个agent
通过一个名为`flume-ng`的shell脚本来启动agent，这个脚本是在flume的bin目录下。你需要在命令行中指定agent的名字，配置文件目录，和配置文件：
```
$ bin/flume-ng agent -n $agent_name -c conf -f conf/flume-conf.properties.template
```
现在，agent将会启动在配置文件中配置的source和sinks。

### 一个简单的例子
这里，我们给出了一个示例配置文件，描述了一个单节点的Flume部署。这个配置是让用户产生events并随后将日志打印在控制台输出。
```
# example.conf: A single-node Flume configuration

# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```
这个配置定义了一个agent a1。a1有一个source，这个source监听44444端口的数据，channel缓存数event数据在内存中，sink打印日志在控制台上。配置文件命名不同的组件，然后描述他们的类型和配置参数。配置文件可以定义一些已命名的agent；当给出的Flume进程已经在运行，将会发送一个标识告诉它是哪个agent。

对于给出的这个配置，我们可以启动Fluem：
```
$ bin/flume-ng agent --conf conf --conf-file example.conf --name a1 -Dflume.root.logger=INFO,console
```
注意：在这个完整的部署中，通常包含一个或多个配置项：`--conf=<conf-dir>`。`<conf-dir>`一个包含shell脚本`flume-env`和一个可能的log4j属性文件的目录。在这个例子中，我们传递了一个Java配置项来强制Flume日志输出到控制台，且我们没有使用一个自定义的环境脚本。

从另外一个终端，我们可以`telnet`端口44444并发送一个Flume event：
```
$ telnet localhost 44444
Trying 127.0.0.1...
Connected to localhost.localdomain (127.0.0.1).
Escape character is '^]'.
Hello world! <ENTER>
OK
```

在启动agent的那个终端，将会以日志信息格式输出event：
```
12/06/19 15:32:19 INFO source.NetcatSource: Source starting
12/06/19 15:32:19 INFO source.NetcatSource: Created serverSocket:sun.nio.ch.ServerSocketChannelImpl[/127.0.0.1:44444]
12/06/19 15:32:34 INFO sink.LoggerSink: Event: { headers:{} body: 48 65 6C 6C 6F 20 77 6F 72 6C 64 21 0D          Hello world!. }
```
恭喜你，你成功地配置并部署了一个Flume agent。随后的章节会更加详细的描述agent配置。

### Logging raw data
通过注入管线来记录原始数据流到日志文件，在许多生产环境中是不想看到的行为，因为这可能导致泄露敏感数据或安全相关的配置，例如安全密钥。默认情况下，Flume不会记录这些信息。另一方面，如果数据管线破坏了，Flume将会为调试问题尝试提供线索。

调试event管线问题的一种方式是，设置一个额外的memory channel连接到Logger sink，这将输出所有的event数据到Flume日志。然而，这种情况下，这种方式无法满足。

为了能够记录配置相关和数据相关的event，除了log4j属性，必须设置Java系统配置。

为了能够记录配置相关的event。可以设置Java系统属性`-Dorg.apche.flume.log.printconfig=true`。这可以在命令行输入，也可以在`flume-env.sh`中设置在变量`JAVA_OPTS`中。

为了能够记录数据相关的event。可以设置Java系统属性`-Dorg.apache.flume.log.rawdata=true`。对于大多数组件，log4j的日志级别也必须设置为DEBUG或TRACE，以确保指定的event能够附加到Flume的日志中。

下面是一个例子，能够记录配置和原始数据，不过，也需要设置Log4j的loglevel为DEBUG：
```
$ bin/flume-ng agent --conf conf --conf-file example.conf --name a1 -Dflume.root.logger=DEBUG,console -Dorg.apache.flume.log.printconfig=true -Dorg.apache.flume.log.rawdata=true
```

### Zookeeper based Configuration
Flume支持通过Zookeeper对Agent配置。这是一个试验性的特征。配置文件需要上传到Zookeeper，配置文件是存储在Zookeeper节点数据中。对于agents a1和a2，以下是Zookeeper节点树的样子：
```
- /flume
 |- /a1 [Agent config file]
 |- /a2 [Agent config file]
```

配置文件一旦上传，使用以下配置启动agent：
```
$ bin/flume-ng agent –conf conf -z zkhost:2181,zkhost1:2181 -p /flume –name a1 -Dflume.root.logger=INFO,console
```
|参数名|默认值|描述|
|:----|:-----|:---|
|z|－|Zookeeper连接字符串，逗号分隔的hostname:port列表|
|p|/flume|Zookeeper存储agent配置文件的根目录|

### 安装三方插件
Flume有一个基于插件的框架。虽然Flume已经有许多内置的sources, channel , sinks , serializers等，许多已存在的实现都是来自Flume。

虽然通过添加jars到文件flume-env.sh里的变量`FLUME_CLASSPATH`可以加入定制的Flume组件，现在，FLume支持一个名为`plugins.d`的特定目录，它会自动加载使用指定格式打包的插件。这就使得管理插件包问题更加容易，一些类的问题的调试更加简单，尤其是包的冲突问题。

#### plugins.d目录
`plugins.d`目录在`$FLUME_HOME/plugins.d`下，在启动的时候，`flume-ng`脚本会查看目录`plugins.d`的插件，这些插件是遵照相应格式，当启动`java`时，插件会包括到合适的路径下。

#### 插件目录的布局
每个`plugins.d`下的每个插件（子目录）有多达三个子目录：
1. lib - 插件的jar(s)
2. libext - 插件依赖的jar(s)
3. native - 一些必要的本地库，例如`.so`文件

在plugins.d目录下有两个插件的例子：
```
plugins.d/
plugins.d/custom-source-1/
plugins.d/custom-source-1/lib/my-source.jar
plugins.d/custom-source-1/libext/spring-core-2.5.6.jar
plugins.d/custom-source-2/
plugins.d/custom-source-2/lib/custom.jar
plugins.d/custom-source-2/native/gettext.so
```

## 数据注入
Flume支持一些机制来从外部源注入数据。

### RPC 
Flume发行版本中的Avro client可以使用avro RPC机制来发送一个文件到Flume Avro source。
```
$ bin/flume-ng avro-client -H localhost -p 41414 -F /usr/logs/log.10
```
以上命令将会发送文件`/usr/logs/log.10`的内容到Flume source监听的端口。

### 命令执行
有一个exec source，这个源执行给出的命令并消费输出。也就是单行的输出，文本后面返回`\r`，`\n`或`\r\n`。
注意：Flume不支持tail作为source。可以包装tail命令在一个exec source到流文件。

### 网络流
Flume支持以下机制来从日志流类型读取数据，例如：
1. Avro
2. Thrift
3. Syslog
4. Netcat 

## multi-agent设置
![](http://flume.apache.org/_images/UserGuide_image03.png)

为了让数据在多个agent流动，前一个agent的sink和当前agent的source需要是avro类型，sink指向source的主机和端口。

## 合并
在日志收集中一个非常常见的场景是，产生日志的客户端有许多个，他们需要发送数据到少量几个连接到存储系统的消费者agent。例如，来自上百个web服务收集的日志发送到12个写数据到HDFS集群agent。
![](http://flume.apache.org/_images/UserGuide_image02.png)

在Flume，通过在第一层配置并排的多个avro sink的agent，所有的agent的sink指定一个单个agent的avro source可以实现上述功能（同样情况下，你也可以使用thrift source/sinks/clients）。第二层agent的source合并收到的event到一个单个的channel，这个channel的sink是消费channel的event到最终目的地（在这里是HDFS）。

## 多路传输流
Flume支持多路发送event 流到一个或多个目的地。这可以通过定义一个多路发送流来实现，它可以复制或有选择地路由一个event到一个或多个channel。
![](http://flume.apache.org/_images/UserGuide_image01.png)

在上述例子中，显示了一个来自agent foo的source，扇出流到三个不同的channel。这个扇出可以复制或多路发送。在复制流的情况下，每个event被发送到所有的三个channel。对于多路发送的情况，当event的属性匹配预先配置的值，event会被传输到一个可用的channel子集。例如，如果一个event属性名为“txnType“，被设置为”customer“，然后，它应该流向channel1和channel3，如果它被设置为“vendor“，然后，它应该流向channel2，否则，流向channel3。这种映射可以在agent的配置文件中进行设置。

# 配置 
如前所述，Flume agent配置是从一个文件中读取的，此文件类似于java属性文件格式，有分层的属性设置。