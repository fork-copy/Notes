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


翻译自：
https://cwiki.apache.org/confluence/display/Hive/Tutorial