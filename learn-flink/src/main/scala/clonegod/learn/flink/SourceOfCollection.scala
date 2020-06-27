package clonegod.learn.flink

import org.apache.flink.streaming.api.scala._

object SourceOfCollection {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // Source
    val data = List(
      SensorReading("sensor#1", 1593270588685L, 10.1),
      SensorReading("sensor#2", 1593270588185L, 12.7),
      SensorReading("sensor#3", 1593270588085L, 11.4),
      SensorReading("sensor#4", 1593270588485L, 14.2),
      SensorReading("sensor#5", 1593270588285L, 12.5),
      SensorReading("sensor#6", 1593270588985L, 10.9)
    )

    // val stream = env.fromElements(1, "alice", 20) // 支持多种数据类型
    val stream = env.fromCollection(data)

    // Sink
    stream.print()

    env.execute("source of collection")

  }
}
