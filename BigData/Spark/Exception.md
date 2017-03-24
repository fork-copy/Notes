
# User class threw exception: java.io.FileNotFoundException: No Avro files found. Hadoop option "avro.mapred.ignore.inputs.without.extension" is set to true. Do all input files have ".avro" extension

```
User class threw exception: java.io.FileNotFoundException: No Avro files found. Hadoop option "avro.mapred.ignore.inputs.without.extension" is set to true. Do all input files have ".avro" extension
```

## 提交到yarn运行时出现异常

```
Application application_1486170743656_0012 failed 2 times due to AM Container for appattempt_1486170743656_0012_000002 exited with exitCode: -1000
For more detailed output, check the application tracking page: http://node2:8088/cluster/app/application_1486170743656_0012 Then click on links to logs of each attempt.
Diagnostics: File does not exist: hdfs://node1:8020/user/yang/.sparkStaging/application_1486170743656_0012/__spark_conf__.zip
java.io.FileNotFoundException: File does not exist: hdfs://node1:8020/user/yang/.sparkStaging/application_1486170743656_0012/__spark_conf__.zip
at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1427)
at org.apache.hadoop.hdfs.DistributedFileSystem$25.doCall(DistributedFileSystem.java:1419)
at org.apache.hadoop.fs.FileSystemLinkResolver.resolve(FileSystemLinkResolver.java:81)
at org.apache.hadoop.hdfs.DistributedFileSystem.getFileStatus(DistributedFileSystem.java:1419)
at org.apache.hadoop.yarn.util.FSDownload.copy(FSDownload.java:253)
at org.apache.hadoop.yarn.util.FSDownload.access$000(FSDownload.java:63)
at org.apache.hadoop.yarn.util.FSDownload$2.run(FSDownload.java:361)
at org.apache.hadoop.yarn.util.FSDownload$2.run(FSDownload.java:359)
at java.security.AccessController.doPrivileged(Native Method)
at javax.security.auth.Subject.doAs(Subject.java:422)
at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1724)
at org.apache.hadoop.yarn.util.FSDownload.call(FSDownload.java:358)
at org.apache.hadoop.yarn.util.FSDownload.call(FSDownload.java:62)
at java.util.concurrent.FutureTask.run(FutureTask.java:266)
at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
at java.util.concurrent.FutureTask.run(FutureTask.java:266)
at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
at java.lang.Thread.run(Thread.java:745)
Failing this attempt. Failing the application.
```