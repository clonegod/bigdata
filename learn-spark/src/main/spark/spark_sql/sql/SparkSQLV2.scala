package spark_sql.sql


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField

/**
 * sparkSQL 2.x 版本的写法
 */
object SparkSQLV2 {
  def main(args: Array[String]): Unit = {
    // spark2.x SQL编程API
    val session = SparkSession.builder()
      .appName("testSparkSQL2.x")
      .master("local[*]")
      .getOrCreate();

    // 创建RDD
    val lines = session.sparkContext.textFile("hdfs://localhost:8020/testdata/person.txt")


    // 整理数据：非结构化数据转换为结构化数据
    var personRow = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1).toString
      val age = fields(2).toInt
      Row(id, name, age)
    });

    // 定义表头，描述DataFrame
    val schema = StructType(List(
      StructField("ID", LongType, false),
      StructField("NAME", StringType, true),
      StructField("AGE", IntegerType, true)
    ))

    // 将rowRDD与schema进行关联,创建得到DataFrame
    val personDF = session.createDataFrame(personRow, schema)
    import session.implicits._
    val result = personDF.filter($"age" > 20).orderBy($"age" desc)

    // 输出结果
    result.show()
    //result.write.jdbc(url, table, connectionProperties)

    // 释放资源
    session.stop()

  }
}
