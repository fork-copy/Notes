
# 数据定义语言（DDL）操作

- Create/Drop/Alter/Use Database
- Create Table
 - Managed and External Tables
 - Storage Formats
 - Row Formats & SerDe
 - Partitioned Tables
 - External Tables
 - Create Table As Select (CTAS)
 - Create Table Like
 - Bucketed Sorted Tables
 - Skewed Tables
 - Temporary Tables
- Drop Table
- Truncate Table

创建数据库
--
语法如下

```
CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name
  [COMMENT database_comment]
  [LOCATION hdfs_path]
  [WITH DBPROPERTIES (property_name=property_value, ...)];
```
示例：创建名为“ecbdc”的数据库，如下：

```
hive> create database ecbdc;
```
删除数据库
--
语法如下：

```
DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];
```
示例：删除名为“ecbdc”的数据库，如下：

```
hive> drop database ecbdc;

```

# 创建表

## 托管表和外部表
默认情况下，Hive创建托管表，即文件，元数据和统计信息由Hive进程进行管理。托管表是存储在`hive.metastore.warehouse.dir`目录属性下，默认是在目录`/apps/hive/warehouse/databasename.db/tablename/`。默认位置可以在创建表的时候重写。如果托管表或分区被删除，相关的数据和元数据都被删除。

当需要Hive来管理表的生命周期，或产生临时表的时候，使用托管表。

一个外部表的元数据/schema是在描述在外部文件上的。外部表文件可以通过Hive之外的进程访问和管理。外部表可以访问的数据存储源有Azure Storage Volumes(ASV)和远程的HDFS。如果外部表的结构和分区改变了，需要使用命令`MSCK REPAIR table_name`来刷新元数据。

使用外部表的时候，如果谁的已经存在或在远程位置上，那么，当表被删除时，文件仍然会补保留下来。

可以使用命令`DESCRIBE FORMATTED table_name`来判断是托管表还是外部表，在表类型那里会显示`MANAGED_TABLE`或`EXTERNAL_TABLE`。

