package clonegod.learn.flink

import org.apache.flink.streaming.api.scala._

object SourceOfFile {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // Source
    val file = "/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt"

    val stream:DataStream[String] = env.readTextFile(file)

    // Sink
    stream.print()

    env.execute("source of file")

  }
}
