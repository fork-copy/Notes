# MapReduce 教程（官方 Tutorial)
- MapReduce Tutorial
  - Purpose
  - Prerequisites
  - Overview
  - Inputs and Outputs
  - Example: WordCount v1.0
    - Source Code
    - Usage
    - Walk-through
  - MapReduce - User Interfaces
    - Payload
      - Mapper
      - Reducer
      - Partitioner
      - Counter
    - Job Configuration
    - Task Execution & Environment
      - Memory Management
      - Map Parameters
      - Shuffle/Reduce Parameters
      - Configured Parameters
      - Task Logs
      - Distributing Libraries
    - Job Submission and Monitoring
      - Job Control
    - Job Input
      - InputSplit
      - RecordReader
    - Job Output
      - OutputCommitter
      - Task Side-Effect Files
      - RecordWriter
    - Other Useful Features
      - Submitting Jobs to Queues
      - Counters
      - DistributedCache
      - Profiling
      - Debugging
      - Data Compression
      - Skipping Bad Records
    - Example: WordCount v2.0
      - Source Code
      - Sample Runs
      - Highlights

## 目的
这个文档全面描述了Hadoop MapReduce框架面向用户的层面，且这个文档可以作为一个教程。

## 先决条件
确保Hadoop已经安装，配置且正在运行。详情：
- 对于新用户，[单节点设置](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-common/SingleCluster.html)

- 对于大型，分布式集群，[集群设置](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-common/ClusterSetup.html)

## 概述
Hadoop MapReduce是一个软件框架，它使得编写在普通硬件的大集群（上千节点）上，以可靠、容错的方式，并行地处理海量数据的应用更容易。

一个MapReduce作业通常把输入数据集切成独立的“数据块”，这些“数据块”被map任务以一个完全并行的方式被处理。框架会对map的输出进行排序，然后这些输出会成为reduce任务的输入。通常，作业的输入和输出都是存储在文件系统上。框架负责任务调度，任务管理和重新执行失败的任务。

通常，计算节点和存储节点是相同的，也就是说，MapReduce框架和Hadoop分布式文件系统（请查阅HDFS框架指南）是运行在相同的节点集合之上。这种配置使得框架可以高效地对有数据的节点上的任务进行调度，导致整个集群具有非常高的聚合宽带。

// TODO

本文翻译自：http://hadoop.apache.org/docs/r2.7.3/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html