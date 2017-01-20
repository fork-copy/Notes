# http://dev.mysql.com/doc/refman/5.7/en/insert-speed.html

在MySQL中，插入一条数据所需要的时间，主要由以下因素决定，后面的数字表示近似的比例：

- 连接（Connecting）: (3)

- 发送查询到服务器（Sending query to server）: (2)

- 解析查询（Parsing query）: (2)

- 拷入行（Inserting row）: (1 × size of row)

- 拷入索引（Inserting indexes）: (1 × number of indexes)

- 关闭连接（Closing）: (1)

