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


## 引言
Hadoop Distributed File System(HDFS)是一个分布式的文件系统，它可以运行在普通硬件之上。它与其他的分布式文件系统有许多相似之处，然而，它与其他的分布式文件系统的区别也很明显。
HDFS的容错能力极强，最初的设计是可将其部署在廉价的硬件之上。
HDFS对应用数据提供了高吞吐量的访问，适合那些具有大量数据集的应用。
HDFS降低了一些POSIX要求，以允许对文件系统数据的流式访问。HDFS最初作为Apache Nutch网页搜索引擎项目的基础设施。HDFS是Apache Hadoop 的核心项目，项目地址为：http://hadoop.apache.org/

## 假设与目标

### 硬件出错
  假设硬件出错是常态，而不是意外，一个HDFS实例可能包含成百上千个节点来存储数据，所以有机器发生故障是非常正常且普遍的。因此，快速地探测错误的发生及修复是HDFS核心框架的目标。

### 流式数据访问
  HDFS需要流式处理应用数据，设计的初衷更偏向于批处理，而非用户交互使用，注重高吞吐，而非低延迟。

### 大量数据集
  HDFS支持海量数据集

### 简单一致性
  一次写入，多次读取，更加高效

### 移动计算比移动数据便宜
  移动计算相比移动数据，花费更少的网络带宽，将计算移动到离数据更近的节点，能够提交计算效率。

### 可移值性
  HDFS可以简单地从一个平台移植到另一个平台。

## NameNode和DataNodes


HDFS是一个master/slave框架。一个HDFS集群包含一个NameNode，它是作为一个主服务，用来管理文件系统命名空间和来自客户端的文件访问请求。另外，有许多DataNodes，通常，在集群中，每个节点有一个DataNode，它用来管理存储实际数据。HDFS暴露了一个文件系统空间，用来允许用户存储数据到文件，在文件内部，一个文件被切分成一个或多个数据块，这些数据块是存储在一个DataNode集合里，即它可能存储在一个或多个DataNode上。NameNode执行文件系统命名空间操作，例如打开，关闭，重命名文件或目录。它也决定数据块到DataNode的映射关系。DataNode的职责是服务来自文件系统客户端的读和写请求。DataNode也执行来自NameNode的数据块的创建，探测，和拷贝指令。


![HDFS Architecture](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/images/hdfsarchitecture.png)

## 文件系统命名空间

NameNode维护文件系统命名空间，任何的文件系统命名空间或属性的改变，NameNode都会记录。

## 数据复本

关于数据块的复本，NameNode全权负责，它定时接收来自集群中DataNode的“心跳”和“块报告”。“心跳”的接收意味着DataNode正常运作。“块报告”包含了DataNode上所有数据块的列表信息。

![Blocks Replication](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/images/hdfsdatanodes.png)

### 副本的放置

对于HDFS的可靠性和必能，副本的放置至关重要。优化的副本放置，是HDFS与其也分布式文件系统的重要区别。

当副本因子为3，HDFS的副本放置策略会把一个副本放在本地机架，第二个副本放置在本地机架的另外一个节点上，然后把第三个副本放在其他机架上。

### 副本的选择

为了减少网络带宽消耗和读延迟，HDFS会尽量满足读请求，从一个与自己最近的节点读取数据。如果与读节点同一个机架上有一个副本，那么，这个副本会被优先选择。

### 安全模式

NameNode在启动的时候，会进入安全模式，在此模式期间，不能进行数据块的复制。NameNode接收来自DataNodes的“心跳”和“块报告”。“块报告”包含DataNode上的数据块的列表。每个数据块都有一个指定的最小副本数。NameNode会对此进行检查。如果检查到安全的副本的百分比小于指定配置的阈值，则会进入安全模式，然后NameNode会复制相应的数据块，直到满足最小要求的副本数。当比值达到了阈值时，NameNode会自动退出安全模式。

## 文件系统元数据的持久化

