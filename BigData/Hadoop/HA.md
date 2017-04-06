# High Availibility 高可用

要实现HDFS的高可用，有两种方式：
-   Quorum Journal Manager
-   Network File System

## Using Quorum Journal Manager
对于这种方式，根据对[官方文档](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html)的理解，我画了个图，如下：
![这里写图片描述](http://img.blog.csdn.net/20170405205519034?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc3Ryb25neW91bmc4OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
图上表述了一个集群大概的样子，有3个DataNode, 有两个NameNode，一个是Active, 一个是Standby， 每个NameNode节点都有开启ZKFailoverController（简写ZKFC）守护进程，有3个JournalNode。

那么问题来了，它是如何达到高可用呢？
1、首先，它有两个NameNode，一个是Active的，一个是Standby(备援状态)的，当Active宕机后，可以使用Standby的机器。
2、为了保证Standby的机器中，namespace和文件块与Active宕机之前一致，DataNode在向Active NameNode发送块的位置信息和“心跳”时，也要把这些信息发送给Standby NameNode，这样才能保证Standby中的文件块信息与Active NameNode中的数据一致。
3、为了保证Standby NameNode中的namespace与Active NameNode一致，Active NameNode需要把所有对namespace进行的操作都要记录到JournalNode.
4、作为Standby 的NameNode只要监控到JournalNode中namespace有更改日志，就合并到当前namespace，以保证namespace与Active Namenode一致。



## 自动失效备援
Zookeeper是一个高可用的服务，它可以用来维护数据协同，监测客户端，通知客户端数据中的改变
在失效备援中，Zookeeper的职责是：
- 故障探测
- Active NameNode选举

ZKFC是Zookeeper中的一个组件，它可以用来监测和管理NameNode的状态，每台运行NameNode的机器都要运行一个ZKFC，ZKFC的职责是：
- 健康监测
周期性Ping NameNode，如果能够及时得到回应，则表示健康
- Zookeeper session 管理
如果本地的NameNode健康，则在Zookeeper中保持一个session，如果本地的NameNode不仅是健康的，而且还是Active的，那么此时还会拥有一把特殊的“锁”，如果session过期，这个“锁”会丢失。
- 基于Zookeeper的选举
如果本地的NameNode健康，则ZKFC会检查是否持有“锁”，如果没有，则会尝试获取“锁”，如果得到“锁”，则会将本地的NameNode切换成Active。

# 参考文献
http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html