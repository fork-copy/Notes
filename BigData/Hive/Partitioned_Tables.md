# 分区的表
关于分区的表，需要明白以下几个问题：
- 对表分区有什么好处？
- 如何对表进行分区？

## 表分区的优点
通常情况下，一个`SELECT`查询会扫描整个表，但是，如果对表进行了分区，然后在`WHERE`中检索的是分区的列，那么查询只会扫描指定的那个分区[1]。相当于有针对性的查询，减少不必要的扫描，从而提高了效率。

## 如何对表进行分区

使用`PARTITIONED BY`语句来创建分区表。一个表可以有一个或多个分区列，且分区的每个列都有一个单独的数据目录。再者，对列使用`CLUSTERED BY`表或分区可以分桶（bucket)，还可以通过使用`SORT BY`对列进行桶内排序。

例如
--
假设，原始表有三列：id, date, 和name。
```
id     int,
date   date,
name   varchar
```
现在，想对`date`进行分区。可以在Hive中使用'dtDontQuery'作为列名进行定义，使得`date`可以被用来分区（和查询）。
```
create table table_name (
  id                int,
  dtDontQuery       string,
  name              string
)
partitioned by (date string)
```
现在，我们仍然可以使用`where date='...'`来查询，但是，第二列`dtDontQuery`将查到的是原始值。

以下是创建一个分区表的例子：
```
CREATE TABLE page_view(viewTime INT, userid BIGINT,
     page_url STRING, referrer_url STRING,
     ip STRING COMMENT 'IP Address of the User')
 COMMENT 'This is the page view table'
 PARTITIONED BY(dt STRING, country STRING)
 STORED AS SEQUENCEFILE;
```
以上语句创建了表`page_view`，包括列：viewTime, userid, page_url, referrer_url和ip，还包括注释。表也是分区的，数据存储在顺序文件中。这个数据格式的列之间以`ctrl-A`分隔，行之间以换行分隔。

```
CREATE TABLE page_view(viewTime INT, userid BIGINT,
     page_url STRING, referrer_url STRING,
     ip STRING COMMENT 'IP Address of the User')
 COMMENT 'This is the page view table'
 PARTITIONED BY(dt STRING, country STRING)
 ROW FORMAT DELIMITED
   FIELDS TERMINATED BY '\001'
STORED AS SEQUENCEFILE;
```
以上语句创建了一个和之前一样的表。只是行和列之间的分隔符是不一样的。

# 参考文献
[1] https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Select#LanguageManualSelect-PartitionBasedQueries
