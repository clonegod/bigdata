package spark_sql.wc

import org.apache.spark.{SparkConf, SparkContext}

object SparkWordCount {
  def main(args: Array[String]): Unit = {
    // 1、设置SparkApplication的名称，设置Spark的运行模式
    // setMaster 配置要连接到Master到地址
    val conf = new SparkConf().setMaster("local").setAppName("wordcount")

    // 2、SparkContext是连接spark集群的唯一通道
    val sc = new SparkContext(conf)

    // 3、调用sparkAPI进行业务逻辑操作
    wordCount1(sc)

    wordCount2(sc)


    // 4、释放资源
    sc.stop()
  }

  def wordCount1(sc: SparkContext) {
    // 参数path可以指向单一文件，也可以指向一个文件目录
    // RDD：更适合并行计算到数据模型
    val result1 = sc.textFile("./data")
      .flatMap(_.split("\\s+"))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .collect();

    result1.foreach(println)
  }

  def wordCount2(sc: SparkContext) {
    val result2 = sc.textFile("./data")
      .flatMap(_.split("\\s+"))
      .groupBy(word => word)
      .map({
        case (word, iter) => (word, iter.size)
      })
      .collect();

    println(result2.mkString("\n"))
  }


}
