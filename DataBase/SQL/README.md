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