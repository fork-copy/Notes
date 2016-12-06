# 这里是Hive相关的学习笔记

# Apache Hive

[Apache Hive™](http://hive.apache.org/) 是一个数据仓库，区别于数据库，它帮助我们更好地读，写，管理和使用SQL语法查询大量存储在分布式存储系统中的数据集。


Hive有以下特征：
- 使用SQL，提供了更加简单的数据访问工具，因此可用于数据仓库任务，例如extract/transform/load(ETL)，报表和数据分析。
- 对各种数据格式使用结构的机制。
- 可以直接访问Apache HDFS文件系统或其他的数据存储系统，例如Apache HBase.
- 执行查询。使用Apache Tez, Apache Spark, 或MapReduce执行查询。
- Procedural language with HPL-SQL
- Sub-second query retrieval via Hive LLAP, Apache YARN and Apache Slider.

Hive提供了标准的SQL功能，包括[SQL:2003](https://en.wikipedia.org/wiki/SQL:2003)和[SQL:2011](https://en.wikipedia.org/wiki/SQL:2011)。

Hive的SQL也可以通过用户自定义函数（User defined functions,UDFs),用户自定义聚合函数（user defined aggregates,UDAFs）,用户自定义表函数(user defined table functions, UDTFs)的用户代码进行扩展。

Hive支持多种数据格式，如逗号和TAB为分隔符的文本文件（CSV/TSV)，Apache Parquet，Apache ORC，和其他格式.

Hive不是为在线事务处理（online transaction processing,OLTP）而设计的
Hive is not designed for online transaction processing (OLTP) workloads. It is best used for traditional data warehousing tasks.
Hive is designed to maximize scalability (scale out with more machines added dynamically to the Hadoop cluster), performance, extensibility, fault-tolerance, and loose-coupling with its input formats.
