package clonegod.learn.flink

import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._

object SinkToConsole {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.fromElements("1,2,3,4")

    val dataStream2 = dataStream.flatMap(_.split(",")).map(_.toInt)

    // 自定义Sink
    dataStream2.addSink(new RichSinkFunction[Int] {
      override def invoke(value: Int, context: SinkFunction.Context[_]): Unit = {
        val subTaskIndex = getRuntimeContext.getIndexOfThisSubtask()
        println(subTaskIndex + ">> " + value)
      }
    })

    env.execute("SinkToConsole")
  }
}
