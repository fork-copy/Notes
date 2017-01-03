# SQL相关的学习笔记

## 日志与时间
### 计算年龄

给出生日`@dob`，以下是两个简单的计算年龄的方式：
```
Date_format( From_Days( To_Days(Curdate()) - To_Days(@dob) ), '%Y' ) + 0 

Year(Curdate()) - Year(@dob) - ( Right(Curdate(),5) < Right(@dob,5) ) 
```

以下是忽略每月的day，保留两个小数点的年龄：
```
Round((((Year(now()) - Year(@dob)))*12 + (((Month(now()) - Month(@dob)))))/12, 2) 
```
*解释*:
- `To_Days`是把日期转换成天数； 
- `From_Days`是把天数转换成日期；
- `Date_format`是把日期转换成指定的格式，这里`%Y`是取日期的年份。
- `Right`是取最右边的指定个数的字符串。
- `Round`是四舍五入。

### 根据年份，第几周，星期几，来计算日期

```
SET @yr=2012, @wk=26, @day=0; 
SELECT Str_To_Date( Concat(@yr,'-',@wk,'-',If(@day=7,0,@day) ), '%Y-%U-%w' ) AS Date; 
+------------+ 
| Date       | 
+------------+ 
| 2012-06-24 | 
+------------+ 
```
*解释*
- `str_to-date`是将字符串转换成日期； 
- `concat`是连接字符串；
- `IF(expr1,expr2,expr3)`如果表达式1为true（expr1 <> 0 且 expr1 <> NULL），则返回表达式2，否则返回表达式3.

### 下个月的第一天日期

下个月的第一天日期:
```
concat(left(curdate() + interval 1 month, 8), '-01'); 
```
前一个月的第一天日期:
```
concat(left(curdate() - interval 1 month, 8), '-01'); 
```
*解释*
- `left`是取字符串最左边的指定个字符，与`right`类似，但方向相反。
- `curdate`是当前日期
- `interval`是间隔多少时间，可以间隔年，月，日，如下：
```
mysql> select curdate();
+------------+
| curdate()  |
+------------+
| 2017-01-03 |
+------------+

# 下一个月
mysql> select curdate()+interval 1 month;
+----------------------------+
| curdate()+interval 1 month |
+----------------------------+
| 2017-02-03                 |
+----------------------------+

# 上一个月
mysql> select curdate()-interval 1 month;
+----------------------------+
| curdate()-interval 1 month |
+----------------------------+
| 2016-12-03                 |
+----------------------------+

# 上两个月
mysql> mysql> select curdate()-interval 2 month;
+----------------------------+
| curdate()-interval 2 month |
+----------------------------+
| 2016-11-03                 |
+----------------------------+

# 前两年
mysql> select curdate()-interval 2 year;
+---------------------------+
| curdate()-interval 2 year |
+---------------------------+
| 2015-01-03                |
+---------------------------+

# 前两天
mysql> select curdate()-interval 2 day;
+--------------------------+
| curdate()-interval 2 day |
+--------------------------+
| 2017-01-01               |
+--------------------------+

```