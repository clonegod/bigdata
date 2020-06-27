
#### DataSet 与 DataFrame
DataSet是spark1.6之后新推出的API，也是一个分布式数据集。
与RDD相比，DataSet保存了更多的描述类信息，概念上等同于关系型数据库中的二维表。
由于DataSet中保存了更多与数据相关的描述信息，使得spark可以在运行时基于DataSet提供更多性能上的优化。

DataFrame = DataSet[Row] + Schema（数据的描述信息） ===> Row的集合 + 每个Row的描述 ===> 二维表格

DataSet就是对RDD的进一步扩展，提供更上层的API，

DataSet的特点：
0、强类型的数据集合
1、底层依赖RDD，有一系列的分区
2、每个切片上会有对于的函数（函数表示一系列的操作）
3、kv类型shuffle也会有分区器
4、如果读取hdfs上的数据，会感知最优位置
5、提供优化的执行计划
6、支持多种数据源的统一API操作



