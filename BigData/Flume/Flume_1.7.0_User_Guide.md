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

