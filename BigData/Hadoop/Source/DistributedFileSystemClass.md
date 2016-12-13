# 学习类`DistributedFileSystem`

依然以命令`hadoop fs -mkdir dir`为例，整个命令的执行过程为：
1. 首先执行`hadoop`脚本，更详细的过程可参考`RunExample.md`和`HadoopDistroCMD.md`文件。
2. 然后执行`FsShell`JAVA类，而这个类在执行相应的命令时，是将命令`mkdir`转换成`Mkdir`类，此过程可参考`FsShell.md`文件。
3. 再执行此`Mkdir`类的`processNonexistentPath`方法。

`processNonexistentPath`方法源码如下：
```
  protected void processNonexistentPath(PathData item) throws IOException {
    // check if parent exists. this is complicated because getParent(a/b/c/) returns a/b/c, but
    // we want a/b
    if (!item.fs.exists(new Path(item.path.toString()).getParent()) && !createParents) {
      throw new PathNotFoundException(item.toString());
    }
    if (!item.fs.mkdirs(item.path)) {
      throw new PathIOException(item.toString());
    }
  }
```
这个方法的功能是：
判断这个目录的父目录是否存在，如果不存在且不创建父目录，则抛出`PathNotFoundException`异常，即没有找到路径。
否则，通过方法`item.fs.mkdirs()`创建目录。

`PathData`类的成员`fs`是抽象类`FileSystem`的实现类，其中，`DistributedFileSystem`类就是抽象类`FileSystem`的一个实现类，如下：
```public class DistributedFileSystem extends FileSystem ```

但在上述`mkdirs()`方法调用的时候，具体使用哪个文件系统，取决于配置文件里的配置。

调用`mkdirs()`方法之后，最后会调用类`DistributedFileSystem`的`mkdirsInternal`方法，如下：
```
  private boolean mkdirsInternal(Path f, final FsPermission permission,
      final boolean createParent) throws IOException {
    statistics.incrementWriteOps(1); // 更新统计数据，这个统计数据用于跟踪到目前为止在这个文件系统中有多少次读和写操作。这里统计的是写操作。
    Path absF = fixRelativePart(f); // 将相对路径转换成绝对路径。
    // 创建一个`FileSystemLinkResolver`类的对象，并重写此类的`doCall`和`next`方法。
    // 然后调用对象的`resolve`方法。
    return new FileSystemLinkResolver<Boolean>() {
      @Override
      public Boolean doCall(final Path p)
          throws IOException, UnresolvedLinkException {
        return dfs.mkdirs(getPathName(p), permission, createParent);
      }

      @Override
      public Boolean next(final FileSystem fs, final Path p)
          throws IOException {
        // FileSystem doesn't have a non-recursive mkdir() method
        // Best we can do is error out
        if (!createParent) {
          throw new IOException("FileSystem does not support non-recursive"
              + "mkdir");
        }
        return fs.mkdirs(p, permission);
      }
    }.resolve(this, absF);
  }
```
`resolve`方法的功能是：
1） 尝试使用指定的文件系统和路径，去执行`doCall`方法。
2） 如果`doCall`方法调用失败，则尝试重新解析路径，然后执行`next`方法。

`doCall`方法
--
在`doCall`方法中，调用的是`dfs.mkdirs(getPathName(p), permission, createParent)`方法，`dfs`是一个`DFSClient`类的对象。
`DFSClient`可以连接到一个Hadoop文件系统，并执行一些基本的文件任务。它使用`ClientProtocol`协议与`NameNode`守护进程进行通信，能够直接连接到`DataNodes`读/写数据块。

Hadoop DFS用户应该获取一个`DistributedFileSystem`类的实例，这个实例使用`DFSClient`来处理文件系统任务。

在这里，调用了`DFSClient`类的`mkdirs`方法，此方法的源代码如下：
```
  public boolean mkdirs(String src, FsPermission permission,
      boolean createParent) throws IOException {
    if (permission == null) {
      permission = FsPermission.getDefault();
    }
    FsPermission masked = permission.applyUMask(dfsClientConf.uMask);
    return primitiveMkdir(src, masked, createParent);
  }
```
此方法实现的功能就是：使用给出的名字和权限，创建目录。

`next`方法 
--

