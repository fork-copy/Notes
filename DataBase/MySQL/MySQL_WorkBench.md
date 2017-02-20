#

MySQL Workbench是一个可视化的MySQL辅助工具。


异常：
```
Error Code: 1175. You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column To disable safe mode, toggle the option in Preferences -> SQL Editor and reconnect.
```
解决方法：
```
SET SQL_SAFE_UPDATES = 0;
```

参考：
http://stackoverflow.com/questions/11448068/mysql-error-code-1175-during-update-in-mysql-workbench