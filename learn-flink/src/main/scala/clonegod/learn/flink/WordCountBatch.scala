package clonegod.learn.flink

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
// 隐式转换-TypeInformation
import org.apache.flink.api.scala._

object WordCountBatch {
  def main(args: Array[String]): Unit = {
    println("app start...")

    // 创建一个批处理的执行上下文
    val env = ExecutionEnvironment.getExecutionEnvironment

    // 从本地文件中读取数据
    // 可以指定目录或具体文件
    val inputDataSet: DataSet[String] = env.readTextFile("/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources")

    // 基于dataset做转换，按空格切分单词，再按word作为key做分组统计
    val resultDataSet: DataSet[(String,Int)] = inputDataSet
      .flatMap(_.split(" "))
      .filter(_.nonEmpty)// 过滤
      .map( (_, 1) ) // 转换为一个二元组(word, count)
      .groupBy(0) // 按元组中第一个元素分组
      .sum(1) // 集合元组中第二个元素的值

    // 输出结果
    resultDataSet.print()

  }
}