HDFS命名空间由NameNode存储。NameNode使用一个名为“EditLog“的事务日志来永久记录发生在文件系统元数据上的每一个改变。例如，在HDFS上创建一个新文件，将会引起NameNode来插入一条记录到“EditLog“来指示这个改变。同样，改变文件的副本因子，也会引起一条记录插入到EditLog。NameNode会使用本机操作系统的文件系统文件来存储这个EditLog。整个文件系统命名空间，是存储在一个名为“FsImage“的文件里，其中包括数据块到文件的映射和文件系统属性。”FsImage“也是作为一个文件存储在"NameNode"的本地文件系统之上。

“NameNode“会保持整个文件系统命名空间的镜像和文件数据块的映射在内存中。这个重要的元数据项设计地很紧凑，例如一个4GB的NameNode是很大的，可以支持大量的文件或目录。当NameNode启动的时候，从磁盘上读取FsImage和EditLog，应用所有来自EditLog中的事务，合并到FsImage，形成一个最新的FsImage，并且把这个最新的FsImage写到磁盘上。然后，NameNode会截断老的EditLog，因为它的事务已经作用到了持久化的FsImage。这个过程称为检查点（checkpoint).在当前的实现中，检查点只会发生在NameNode启动的时候。支持周期性地检查点的工作正在进行中。

DataNode以文件的形式存储HDFS数据在本地文件系统。DataNode没有HDFS文件的概念。它以单独的文件存储HDFS数据块在本地文件系统。DataNode不会在同一个目录下创建所有的文件。而是，它使用一个启发式方法来决定每个目录下最佳的文件数量并适当地创建子目录。在同一个目录下创建所有的本地文件不是最佳的，因为本地文件系统可能无法在单个目录下支持大量的文件。当DataNode启动的时候，它会扫描整个本地的文件系统，产生一个所有HDFS数据块的列表，并发送到NameNode：这个就是数据块报告（Blockreport)。

## 通信协议

所有的HDFS通信协议，都是位于TCP/IP之上。客户端建立一个连接到NameNode机器上的一个可配置的TCP端口。它与NameNode使用Client Protocol通信。DataNode与NameNode使用DataNode Protocol通信。远程过程调用（Remote Procedure Call,RPC)是Client Protocol和DataNode Protocol的抽象。在设计上，NameNode从来不会初始化RPCs。而是，它仅仅响应来自DataNodes或客户端发来的RPC请求。

## 健壮性

HDFS最主要的目标就是可靠地存储数据，三个最常见的错误类型是：NameNode失败，DataNode失败和网络割裂（network partitions).

### 数据磁盘失败，心跳和重新拷贝（data disk failure , heartbeat and re-replication)

每个DataNode会周期性地发送一个“心跳”消息给NameNode。网络割裂会引起一些DataNode与NameNode失去连接。NameNode通过是否有“心跳”信息来探测这种情况。如果最近没有收到“心跳“，NameNode会标记此DataNode为不可用，也称为死的DataNode，然后不会发送一些IO请求给该DataNode。在死的DataNode上的任何数据，都是无法获取得到的。DataNode的死（或者说不可用）可能引起一些数据块的“副本因子”降到他们指定值的大小。NameNode会持续跟踪需要拷贝的数据块并在需要的时候进行拷贝。引起重新拷贝的原因有：一个DataNode变得不可用，一个副本不能用，DataNode的硬盘失败，或者一个文件的副本因子变大了。


### 集群均衡

HDFS框架与数据均衡方案是兼容的。如果某个DataNode的空闲空间降到了特定的阈值，均衡方案可以自动地从一个DataNode移动数据到另外一个。对于一个指定的文件，如果突然变得要求很高，均衡方案可以动态地创建额外的副本，平衡集群中的其他数据。这些类型的数据均衡方案还没有实现。

### 数据完整性

一个数据块，从DataNode获取，然后有可能收到的时候被损坏。这种情况可能是：存储设备的出错，网络出错，或软件的问题。HDFS的客户端软件实现了对HDFS文件内容检验的校验和。当一个客户端创建一个HDFS文件，它会计算每个文件数据块的校验和，并且会在相同的HDFS命名空间中，存储这些校验和在一个隐藏的单独文件中。当客户端检索文件内容时，它也会验证数据是否匹配存储在相应校验和文件中的校验和。如果不匹配，客户端将会从那个数据块的另外一个副本检索数据。

### 元数据磁盘故障

