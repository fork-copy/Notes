# 数据操纵语言（DML）操作

Hive数据操纵语言操作文档在[Hive Data Manipulation Language](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DML).


- Hive Data Manipulation Language
 - Loading files into tables
   - Syntax
   - Synopsis
   - Notes
 - Inserting data into Hive Tables from queries
   - Syntax
   - Synopsis
   - Notes
   - Dynamic Partition Inserts
     - Example
     - Additional Documentation
 - Writing data into the filesystem from queries
   - Syntax
   - Synopsis
   - Notes
 - Inserting values into tables from SQL
   - Syntax
   - Synopsis
   - Examples
 - Update
   - Syntax
   - Synopsis
   - Notes
 - Delete
   - Syntax
   - Synopsis
   - Notes
 - Merge
   - Syntax
   - Synopsis
   - Notes

## 载入文件数据到表中
当加载数据到表中时，Hive不做任何转换。当前，加载操作是纯净地拷贝或移动操作，即移动数据文件到Hive表相应的位置。

### 语法
```
LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)]
```

例如：

从平面文件加载数据到hive：
```
 hive> LOAD DATA LOCAL INPATH './examples/files/kv1.txt' OVERWRITE INTO TABLE pokes;
 ```

加载本地文件系统的`./examples/files/kv1.txt`文件内容到hive的pokes表中。文件包含两列，由`ctrl-a`分隔。`LOCAL`表示输入文件在本地文件系统，如果没有加`LOCAL`，hive则会去HDFS上查找该文件。

关键语`OVERWRITE`表示如果表中有数据，则先删除数据，再插入新数据，如果没有这个关键词，则直接附加数据到表中。

### 概要
加载操作是纯粹地拷贝或移动操作，即将数据文件移动到Hive表所在的位置。
- `filepath` 可以是：
  - 一个相对路径，例如`project/data1`
  - 一个绝对路径，例如`/user/hive/project/data1`
  - 全路径URI，例如`hdfs://namenode:9000/user/hive/project/data1`
- 加载的目标可以是一个表，也可以是一个分区。如果表是分区的，则必须通过指定所有分区列的值来指定一个表的分区。
- `filepath`可以是一个文件，也可以是一个目录。不管什么情况下，`filepath`被认为是一个文件集合。
- 如果指定了关键词`LOCAL`，则
  - 加载命令会在本地文件系统中查找这个路径。如果指定的是一个相对路径，它将被解释成用户的当前工作目录的相对路径。用户可以指定一个本地文件的全限定URI，例如`file:///user/hive/project/data1`
  - 加载命令将设法拷贝路径下的所有文件到目标文件系统。目标文件系统由表的位置属性推测而来。拷贝过来的文件然后被移动到表中。
- 如果关键词`LOCAL`没有指定，然后Hive就会使用`filepath`的全限定URI，将会使用以下规则：
 - 如果schema或authority没有指定，Hive将使用hadoop配置变量`fs.default.name`指定的`namenode`URI。
 - 如果路径不是绝对路径，然后Hive将会解释成`/user/<username>`的相对路径
 - Hive移动文件到表（或分区）中
- 如果使用了关键词`OVERWRITE`，目标表中的数据将会被删除并被指定的文件内容所替换。否则指定的文件数据将会附加到表中。
 - 注意:如果目标表已经存在一个文件名与`filepath`中的任意一个文件相同产生冲突时，已存在的文件将会被替换成新的文件。

### 说明
- `filepath`不能包含子目录。
- 如果关键词`LOCAL`没有给出，`filepath`必须指向与表位置相同的文件系统的文件。
- Hive做了少量的检查，以确保文件能匹配表，然后被加载，目前，它检查表是否存储在一个顺序文件格式，被加载的文件是否也是顺序文件，反之亦然

## 将查询结果插入到Hive表
使用`insert`关键词可以把查询结构插入到Hive表中。
### 语法
```
Standard syntax:
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...) [IF NOT EXISTS]] select_statement1 FROM from_statement;
INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1 FROM from_statement;
INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] (z,y) select_statement1 FROM from_statement;

Hive extension (multiple inserts):
FROM from_statement
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...) [IF NOT EXISTS]] select_statement1
[INSERT OVERWRITE TABLE tablename2 [PARTITION ... [IF NOT EXISTS]] select_statement2]
[INSERT INTO TABLE tablename2 [PARTITION ...] select_statement2] ...;
FROM from_statement
INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1
[INSERT INTO TABLE tablename2 [PARTITION ...] select_statement2]
[INSERT OVERWRITE TABLE tablename2 [PARTITION ... [IF NOT EXISTS]] select_statement2] ...;

Hive extension (dynamic partition inserts):
INSERT OVERWRITE TABLE tablename PARTITION (partcol1[=val1], partcol2[=val2] ...) select_statement FROM from_statement;
INSERT INTO TABLE tablename PARTITION (partcol1[=val1], partcol2[=val2] ...) select_statement FROM from_statement;
```

### 概要
- `INSERT OVERWEITE`将会覆盖表或分区中的所有数据。
 - 除非对分区提供了`if not exists`
- `INSERT INTO`将会追加数据到表或分区中，保持已有数据。
- 在相同的查询中，可以指定多个insert语句。
- 每个查询语句的输出都被写到所选的表（或分区）中。
- 输出格式和序列化类由表的元数据决定。
- 从Hive 1.1.0起，关键词`TABLE`是可选。
- 从Hive 1.2.0起，每个`INSERT INTO T`可以带上列的列表，就像这样`INSERT INTO T(z,x,c1)`。

