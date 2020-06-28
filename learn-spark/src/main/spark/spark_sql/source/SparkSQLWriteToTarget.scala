package spark_sql.source

import java.util.Properties

import org.apache.spark.sql.{Dataset, Row, SparkSession}

object SparkSQLWriteToTarget {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder()
        .appName("UDAFDemo").master("local[*]").getOrCreate();

    val jdbcOptions = Map(
      "url"->"jdbc:mysql://localhost:3306/test?useSSL=false", // mysql哪个库
      "driver" -> "com.mysql.jdbc.Driver",
      "dbtable" -> "person", // mysql哪个表
      "user" -> "root",
      "password" -> "123456"
    )

    // 从mysql加载数据
    var persons = session.read.format("jdbc").options(jdbcOptions).load()

    persons.printSchema()

    import session.implicits._
    // val result:Dataset[Row] = persons.filter(r => r.getAs("name") == "alice")
    // val result:Dataset[Row] = persons.filter($"name" === "alice")
    // val result:Dataset[Row] = persons.where($"name" === "alice")
    val result:Dataset[Row] = persons.select($"id" * 10 as "ID", $"name")

    // 保存结果到mysql
    val props = new Properties()
    props.put("user", "root")
    props.put("password", "123456")
    result.write.mode("append")
            .jdbc("jdbc:mysql://localhost:3306/test?useSSL=false", "t_result", props)

    // 写text要求：数据只能有一列
    //result.write.text("./result.text")
    //result.write.json("target/result.json")
    //result.write.csv("target/result.csv")

    // Accepted save modes are 'overwrite', 'append', 'ignore', 'error', 'errorifexists'.
    // parquet：高效的列式存储，使用snappy压缩
    result.write.mode("overwrite").parquet("target/result")

    // 将结果写入hdfs
    result.write.mode("overwrite")
            .parquet("hdfs://localhost:8020/testdata/sparkSQL_write_parquet_person")

    result.show()

    session.stop()

  }
}
