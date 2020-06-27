package clonegod.learn.flink

import org.apache.flink.streaming.api.scala._

object SourceOfCustom {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // Source
    val stream = env.addSource(new MySensorSource())

    // Sink
    stream.print()

    env.execute("source of custom")
  }
}
