
# 使用json2avro转换

## install cmake

## errors
```
-- Could NOT find ZLIB (missing:  ZLIB_LIBRARY ZLIB_INCLUDE_DIR) 
Disabled deflate codec. zlib not found.
-- Could NOT find Snappy (missing:  SNAPPY_LIBRARIES SNAPPY_INCLUDE_DIR) 
Disabled snappy codec. libsnappy not found or zlib not found.
-- Checking for module 'liblzma'
--   No package 'liblzma' found
Disabled lzma codec. liblzma not found.

```


## install zlib

download:http://www.zlib.net/
untar:
tar -xvf ....
./configure
su root
make install

## install snappy-level
yum install snappy-devel.x86_64 

```
[yang@master build]$ cmake .. -DCMAKE_INSTALL_PREFIX=$PREFIX -DCMAKE_BUILD_TYPE=RelWithDebInfo
...
-- Configuring done
-- Generating done
-- Build files have been written to: /home/yang/GitHub/json2avro/avro-c/build

```
```
make 
make test
make install

```


```
-- Install configuration: "RelWithDebInfo"
CMake Error at src/cmake_install.cmake:36 (file):
  file cannot create directory: /include.  Maybe need administrative
  privileges.
Call Stack (most recent call first):
  cmake_install.cmake:37 (include)


make: *** [install] Error 1
```

```
[yang@master json2avro]$ cc -o json2avro json2avro.c avrolib/lib/libavro.a -I avrolib/include -I avro-c/jansson/src -lz -llzma -lsnappy
/usr/bin/ld: cannot find -llzma
collect2: error: ld returned 1 exit status
```
此类错误是由于缺少库导致，可以参考：http://eminzhang.blog.51cto.com/5292425/1285705

```
解决方法：缺少库liblzma导致，所以要安装这个库
```
[root@master lib]# yum install xz-devel.x86_64 
```

# 使用avro-tools转换
但在先下载`avro-tools`jar包，以及提供要转换json文件的schema：
```
[yang@master Data]$ java -jar ../Soft/avro-tools-1.8.1.jar fromjson EnZSP2.json   --schema-file enzsp.avsc > bb.avro
```