package spark_sql.sql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Dataset

/**
 * 使用sparkSQL2.x编写WordCount
 */
object SparkSQLV2WordCount {
  def main(args: Array[String]): Unit = {
    // spark2.x SQL编程API
    val session = SparkSession.builder()
      .appName("testSparkSQL2.x")
      .master("local[*]")
      .getOrCreate();

    // 创建RDD
    // DateSet分布式数据集，是对RDD的进一步封装
    // 返回的dataSet只有1列，默认这列叫value
    val lines: Dataset[String] = session.read.textFile("hdfs://localhost:8020/testdata/words.txt")

    import session.implicits._
    val words = lines.flatMap(_.split("\\s+"))

    //bySQL(session, words)

    byDataSetAPI(session, words)

    session.stop()

  }

  def bySQL(session: SparkSession, words: Dataset[String]): Unit = {
    // 创建视图-虚拟表
    words.createTempView("v_words")

    // 执行SQL
    val result = session.sql(
      """
        select value as word, count(*) as cnt
          from v_words
          group by value
          order by cnt desc""")

    // 输出结果
    result.show()
  }

  def byDataSetAPI(session: SparkSession, words: Dataset[String]): Unit = {
    import session.implicits._

    // 使用DataSet的API进行数据计算
    //val result = words.groupBy($"value" as "word").count().sort($"count" desc)

    import org.apache.spark.sql.functions._
    val result = words.groupBy($"value" as "word")
                      .agg(count("*") as "cnt")
                      .orderBy($"cnt" desc)

    // 输出结果
    result.show()
  }

}