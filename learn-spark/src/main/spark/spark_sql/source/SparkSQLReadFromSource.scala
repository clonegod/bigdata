package spark_sql.source

import org.apache.spark.sql.SparkSession

object SparkSQLReadFromSource {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder()
                      .appName("readFromSomeSource")
                      .master("local[*]").getOrCreate();

    readFromJson(session)

    readFromCSV(session)

    readFromHDFS_Parquet(session)

    session.stop()
  }

  def readFromJson(session:SparkSession) {
    import session.implicits._

    // 读取json格式的数据源
    val dataFrame = session.read.json("src/main/resources/data/person.json")

    // 过滤
    val result = dataFrame.where($"age" > 20)

    result.printSchema()

    result.show()
  }

  def readFromCSV(session:SparkSession) {
    import session.implicits._

    // 读取csv格式的数据源
    val dataFrame = session.read.csv("src/main/resources/data/person.csv")
                        .toDF("id", "name", "age");

    // 过滤
    val result = dataFrame.where($"age" <= 20)

    result.printSchema()

    result.show()
  }

  def readFromHDFS_Parquet(session:SparkSession) {
    import session.implicits._

    // 读取Parquet的数据源
    //session.read.format("parquet").load("hdfs://")
    val dataFrame = session.read.parquet("hdfs://localhost:8020/testdata/sparkSQL_write_parquet_person")

    // 过滤
    val result = dataFrame.where($"ID" < 20)

    result.printSchema()

    // show才是真正触发action
    result.show()
  }

}
