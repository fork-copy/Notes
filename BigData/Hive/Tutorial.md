# Tutorial
- Hive Tutorial
- Concepts
  - What Is Hive
  - What Hive Is NOT
  - Getting Started
  - Data Units
  - Type System
  - Built In Operators and Functions
  - Language Capabilities
- Usage and Examples
  - Creating, Showing, Altering, and Dropping Tables
  - Loading Data
  - Querying and Inserting Data

# 概念
## Hive是什么
Hive是一个基于Apache Hadoop的数据仓库。对于数据存储与处理，Hadoop提供了主要的扩展和容错能力。

Hive设计的初衷是：对于大量的数据，使得数据汇总，查询和分析更加简单。它提供了SQL，允许用户更加简单地进行查询，汇总和数据分析。同时，Hive的SQL给予了用户多种方式来集成自己的功能，然后做定制化的查询，例如用户自定义函数（User Defined Functions，UDFs).

## Hive不适合做什么
Hive不是为在线事务处理而设计。它最适合用于传统的数据仓库任务。

## Getting Started
对于Hive, HiveServer2 和Beeline的设置详情，请参考[指南](https://cwiki.apache.org/confluence/display/Hive/GettingStarted)。

对于学习Hive，这些[书单](https://cwiki.apache.org/confluence/display/Hive/Books+about+Hive)可能对你有所用处。

以下部分提供了一个关于Hive系统功能的教程。先描述数据类型，表和分区的概念，然后使用例子来描述Hive的功能。

## 数据单元
根据颗粒度的顺序，Hive数据被组织成：
- 数据库：命名空间功能，为了避免表，视图，分区，列等等的命名冲突。数据库也可以用于加强用户或用户组的安全。

- 表：相同数据库的同类数据单元。例如表`page_views`，表的每行包含以下列：
  - timestamp －这是一个`INT`类型，是当页面被访问时的UNIX时间戳。
  - userid －这是一个`BIGINT`类型，用于惟一识别访问页面的用户。
  - page_url －这是一个`STRING`类型，用于存储页面地址。
  - referer_url －这是一个`STRING`类型，用于存储用户是从哪个页面跳转到本页面的地址。
  - IP －这是一个`STRING`类型，用于存储页面请求的IP地址。

- 分区：每个表可以有一个或多个用于决定数据如何存储的分区键。分区（除存储单元之外）也允许用户有效地识别满足指定条件的行；例如，`STRING`类型的`date_partition`和`STRING`的`country_partition`。这些分区键的每个惟一的值定义了表的一个分区。例如，所有的“2009－12－23”日期的“US”数据是表`page_views`的一个分区。（注意区分，分区键与分区，如果分区键有两个，每个分区键有三个不同的值，则共有6个分区）。因此，如果你只在日期为“2009－12－23”的“US”数据上执行分析，你将只会在表的相关数据上执行查询，这将有效地加速分析。然而要注意，那仅仅是因为有个分区叫2009-12-23，并不意味着它包含了所有数据，或者说，这些数据仅仅是那个日期的数据。

翻译自：
https://cwiki.apache.org/confluence/display/Hive/Tutorial