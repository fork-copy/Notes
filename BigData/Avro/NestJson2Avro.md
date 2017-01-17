# 嵌套的JSON数据与AVRO文件的相互转换

JSON是一种常用的数据交换格式，很多系统都会使用JSON作为数据接口返回的数据格式，然而，由于JSON数据中包含大量的字段名字，导致空间的严重浪费，尤其是数据文件较大的时候，而AVRO是一种更加紧凑的数据序列化系统，占用空间相对较少，更利于数据在网络当中的传输，本文介绍如何使用`avro-tools`工具对这两种文件格式进行转换。

# 准备转换工具
使用的是`avro-tools-1.8.1.jar`，可到[官方网站](https://avro.apache.org/docs/current/gettingstartedjava.html)下载。

# 准备嵌套的JSON数据
```
[
  {"product_seris":"S_01","product_name":"iphone7","prices":[{"model":"iphone7","price":5200},{"model":"iphone7 plus","price":5800}]},
  {"product_seris":"S_02","product_name":"iphone6","prices":[{"model":"iphone6","price":4600},{"model":"iphone6 plus","price":5200}]}
]
```
模拟数据，请忽略价格信息。以上内容保存为`products.json`.


# 准备schema

在对JSON数据转换成AVRO数据时，需要提供AVRO数据的schema，这个schema有些复杂，容易出错，特别是当JSON记录中有数组的情况。

```
{"type": "array",
 "items":{
   "type":"record",
   "name":"products",
     "fields": [
         {"name": "product_seris", "type": "string"},
         {"name": "product_name", "type": "string"},
         {"name": "prices", "type":
            {"type": "array",
                "items":{
                    "type":"record",
                    "name" : "price",
                    "fields":[
                      {"name":"model","type":"string"},
                      {"name":"price","type":"float"}
                    ]}
              }
         }
     ]
 }
}
```

以上内容保存为`products.avsc`.

几点解释：

- 类型为`array`，因为从前面的JSON数据上可以看到，整个JSON数据是一个数组，因此，这里的类型为`array`，当类型为`array`时，*必须*指定`items`。

- 由于数组内部是记录形式，因此，在`items`里面的`type`是`record`，这里必须指定`name`和`fields`。

- `fields`里的`name`必须与JSON数据里的字段名保持一致。

- 值得注意的是，从前面的JSON数据可以看到，`prices`信息为数组字段，因此，必须先指定`name`为`prices`，而且`type`是一个对象，并不能单纯地指定为`array`，而是需要在对象里再用一个`type`来指定`array`，然后再加上`items`。

# 嵌套的JSON数据转换成AVRO数据

```
java -jar avro-tools-1.8.1.jar fromjson products.json --schema-file products.avsc > products.avro
```

- `fromjson`表示将JSON转换成AVRO。

- `--schema-file` 后面跟上AVRO 的schema文件。

- `>`表示重定向输出到文件`products.avro`，因为默认的是输出到控制台。


# AVRO数据转换成相应的JSON数据
```
java -jar avro-tools-1.8.1.jar tojson products.avro
```

- `tojson` 表示将AVRO数据转换成JSON数据。结果默认输出到控制台，也可使用重定向输出到文件。

转换结果如下：
```
[yang@master etl]$ java -jar avro-tools-1.8.1.jar tojson products.avro
log4j:WARN No appenders could be found for logger (org.apache.hadoop.metrics2.lib.MutableMetricsFactory).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
[{"product_seris":"S_01","product_name":"iphone7","prices":[{"model":"iphone7","price":5200.0},{"model":"iphone7 plus","price":5800.0}]},{"product_seris":"S_02","product_name":"iphone6","prices":[{"model":"iphone6","price":4600.0},{"model":"iphone6 plus","price":5200.0}]}]

```

参考文献：
[1] https://avro.apache.org/docs/current/spec.html

[2] https://avro.apache.org/docs/current/gettingstartedjava.html

[3] http://grokbase.com/t/avro/user/129hab256y/converting-arbitrary-json-to-avro

[4] http://stackoverflow.com/questions/22443051/avro-tools-json-to-avro-schema-fails-org-apache-avro-schemaparseexception-unde