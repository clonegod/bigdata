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
Flink实时计算程序，为了容错，需要开启CheckPointing。一旦 开启CheckPoint，如果没有设置重启策略，默认策略为无限次重启，每次延迟若干时间。

1、flink开启checkpoint功能，同时就开启了重启策略。默认是不重启的。
2、flink的重启策略可以配置成启动固定次数且每次延迟指定时间启动。
3、flink任务出现异常后，会根据配置的重启策略自动重启。将原来的subtask释放，重新生成subtask并调度到TaskManager的slot中执行。
4、flink任务重启后，重新生成subtask被调度到TaskManager中，会从StageBackend中恢复上一次checkpoint的状态。
```

#### Flink 的容错
State状态
    Flink实时程序为了保证在计算过程中，出现异常时可以容错，会把中间的计算结果数据保存起来，这些中间状态数据就叫做State。

StateBackend
    用来保存State状态数据的存储系统就叫做StateBackend。
    State可以是多种类型的，默认是保存在JobManager的内存中，也可以保存到TaskManager本地文件系统，或者HDFS这样的分布式文件系统。
    
    基于内存的HeapStateBackend,仅用于本地开发时调试使用。
    基于HDFS的FsStateBackend。分布式文件持久化，每次读写都产生网络IO，整体性能不佳。
    基于RocksDB的RocksDBStateBackend，本地文件+异步HDFS持久化。
    

#### Flink CheckPoint
    Flink实时计算为了容错，支持将中间数据定期保存起来。这种定期触发的机制叫做CheckPoint。
    CheckPointing是周期性执行的。
    具体过程是JobManager定期向TaskManager中的SubTask发送RPC消息,SubTask将计算的State保存到StateBackend中，
    并且向JobManager响应CheckPoinit是否成功。如果程序出现异常或重启，TaskManager的SubTask可以从上一次成功的CheckPoint的State进行恢复。
    

#### Flink Exactly-Once
什么是Exactly-Once:
    Excatly-Once 指的是：数据只会被成功的计算1次。不允许多算，也不允许少算，要保证准确性。
    
存在的问题：
    KafkaSource相关的消费者没有自己管理消息的offset，消费者会定期的自动提交offset确认消息被正常消费了。
    这就会导致Sink在出现问题的时候，无法将数据的offset进行回滚。
    
Flink怎样保证Exactly-Once:
    CheckPoint机制会把中间结果保存起来，还可以把消息的offset保存起来，相当于flink可以自己管理Kafka的偏移量。
    flink还可以保证结果写入到redis等存储是否成功。
    如果写redis成功了，才会更新Kafka的偏移量，如果没有成功，就不更新Kafka的偏移量。
    以后重新计算，可以把redis的数据覆盖掉。
    flink提供了两种机制来实现Source和Sink在操作上的事务要求：
    一种方式叫做幂等，又一个栅栏barrier，可以保证整个流程都处理成功了，才释放栅栏，更新数据源Kafka的偏移量。
    另一个种方式，基于事物的，二阶段提交，实现exactly-once。事务没提交，Kafka的偏移量也不更新。也就是写入外部系统和更新Kafka偏移量在一个事务中进行。
    
另外一个地方：
    Flink并不是对所有的数据源Source都可以支持Exactly-Once。
    需要 Source支持数据的重放功能，比如Kafka允许数据重复消费。Flink与Kafka的消息可以多次消费机制结合，才能实现了Exactly-Once。
    
```
1、使用支持Exactly-Once的数据源，比如kafka
2、使用FlinkKafkaConsumer，开始CheckPoint，偏移量会通过CheckPoint保存到SavePoint中，并且默认会将偏移量写入到Kafak的特殊topic中，__consumer_offsets
3、FlinkKafkaConsumer的setCommitOffsetsOnCheckpoints()参数默认为true ，即会默认将消息偏移量写入到特殊topic中。目的是支持消息处理进度的监控，以及重启任务没有指定SavePoint时可以接着以前的偏移量继续消费。
4、需要设置EXACTLY_ONCE模式：env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
5、Barrier机制：保证一个流水线上的所有算子都计算成功，再对这条数据进行CheckPoint。记录CheckPoint成功，即表明消息被正确处理了，否则当作未处理，下次会重新处理这个消息。
6、存储系统支持事务：flink会在事务操作成功之后，才更新偏移量。如果事务失败，则不更新偏移量，放弃本次消息的消费。
7、存储系统不支持事务，但支持幂等性：比如redis，hbase,es等系统支持幂等行操作，可以多次重复写入，覆盖历史计算结果。

```
    
#### Flink State 的分类

    1、OperatorState
    
    2、KeyedState
    
#### Flink 如何处理 Kafkap偏移量

CheckPointing disabled:
    当没有开启CheckPointing时，flink对Kafka偏移量对处理可以通过kafka properties进行控制：
    enable.auto.commit
    auto.commit.interval.ms

CheckPointing enabled:
    当开启CheckPointing时，flink对Kafka偏移量是这样处理的：
    当flink保存checkpoint成功之后，flink会通过kafka consumer提交偏移量。这样可以保证Kafka上保存当偏移量位置与checkpoint中保存的偏移量状态处于一致。
    用户可以选择开启/关闭提交偏移量到kafka上（__consumer_offsets topic）, 通过调用 setCommitOffsetOnCheckpoints() 方法来控制。
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true);
    默认是开启的。 在这种情况下，通过kafka properties设置自动提交的周期参数是无效的。
    
    flink既然使用checkpoint记录了历史消息的偏移量，那为什么还在Kafka上又记录一遍偏移量呢？
    1、当运行任务时，如果没有指定SavePoint的位置，则flink可以查询 __consumer__offsets topic，是否能找到历史消息的偏移量，又则使用这个偏移量作为开始位置。
    2、在 __consumer_offsets 保存偏移量，可以监控到 flink对Kafka消息的处理进度。