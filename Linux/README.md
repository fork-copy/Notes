# Linux 学习笔记

- 找到含有flume的进程
  ```
  ps -ax | grep flume
  ```
  找到不含有flume的进程
  ```
  ps -ax | grep -v flume
  ```
  统计不含有flume的进程有多少个
  ```
  ps -ax | grep flume | wc -l
  ```
- 去除字符串前面的空格
  ```
  echo " abc" | sed -e 's/^[[:space:]]*//'
  ```
  