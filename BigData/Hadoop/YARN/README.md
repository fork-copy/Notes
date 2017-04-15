# YARN
这个新的框架是在hadoop 0.23开始引进的，把`JobTracker`的两个主要的功能：资源管理器和作业生命周期管理器分割成独立的组件。

新的资源管理器（`ResourceManager`）控制着全局的计算资源的分配，*每个应用*的`ApplicationMaster`管理着应用的调度和协同。

一个作业，可以是经典的MapReduce作业，也可以是这些作业的一个有向无环图（DAG)。

资源管理器（`ResourceManager`）和每台机器的`NodeManager`守护进程，他们管理着那台机器的用户进程，从而形成计算框架

实际上，每个应用的`ApplicationMaster`是一个明确的框架，任务是向`ResourceManager`申请资源，且与`NodeManager(s)`携手工作来执行和监视任务。

官方网址：http://hadoop.apache.org/docs/r2.7.3/hadoop-yarn/hadoop-yarn-site/index.html