## 存储格式
Hive 支持内置和定制开发的文件格式。对于压缩的表存储详情请参阅[CompressedStorage](https://cwiki.apache.org/confluence/display/Hive/CompressedStorage)

以下是Hive内置的一些格式：

|存储格式|描述|
|:-----|:-----|
|存储为TEXTFILE|存储为平面文本文件，TEXTFILE is the default file format, unless the configuration parameter hive.default.fileformat has a different setting.<br/><br/>Use the DELIMITED clause to read delimited files. <br/><br/>Enable escaping for the delimiter characters by using the 'ESCAPED BY' clause (such as ESCAPED BY '\') .<br/><br/>Escaping is needed if you want to work with data that can contain these delimiter characters.<br/><br/>A custom NULL format can also be specified using the 'NULL DEFINED AS' clause (default is '\N').|
|STORED AS SEQUENCEFILE|Stored as compressed Sequence File.|
||Stored as ORC file format. Supports ACID Transactions & Cost-based Optimizer (CBO). Stores column-level metadata.|
|STORED AS PARQUET|Stored as Parquet format for the Parquet columnar storage format in Hive 0.13.0 and later; Use ROW FORMAT SERDE ... STORED AS INPUTFORMAT ... OUTPUTFORMAT syntax ... in Hive 0.10, 0.11, or 0.12.|
|STORED AS ORC|Stored as Avro format in Hive 0.14.0 and later (see Avro SerDe).|
|STORED AS RCFILE|Stored as Record Columnar File format.|
|STORED BY|Stored by a non-native table format. To create or link to a non-native table, for example a table backed by HBase or Druid or Accumulo. See StorageHandlers for more information on this option.|
|INPUTFORMAT and OUTPUTFORMAT|in the file_format to specify the name of a corresponding InputFormat and OutputFormat class as a string literal.<br/><br/>For example, 'org.apache.hadoop.hive.contrib.fileformat.base64.Base64TextInputFormat'.<br/><br/> For LZO compression, the values to use are<br/><br/> 'INPUTFORMAT "com.hadoop.mapred.DeprecatedLzoTextInputFormat"<br/><br/> OUTPUTFORMAT "org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat"' <br/><br/>(see LZO Compression).|

创建Hive表
--
在操作表之前，我们需要先选择一个数据库，在这里，我们使用刚才创建的数据库ecbdc：

```
hive> use ecbdc;

```
意思就是，在此之后，我的操作都是在此数据库上进行，否则，是在数据库default上。这和mysql的使用是一样的。



创建一个名为“pokes”的表，有两列，一列为foo，为整型，另一列为bar，为字符串型。

```
hive> create table pokes (foo int,bar string);

```
创建一个名为“invites”的表，有两列和一个分区列，分区列ds是一个虚拟列，它并不存储数据本身。

```
hive> create table invites (foo int, bar string) partitioned by (ds string);

```
默认情况下，表是以文本输入格式进行存储，分隔符为^A(ctrl+a)。

浏览表
--
列出所有表：

```
hive> show tables;
OK
invites
pokes
Time taken: 0.223 seconds, Fetched: 2 row(s)

```
列出所有以s结尾的表，这里匹配的是java正则表达式：

```
hive> show tables '.*s';
OK
invites
pokes
Time taken: 0.224 seconds, Fetched: 2 row(s)

```
显示表invites的列信息：

```
hive> describe invites;
OK
foo                 	int
bar                 	string
ds                  	string

# Partition Information
# col_name            	data_type           	comment

ds                  	string
Time taken: 0.539 seconds, Fetched: 8 row(s)

```
修改表
--

修改表的名字：

```
hive> alter table pokes rename to pokes_2;
OK
Time taken: 0.43 seconds
hive> show tables;
OK
invites
pokes_2
Time taken: 0.218 seconds, Fetched: 2 row(s)
```
给表增加新列：

```
hive> alter table pokes_2 add columns (new_col int);

```
给表添加新列，并添加注释：

```
hive> alter table pokes_2 add columns (new_col_2 int comment 'a comment');
OK
Time taken: 0.623 seconds
hive> describe pokes_2;
OK
foo                 	int
bar                 	string
new_col             	int
new_col_2           	int                 	a comment
Time taken: 0.373 seconds, Fetched: 4 row(s)

```
替换列：

```
hive> alter table pokes_2 replace columns (foo int, baz int, baz2 int);
OK
Time taken: 0.419 seconds
hive> describe pokes_2;
OK
foo                 	int
baz                 	int
baz2                	int
Time taken: 0.349 seconds, Fetched: 3 row(s)

```
*replace columns 替换所有已存在的列，并且仅仅是修改表的schema，而不是数据本身。*

删除表
--
删除表：

```
hive> drop table pokes_2;
```

元数据存储
--
Hive的元数据是存储在一个内置的Derby数据库中，它的磁盘位置由Hive配置的变量`javax.jdo.option.ConnectionURL`决定。默认情况下，这个位置是`./metastore_db`，（详见`conf/hive-default.xml`）。

现在，在默认的配置中，这个元数据在某个时刻，只能被一个用户看到。

元数据可以被其他任意由JPOX支持的数据库。使用的RDBMS的位置和类型由两个变量控制，分别是`javax.jdo.option.ConnectionURL`和`javax.jdo.option.ConnectionDriverName`。想查看更加详细关于支持的数据库，请参考JDO(或JPOX)文档。这些数据库的schema是定义在JDO元数据注解文件`package.jdo`中，这个文件在`src/contrib/hive/metastore/src/model`。




翻译自：https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-DDLOperations


See Also:
# Hive的安装与配置
请参考[官方文档](https://cwiki.apache.org/confluence/display/Hive/GettingStarted)

如果你有安装Ambari，可以参考我之前的文章：
[CentOS7使用本地库(Local Repository)安装Ambari-2.4.1和HDP-2.5.0](http://blog.csdn.net/strongyoung88/article/details/53149538)


也可以参考这篇：
[Hive安装与配置](http://blog.csdn.net/strongyoung88/article/details/53007299)
