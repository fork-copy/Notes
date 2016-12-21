# HCatalog

HCatalog是一个表，也是一个存储管理层，用来使得Hadoop可以允许用户使用不同的数据处理工具（Pig, MapReduce）来更加简单的在表上进行读写数据。

HCatalog手册包括以下：

- Using HCatalog
- Installation from Tarball
- HCatalog Configuration Properties
- Load and Store Interfaces
- Input and Output Interfaces
- Reader and Writer Interfaces
- Command Line Interface
- Storage Formats
- Dynamic Partitioning
- Notification
- Storage Based Authorization

对于HCatalog的REST API，WebHCAT信息，请参考[WebHCat Manual](https://cwiki.apache.org/confluence/display/Hive/WebHCat)。

## 使用HCatalog

- Overview
- HCatalog Architecture
    - Interfaces
    - Data Model
- Data Flow Example
    - First: Copy Data to the Grid
    - Second: Prepare the Data
    - Third: Analyze the Data
- HCatalog Web API

### 概述
HCatalog是一个表，也是一个存储管理层，用来使得Hadoop可以允许用户使用不同的数据处理工具（Pig, MapReduce）来更加简单的在表上进行读写数据。HCatalog的表抽象是把在HDFS上的数据以一个关系型视图呈现给用户，使得用户不需要担心他们的数据存储在哪以及如何存储－RCFile格式，文本格式，顺序文件或ORC文件。

HCatalog支持任意SerDe(序列化和反序列化)能够被读的格式来读写文件。默认情况下，HCatalog支持RCFile， CSV， JSON，和顺序文件，和ORC文件格式。想使用一个定制化的格式，我们必须提供InputFormat, OutputFormat和SerDe。

![](https://cwiki.apache.org/confluence/download/attachments/34013260/hcat-product.jpg?version=1&modificationDate=1375694292000&api=v2)

### Hive的架构
HCatalog是构建在Hive元数据之上的，合并了Hive的DDL。HCatalog提供读写接口给Pig和MapReduce，且为发布数据定义和元数据探索命令提供了Hive命令行接口。

#### 接口
HCatalog对Pig的接口包含HCatLoader和HCatStorer，这两个都分别实现了Pig的load和store接口。HCatLoader接受从一个表中读数据； 你可以通过使用一个分区筛选语句来指定要扫描的分区。HCatStorer接受写数据到表，也可以指定一个分区key来创建一个新的分区。我们可以在`STORE`语句中指定分区key和value，然后写到单个分区。并且，如果分区是要存储的数据列，我们也可以读多个分区。HCatLoader实现了接口HCatInputFormat，HCatStorer实现了接口HCatOutputFormat。（可参考[Load and Store Interface]()https://cwiki.apache.org/confluence/display/Hive/HCatalog+LoadStore）。

HCatalog对MapReduce的接口－HCatInputFormat和HCatOutputFormat是hadoop的InputFormat和OutputFormat接口的实现。HCatInputFormat接受从表中读取数据，且也可以选择要扫描哪个分区。HCatOutputFormat接受写数据到表中，且也可以指定分区key来创建一个新的分区。我们可以通过指定分区的key和value来写单个分区，如果分区是要被存储的数据列，则可以写多个分区。

注意：没有Hive专用的接口，因为HCatalog使用Hive的元数据存储，Hive可能直接使用HCatalog读取数据。

使用HCatalog的命令行接口（CLI)来定义数据。HCatalog CLI支持所有不需要使用MapReduce来执行的Hive DLL，允许用户来创建，修改和删除表等。CLI也支持Hive命令行的数据探索部分，例如`SHOW TABLES`,`DESCRIBE TABLE`等。不支持Hive DDL的包括：`import/export`，`ALTER TABLE, CRATE TABLE AS SELECT和ANALYZE TABLE...COMPUTE STATISTICS`的`REBUILD`和`CONCATENATE`.(请参考[Command Line Interface](https://cwiki.apache.org/confluence/display/Hive/HCatalog+CLI))

#### 数据模型

HCatalog以关系型视图呈现数据。数据在存储在表中，这些表是在数据库。表可以有一个或多个key的哈希分区。对于一个给出的key的value，这将是一个包含所有这个值的行的分区。例如，如果一个表有分区日期date，表中的数据有三天，则在表中有三个分区。可以在表中加入新的分区，也可以删除分区。分区的表在创建的时候是没有分区的。没有分区的表有一个默认的分区，但这个分区一定是在创建表的时候创建的。当分区被删除时，不能保证读的一致性。

分区包含记录。一旦分区被创建，则记录不能再增加，删除和修改。分区是多维的，不是层级记录，被分割到各列。列有名字和类型。HCatalog与Hive支持同样的数据类型，想要更多关于数据类型的信息，请参考[Load and store interfaces](https://cwiki.apache.org/confluence/display/Hive/HCatalog+LoadStore)。

### 数据流例子
这些简单的数据流例子展示了HCatalog如何帮助用户共享和访问数据。

#### 第一：拷贝数据到Grid
Joe(人名)在数据获取中，采用`distcp`来获取数据到Grid中：
```
hadoop distcp file:///file.dat hdfs://data/rawevents/20100819/data

hcat "alter table rawevents add partition (ds='20100819') location 'hdfs://data/rawevents/20100819/data'"
```
#### 第二：准备数据
Sally在数据处理中，使用Pig来清理和准备数据。
在没有HCatalog的情况下，当数据可以获取的时候，Sally只能让Jeo手动通知，或从HDFS上轮询。
```
A = load '/data/rawevents/20100819/data' as (alpha:int, beta:chararray, ...);
B = filter A by bot_finder(zeta) = 0;
...
store Z into 'data/processedevents/20100819/data';
```
有了HCatalog，HCatalog将发送一个JMS信息，通知数据可以获取了。然后Pig任务可以被启动：
```
A = load 'rawevents' using org.apache.hive.hcatalog.pig.HCatLoader();
B = filter A by date = '20100819' and by bot_finder(zeta) = 0;
...
store Z into 'processedevents' using org.apache.hive.hcatalog.pig.HCatStorer("date=20100819");
```

#### 第三：分析数据
在客户端管理中，Robert使用Hive来分析客户的结果。
在没有HCatalog的情况下，如果要增加需要的分区，Robert必须修改表。
```
alter table processedevents add partition 20100819 hdfs://data/processedevents/20100819/data

select advertiser_id, count(clicks)
from processedevents
where date = '20100819'
group by advertiser_id;
```
有了HCatalog，Robert不再需要修改表的结构：
```
select advertiser_id, count(clicks)
from processedevents
where date = ‘20100819’
group by advertiser_id;
```
### HCatalog Web API
WebHCat是一个HCatalog的REST API。WebHCat的原始名字为Templeton，更加的信息请参考[WebHCat manual](https://cwiki.apache.org/confluence/display/Hive/WebHCat)。
## 从Tarball安装
- HCatalog Installed with Hive
- HCatalog Command Line
- HCatalog Client Jars
- HCatalog Server

### HCatalog Installed with Hive
从Hive的发布版本0.11.0开始，HCatalog就随着Hive被一起安装。

Hive的安装文档在[这里](https://cwiki.apache.org/confluence/display/Hive/AdminManual+Installation).

### HCatalog Command Line
如果我们是用二进制压缩包安装Hive的话，那命令`hcat`在目录`hcatalog/bin`下。

命令`hcat`与命令`hive`相似，最主要的区别在于它的查询受限，它只能运行元数据操作，例如DDL和DML，只能用于读取元数据（例如“`show tables`")。

HCatalog CLI文档在[这里](https://cwiki.apache.org/confluence/display/Hive/HCatalog+CLI)，Hive CLI的文档在[这里](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Cli)。

大多数的`hcat`命令可以像`hive`命令一样使用，除了`hcat -g`和`hcat -p`。因为，命令`hcat`使用`-p`时是用于权限，而`hive`命令的`-p`是用来指定端口。

### HCatalog Client Jars

在Hive的tar.gz中，HCatalog库是在目录`hcatalog/share/hcatalog/`下。

### HCatalog Server
HCatalog Server与Hive元数据存储相同。你也可以参考[Hive metastore documentation](https://cwiki.apache.org/confluence/display/Hive/AdminManual+MetastoreAdmin)来设置。

## HCatalog配置属性

- Setup
- Storage Directives
- Cache Behaviour Directives
- Input Split Generation Behaviour
- Data Promotion Behaviour
- HCatRecordReader Error Tolerance Behaviour

Apache HCatalog的行为可以通过使用一些配置参数来改变。这个文档详细说明了所有参数。

### 设置
这里描述的属性是作业级别的属性，通过传递的`jobConf`来设设置HCatalog。这意味着这页与`HCatLoader/HCatStorer`的Pig用户，或`HCatInputFormat/HCatOutputFormat`的MapReduce用户相关。对于HCatalog的MapReduce用户而言，这些属性必须在配置（JobConf/Job/JobContext）里以key-value的形式表示，这些配置用于实例化HCatOutputFormat或HCatInputFormat。对于HCatStorer的Pig用户来说，这些参数是在实例化`HCatLoader/HCatStorer`之前使用Pig的`set`命令来设置。

### 存储指令

//TODO


## 加载和存储接口

## 输入和输出接口

## 读写接口

## 命令行接口

## 存储格式

## 动态分区

## 通知

## 授权存储

本文翻译自：https://cwiki.apache.org/confluence/display/Hive/HCatalog