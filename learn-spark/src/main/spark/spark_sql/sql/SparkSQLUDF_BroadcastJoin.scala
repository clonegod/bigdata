package spark_sql.sql

import org.apache.spark.sql.SparkSession

/**
  * access_log.txt  系统访问日志
  * ips.txt         ip与城市的映射
  *
  * join 代价太昂贵，优化办法：将小表缓存到执行任务到executor上
  *
  */
object SparkSQLUDF_BroadcastJoin {

  def main(args: Array[String]): Unit = {
    val session = SparkSession
      .builder()
      .appName("locationIPJoin")
      .master("local[*]")
      .getOrCreate();

    // 1、从HDFS读取IP规则
    val ipsDataset = session.read.textFile("src/main/resources/data/ips.txt")
    import session.implicits._
    // map操作相当于dataset上的RDD操作，需要引入隐式转化
    // 整理IP规则为结构化数据
    val ipRuleInDriverSide:Array[(Long, Long, String)] = ipsDataset.map(line => {
                                      val fields = line.split("\\|")
                                      val ipSegStart = fields(0).toLong
                                      val ipSegEnd = fields(1).toLong
                                      val city = fields(2)
                                      (ipSegStart, ipSegEnd, city)
                                    }).collect();

    // ===> 广播IP规则数据集给将要执行任务的executor, 使得在查询IP地址时不用做表的join操作，以达到优化SQL执行速度的目的
    val broadcastRef = session.sparkContext.broadcast(ipRuleInDriverSide)

    // 2、从HDFS读取access_log
    val accessLogDataset = session.read.textFile("src/main/resources/data/access_log.txt")
    // 整理access_log规则为结构化数据
    val accessLogDataFrame = accessLogDataset
      .map(line => {
        val fields = line.split("\\|")
        val time = fields(0)
        val ip = fields(1).toLong
        (ip)
      })
      .toDF("ip");

    // 创建视图
    accessLogDataFrame.createTempView("v_access_log")

    // driver端自定义SQL函数-ip2cityFunc
    session.udf.register("ip2cityFunc", (ip:Int)=>{
      val ipRulesInExecutor:Array[(Long, Long, String)] = broadcastRef.value
      // executor中，使用广播变量的引用，就可以获取到相关到IP规则数据
      var city = ipRulesInExecutor.toStream.filter(e => e._1 <= ip && e._2 >= ip).map(_._3).iterator.next()
      city
    })

    // 执行SQL
    // 原来的两表join操作，现在变为只需要查询access_log表，IP地址与城市名称的映射关系则是通过调用自定义函数来实现！
    // 好处：规则就在本地机器上，不用shuffle从其它节点获取数据，因此计算速度更快
    val resultDataFrame = session.sql("""
                                    |SELECT ip2cityFunc(ip) as city, COUNT(*) as cnt
                                    |FROM v_access_log
                                    |GROUP BY city ORDER BY cnt ASC
                                    |""".stripMargin);


    // 执行action
    resultDataFrame.show()

    // 释放资源
    session.stop()

  }
}
