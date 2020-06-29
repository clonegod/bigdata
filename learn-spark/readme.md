
#### spark基本概念
Spark是一个分布式编程模型，用户可以在其中指定转换操作Transformation。

多次转换操作后建立 起指令的有向无环图DAG。

指令图DAG的执行过程Stage作为一个作业Job由一个动作Action操作触发，在执行过程中一个作业Job被分解为多个阶段Stage和任务Task在集群上执行。

转换操作Transformation和动作操作Action操纵的逻辑结构是DataFrame和Dataset。

执行一次转换操作会都会创建一个新的DataFrame或Dataset，而动作操作Action则会触发计算Calculate，或者将DataFrame和Dataset转换成本地语言类型的数据。

#### RDD：弹性分布式数据集
无论是DataFrame还是Dataset，运行的所有Spark代码都将编译 成一个RDD。
RDD是一个只读不可变的且已分块的记录集合，并可以被并行处理。
RDD 与 DataFrame不同，DataFrame中每个记录即是一个结构化的数据行，各字段已知且schema已知，
而 RDD中的记录仅仅是程序员选择的Java、Scala 或 Python 对象。

定义RDD。每个RDD具有以下五个主要内部属性:
• 数据分片(Partition)列表。
• 作用在每个数据分片的计算函数。
• 描述与其他RDD的依赖关系列表。
• (可选)为key-value RDD配置的Partitioner(分片方法，如hash分片)。
• (可选)优先位置列表，根据数据的本地特性，指定了每个Partition分片的处理位 置偏好(例如，对于一个HDFS文件来说，这个列表就是每个文件块所在的节点)。

这些属性决定了S p a r k的所有调度和执行用户程序的能力，不同R D D都各自实现了上 述的每个属性，并允许你定义新的数据源。

RDD 支持两种类型(算子)操作:惰性执行的转换操作和立即执行的动作操作，都是以分布式的方式来处理数据。


#### 数据分区
```
一个完整的数据集分布在不同的节点上存储，每个节点上存储的那部分数据就是一个分区；
一个节点上可以有1个或多个分区；
一个分区内包含多行数据记录；
```

为了让多个执行器并行地工作，Spark将数据分解成多个数据块，每个数据块叫做一个分区。

分区是位于集群中的一台物理机上的多行数据的集合，DataFrame的分区也说明了在执行过程中，数据在集群中的物理分布。

如果只有一个分区，即使拥有数千个执行器，Spark也只有一个执行器在处理数据。

类似地，如果有多个分区，但只有 一个执行器，那么Spark仍然只有一个执行器在处理数据，就是因为只有一个计算资源单位。

DataFrame的分区定义了DataFrame以及Dataset在集群上的物理分布。


#### Spark的结构化API
非结构化的日志文件、半结构化的CSV文件、高度结构化的Parquet文件

结构化API指以下三种核心分布式集合类型的API:
• Dataset类型
• DataFrame类型
• SQL表和视图

##### DataFrame, DataSet, Schema
Spark中的DataFrame和Dataset代表不可变的数据集合，可以通过它指定对特定位置数据的操作，该操作将以惰性评估方式执行。

当对DataFrame执行动作操作时，将触发Spark执行具体转换操作并返回结果，这些代表了如何操纵行和列来计算出用户期望结果的执行计划。

> DataFrame与表和视图从表现上看基本相同，所以我们通常在DataFrame上执行SQL操作，而不是用DataFrame专用的执行代码。

DataFrame的类型由Spark完全负责维护，仅在运行时检查这些类型是否与schema中指定的类型一致。

而Dataset在编译时就会检查类型是否符合规范。
Dataset仅适用于基于Java虚拟 机(JVM)的语言(比如Scala和Java)，并通过case类或Java beans指定类型。

Schema数据模式定义了 该分布式集合中存储的数据类型。
Schema定义了DataFrame的列名和类型，可以手动定义或者从数据源读取模式(通常定义为模式读取)。
模式定义DataFrame的列名以及列的数据类型，它可以由数据源来定义模式(称为读 时模式，schema-on-read)，也可以由我们自己来显式地定义。

#### 一个结构化API查询任务的执行过程

• 编写DataFrame / Dataset / SQL代码。

• 如果代码能有效执行，Spark将其转换为一个逻辑执行计划(Logical Plan)。

• Spark将此逻辑执行计划转化为一个物理执行计划(Physical Plan)，检查可行的 优化策略，并在此过程中检查优化。

• Spark在集群上执行该物理执行计划(RDD操作)。物理规划产生一系列的RDD和转换操作。
这就是Spark被称为编译器的原因。因为它将对DataFrame、Dataset和SQL中的查询为你编译一系列RDD的转换操作。
RDD是spark的底层核心。

---

#### Spark SQL与Hive
Spark SQL与Hive的联系很紧密，因为Spark SQL可以与Hive metastores连接。
Hive metastore维护了Hive跨会话数据表的信息，使用Spark SQL可以连接到Hive metastore 访问表的元数据。
这使得sparkSQL可以在访问数据的时候减少文件列表操作带来的开销（metastore维护了数据库/表在hdfs系统上存储的位置信息）。

需要将hive-site.xml放到sparkSQL执行环境中。对于应用程序而言，则是放到classpath路径下。

#### Spark SQL
Spark SQL 的目的是作为一个在线分析处理(OLAP)数据库，而不是在线事务 处理(OLTP)数据库。这意味着Spark SQL现在还不适合执行对低延迟要求极 高的查询
SparkSession对象上的sql方法来实现，这将返回一个DataFrame。

#### 视图
定义视图即指定基于现有表的一组转换操 作，基本上只是保存查询计划, 这可以方便地组织或重用查询逻辑。



#### spark重要的参数 
spark.conf.set("spark.sql.shuffle.partitions"，50)
