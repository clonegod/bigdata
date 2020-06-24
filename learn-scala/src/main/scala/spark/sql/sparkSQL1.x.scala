package spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

case class Person(id:Long, name:String, age:Int) 

object TestSparkSQL1 {
  def main(args: Array[String]): Unit = {
    // 配置要连接的spark集群
    var conf = new SparkConf().setAppName("testSparkSQL").setMaster("local[2]")
    
    // 创建基于RDD的spark集群连接入口
    val sc = new SparkContext(conf)
    
    // sparkContext不能创建特殊的RDD（DataFrame）,需要将sparkContext进行包装增强
    // 特殊的RDD，可以理解为存在schema信息的RDD
    val sqlContext = new SQLContext(sc)
    
    // 先有普通的RDD，然后再关连上schema，进而转换成DataFrame
    
    val lines = sc.textFile("hdfs://localhost:8020/testdata/person.txt")
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
    
    // 释放资源
    sc.stop()
  }
  
  
}


