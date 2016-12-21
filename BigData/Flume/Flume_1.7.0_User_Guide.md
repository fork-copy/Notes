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


