package spark_sql.sql

import org.apache.spark.sql.SparkSession

/**
  * access_log.txt  系统访问日志
  * ips.txt         ip与城市的映射
  */
object SparkSQLCalcIPLocation {

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
    val ipRulesDataFrame = ipsDataset
      .map(line => {
        val fields = line.split("\\|")
        val ipSegStart = fields(0).toLong
        val ipSegEnd = fields(1).toLong
        val city = fields(2)
        (ipSegStart, ipSegEnd, city)
      })
      .toDF("ipSegStart", "ipSegEnd", "city");

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

    // 将dataFrame创建虚拟视图
    ipRulesDataFrame.createTempView("v_ips")
    accessLogDataFrame.createTempView("v_access_log")

    // 在视图上执行SQL查询
    val resultDataFrame = session.sql(
      """
        |SELECT city, COUNT(*) AS visit_cnt
        |FROM v_ips JOIN v_access_log ON (ip >= ipSegStart AND ip <= ipSegEnd )
        |GROUP BY city
        |ORDER BY visit_cnt desc
        |""".stripMargin)

    // 执行action
    resultDataFrame.show()

    // 释放资源
    session.stop()

  }
}
