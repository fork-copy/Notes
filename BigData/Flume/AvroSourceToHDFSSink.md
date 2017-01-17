# 使用flume将avro文件上传到hdfs上

场景介绍：把一个文件夹下的avro文件上传到hdfs上。source使用的是`spooldir`，sink使用的是`hdfs`。

# 配置 flume.conf
```
# memory channel called ch1 on agent1
agent1.channels.ch1.type = memory

# source
agent1.sources.spooldir-source1.channels = ch1
agent1.sources.spooldir-source1.type = spooldir
agent1.sources.spooldir-source1.spoolDir=/home/yang/SVN/V2.0/Sentry/etl/data/avro/
agent1.sources.spooldir-source1.basenameHeader = true
agent1.sources.spooldir-source1.deserializer = AVRO
agent1.sources.spooldir-source1.deserializer.schemaType = LITERAL

# sink
agent1.sinks.hdfs-sink1.channel = ch1
agent1.sinks.hdfs-sink1.type = hdfs

agent1.sinks.hdfs-sink1.hdfs.path = hdfs://node1:8020/user/yang/test
agent1.sinks.hdfs-sink1.hdfs.fileType = DataStream
agent1.sinks.hdfs-sink1.hdfs.fileSuffix = .avro
agent1.sinks.hdfs-sink1.serializer =  org.apache.flume.serialization.AvroEventSerializer$Builder
agent1.sinks.hdfs-sink1.serializer.compressionCodec = snappy

agent1.sinks.hdfs-sink1.hdfs.filePrefix = %{basename}
agent1.sinks.hdfs-sink1.hdfs.rollSize = 0
agent1.sinks.hdfs-sink1.hdfs.rollCount = 0

# Finally, now that we've defined all of our components, tell
# agent1 which ones we want to activate.
agent1.channels = ch1
agent1.sources = spooldir-source1
agent1.sinks = hdfs-sink1
```

*注意*：上面的配置有几个需要特别注意一下。

- 源（source)部分：
```
agent1.sources.spooldir-source1.deserializer = AVRO
agent1.sources.spooldir-source1.deserializer.schemaType = LITERAL
```
deserializer默认的是`LINE`,如果不设置avro，会报异常，因为我们这里的文件是avro文件。

而deserializer.schemaType默认的是HASH，如果不设为LITERAL，会报以下异常：`process failed org.apache.flume.FlumeException: Could not find schema for event`

- sink部分：
```
agent1.sinks.hdfs-sink1.hdfs.fileType = DataStream
agent1.sinks.hdfs-sink1.hdfs.fileSuffix = .avro
agent1.sinks.hdfs-sink1.serializer =  org.apache.flume.serialization.AvroEventSerializer$Builder
agent1.sinks.hdfs-sink1.serializer.compressionCodec = snappy
```
`hdfs.fileType`默认的是`SequenceFile`，如果使用这个文件类型，数据传输到hdfs之后，会出现无法正常解析avro文件的情况，如异常`Not a avro data file`；`hdfs.fileSuffix`是指示在文件后面加上后缀名，注意，文件后缀名的那个点（.）不能省略掉，为什么要加后缀名呢？因为在许多情况下，比如使用spark进行读取avro文件的时候，它会先判断文件后缀名，如果不是`.avro`结尾的文件，它会认为这不是一个avro文件，然后会抛出异常。

特别要注意的是，`serializer`的前面没有`hdfs`，而且这个序列化类不是flume自带的，需要自己去下载源代码并打包，这个源码在[github](https://github.com/cloudera/cdk)项目上，`AvroEventSerializer$Builder`在项目的[位置](https://github.com/cloudera/cdk/blob/master/cdk-flume-avro-event-serializer/src/main/java/org/apache/flume/serialization/AvroEventSerializer.java)。我们可以使用git clone项目下来，然后切换到目录`cdk-flume-avro-event-serializer`下，然后`mvn package`，再把生成的jar包（在target目录下）拷贝到flume的`lib`目录下。

参考文献：

[1] http://flume.apache.org/FlumeUserGuide.html

[2] http://stackoverflow.com/questions/21617025/flume-directory-to-avro-avro-to-hdfs-not-valid-avro-after-transfer?rq=1