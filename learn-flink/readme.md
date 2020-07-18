#### Flink简介
特点：
```
1、完全面向流式处理，对批处理对支持通过窗口数据来实现；
2、高吞吐、低延迟
3、保证Exactly-Once
4、丰富对编程API，支持Java，Scala两种语言
```

#### Flink编程模型
抽象数据集
```
1、DataStream - 实时API
2、DataSet - 离线API
```

#### Flink重要概念
```
1、JobManager Flink中对管理进程
2、TaskManager Flink中负责管理计算资源和执行subTask的工作进程
3、Clinet Flink用来提交任务的客户端，可以用命令行提交，也可以通过webUI提交
4、Task Task是一个阶段多个功能相同的subTask的集合，类似spark中的TaskSet
5、SubTask subTask是Flink中任务执行的最小单元，是一个Java类的实例，完成具体的计算逻辑
6、Operator Chain 不需要进行shuffle操作的多个算子合并在一个subTask中就构成了Operator Chain,类似Spark中的Pipeline
7、Slot Flink中对计算资源进行隔离对最小单元。一个Slot中可以运行多个subTask，但是要求这些subTask必需来自同一个Job的不同Task(阶段)的subTask
8、State Flink任务在运行过程中计算得到的中间结果
9、CheckPoint Flink用来将中间计算结果持久化到指定存储系统的一种定期执行机制
10、StateBackend Flink用来存储中间计算结果的存储系统。Flink支持3中类型的StateBackend: Memory, FsBackend, RocksDB
```

#### Flink Task(阶段）的划分
```
1、调用keyBy等会触发redistribution/shuffle的算子 
2、Task的并行度parallelism发生变化
3、任务调用了startNewChain方法(任务开启前会划分出一个新的stage)
4、任务调用了disableChaning方法（当前任务与前后任务会隔离，不会chain到一起）
```

#### Flink Task共享资源槽
```
1、Flink任务资源槽的默认名称为default
2、通过slotSharingGroup可以设置资源槽的名称
3、改变共享资源槽的名称之后，如果后面的任务没有再重新设置资源槽的名称，则保持上一次所修改的名称一致
4、槽位名称不同的subTask不能在同一个槽位中执行
```


#### Flink 的时间
```
1、EventTime 数据产生时的时间
2、IngestionTime 数据从消息中间件读取，进入到Flink的时间
3、ProcessingTime 数据被Flink Operator处理的时间
```

#### Flink 的窗口
```
1、CountWindow 按照数据的条数触发窗口的执行 
2、TimeWindow 按时间划分窗口。

滚动窗口Tumbling window
    按固定的窗口长度对数据进行划分。
    特点：时间对齐，窗口长度固定，数据无重叠。窗口与窗口之间在时间上是连续的。
    应用场景：按固定时间段对数据进行计算。
    比如，指定窗口大小为5分钟，则每5分钟的数据划分到一个窗口，对该窗口的数据进行独立计算。

滑动窗口Sliding Window
    滑动窗口是固定窗口的更广义的一种形式。滑动窗口由固定的窗口长度和滑动间隔组成。
    特点：时间对齐，窗口长度固定，数据存在部分重叠。
    应用场景：计算一段时间内的数据变化趋势。比如窗口大小为30分钟（统计最近30分钟的数据），每隔1分钟滑动1次（每隔1分钟重新计算1次数据）。
    窗口大小参数、滑动频率参数

会话窗口Session Window
    类似web应用中的session会话，也就是一段时间内没有接收到新数据，就会产生一个新的window
    特点：窗口之间的时间无对齐，数据无重叠。    
    session窗口分配器通过session活动对元素进行划分。
    一个session窗口通过一个session间隔来配置。这个session周期定义了非活跃周期的长度，
    当这个非活跃周期产生，那么当前的session窗口就会关闭。后续产生的数据将被分配到下一个session窗口中。
```

#### Flink 重启策略
```
1、flink开启checkpoint功能，同时就开启了重启策略。默认是不重启的。
2、flink的重启策略可以配置成启动固定次数且每次延迟指定时间启动
3、flink任务出现异常后，会根据配置的重启策略自动重启。将原来的subtask释放，重新生成subtask并调度到TaskManager的slot中执行。
4、flink任务重启后，重新生成subtask被调度到TaskManager中，会从StageBackend中恢复上一次checkpoint的状态。
```


#### Flink CheckPoint


#### Flink Exactly-Once

