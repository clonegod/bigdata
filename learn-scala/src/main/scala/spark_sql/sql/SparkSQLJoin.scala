package spark_sql.sql

import org.apache.spark.sql.SparkSession

object SparkSQLJoin {
  def main(args: Array[String]): Unit = {
    // 创建sparkSQL session
    val session = SparkSession.builder()
      .appName("sparkSQLJoin")
      .master("local[*]")
      .getOrCreate();

    // 读取数据，获得dataset数据集
    import session.implicits._
    val persons = session.createDataset(List(
      "1,zs,china",
      "2,ls,usa",
      "3,ww,jp"
    ))
    val countries = session.createDataset(List(
      "china,中国",
      "usa,美国"
    ))

    // 非结构化数据转化为结构化数据
    val personDataSet = persons.map(line => {
      val fields = line.split(",")
      (fields(0).toLong, fields(1), fields(2))
    })
    val countryDataSet = countries.map(line => {
      val fields = line.split(",")
      (fields(0), fields(1))
    })

    // dataset 转化为dataFrame，并指定schema信息
    val personDataFrame = personDataSet.toDF("id", "name", "countryCode")
    val countryDataFrame = countryDataSet.toDF("code", "name")

    /**
    // 第1种方式：创建临时视图，通过SQL 进行join
    personDataFrame.createTempView("v_person")
    countryDataFrame.createTempView("v_country")

    val joinedDataFrame = session.sql(
      """
        |select v1.id as personId, v1.name as personName, v2.name as countryName
        |from v_person v1 join v_country v2 on v1.countryCode = v2.code
        |""".stripMargin)
    */

    // 第二种方式，在dataFrame上进行join，不用注册临时表
    val joinedDataFrame = personDataFrame.join(countryDataFrame,
                                      $"countryCode" === $"code", "left_outer")

    // 执行action
    personDataFrame.show()
    countryDataFrame.show()
    joinedDataFrame.show()

    session.stop()
  }
}
