- HDFS Architecture
  - Introduction
  - Assumptions and Goals
    - Hardware Failure
    - Streaming Data Access
    - Large Data Sets
    - Simple Coherency Model
    - “Moving Computation is Cheaper than Moving Data”
    - Portability Across Heterogeneous Hardware and Software Platforms
  - NameNode and DataNodes
  - The File System Namespace
  - Data Replication
    - Replica Placement: The First Baby Steps
    - Replica Selection
    - Safemode
  - The Persistence of File System Metadata
  - The Communication Protocols
  - Robustness
    - Data Disk Failure, Heartbeats and Re-Replication
    - Cluster Rebalancing
    - Data Integrity
    - Metadata Disk Failure
    - Snapshots
  - Data Organization
    - Data Blocks
    - Staging
    - Replication Pipelining
  - Accessibility
    - FS Shell
    - DFSAdmin
    - Browser Interface
  - Space Reclamation
    - File Deletes and Undeletes
    - Decrease Replication Factor
  - References


引言
--
Hadoop Distributed File System(HDFS)是一个分布式的文件系统，它可以运行在普通硬件之上。它与其他的分布式文件系统有许多相似之处，然而，它与其他的分布式文件系统的区别也很明显。
HDFS的容错能力极强，最初的设计是可将其部署在廉价的硬件之上。
HDFS对应用数据提供了高吞吐量的访问，适合那些具有大量数据集的应用。
HDFS降低了一些POSIX要求，以允许对文件系统数据的流式访问。HDFS最初作为Apache Nutch网页搜索引擎项目的基础设施。HDFS是Apache Hadoop 的核心项目，项目地址为：http://hadoop.apache.org/

假设与目标
--
- 硬件出错
  假设硬件出错是常态，而不是意外，一个HDFS实例可能包含成百上千个节点来存储数据，所以有机器发生故障是非常正常且普遍的。因此，快速地探测错误的发生及修复是HDFS核心框架的目标。

- 流式数据访问
  HDFS需要流式处理应用数据，设计的初衷更偏向于批处理，而非用户交互使用，注重高吞吐，而非低延迟。

- 大量数据集
  HDFS支持海量数据集

- 简单一致性
  一次写入，多次读取，更加高效

- 移动计算比移动数据便宜
  移动计算相比移动数据，花费更少的网络带宽，将计算移动到离数据更近的节点，能够提交计算效率。

- 可移值性
  HDFS可以简单地从一个平台移植到另一个平台。

NameNode和DataNodes
--

HDFS是一个master/slave框架。一个HDFS集群包含一个NameNode，它是作为一个主服务，用来管理文件系统命名空间和来自客户端的文件访问请求。另外，有许多DataNodes，通常，在集群中，每个节点有一个DataNode，它用来管理存储实际数据。HDFS暴露了一个文件系统空间，用来允许用户存储数据到文件，在文件内部，一个文件被切分成一个或多个数据块，这些数据块是存储在一个DataNode集合里，即它可能存储在一个或多个DataNode上。NameNode执行文件系统命名空间操作，例如打开，关闭，重命名文件或目录。它也决定数据块到DataNode的映射关系。DataNode的职责是服务来自文件系统客户端的读和写请求。DataNode也执行来自NameNode的数据块的创建，探测，和拷贝指令。


![HDFS Architecture](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/images/hdfsarchitecture.png)

文件系统命名空间
-- 
NameNode维护文件系统命名空间，任何的文件系统命名空间或属性的改变，NameNode都会记录。

数据复本
--
关于数据块的复本，NameNode全权负责，它定时接收来自集群中DataNode的“心跳”和“块报告”。“心跳”的接收意味着DataNode正常运作。“块报告”包含了DataNode上所有数据块的列表信息。

![Blocks Replication](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/images/hdfsdatanodes.png)

副本的放置
--
对于HDFS的可靠性和必能，副本的放置至关重要。优化的副本放置，是HDFS与其也分布式文件系统的重要区别。

当副本因子为3，HDFS的副本放置策略会把一个副本放在本地机架，第二个副本放置在本地机架的另外一个节点上，然后把第三个副本放在其他机架上。

副本的选择
--
为了减少网络带宽消耗和读延迟，HDFS会尽量满足读请求，从一个与自己最近的节点读取数据。如果与读节点同一个机架上有一个副本，那么，这个副本会被优先选择。

安全模式
--
NameNode在启动的时候，会进入安全模式，在此模式期间，不能进行数据块的复制。NameNode接收来自DataNodes的“心跳”和“块报告”。“块报告”包含DataNode上的数据块的列表。每个数据块都有一个指定的最小副本数。NameNode会对此进行检查。如果检查到安全的副本的百分比小于指定配置的阈值，则会进入安全模式，然后NameNode会复制相应的数据块，直到满足最小要求的副本数。当比值达到了阈值时，NameNode会自动退出安全模式。

文件系统元数据的持久化
--
HDFS命名空间由NameNode存储。NameNode使用一个名为“EditLog“的事务日志来永久记录发生在文件系统元数据上的每一个改变。例如，在HDFS上创建一个新文件，将会引起NameNode来插入一条记录到“EditLog“来指示这个改变。同样，改变文件的副本因子，也会引起一条记录插入到EditLog。NameNode会使用本机操作系统的文件系统文件来存储这个EditLog。整个文件系统命名空间，是存储在一个名为“FsImage“的文件里，其中包括数据块到文件的映射和文件系统属性。”FsImage“也是作为一个文件存储在"NameNode"的本地文件系统之上。

“NameNode“会保持整个文件系统命名空间的镜像和文件数据块的映射在内存中。这个重要的元数据项设计地很紧凑，例如一个4GB的NameNode是很大的，可以支持大量的文件或目录。当NameNode启动的时候，从磁盘上读取FsImage和EditLog，应用所有来自EditLog中的事务，合并到FsImage，形成一个最新的FsImage，并且把这个最新的FsImage写到磁盘上。然后，NameNode会截断老的EditLog，因为它的事务已经作用到了持久化的FsImage。这个过程称为检查点（checkpoint).在当前的实现中，检查点只会发生在NameNode启动的时候。支持周期性地检查点的工作正在进行中。

DataNode以文件的形式存储HDFS数据在本地文件系统。DataNode没有HDFS文件的概念。它以单独的文件存储HDFS数据块在本地文件系统。DataNode不会在同一个目录下创建所有的文件。而是，它使用一个启发式方法来决定每个目录下最佳的文件数量并适当地创建子目录。在同一个目录下创建所有的本地文件不是最佳的，因为本地文件系统可能无法在单个目录下支持大量的文件。当DataNode启动的时候，它会扫描整个本地的文件系统，产生一个所有HDFS数据块的列表，并发送到NameNode：这个就是数据块报告（Blockreport)。