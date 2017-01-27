# 这里是大数据相关的学习笔记，包括以下内容：
- Ambari
- Flume
- Hadoop
- HBase
- Hive
- Spark

将打包好的jar包，上传到HDFS，以YARN方式运行
```
# Run on a YARN cluster
export HADOOP_CONF_DIR=XXX
./bin/spark-submit \
  --class org.apache.spark.examples.SparkPi \
  --master yarn \
  --deploy-mode cluster \  # can be client for client mode
  --executor-memory 20G \
  --num-executors 50 \
  /path/to/examples.jar \
  1000
```

```
./bin/spark-submit --class com.lancoo.ecbdc.Main --master yarn --deploy-mode cluster --executor-memory 10G --num-executors 20 ./EduCloud-Backend-assembly-2.0.jar 1000
```
