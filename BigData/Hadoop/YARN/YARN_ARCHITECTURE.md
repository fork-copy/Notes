# Apache Hadoop YARN
YARN的基本理念是要将资源管理器和作业调度/监控分离成独立的守护进程。这个理念就是说，要有一个全局的ResourceManager（RM）和每个应用有一个ApplicationMaster (AM)，一个应用可以是一个单一的作业，也可以是作业中的一个有向无环图（DAG）。

`ResourceManager`和`NodeManager`形成了数据计算框架，在整个系统中，ResourceManager是控制着所有应用的资源请求。NodeManager是每个机器的框架中介，它有责任为容器监控他们的资源（cpu,内存,磁盘，网络），并且上报同样的信息给ResourceManager/Scheduler。

实际上，每个应用的`ApplicationMaster`是一个明确的框架，任务是向`ResourceManager`申请资源，且与`NodeManager(s)`携手工作来执行和监视任务。

![hadoop yarn architecture](http://img.blog.csdn.net/20170415113243606?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc3Ryb25neW91bmc4OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

ResourceManager有两个主要的组件：Scheduler 和 ApplicationsManager.
**注意：** `ApplicationsManager`和`ApplicationMaster`的区别。



`Scheduler`的责任是分配资源给正在运行的应用，Scheduler是纯净的调度器，它不执行监控或跟踪应用的状态。它也不提供关于重启失败任务的保证。Scheduler是基于应用的资源要求来执行它的调度功能。它也是基于资源容器（resource container)的一个抽象概念，这个容器合并了例如内存，cpu，磁盘和网络等组件。

`ApplicationsManager`的职责是接受作业提交，谈判第一个容器来执行应用指定的`ApplicationMaster`，并且在出错的时候，提供重启`ApplicationMaster`的服务。每个应用的`ApplicationMaster`都有职责从`Scheduler`来谈判合适的资源容器，并且跟踪他们的状态和监控进度。


下面说一说我对上面那图的理解：
1、客户端向`ResourceManager`发出作业提交申请；
2、由`ResourceManager`中的`ApplicationsManager`负责接收作业的提交；
3、`ApplicationMaster`可能需要多个`Container`来执行应用，这个时候，`ApplicationMaster`需要向`ResourceManager`申请资源，即`Container`。
4、然后，`ResourceManager`中的`ApplicationsManager`向`ResourceManager`中的`Scheduler`进行谈判，告诉`Scheduler`，我要一个容器（`Container`，这个容器包括应用运行所需的所有资源（memory,cpu,disk,net etc）。
5、`Scheduler`根据某种策略，响就`ApplicationsManager`的请求，来，给你一个`Containner`，我只负责资源分配，其他的我不管。这个时候，`ApplicationsManager`谈判成功，`ApplicationMaster`得到一个`Container`。
6、`Container`需要向`ApplicationMaster`发送MapReduce Status。如果节点的任务发生错误，`ApplicationsManager`还需要为其进行重启。
7、`NodeManager`要向`ResourceManager`中的`ApplicationsManager`报告节点的状态(Node Status)，这个节点的状态，给我的理解就是，告诉老大，我这还有哪些资源。