
E155010
```
[yang@master EduCloud-Frontend-v2]$ svn cleanup 
svn: E155010: The node '/home/yang/SVN/V2.0/EduCloud-Frontend-v2/ecbdc-frontend-v2/node_modules/postcss-message-helpers/LICENSE' was not found.

```
解决方法：
如果没有安装`sqlite3`，则需要先安装这个软件
```
> sqlite3 .svn/wc.db
SQLite version 3.6.20
Enter ".help" for instructions
Enter SQL statements terminated with a ";"
sqlite> delete from work_queue;
sqlite> .quit

> svn cleanup
```

ref:
http://stackoverflow.com/questions/13376483/how-to-solve-svn-error-e155010