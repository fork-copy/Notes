使用github，在git push时，需要输入用户名和密码，这会很不方便，本文介绍使用ssh来实现无需输入用户名和密码。

场景：我有一个repository：https://github.com/strongyoung/Notes，我要实现对这个repository的git push不用输入用户名和密码。

# 生成key
找到自己的公共key，切换到家目录，进入.ssh目录下，看下有没有公共key，如果有，跳过这一步，如果没有，使用以下命令进行生成。

```
[yang@master ~]$ cd
[yang@master ~]$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/home/yang/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/yang/.ssh/id_rsa.
Your public key has been saved in /home/yang/.ssh/id_rsa.pub.
The key fingerprint is:
38:fb:83:ff:2c:60:08:a2:0b:c8:e8:0b:b6:c0:c5:71 yang@master
The key's randomart image is:
+--[ RSA 2048]----+
|                 |
|                 |
|   . E           |
|. o o  .         |
|=. + .o S        |
|*.. . oo         |
|=+   ..o         |
|=..   ..o.       |
| o.    .o+o      |
+-----------------+
[yang@master ~]$ ls .ssh/
id_rsa      id_rsa.pub
```
对于生成key有疑问，可以参考以下：
https://help.github.com/articles/generating-an-ssh-key/



# 部署key
登录github，找到相应的repository，点击“setting"，然后点左边的“Deploy keys"，再点右边的"add deploy key"，如下图：
![这里写图片描述](http://img.blog.csdn.net/20161124160450529)

在Title输入标题，在key的输入框中输入刚才生成的公有key，即id_rsa.pub的内容。再在“allow write access”前面打上勾。如下：

![这里写图片描述](http://img.blog.csdn.net/20161124161118164)

部署key也可以参考以下：
https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/


# 测试ssh 连接
使用以下命令进行测试

```
[yang@master Notes]$ ssh -T git@github.com
The authenticity of host 'github.com (192.30.253.113)' can't be established.
RSA key fingerprint is 16:27:ac:a5:76:28:2d:36:63:1b:56:4d:eb:df:a6:48.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'github.com,192.30.253.113' (RSA) to the list of known hosts.
Hi strongyoung/Notes! You've successfully authenticated, but GitHub does not provide shell access.

```
已成功连接，如有其他问题，可以参考以下：
https://help.github.com/articles/testing-your-ssh-connection/


# 配置remote url

使用以下命令进行配置remote url：

```
[yang@master Notes]$ git remote set-url origin git@github.com:strongyoung/Notes.git

```
这里的`git@github.com:strongyoung/Notes.git`换成你自己的repository，注意，这个是ssh 地址，不是https地址：
![这里写图片描述](http://img.blog.csdn.net/20161124161533268)
在“clone download”处点击“use SSH”，可以找到ssh的clone地址。


----------
# 测试


```
[yang@master Notes]$ git push origin master
Counting objects: 8, done.
Delta compression using up to 8 threads.
Compressing objects: 100% (4/4), done.
Writing objects: 100% (5/5), 503 bytes | 0 bytes/s, done.
Total 5 (delta 0), reused 0 (delta 0)
To git@github.com:strongyoung/Notes.git
   31da6f8..d7bb118  master -> master

```
恭喜，在push的时候，已无需输入用户名和密码。

参考文献：
[1] http://stackoverflow.com/questions/6565357/git-push-requires-username-and-password