FsImage和EditLog是HDFS的中央数据结构。这些文件的损坏会导致HDFS实例无法工作。对于这个原因，NameNode可以通过配置，以支持保留多个FsImage和EditLog的副本。任何对这两个文件的更新都会同步到这两个文件的副本。这种同步多个副本的方法可能会降低NameNode能够支持的命名空间事务每秒的速率。然而，这个降低是可以接受的，因为HDFS应用是天生的数据密集型，他们不是元数据密集型。当NameNode重起时，它会选择最新的一致的FsImage和EditLog文件来使用。

对于增加错误的恢复能力，另外一种选择是：使用[NFS共享存储](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithNFS.html)或[分布式的编辑日志](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html)（称之为Journal)以达到多个NameNode来实现高可用。推荐使用后面这种方式，即Journal。

### 快照

[快照](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)支持存储在某个时刻数据的一个拷贝。这个快照的一种用法是：及时回滚一个失效的HDFS实例到之前正常的某个点。

## 数据组织

HDFS被设计为支持非常大的文件。与HDFS兼容的应用是处理那些海量的数据集的应用。这些应用只写一次数据，但是他们读取一次或多次，并且要求满足流式速度读取。HDFS支持一次写，多次读的文件。HDFS使用的经典的块大小是128MB。因此，一个HDFS文件被分割成128MB的数据块，如果可以的话，每个块将存储在不同的DataNode上。（因为在执行MR程序时，每个数据块会运行一个map任务，数据块分布在不同的DataNode，有助于提交程序的并发度）

### Staging

客户端请求创建一个文件，并不是马上到达NameNode的。实际上，起初HDFS客户端把文件数据缓存到本地缓冲区（buffer）。写申请是被重定向到这个本地缓冲区。当本地文件积累数据到一个数据块大小时（即一个chunk size），客户端会联系NameNode。NameNode插入文件名字到文件系统中，并且分配一个数据块给它。NameNode响应客户端请求，返回DataNode身份和目标数据块信息。然后，客户端会将本地缓冲区的数据写到指定的DataNode上。当一个文件被关闭时，也就是剩下的在本地缓冲区中没有被冲洗的数据全部被转移到DataNode的时候。然后，客户端告诉NameNode文件被关闭。在这个点，NameNode会提交文件创建操作持久化存储。如果在文件关闭之前，NameNode死了（进程挂了），则文件就丢了。

经过慎重考虑，以上方式已经被采用。这些应用需要流式处理读取文件。如果一个客户端没有任何的客户端缓冲区就直接写一个远程的文件，网络速度和网络拥塞会严重影响吞吐。这个方式史无前例。早期的分布式文件系统，例如AFS，使用客户端缓存来改善性能。通过降低POSIX的要求，以达到提高数据上传的性能。

### 管道复制

当客户端写数据到HDFS文件，正如前面部分描述，它的数据首先写到本地缓冲区。假设HDFS文件的副本因子是3.当本地缓冲区积累到一个数据块大小，客户端从NameNode检索到一个DataNode列表。这个列表包含了将要存储那个数据块副本的DataNode。然后，客户端冲洗（flush)数据块到第一个DataNode。第一个DataNode开始接收小部分数据，这些数据写到本地目录，并且传输给DataNode列表中的第二个DataNode。第二个DataNode开始接收数据块的每个小部分数据，写到本地目录，并且冲洗这些数据到第三个DataNode。最后，第三个DataNode写数据到本地目录。因此，一个DataNode可以从管道中的前一个DataNode接收数据，同时，也可以转交数据给管道中的下一个DataNode。因此，数据是从一个DataNode流向下一个DataNode。

## Accessibility

应用访问HDFS有许多不同的方式。HDFS提供了一个[文件系统Java API](http://hadoop.apache.org/docs/current/api/)供应用使用。也有对这个[Java API进行C语言的包装](wrapper)和[REST API](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-hdfs/WebHDFS.html)可以使用。另外，HTTP浏览器也是可以用来浏览HDFS实例的文件。通过使用NFD网关，HDFS可以作为客户端的本地文件系统的一部分被挂载。

### 文件系统脚本（Fs shell)

HDFS允许用户以文件和目录的方式组织数据。它提供了一个命令接口（称之为FS Shell)，可以让用户与HDFS数据进行交互。这个命令的语法与其他脚本（例如bash,csh)类似，以下是一些命令的例子：

