
# MySQL导入数据时，报错
```
mysql> load data infile 'part-r-00000-994a6102-2976-4546-95d3-1ec62ada5a46.csv' into table xxnd fields terminated by ',' enclosed by '"' lines terminated by '\r\n' ;

The MySQL server is running with the --secure-file-priv option so it cannot execute this statement
```
解决方法：
加上`local`关键词
```
mysql> load data local infile 'part-r-00000-994a6102-2976-4546-95d3-1ec62ada5a46.csv' into table xxnd fields terminated by ',' enclosed by '"' lines terminated by '\r\n' ;
```

# 导入数据时，忽略第一个字段
由于在创建表的时候，经常会遇到“自增变量”id，这个时候，采用`load`加载数据，如果没有指定列名，则会向此列（id)加载数据，导致导入数据异常，此时，我们可以在“导入数据”语句的后面加上需要导入数据的列名：
```
mysql> load data local infile 'part-r-00000-994a6102-2976-4546-95d3-1ec62ada5a46.csv' into table xxnd fields terminated by ',' (`SchoolID`, `GradeLength`, `SubjectID`,`QTypeID`,`QTypeName`,   `DifficultyValue`,`UpdateTime`);
```

数据导入（`load data`）语法官方网页：http://dev.mysql.com/doc/refman/5.7/en/load-data.html