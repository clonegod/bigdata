package spark_sql.source

import org.apache.spark.sql.SparkSession

object HiveOnSpark {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("HiveOnSpark")
      .master("local[*]")
      .enableHiveSupport() // 启用spark对hive的支持（可以兼容HQL的语法）
      .getOrCreate();

    // 要想spark使用hive的元数据库，必需指定hive元数据库的位置。添加hive-site.xml到当前工程的classpath路径下即可。
    // hive-site.xml中设置了连接mysql的数据库地址和账号信息
    // spark通过该文件就能连接到hive的元数据库，从而获取到与HDFS文件数据相关的各种schema信息
    val result = spark.sql("select * from testdb.t_person")

    result.printSchema()

    result.show()

    spark.stop()
  }

}
