# NameNode节点学习

`NameNode`作为目录空间的管理者，也是Hadoop DFS的`inode table`。在任何DFS部署里，都有一个`NameNode`在运行。
（当有备份/容错的`NameNode`，或正在使用联合的`NameNode`除外）。

`NameNode`控制两个重要的表：

1） filename->blocksequence (命名空间)
    可通过文件名来查找数据块序列。
    这个表是存储在磁盘上的，非常重要。

2） block->machinelist ("inodes")
    可以知道数据块所在的机器列表。
    这个表是存储在内存中的，在`NameNode`每次启动的时候重新创建。


`NameNode`可以指`NameNode`类，也可以指`NameNode server`。实际上，是`FSNamesystem`类执行了大多数的文件系统管理操作。
而`NameNode`类它本身大多数是暴露`IPC`接口和`HTTP`服务给外部程序，加上一些配置管理。

`NameNode`实现了`org.apache.hadoop.hdfs.protocol.ClientProtocol`接口，用于允许客户端来请求DFS服务。这个接口不能直接被DFS客户端代码使用，终端用户应该使用`org.apache.hadoop.fs.FileSystem`类来替代。

`NameNode`也实现了`org.apache.hadoop.hdfs.server.protocol.DatanodeProtocol`接口，用于让`Datanodes`来实际存储DFS数据块。在一个DFS部署里的所有`DataNodes`，这些方法是自动且重复地被触发。

`NameNode`也实现了`org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol`接口，用于让`secondary namenodes`或负载均衡进程来获取部分`NameNode`状态，例如`partial blocksMap`等。
