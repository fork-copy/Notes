
- 测试远程机器的指定端口是否可达（连通）
方法一：在本机检查，使用`netstat`命令：
```
[root@node1 conf]# netstat -lpten | grep 8020
tcp        0      0 172.16.41.54:8020       0.0.0.0:*               LISTEN      1014       60973      8801/java    
```
方法二：使用`telnet`命令：
```
[root@master Soft]# telnet localhost 22
```
方法三：在远程检查，使用`ssh`命令：

连接不成功的：
```
[root@master Soft]# ssh node1 -p 9000 -v
OpenSSH_6.6.1, OpenSSL 1.0.1e-fips 11 Feb 2013
debug1: Reading configuration data /etc/ssh/ssh_config
debug1: /etc/ssh/ssh_config line 56: Applying options for *
debug1: Connecting to node1 [172.16.41.54] port 9000.
debug1: connect to address 172.16.41.54 port 9000: Connection refused
ssh: connect to host node1 port 9000: Connection refused
```
连接成功的：
```
[root@master Soft]# ssh node1 -p 8020 -v
OpenSSH_6.6.1, OpenSSL 1.0.1e-fips 11 Feb 2013
debug1: Reading configuration data /etc/ssh/ssh_config
debug1: /etc/ssh/ssh_config line 56: Applying options for *
debug1: Connecting to node1 [172.16.41.54] port 8020.
debug1: Connection established.
debug1: permanently_set_uid: 0/0
debug1: identity file /root/.ssh/id_rsa type 1
debug1: identity file /root/.ssh/id_rsa-cert type -1
debug1: identity file /root/.ssh/id_dsa type -1
debug1: identity file /root/.ssh/id_dsa-cert type -1
debug1: identity file /root/.ssh/id_ecdsa type -1
debug1: identity file /root/.ssh/id_ecdsa-cert type -1
debug1: identity file /root/.ssh/id_ed25519 type -1
debug1: identity file /root/.ssh/id_ed25519-cert type -1
debug1: Enabling compatibility mode for protocol 2.0
debug1: Local version string SSH-2.0-OpenSSH_6.6.1
ssh_exchange_identification: Connection closed by remote host
```

参数选项说明：
```
     -p port
             Port to connect to on the remote host.  This can be specified on
             a per-host basis in the configuration file.

     -v      Verbose mode.  Causes ssh to print debugging messages about its
             progress.  This is helpful in debugging connection, authentica‐
             tion, and configuration problems.  Multiple -v options increase
             the verbosity.  The maximum is 3.
```