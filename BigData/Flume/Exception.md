# 使用过程中遇到的一些异常

## org.apache.avro.generic.GenericData$Array cannot be cast to org.apache.avro.generic.GenericRecord
```
Uncaught exception in SpoolDirectorySource thread. Restart or reconfigure Flume to continue processing.
java.lang.ClassCastException: org.apache.avro.generic.GenericData$Array cannot be cast to org.apache.avro.generic.GenericRecord
```

解决方法：

将数组（针对AVRO数据的schema的`type=array`)形式的AVRO数据转换成记录（`type=record`)形式。