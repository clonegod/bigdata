package spark_sql.sql

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructField, StructType}

/**
 * UDF - 1对1。输入1行数据，返回1个结果。比如，对某个字段的值进行转化映射。
 * 用户自定义函数（当框架提供的函数不满足需求时，用户可以自定义函数实现更复杂的功能）
 *
 * UDTF - 1对多。输入1行返回多行。（hive中有这种类型的函数）。比如，将一行数据打散为多列。
 * spark SQL中没有UDTF，spark中用flatMap即可实现该功能
 *
 * UDAF - 多对1。输入多行，返回1行。比如，聚合函数，sum,min,max,avg,count等
 * 用户自定义聚合函数
 */
object SparkSQLUADF {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder().appName("UDAFDemo").master("local[*]").getOrCreate()

    val range = session.range(1, 11)
    range.show()

    range.createTempView("v_range_numbers")

    // 创建自定义聚合函数实例
    val geoMean = new GeoMean

    /** SQL 风格：使用自定义聚合函数 */
    // 注册自定义聚合函数
    session.udf.register("aggPow", geoMean)
    // 在SQL中使用自定义函数完成数据计算
    val resultDataFrame = session.sql("SELECT aggPow(id) as result FROM v_range_numbers")

    /** DSL 风格：使用自定义聚合函数 */
    //import session.implicits._
    //val resultDataFrame = range.agg(geoMean($"id") as "result")

    resultDataFrame.show()

    session.stop()
  }
}

/**
 * 自定义聚合函数的实现类 - UDAF
 */
class GeoMean extends UserDefinedAggregateFunction {

  // 输入数据的类型
  override def inputSchema: StructType = StructType(List(
    StructField("id", DoubleType)
  ))

  // 产生中间结果的类型
  override def bufferSchema: StructType = StructType(List(
    // 参与运算的数字个数
    StructField("count", LongType),
    // 运算的中间结果类型
    StructField("tmp_value", DoubleType)
  ))

  // 最终返回结果的类型
  override def dataType: DataType = DoubleType

  // 结果一致性保证
  override def deterministic: Boolean = true

  // 指定初始值
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    // 初始参与运算的个数为0
    buffer(0) = 0L
    // 初始结果值为1
    buffer(1) = 1.0D
  }

  // 结果更新：每有一条数据参与运算，就更新一下中间结果（update相当于在每一个分区中的运算）
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    // 更新参与运算的数据个数
    buffer(0) = buffer.getLong(0) + 1L
    // 使用新数据更新中间结果
    buffer(1) = buffer.getDouble(1) * input.getDouble(0)
  }

  // reduce 全局聚合：所有节点上的中间结果进行合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getDouble(1) * buffer2.getDouble(1)
  }

  // 计算最终结果
  override def evaluate(buffer: Row): Any = {
    val count = buffer.getLong(0)
    val accVal = buffer.getDouble(1)
    println(s"------------------->>> count=$count, accVal=$accVal")
    math.pow(accVal, 1.toDouble / count)
  }
}
