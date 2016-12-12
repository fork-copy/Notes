# 学习命令`hadoop.distro`
这一节，主要学习`/usr/hdp/2.5.0.0-1245//hadoop/bin/hadoop.distro`命令

在上一节中，我们运行例子的时候，使用到了`hadoop fs`和`hadoop jar`等命令。不管使用的是哪个命令，最后都是在执行`hadoop.distro`命令，并且把之前的参数带过来。

我们先来看下命令`hadoop.distro`的源代码：
```
[yang@node3 bin]$ cat hadoop.distro
#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script runs the hadoop core commands.

bin=`which $0`
bin=`dirname ${bin}`
bin=`cd "$bin"; pwd`

DEFAULT_LIBEXEC_DIR="$bin"/../libexec

if [ -n "$HADOOP_HOME" ]; then
  DEFAULT_LIBEXEC_DIR="$HADOOP_HOME"/libexec
fi

HADOOP_LIBEXEC_DIR=${HADOOP_LIBEXEC_DIR:-$DEFAULT_LIBEXEC_DIR}
. $HADOOP_LIBEXEC_DIR/hadoop-config.sh

function print_usage(){
  echo "Usage: hadoop [--config confdir] [COMMAND | CLASSNAME]"
  echo "  CLASSNAME            run the class named CLASSNAME"
  echo " or"
  echo "  where COMMAND is one of:"
  echo "  fs                   run a generic filesystem user client"
  echo "  version              print the version"
  echo "  jar <jar>            run a jar file"
  echo "                       note: please use \"yarn jar\" to launch"
  echo "                             YARN applications, not this command."
  echo "  checknative [-a|-h]  check native hadoop and compression libraries availability"
  echo "  distcp <srcurl> <desturl> copy file or directories recursively"
  echo "  envvars              display computed Hadoop environment variables"
  echo "  archive -archiveName NAME -p <parent path> <src>* <dest> create a hadoop archive"
  echo "  classpath            prints the class path needed to get the"
  echo "  credential           interact with credential providers"
  echo "                       Hadoop jar and the required libraries"
  echo "  daemonlog            get/set the log level for each daemon"
  echo "  trace                view and modify Hadoop tracing settings"
  echo ""
  echo "Most commands print help when invoked w/o parameters."
}

if [ $# = 0 ]; then
  print_usage
  exit
fi

COMMAND=$1
case $COMMAND in
  # usage flags
  --help|-help|-h)
    print_usage
    exit
    ;;

  #hdfs commands
  namenode|secondarynamenode|datanode|dfs|dfsadmin|fsck|balancer|fetchdt|oiv|dfsgroups|portmap|nfs3)
    echo "DEPRECATED: Use of this script to execute hdfs command is deprecated." 1>&2
    echo "Instead use the hdfs command for it." 1>&2
    echo "" 1>&2
    #try to locate hdfs and if present, delegate to it.  
    shift
    if [ -f "${HADOOP_HDFS_HOME}"/bin/hdfs ]; then
      exec "${HADOOP_HDFS_HOME}"/bin/hdfs ${COMMAND/dfsgroups/groups}  "$@"
    elif [ -f "${HADOOP_PREFIX}"/bin/hdfs ]; then
      exec "${HADOOP_PREFIX}"/bin/hdfs ${COMMAND/dfsgroups/groups} "$@"
    else
      echo "HADOOP_HDFS_HOME not found!"
      exit 1
    fi
    ;;

  #mapred commands for backwards compatibility
  pipes|job|queue|mrgroups|mradmin|jobtracker|tasktracker)
    echo "DEPRECATED: Use of this script to execute mapred command is deprecated." 1>&2
    echo "Instead use the mapred command for it." 1>&2
    echo "" 1>&2
    #try to locate mapred and if present, delegate to it.
    shift
    if [ -f "${HADOOP_MAPRED_HOME}"/bin/mapred ]; then
      exec "${HADOOP_MAPRED_HOME}"/bin/mapred ${COMMAND/mrgroups/groups} "$@"
    elif [ -f "${HADOOP_PREFIX}"/bin/mapred ]; then
      exec "${HADOOP_PREFIX}"/bin/mapred ${COMMAND/mrgroups/groups} "$@"
    else
      echo "HADOOP_MAPRED_HOME not found!"
      exit 1
    fi
    ;;

  #core commands  
  *)
    # the core commands
    if [ "$COMMAND" = "fs" ] ; then
      CLASS=org.apache.hadoop.fs.FsShell
    elif [ "$COMMAND" = "version" ] ; then
      CLASS=org.apache.hadoop.util.VersionInfo
    elif [ "$COMMAND" = "jar" ] ; then
      CLASS=org.apache.hadoop.util.RunJar
      if [[ -n "${YARN_OPTS}" ]] || [[ -n "${YARN_CLIENT_OPTS}" ]]; then
        echo "WARNING: Use \"yarn jar\" to launch YARN applications." 1>&2
      fi
    elif [ "$COMMAND" = "key" ] ; then
      CLASS=org.apache.hadoop.crypto.key.KeyShell
    elif [ "$COMMAND" = "checknative" ] ; then
      CLASS=org.apache.hadoop.util.NativeLibraryChecker
    elif [ "$COMMAND" = "distcp" ] ; then
      CLASS=org.apache.hadoop.tools.DistCp
      CLASSPATH=${CLASSPATH}:${TOOL_PATH}
    elif [ "$COMMAND" = "daemonlog" ] ; then
      CLASS=org.apache.hadoop.log.LogLevel
    elif [ "$COMMAND" = "envvars" ] ; then
      echo "JAVA_HOME='${JAVA_HOME}'"
      echo "HADOOP_COMMON_HOME='${HADOOP_COMMON_HOME}'"
      echo "HADOOP_COMMON_DIR='${HADOOP_COMMON_DIR}'"
      echo "HADOOP_COMMON_LIB_JARS_DIR='${HADOOP_COMMON_LIB_JARS_DIR}'"
      echo "HADOOP_COMMON_LIB_NATIVE_DIR='${HADOOP_COMMON_LIB_NATIVE_DIR}'"
      echo "HADOOP_CONF_DIR='${HADOOP_CONF_DIR}'"
      echo "HADOOP_TOOLS_PATH='${TOOL_PATH}'"
      exit 0
    elif [ "$COMMAND" = "archive" ] ; then
      CLASS=org.apache.hadoop.tools.HadoopArchives
      CLASSPATH=${CLASSPATH}:${TOOL_PATH}
    elif [ "$COMMAND" = "credential" ] ; then
      CLASS=org.apache.hadoop.security.alias.CredentialShell
    elif [ "$COMMAND" = "trace" ] ; then
      CLASS=org.apache.hadoop.tracing.TraceAdmin
    elif [ "$COMMAND" = "classpath" ] ; then
      if [ "$#" -gt 1 ]; then
        CLASS=org.apache.hadoop.util.Classpath
      else
        # No need to bother starting up a JVM for this simple case.
        if [ "$cygwin" = true ]; then
          CLASSPATH=$(cygpath -p -w "$CLASSPATH" 2>/dev/null)
        fi
        echo $CLASSPATH
        exit
      fi
    elif [[ "$COMMAND" = -*  ]] ; then
        # class and package names cannot begin with a -
        echo "Error: No command named \`$COMMAND' was found. Perhaps you meant \`hadoop ${COMMAND#-}'"
        exit 1
    else
      CLASS=$COMMAND
    fi

    # cygwin path translation
    if [ "$cygwin" = true ]; then
      CLASSPATH=$(cygpath -p -w "$CLASSPATH" 2>/dev/null)
      HADOOP_LOG_DIR=$(cygpath -w "$HADOOP_LOG_DIR" 2>/dev/null)
      HADOOP_PREFIX=$(cygpath -w "$HADOOP_PREFIX" 2>/dev/null)
      HADOOP_CONF_DIR=$(cygpath -w "$HADOOP_CONF_DIR" 2>/dev/null)
      HADOOP_COMMON_HOME=$(cygpath -w "$HADOOP_COMMON_HOME" 2>/dev/null)
      HADOOP_HDFS_HOME=$(cygpath -w "$HADOOP_HDFS_HOME" 2>/dev/null)
      HADOOP_YARN_HOME=$(cygpath -w "$HADOOP_YARN_HOME" 2>/dev/null)
      HADOOP_MAPRED_HOME=$(cygpath -w "$HADOOP_MAPRED_HOME" 2>/dev/null)
    fi

    shift

    # Always respect HADOOP_OPTS and HADOOP_CLIENT_OPTS
    HADOOP_OPTS="$HADOOP_OPTS $HADOOP_CLIENT_OPTS"

    #make sure security appender is turned off
    HADOOP_OPTS="$HADOOP_OPTS -Dhadoop.security.logger=${HADOOP_SECURITY_LOGGER:-INFO,NullAppender}"

    export CLASSPATH=$CLASSPATH
    exec "$JAVA" $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"
    ;;

esac
```

在此脚本中，先是判断输入参数是否为0，如果是0，则打印命令使用帮助信息，退出程序。

如果参数个数不为0，则取到第一个输入参数。然后进行匹配,然后执行相应的命令，以命令`hadoop fs -mkdir dir`为例，这条命令是在HDFS上创建一个名为`dir`的目录。此时取到的第一个参数是`fs`,做了一些赋值之后，执行命令`exec "$JAVA" $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"`,如下：
```
if [ "$COMMAND" = "fs" ] ; then
  CLASS=org.apache.hadoop.fs.FsShell
...
exec "$JAVA" $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"

```

简单来说，就是使用JAVA命令来执行`org.apache.hadoop.fs.FsShell`这个类，然后带上所有的参数列表。

因此，当我们在终端下执行脚本命令`hadoop`时，实际上是在执行JAVA程序，而执行的JAVA类就是`FsShell`这个类。在下一节，我们来看看这个JAVA类的内部实现。