|行为|命令|
|:----|:----|
|创建一个名为 /foodir 的目录	|bin/hadoop dfs -mkdir /foodir|
|删除一个名为 /foodir	的目录 |bin/hadoop fs -rm -R /foodir|
|查看一个名为 /foodir/myfile.txt 的文件内容	|bin/hadoop dfs -cat /foodir/myfile.txt|

### DFSAdmin

DFSAdmin命令集是被用于管理一个HDFS集群。这些命令只能被HDFS管理员使用。以下是一些例子：
|行为|命令|
|:----|:----|
|让集群进入安全模式	| bin/hdfs dfsadmin -safemode enter |
|产生一个DataNodes列表	| bin/hdfs dfsadmin -report |
|重新委任或解除DataNode(s)	| bin/hdfs dfsadmin -refreshNodes |

### 浏览器接口


一个常见的HDFS安装会配置一个网页服务来暴露HDFS命名空间。这使得用户可以通过网页浏览器来浏览HDFS命名空间和文件内容。

## 空间回收

### 文件删除与摊销删除

如果垃圾回收配置是启用的，通过FS Shell删除的文件并没有立即从HDFS移除。而是，HDFS移动它到一个垃圾箱目录（每个用户有一个自己的垃圾箱目录，在`/user/<username/.Trash`下）。只要文件还在垃圾箱目录中，文件就可以马上恢复。

大多数最近被删除的文件是移动到垃圾箱的`current`目录（`/user/<username>/.Trash/Current`)，在一个可配置的时间间隔内，HDFS会创建检查点（在`/user/<username>/.Trash/<date>`下），并删除过期的检查点。关于垃圾箱的检查点，请查看[expunge command of FS shell](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-common/FileSystemShell.html#expunge)。

当文件在垃圾箱内超过一定期限，NameNode会从HDFS命名空间删除这个文件。文件的删除会引起相关数据块的释放。注意：文件的删除与相应数据块的释放会有时间延迟。

以下是一个例子，展示了文件是如何通过FS Shell从HDFS被删除的。在目录`delete`下，我们创建了两个文件（test1和test2）。

```
$ hadoop fs -mkdir -p delete/test1
$ hadoop fs -mkdir -p delete/test2
$ hadoop fs -ls delete/
Found 2 items
drwxr-xr-x   - yang hdfs          0 2016-12-15 10:34 delete/test1
drwxr-xr-x   - yang hdfs          0 2016-12-15 10:34 delete/test2
```
我们将要删除文件test1，以下命令显示了删除的文件被移动到了垃圾箱目录下：
```
$ hadoop fs -rm -r delete/test1
16/12/15 10:35:20 INFO fs.TrashPolicyDefault: Moved: 'hdfs://node1:8020/user/yang/delete/test1' to trash at: hdfs://node1:8020/user/yang/.Trash/Current/user/yang/delete/test1

```
现在我们使用配置项`skipTrash`来删除文件test2，使用这个配置项之后，删除的文件不会移动到垃圾箱目录下，它将完全从HDFS上删除：
```
$ hadoop fs -rm -r -skipTrash delete/test2
Deleted delete/test2
```
现在，我们发现，在垃圾箱目录下，只发现了文件test1：
```
$ hadoop fs -ls .Trash/Current/user/yang/delete/
Found 1 items\
drwxr-xr-x   - yang hdfs          0 2016-12-15 10:34 .Trash/Current/user/yang/delete/test1

```
因此，文件test1被删除之后进入了垃圾箱，而文件test2则永久被删除。

### 降低副本因子

当一个文件的副本因子降低了，NameNode会删除超出的副本。下一次心跳会传输这个信息给DataNode。DataNode会删除相应的数据块并释放集群中相应的空闲空间。再次提醒：在删除副本与释放空间之间可能存在时间延迟。

## 引用

Hadoop [JavaDoc API](http://hadoop.apache.org/docs/current/api/)

HDFS source code: http://hadoop.apache.org/version_control.html