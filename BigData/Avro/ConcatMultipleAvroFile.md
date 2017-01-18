```
IN=$(hadoop fs -ls /user/yang/test/2017-01-13/*xxnd* | awk '{printf "%s ", $NF}')

hadoop jar avro-tools-1.8.1.jar concat ${IN} /user/yang/test/2017-01-13/xxnd.avro
```