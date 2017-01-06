
很多时候，我们需要对编写的程序进行打包，这个时候，我们可以借助一些项目构建工具，如maven, sbt, ant等，这里我使用的是maven。

# 打包成可执行有主类的jar包(jar包中无依赖)

以下是配置打包成可执行，带主类的jar包：
```
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        ...
        <configuration>
          <archive>
            <manifest>
              <addClasspath>true</addClasspath>
              <mainClass>fully.qualified.MainClass</mainClass> <!-- 你的主类名 -->
            </manifest>
          </archive>
        </configuration>
        ...
      </plugin>
    </plugins>
  </build>
```
But, 虽然现在把程序打包成了jar文件，也可以运行，但是，这个jar包是没有包含依赖的，因此，如果这个程序有其他依赖，那么在运行这个程序的时候，需要指定类目录，并且要把所有的依赖都放到类目录下去，手动添加依赖到类目录下，简直就是恶梦。。。

我曾经就这么干过，它少一个依赖，我就手动加一个到类目录，少一个，加一个，少一个，加一个，当我加了大概有10几个之后，而且还看不到尽头。。。简直要疯的节奏，然后想起之前用sbt打包，也是可以把依赖添加到jar包，心情顿时好转，赶紧google，于是找到google到了maven的官网...。顺便说一下，虽然我们google的时候，会搜索到很多结果，但很多时候，我们最好选择合适的结果进行阅读，我的一般顺序是：官方优先，StackExchange。。。

# 打包成带依赖的jar包

## 配置

### 不加main类而带依赖的jar包
编辑pom.xml文件，内容如下：
```
<project>
  [...]
  <build>
    [...]
    <plugins>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
        </configuration>
        <executions>
          <execution>
            <id>make-assembly</id> <!-- this is used for inheritance merges -->
            <phase>package</phase> <!-- bind to the packaging phase -->
            <goals>
              <goal>single</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      [...]
</project>
```

### 加main类也带依赖的jar包

编辑pom.xml文件，内容如下：
```
<project>
    <build>
        <plugins>
            <!--(start) for package jar with dependencies -->
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>Main</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id> <!-- this is used for inheritance merges -->
                        <phase>package</phase> <!-- bind to the packaging phase -->
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!--(end) for package jar with dependencies -->
        </plugins>
    </build>
</project>
```
值得注意的是，不要像以下那样修改pom.xml文件，那样虽然不会报错，但生成的jar包中的`MANIFEST.MF`中没有主类，这样的jar包无法直接运行。

```
<project>
  [...]
  <build>
    [...]
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        ...
        <configuration>
          <archive>
            <manifest>
              <addClasspath>true</addClasspath>
              <mainClass>fully.qualified.MainClass</mainClass> <!-- 你的主类名 -->
            </manifest>
          </archive>
        </configuration>
        ...
      </plugin>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
        </configuration>
        <executions>
          <execution>
            <id>make-assembly</id> <!-- this is used for inheritance merges -->
            <phase>package</phase> <!-- bind to the packaging phase -->
            <goals>
              <goal>single</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      [...]
</project>
```

## 打包 
使用以下命令打包：
```
mvn package
```

就像官方网站说的，" this is pretty simple! "
<hr>
然而，当在运行操作hdfs的jar包时，我出现了以下问题：
```
Exception in thread "main" java.io.IOException: No FileSystem for scheme: hdfs
        at org.apache.hadoop.fs.FileSystem.getFileSystemClass(FileSystem.java:2660)
        at org.apache.hadoop.fs.FileSystem.createFileSystem(FileSystem.java:2667)
        at org.apache.hadoop.fs.FileSystem.access$200(FileSystem.java:94)
        at org.apache.hadoop.fs.FileSystem$Cache.getInternal(FileSystem.java:2703)
        at org.apache.hadoop.fs.FileSystem$Cache.get(FileSystem.java:2685)
        at org.apache.hadoop.fs.FileSystem.get(FileSystem.java:373)
        at sentry.magic.fs.Dfs.<init>(Dfs.java:20)
        at sentry.magic.fs.CheckFlumeIsCompleted.<init>(CheckFlumeIsCompleted.java:26)
        at Main.main(Main.java:26)
```
好在有位仁兄[2]也遇到了这个问题，从而使得问题解决。他的解决方法如下：
```
    FileSystem dfs = null;
    Configuration conf = new Configuration();

    public Dfs(URI uri) throws IOException {

        // 必须有下面这一句
        conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
        // 必须有上面那一句

        dfs = FileSystem.get(uri,conf);
    }
```
除了他之种方法之外，我也自己摸索出了一种方法，经过实践，也是可行的，我的方法如下：
找到打包好的jar包，然后使用归档管理器打开这个jar包，即“open with archive manager",找到`META-INFO/services`目录，再找到这个目录下的`org.apache.hadoop.fs.FileSystem`文件，编辑这个文件，在文件末尾加上一行：
```
org.apache.hadoop.hdfs.DistributedFileSystem
```
但是，这么修改jar的方法，不太合适，因为每生成一次，需要修改一次，所以，建议暂时使用前面那种解决方法。

参考文献：

[1] https://maven.apache.org/plugins/maven-assembly-plugin/usage.html
[2] http://www.cnblogs.com/justinzhang/p/4983673.html