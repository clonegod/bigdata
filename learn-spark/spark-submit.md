#### Spark启动的进程
Master  主节点

Worker  计算节点

SparkSubmit 任务提交进程

CoarseGrainedExecutorBackend Worker上执行计算的进程


#### Spark Submit

```
bin/spark-submit \
--master spark://localhost:7077 \
--class org.apache.spark.examples.SparkPi \
--executor-memory 1024m --total-executor-cores 8 \
examples/jars/spark-examples_2.11-2.3.4.jar 10


executor-memory : 给每个执行器分配多大的运行内存
total-executor-cores : 整个app一共最多可以使用的core数（所有Worker节点？）

提交任务的时候可以指定多个master，防止某个master连接不上：
--master spark://master1:7077,master2:7077

```

#### Spark-Shell
```
# local模式
$ bin/spark-shell

# 集群模式，启动后可以在webUI上看到一个spark-shell任务正在执行
$ bin/spark-shell --master spark://localhost:7077

# 读取hdfs上的文件，统计单词个数
scala> sc.textFile("hdfs://localhost:8020/testdata/words.txt").flatMap(_.split("\\s+")).map((_,1)).reduceByKey(_+_).collect
res0: Array[(String, Int)] = Array((alice,1), (hello,4), (jack,1), (bob,2))  

# 按元组第二个元素排序，false-倒序
# .sortBy(_._2, false)
scala> sc.textFile("hdfs://localhost:8020/testdata/words.txt").flatMap(_.split("\\s+")).map((_,1)).reduceByKey(_+_).sortBy(_._2, false).collect
res1: Array[(String, Int)] = Array((hello,4), (bob,2), (alice,1), (jack,1))
```

