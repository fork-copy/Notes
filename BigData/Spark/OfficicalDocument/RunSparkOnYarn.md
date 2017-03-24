# 在YARN上运行Spark
这个特征是在spark 0.6.0上开始支持的，在随后的版本进行了改进。

# 在YARN上执行Spark
在提交spark程序之前，请确认`HADOOP_CONF_DIR`或`YARN_CONF_DIR`指向了包含Hadoop集群的配置文件的目录。这些配置是用于写到HDFS和连接到YARN资源管理器。这个目录包含的配置将会分发到YARN集群，以至于这人应用用到的所有容器都使用相同的配置。如果这些配置不是由YARN管理，而是引用Java系统属性或环境变量，他们也会被设置到Spark的应用配置里（当以客户端模式运行的时候，driver,executors和AM）

在YARN上发布Spark程序有两种部署模式可以使用。分别为`cluster`和`client`模式。

在`cluster`模式，spark驱动运行在一个由YARN管理的application master进程里,在初始化应用之后，客户端可以撤开了（也就是没有客户端的事情了）。

在`client`模式，spark驱动运行在客户端进程里，application master仅仅用于从YARN请求资源。

不像Spark standalone和Mesos模式，master的地址是`--master`的参数指定，在YARN模式，资源管理器的地址来源于Hadoop的配置，因此，`--master`的参数是`yarn`.

以`cluster`模式提交一个Spark应用：
```
$ ./bin/spark-submit --class path.to.your.Class --master yarn --deploy-mode cluster [options] <app jar> [app options]
```

例如：

```
$ ./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
    --queue thequeue \
    lib/spark-examples*.jar \
    10
```

以上启动了一个YARN客户端程序，它启动默认的Application Master。然后，SparkPi将会作为一个Application master的子线程运行。客户端会周期性地轮询Application master的状态更新并显示到控制台。一旦你的应用完成运行，客户端就会退出。对于如何查看驱动和executor的日志，请参考“Debugging your application“部分。

要以`client`模式提交一个spark应用，一样的做法，但要把`cluster`替换成`client`。以下显示了如何以`client`模式运行`spark-shell`。
```
$ ./bin/spark-shell --master yarn --deploy-mode client
```

# 增加其他的jars

`cluster`模式相比`client`模式，它是运行在不同的机器上，因此，不像`client`一样，本地的jar可以直接使用，在`cluster`模式，` SparkContext.addJar`将无法工作，为了让其能正常工作，需要使用在提交的命令里加上参数`--jars`：
```
$ ./bin/spark-submit --class my.main.Class \
    --master yarn \
    --deploy-mode cluster \
    --jars my-other-jar.jar,my-other-other-jar.jar \
    my-main-jar.jar \
    app_arg1 app_arg2
```

# 调试应用
在YARN中，executors和application master都是运行在容器中。对于在application完成之后处理容器日志，YARN有两种模式。如果log aggregation是打开的（配置项：`yarn.log-aggregation-enable`），则容器日志被拷贝到HDFS上，并且删除本地机器上的日志。这些日志可以从集群上有`yarn logs`命令的任何一台机器上查看：
```
yarn logs -applicationId <app ID>
```
这将打印出给出应用的所有容器的日志文件内容。你也可以使用HDFS shell或API查看容器日志文件目录。这个目录可以通过YARN的配置找到（`yarn.nodemanager.remote-app-log-dir`和`yarn.nodemanager.remote-app-log-dir-suffix`）。
