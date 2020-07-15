#### spark的集群管理器
应用程序支持在不同的集群管理器下进行任务调度，包括：

1、Standalone(独立部署-spark内置提供的独立的集群环境)

2、YARN（与Hadoop集群集成-通过YARN提供的资源管理调度来运行任务）
    
    配置HADOOP_CONF_DIR环境变量，编辑：$Spark_HOME/conf/spark-env.sh
    export HADOOP_CONF_DIR=/Users/huangqihang/cloudera/cdh5.7/hadoop/etc/hadoop
    
    # 提交spark到yarn
    bin/spark-submit --master yarn ...
    
3、Mesos

#### Spark执行任务启动的相关进程
Master  主节点

Worker  计算节点

SparkSubmit 任务提交进程

CoarseGrainedExecutorBackend Worker上执行计算的后台进程


#### Spark-Shell （本地调试模式）
```
# local模式
$ bin/spark-shell

# 集群模式，启动后可以在webUI上看到一个spark-shell任务正在执行
$ bin/spark-shell --master spark://localhost:7077

# 在spark-shell中执行计算
# 读取hdfs上的文件，统计单词个数
scala> sc.textFile("hdfs://localhost:8020/testdata/words.txt").flatMap(_.split("\\s+")).map((_,1)).reduceByKey(_+_).collect
res0: Array[(String, Int)] = Array((alice,1), (hello,4), (jack,1), (bob,2))  

# 按元组第二个元素排序，false-倒序
# .sortBy(_._2, false)
scala> sc.textFile("hdfs://localhost:8020/testdata/words.txt").flatMap(_.split("\\s+")).map((_,1)).reduceByKey(_+_).sortBy(_._2, false).collect
res1: Array[(String, Int)] = Array((hello,4), (bob,2), (alice,1), (jack,1))
```

#### Spark Submit（生产环境提交任务）

启动本地driver，提交任务到spark集群上执行

```
## --master local 本地运行
bin/spark-submit \
--master local \
--class org.apache.spark.examples.SparkPi \
examples/jars/spark-examples_2.11-2.3.4.jar 10

# --master spark://localhost:7077 提交到指定集群环境运行
bin/spark-submit \
--master spark://localhost:7077 \
--class org.apache.spark.examples.SparkPi \
examples/jars/spark-examples_2.11-2.3.4.jar 10

# 设置应用程序需要的资源
executor-memory : 给每个执行器分配多大的运行内存
total-executor-cores : 本次提交任务的application一共最多可以使用的core数，core相当于计算资源，一个core看作一个工作线程（所有Worker节点？）

# --executor-memory 1024m --total-executor-cores 8 
bin/spark-submit \
--master spark://localhost:7077 \
--class org.apache.spark.examples.SparkPi \
--executor-memory 1024m --total-executor-cores 8 \
examples/jars/spark-examples_2.11-2.3.4.jar 10


提交任务的时候可以指定多个master，防止某个master连接不上：
--master spark://master1:7077,master2:7077


> 测试一个自己的jar包
bin/spark-submit \
--master local \
--class spark_sql.source.HiveOnSpark \
/Users/huangqihang/workspace_bigdata/learn-spark/target/learn-spark-1.0-SNAPSHOT-jar-with-dependencies.jar

```


#### python代码的提交方式
```
# python示例代码位置
examples/src/main/python/pi.py

# 提交python版本的pi计算任务
./bin/spark-submit --master local ./examples/src/main/python/pi.py 10

```
