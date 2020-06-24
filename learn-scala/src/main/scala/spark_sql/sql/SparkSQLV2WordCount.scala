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

    // 创建视图-虚拟表
    words.createTempView("v_words")

    // 执行SQL
    val result = session.sql(
      """
        select value as word, count(*) as cnt 
          from v_words 
          group by value 
          order by cnt desc""")

    result.show()

    session.stop()

  }
}