### 说明

- 多表插入可以最小化数据扫描次数。Hive可以通过只扫描一次输入数据（应用不同的查询操作），然后插入到多个表中。


## 将查询结果写入文件系统

###　语法
```
Standard syntax:
INSERT OVERWRITE [LOCAL] DIRECTORY directory1
  [ROW FORMAT row_format] [STORED AS file_format] (Note: Only available starting with Hive 0.11.0)
  SELECT ... FROM ...
 
Hive extension (multiple inserts):
FROM from_statement
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 select_statement1
[INSERT OVERWRITE [LOCAL] DIRECTORY directory2 select_statement2] ...
 
 
row_format
  : DELIMITED [FIELDS TERMINATED BY char [ESCAPED BY char]] [COLLECTION ITEMS TERMINATED BY char]
        [MAP KEYS TERMINATED BY char] [LINES TERMINATED BY char]
        [NULL DEFINED AS char] (Note: Only available starting with Hive 0.13)
```

### 概要
- 目录可以是一个全限定URI。如果没有指定schema，将使用hadoop配置变量`fs.default.name`指定的NameNode URI。

- 如果使用了关键词`LOCAL`，Hive将写数据到本地文件系统的目录中。
- 数据将以文本格式，列由`^A`分隔，行由换行分隔，存储到本地文件系统。如果有任意列不是原生类型，则这些列会被序列化成JSON格式。

### 说明
- `INSERT OVERWRITE`语句，对于本地目录，在同的查询里，表（或分区）可以一起使用。
- `INSERT OVERWRITE`语句，对于HDFS文件系统目录，最非常适合从Hive中提取大量的数据。Hive可以在一个map-reduce作业中，并行地写数据到HDFS目录中。
## 从SQL中，插入值到表中
语句`INSERT...VALUES`可以直接从SQL插入数据到表中。这个功能是从Hive 0.14之后可以的。

### 语法
```
Standard Syntax:
INSERT INTO TABLE tablename [PARTITION (partcol1[=val1], partcol2[=val2] ...)] VALUES values_row [, values_row ...]
 
Where values_row is:
( value [, value ...] )
where a value is either null or any valid SQL literal
```

### 说明
- `VALUES`语句后的每一行都会被插入表`tablename`中。
- 表中的每一列的值都必须提供。
- 动态分区同样支持`INSERT...SELECT`方式。
- 如果插入的表支持ACID，在使用支持ACID的事务管理，这个操作将会自动成功地完成提交。
- 排序表（在创建表的时候，使用了`SORTED BY`词）不支持插入，更新和删除操作。
- Hive不支持复杂类型（array,map,struct ,union)，因此，它不能在`INSERT INTO ...VALUES`里使用。这意味着，用户不能使用`INSERT INTO...　VALUES`插入一个复杂数据类型列。

### 例子
```
CREATE TABLE students (name VARCHAR(64), age INT, gpa DECIMAL(3, 2))
  CLUSTERED BY (age) INTO 2 BUCKETS STORED AS ORC;
 
INSERT INTO TABLE students
  VALUES ('fred flintstone', 35, 1.28), ('barney rubble', 32, 2.32);
 
 
CREATE TABLE pageviews (userid VARCHAR(64), link STRING, came_from STRING)
  PARTITIONED BY (datestamp STRING) CLUSTERED BY (userid) INTO 256 BUCKETS STORED AS ORC;
 
INSERT INTO TABLE pageviews PARTITION (datestamp = '2014-09-23')
  VALUES ('jsmith', 'mail.com', 'sports.com'), ('jdoe', 'mail.com', null);
 
INSERT INTO TABLE pageviews PARTITION (datestamp)
  VALUES ('tjohnson', 'sports.com', 'finance.com', '2014-09-23'), ('tlee', 'finance.com', null, '2014-09-21');
```

## 更新
### 语法
```
Standard Syntax:
UPDATE tablename SET column = value [, column = value ...] [WHERE expression]
```

### 概要
- 引用的列必须是将要更新表的一列。
- 赋值必须是Hive在`select`中支持的表达式。因此，数学操作，UDFs，转换，字面值等是支持的。子查询是不支持的。
- 只有匹配`where`的行才会被更新。
- 分区列不能被更新。
- 桶（bucket)列不能被更新。

## 删除
在Hive 0.14之后，支持删除
### 语法
```
Standard Syntax:
DELETE FROM tablename [WHERE expression]
```
### 概要
- 只有匹配`where`的行才会被删除。

# 简单示例

## MovieLens 用户评分

首先，创建一个表，字段之前使用`\t`分隔：
```
CREATE TABLE u_data (
  userid INT,
  movieid INT,
  rating INT,
  unixtime STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;
```
然后，从GoupLens datasets下载数据集：
```
wget http://files.grouplens.org/datasets/movielens/ml-100k.zip
```
或者：
```
curl --remote-name http://files.grouplens.org/datasets/movielens/ml-100k.zip
```
解压文件：
```
unzip ml-100k.zip
```
然后把数据`u.data`加载到刚才创建的表：
```
LOAD DATA LOCAL INPATH '<path>/u.data'
OVERWRITE INTO TABLE u_data;
```
查询表`u_data`有多少行：
```
SELECT COUNT(*) FROM u_data;
```


