# NameNode节点学习

`NameNode`作为目录空间的管理者，也是Hadoop DFS的`inode table`。在任何DFS部署里，都有一个`NameNode`在运行。
（当有备份/容错的`NameNode`，或正在使用联合的`NameNode`除外）。

`NameNode`控制两个重要的表：
1） filename->blocksequence (命名空间)
    可通过文件名来查找数据块序列。
    这个表是存储在磁盘上的，非常重要。
2） block->machinelist ("inodes")
    可以知道数据块所在的机器列表。
    这个表是在`NameNode`每次启动的时候重新创建。

/**********************************************************

 * 'NameNode' refers to both this class as well as the 'NameNode server'.
 * The 'FSNamesystem' class actually performs most of the filesystem
 * management.  The majority of the 'NameNode' class itself is concerned
 * with exposing the IPC interface and the HTTP server to the outside world,
 * plus some configuration management.
 *
 * NameNode implements the
 * {@link org.apache.hadoop.hdfs.protocol.ClientProtocol} interface, which
 * allows clients to ask for DFS services.
 * {@link org.apache.hadoop.hdfs.protocol.ClientProtocol} is not designed for
 * direct use by authors of DFS client code.  End-users should instead use the
 * {@link org.apache.hadoop.fs.FileSystem} class.
 *
 * NameNode also implements the
 * {@link org.apache.hadoop.hdfs.server.protocol.DatanodeProtocol} interface,
 * used by DataNodes that actually store DFS data blocks.  These
 * methods are invoked repeatedly and automatically by all the
 * DataNodes in a DFS deployment.
 *
 * NameNode also implements the
 * {@link org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol} interface,
 * used by secondary namenodes or rebalancing processes to get partial
 * NameNode state, for example partial blocksMap etc.
 **********************************************************/