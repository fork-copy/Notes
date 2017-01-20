# MySQL Insert 优化

在MySQL中，插入一条数据所需要的时间，主要由以下因素决定，后面的数字表示近似的比例：

- 连接（Connecting）: (3)

- 发送查询到服务器（Sending query to server）: (2)

- 解析查询（Parsing query）: (2)

- 拷入行（Inserting row）: (1 × size of row)

- 拷入索引（Inserting indexes）: (1 × number of indexes)

- 关闭连接（Closing）: (1)

可以使用以下方法来优化插入：
- If you are inserting many rows from the same client at the same time, use INSERT statements with multiple VALUES lists to insert several rows at a time. This is considerably faster (many times faster in some cases) than using separate single-row INSERT statements. If you are adding data to a nonempty table, you can tune the bulk_insert_buffer_size variable to make data insertion even faster. See Section 6.1.5, “Server System Variables”.

- When loading a table from a text file, use LOAD DATA INFILE. This is usually 20 times faster than using INSERT statements. See Section 14.2.6, “LOAD DATA INFILE Syntax”.

- Take advantage of the fact that columns have default values. Insert values explicitly only when the value to be inserted differs from the default. This reduces the parsing that MySQL must do and improves the insert speed.

- See Section 9.5.5, “Bulk Data Loading for InnoDB Tables” for tips specific to InnoDB tables.

- See Section 9.6.2, “Bulk Data Loading for MyISAM Tables” for tips specific to MyISAM tables.

本文内容来自：[MySQL官方文档](http://dev.mysql.com/doc/refman/5.7/en/insert-speed.html)