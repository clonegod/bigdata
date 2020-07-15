package clonegod.learn.flink

import java.util

import org.apache.flink.shaded.guava18.com.google.common.collect.Sets
import org.apache.flink.streaming.api.scala._

object TransformationSample {
  def main(args: Array[String]): Unit = {
    testMax()
  }

  def testMap(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements(1,2,3,4,5)

    // transform: map - 将操作映射到集合中的每个元素上
    val dataStream2 =dataStream.map(_ * 2)

    // sink: print
    dataStream2.print()
      .setParallelism(1)

    env.execute("transformation-map")
  }


  def testFlatMap(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("Hadoop", "Hive", "HDFS")

    // transform: flatmap - 先 map, 然后再将 map 出来的这些列表首尾相接 (flatten).
    val dataStream2 = dataStream.flatMap(_.toList)
    val dataStream3 =  dataStream2.map((_,1)).keyBy(0).sum(1);

    // sink: print
    dataStream3.print()

    env.execute("transformation-flatmap")
  }


  def testFilter(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromCollection(1.to(10))

    // transform: filter
    val result = dataStream.filter(_ % 2 == 0)

    // sink: print
    result.print()

    env.execute("transformation-filter")
  }

  def testKeyBy(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("flink", "scala", "spark", "flink")

    // transform: keyBy
    // keyBy：根据所设置为key的字段进行分组，相同key的数据会分配同一个TaskManager的TaskSlot中
    // keyBy 算子：类似 hash partition，hashcode % taksManager_subtask_count => 相同key将被shuffle到哪个subtask中
    val keyedStream = dataStream.map((_,1)).keyBy(0)

    // aggregation - 组内聚合
    val result = keyedStream.sum(1)

    // sink: print
    result.print()

    env.execute("transformation-keyBy")
  }

  def testKeyBy2(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("flink", "scala", "spark", "flink")

    // transform: keyBy
    // 自定义类进行数据封装，再进行keyBy
    val keyedStream = dataStream.map(new WordCount(_,1)).keyBy(_.word)

    // aggregation - 组内聚合 - sum
    val result = keyedStream.sum("count")

    // sink: print
    result.print()

    env.execute("transformation-keyBy2")
  }


  def testKeyBy3(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("四川,成都,2000.00", "四川,眉山,1000.00", "四川,成都,2000.00", "四川,眉山,1000.00")

    // transform: keyBy
    // 按多个字段进行分组
    val keyedStream = dataStream.map(line => {
      val arr = line.split(",")
      OrderInfo(arr(0), arr(1), arr(2).toDouble)
    }).keyBy("province", "city")

    // aggregation - 组内聚合 - sum
    val result = keyedStream.sum("amount")

    // sink: print
    result.print()

    env.execute("transformation-keyBy3")
  }

  def testReduce(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("四川,成都,2000.00", "四川,眉山,1000.00", "四川,成都,3000.00", "四川,眉山,2000.00")

    // transform: keyBy
    val keyedStream = dataStream.map(line => {
      val arr = line.split(",")
      OrderInfo(arr(0), arr(1), arr(2).toDouble)
    }).keyBy("province")

    // aggregation - 组内聚合 - reduce
    // reduce可以自定义如何对数据进行聚合: reduce可以实现max,min,avg,sum的逻辑
    val result = keyedStream.reduce((order1, order2) => {
      new OrderInfo(order1.province, order1.city + "," + order2.city, order1.amount + order2.amount)
    })

    // sink: print
    result.print()

    env.execute("transformation-reduce")
  }


  def testMax(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // source:
    val dataStream = env.fromElements("四川,成都,2000.00", "四川,眉山,1000.00", "四川,成都,3000.00", "四川,眉山,2000.00")

    // transform: keyBy
    val keyedStream = dataStream.map(line => {
      val arr = line.split(",")
      OrderInfo(arr(0), arr(1), arr(2).toDouble)
    }).keyBy("province", "city")

    // aggregation - 组内聚合 - max
    val result = keyedStream.max("amount")

    // sink: print
    result.print()

    env.execute("transformation-reduce")
  }





}

case class WordCount(word:String, count:Long)

case class OrderInfo(province:String, city:String, amount:Double)