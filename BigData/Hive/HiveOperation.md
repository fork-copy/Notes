
# Hive的安装与配置
请参考官方文档：
https://cwiki.apache.org/confluence/display/Hive/GettingStarted

如果你有安装Ambari，可以参考我之前的文章：
[CentOS7使用本地库(Local Repository)安装Ambari-2.4.1和HDP-2.5.0](http://blog.csdn.net/strongyoung88/article/details/53149538)


也可以参考这篇：
[Hive安装与配置](http://blog.csdn.net/strongyoung88/article/details/53007299)

# 数据定义语言（DDL）操作

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

删除表：

```
hive> drop table pokes_2;
```

元数据存储：

https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-MetadataStore


The /etc/rc.d/rc.local script is executed by the init command at boot time or when changing runlevels. Adding commands to the bottom of this script is an easy way to perform necessary tasks like starting special services or initialize devices without writing complex initialization scripts in the /etc/rc.d/init.d/ directory and creating symbolic links.
