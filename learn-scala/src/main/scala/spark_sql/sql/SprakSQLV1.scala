package spark_sql.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

case class Person(id: Long, name: String, age: Int)

/**
 * sparkSQL 1.x 版本的写法
 */
object SparkSQLV1 {
  def main(args: Array[String]): Unit = {
    // 配置要连接的spark集群
    var conf = new SparkConf().setAppName("testSparkSQL1.x").setMaster("local[2]")

    // 创建基于RDD的spark集群连接入口
    val sc = new SparkContext(conf)

    // sparkContext不能创建特殊的RDD（DataFrame）,需要将sparkContext进行包装增强
    // 特殊的RDD，可以理解为存在schema信息的RDD
    val sqlContext = new SQLContext(sc)

    // 先有普通的RDD，然后再关连上schema，进而转换成DataFrame

    val lines: RDD[String] = sc.textFile("hdfs://localhost:8020/testdata/person.txt")

    // useDefinedClass(sqlContext, lines)

    // useRDDRow(sqlContext, lines)

    useDataFrameAPI(sqlContext, lines)

    // 释放资源
    sc.stop()
  }

  /**
   * 自定义类描述字段列表
   * 步骤：
   * 1、创建sparkContext，然后创建sqlContext
   * 2、先创建RDD，对数据进行整理，然后关联case class，将非结构化数据转化为结构化数据
   * 3、显示调用toDF方法将RDD转化为DataFrame
   * 4、注册临时表
   * 5、执行SQL（Transformation, lazy）
   * 6、执行Action
   */
  def useDefinedClass(sqlContext: SQLContext, lines: RDD[String]) {
    // 整理数据：非结构化数据转换为结构化数据
    var personRDD = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1).toString
      val age = fields(2).toInt
      Person(id, name, age)
    });

    // 将RDD转换为Data Frame
    import sqlContext.implicits._
    val personDF = personRDD.toDF

    // 注册dataFrame为临时表
    personDF.registerTempTable("t_person")

    // sql查询临时表(SQL方法其实是Transformation)
    val result = sqlContext.sql("select * from t_person order by age desc")

    // 查看结果（触发action）
    result.show()
  }


  /**
   * 使用StructType定义字段列表
   * 步骤：
   * 1、创建sparkContext，然后创建sqlContext
   * 2、先创建RDD，对数据进行整理，然后关联Row，将非结构化数据转化为结构化数据
   * 3、定义schema
   * 4、调用sqlContext的createDateFrame方法
   * 5、注册临时表
   * 6、执行SQL（Transformation, lazy）
   * 7、执行Action
   */
  def useRDDRow(sqlContext: SQLContext, lines: RDD[String]) {
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
      StructField("id", LongType, false),
      StructField("name", StringType, true),
      StructField("age", IntegerType, true)
    ))

    // 将rowRDD与schema进行关联,创建得到DataFrame
    val personDF = sqlContext.createDataFrame(personRow, schema)

    // 注册dataFrame为临时表
    personDF.registerTempTable("t_person")

    // sql查询临时表(SQL方法其实是Transformation)
    val result = sqlContext.sql("select * from t_person order by age asc")

    // 查看结果（触发action）
    result.show()
  }


  /**
   * 直接使用DataFrame查询数据，不用创建临时表
   */
  def useDataFrameAPI(sqlContext: SQLContext, lines: RDD[String]) {
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
      StructField("id", LongType, false),
      StructField("name", StringType, true),
      StructField("age", IntegerType, true)
    ))

    // 将rowRDD与schema进行关联,创建得到DataFrame
    val personDF = sqlContext.createDataFrame(personRow, schema)

    // 直接使用DataFrame查询数据，不用创建临时表
    val df = personDF.select("id", "age", "name")
    import sqlContext.implicits._
    val result = df.orderBy($"age" desc, $"id" asc)

    // 查看结果（触发action）
    result.show()
  }

}