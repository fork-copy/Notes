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
`To_Days`是把日期转换成天数； 
`From_Days`是把天数转换成日期；
`Date_format`是把日期转换成指定的格式，这里`%Y`是取日期的年份。
`Right`是取最右边的指定个数的字符串。
`Round`是四舍五入。