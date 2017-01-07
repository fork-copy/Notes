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
- 找到含有flume的进程，并杀掉（只找到一个进程的情况）
  ```
  kill -s 9 $(ps -ax | grep flume | sed -e 's/^[[:space:]]*//' | cut -d ' ' -f1)
  ```
  先使用`ps`命令找到所进程，然后用`grep`找到含有flume的进程，再用`sed`去掉字符串前面的空格，然后使用`cut`对前面的字符串以空格进行分割，并在分割后的字符串数组中找到第一个字符串，即进程ID。
  最后使用`kill`命令加`-s`参数强制杀掉进程。
