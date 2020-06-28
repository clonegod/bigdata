package clonegod.learn.flink

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, FlinkKafkaProducer011}

object SinkToKafka {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 读取数据源
    val inputStream = env.readTextFile("/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt")

    // 转换数据
    val dataStream:DataStream[String] = inputStream.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0), dataArray(1).toLong, dataArray(2).toDouble).toString
    })

    // 将结果输出到kafka
    val kafkaProducer = new FlinkKafkaProducer[String](
      "localhost:9092",         // broker list
      "flink_output",               // target topic
      new SimpleStringSchema())   // serialization schema

    dataStream.addSink(kafkaProducer)

    dataStream.print()

    env.execute("sink to kafka")

  }

}