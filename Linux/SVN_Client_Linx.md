# 在Linux下配置SVN客户端

在有些情况下，我们会在Linux下使用SVN，这个时候，我们需要对SVN进行用户名和密码配置。

如果没有配置用户名和密码，直接checkout文件，会报错，提示没有权限，但又没给出输入用户和密码的输入。

其实，SVN的用法和Git的用户类似，用过git的用户都知道，在使用git的时候，也是需要配置用户名和密码的。

以下是在系统CentOS 7下，使用SVN的配置过程：

## 配置username

在家目录下的`.subversion`文件夹下，找到servers文件，并编辑此文件：
```
[groups]
group_1 = your_svn_server  # 例如 https://192.16.41.20

[group_1]
username = your_user_name_of_svn ＃你的SVN账号
```
## 从SVN checkout 文件 
使用命令`svn co`检出文件
```
[yang@master SVN]$ svn co https://192.16.41.20/svn/***.file
Error validating server certificate for 'https://192.16.41.20:443':
 - The certificate is not issued by a trusted authority. Use the
   fingerprint to validate the certificate manually!
 - The certificate hostname does not match.
Certificate information:
 - Hostname: WIN-2DV7VJJR5JS
 - Valid: from Sun, 10 Apr 2016 08:22:31 GMT until Wed, 08 Apr 2026 08:22:31 GMT
 - Issuer: WIN-2DV7VJJR5JS
 - Fingerprint: 61:b6:82:a6:5a:a4:75:b1:4f:93:69:37:d7:6b:4b:02:9e:ee:2b:7c
(R)eject, accept (t)emporarily or accept (p)ermanently? p
Authentication realm: <https://192.16.41.20:443> VisualSVN Server
Password for 'yang': 
Authentication realm: <https://192.16.41.20:443> VisualSVN Server
Username: your_user_name_of_svn 
Password for 'your_user_name_of_svn': #这里需要输入你的SVN账号的密码。
```
然后，正常情况下的话，就会checkout出相应的文件了。

## 在SVN中添加文件夹

会用到两个命令，一个是`mkdir`，另一个是`commit`，如下：
```
[yang@master ～]$ svn mkdir testDir
A         testDir
[yang@master ～]$ svn commit -m 'add dir'
Adding         testDir
```
## 在SVN中添加文件 
先创建一个文件，然后使用`add`命令，再使用`commit`命令，`-m`表示对此次提交的备注
```
[yang@master testDir]$ vi testfile
[yang@master testDir]$ ls
testfile
[yang@master testDir]$ svn add testfile 
A         testfile
[yang@master testDir]$ svn commit -m 'add a file'
Adding         testfile
Transmitting file data .
Committed revision 7304.
```

## 在SVN中删除文件
```
[yang@master testDir]$ svn delete testfile 
D         testfile
[yang@master testDir]$ svn commit -m 'delete a file'
Deleting       testfile

Committed revision 7305.
```