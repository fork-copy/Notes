# 学习`FsShell`类

类`FsShell`是使用命令`hadoop fs`时执行的类，它的功能就是：运行一个通用文件系统客户端，能够对文件系统进行相关操作。

它的`main`方法如下：

```
/**
 * main() has some simple utility methods
 * @param argv the command and its arguments
 * @throws Exception upon error
 */
public static void main(String argv[]) throws Exception {
  FsShell shell = newShellInstance(); //创建实例
  Configuration conf = new Configuration();
  conf.setQuietMode(false);
  shell.setConf(conf);
  int res;
  try {
    // 解析参数，并执行命令
    res = ToolRunner.run(shell, argv);
  } finally {
    shell.close();
  }
  System.exit(res);
}
```

在主函数中，先是创建一个`FsShell`实例，然后进行参数解析，最后运行命令。其中，类`ToolRunner`的`run`方法的源代码如下：
```
public static int run(Configuration conf, Tool tool, String[] args)
  throws Exception{
  if(conf == null) {
    conf = new Configuration();
  }
  GenericOptionsParser parser = new GenericOptionsParser(conf, args);
  //set the configuration back, so that Tool can configure itself
  tool.setConf(conf);

  //get the args w/o generic hadoop args
  String[] toolArgs = parser.getRemainingArgs();
  return tool.run(toolArgs);
}

public static int run(Tool tool, String[] args)
  throws Exception{
  return run(tool.getConf(), tool, args);
}
```
类`ToolRunner`的`run`方法主要做了两件事情，1）进行配置解析，即解析参数；2）执行命令。虽然最后执行的是`tool.run(toolArgs)`，但`FsShell`类实现了`Tool`接口，因此，这里执行的`run`方法，实际上就是在执行类`FsShell`里的`run`方法，此方法源码如下：
```
@Override
public int run(String argv[]) throws Exception {
  // initialize FsShell
  init();

  int exitCode = -1;
  if (argv.length < 1) {
    printUsage(System.err);
  } else {
    String cmd = argv[0];
    Command instance = null;
    try {
      instance = commandFactory.getInstance(cmd);
      if (instance == null) {
        throw new UnknownCommandException();
      }
      exitCode = instance.run(Arrays.copyOfRange(argv, 1, argv.length));
    } catch (IllegalArgumentException e) {
      displayError(cmd, e.getLocalizedMessage());
      if (instance != null) {
        printInstanceUsage(System.err, instance);
      }
    } catch (Exception e) {
      // instance.run catches IOE, so something is REALLY wrong if here
      LOG.debug("Error", e);
      displayError(cmd, "Fatal internal error");
      e.printStackTrace(System.err);
    }
  }
  return exitCode;
}
```
在这个`run`方法中，也同样是做了两件事：1）通过`CommandFactory`工厂得到一个实例； 2）执行实例的`run`方法，并返回退出代码。

`CommandFactory`工厂负责将命令转换成类，例如，当我们在终端输入的是`hadoop fs -mkdir dir`时，这个工厂会将命令`mkdir`转换成类`Mkdir`。不同的命令会转换成不同的类，其后，会调用文件系统的相应方法进行操作，根据不同的情况，使用的文件系统也可能不一样，这与配置文件相关，会实例化相应的文件系统。具体的操作是在文件系统代码中实现的。

例如，以下是分布式文件系统`public class DistributedFileSystem extends FileSystem `，其实，这个就是HDFS，创建目录的代码如下：
```
@Override
public boolean mkdirs(Path f, FsPermission permission) throws IOException {
  return mkdirsInternal(f, permission, true);
}
```
```
private boolean mkdirsInternal(Path f, final FsPermission permission,
    final boolean createParent) throws IOException {
  statistics.incrementWriteOps(1);
  Path absF = fixRelativePart(f);
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
