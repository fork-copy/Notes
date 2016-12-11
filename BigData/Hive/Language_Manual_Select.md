# SELECT 语法手册
- Select Syntax
 - WHERE Clause
 - ALL and DISTINCT Clauses
 - Partition Based Queries
 - HAVING Clause
 - LIMIT Clause
 - REGEX Column Specification
 - More Select Syntax

# Select语法
```
[WITH CommonTableExpression (, CommonTableExpression)*]    (Note: Only available starting with Hive 0.13.0)
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
  FROM table_reference
  [WHERE where_condition]
  [GROUP BY col_list]
  [ORDER BY col_list]
  [CLUSTER BY col_list
    | [DISTRIBUTE BY col_list] [SORT BY col_list]
  ]
 [LIMIT number]
```

- 一个`SELECT`语句可以是一个`union`查询的一部分，也可以是另一个查询的子查询。
- `table_reference`指查询的输入。它可以是一个正规表，一个视图，一个`join construct`或一个子查询。
- 表的名字和列名大小写不敏感。
- 简单的查询，例如从表`t1`查询所有列：
```
select * from t1
```
- 要获取当前数据库，使用`current_database()`函数：
```
select current_database()
```
- 要指定一个数据库，可以用数据库名字修饰表名（如从Hive 0.7开始，使用“db_name.table_name”），或者在查询语句前加上`USE语句`（从hive 0.6开始）。“db_name.table_name”允许查询不同数据库的表。
例如：
```
use database_name
select query_specifications;
```
# where 子句
`where`条件是一个布尔表达式。例如，以下查询返回那些来自US区域，`amount`大于10的销售记录。在where子句中，Hive支持一些[操作和UDFs](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF)。
```
SELECT * FROM sales WHERE amount > 10 AND region = "US"
```
从Hive 0.13开始，where子句开始支持一些子查询。

# ALL和DISTINCT子句
all和distinct选项指定重复的行是否返回。如果这些选项没有指定，默认为all（即所有匹配的行都返回）。distinct指定移除结果集中的重复行。
```
hive> SELECT col1, col2 FROM t1
    1 3
    1 3
    1 4
    2 5
hive> SELECT DISTINCT col1, col2 FROM t1
    1 3
    1 4
    2 5
hive> SELECT DISTINCT col1 FROM t1
    1
    2
```
all和distinct也可以使用在一个union子句中。

# 基于分区的查询
通常，一个select会查询扫描整个表。如果一个表在创建的时候，使用了`partitioned by`子句，则查询的时候会做“分区剪枝”，即不会扫描整个数据集，而是只扫描查询中指定的分区相关的数据。目前，Hive支持的“分区剪枝”的情况为：1)在where子句中指定了分区；2）或者在一个JOIN的ON里指定了分区。例如，如果表`page_views`有分区列`date`，则以下查询只会检索那些`days`在“2008－03－01”到”2008－03－31”的数据行。
```
SELECT page_views.*
FROM page_views
WHERE page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31'
```

如果一个表`page_views`join了另外一个表`dim_users`，你可以在ON子句中指定一个分区范围，如下：
```
SELECT page_views.*
FROM page_views JOIN dim_users
  ON (page_views.user_id = dim_users.id AND page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31')
```
- 另请参阅 [Group By](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+GroupBy).
- 另请参阅 [Sort By/ Cluster By/ Distribute By/ Order By](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+SortBy).

# HAVING子句
Hive在0.7.0版本中加入了对`HAVING`子句的支持，在之前的老版本中，也可以通过使用子查询来达到同样的效果。例如：
```
SELECT col1 FROM t1 GROUP BY col1 HAVING SUM(col2) > 10
```
也可以表达成：
```
SELECT col1 FROM (SELECT col1, SUM(col2) AS col2sum FROM t1 GROUP BY col1) t2 WHERE t2.col2sum > 10
```
# LIMIT 子句
`limit`表示返回行的数量。这些行是随机返回的。以下查询随机地从`t1`中返回了5行。
```
SELECT * FROM t1 LIMIT 5
```
- top k查询。以下查询返回了关于amount的前5条销售记录
```
SET mapred.reduce.tasks = 1
SELECT * FROM sales SORT BY amount DESC LIMIT 5
```

# 正则表达式列说明

如果配置属性`hive.support.quoted.identifiers`设置为none,`SELECT`语句可以使用基于正则表达式的列说明。
- 我们使用Java正则语法。对于测试，可以试http://www.fileformat.info/tool/regex.htm。
- 以下查询除`ds`和`hr`之外的所有列：
```
SELECT `(ds|hr)?+.+` FROM sales
```

# 更多的select 语法
以下是select语句其他的特征和语法的文档：
- GROUP BY
- SORT/ORDER/CLUSTER/DISTRIBUTE BY
- JOIN
  - Hive Joins
  - Join Optimization
- Outer Join Behavior
- UNION
- TABLESAMPLE
- Subqueries
- Virtual Columns
- Operators and UDFs
- LATERAL VIEW
- Windowing, OVER, and Analytics
- Common Table Expressions

ref:
https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Select
