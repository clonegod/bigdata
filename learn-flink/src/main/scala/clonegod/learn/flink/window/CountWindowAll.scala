package clonegod.learn.flink.window

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow

/**
 * 计数window-无分组
 */
object CountWindowAll {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /**
     * nc client input:
     * 1
     * 2
     * 3
     * 4
     * 5
     */
    val dataStream:DataStream[String] = env.socketTextStream("localhost", 7777)

    val intStream:DataStream[Int] = dataStream.map(_.toInt)

    // 无界数据流划分为有界数据流处理：每3条数据划分为1个窗口处理1次
    // 不分组，直接划分窗口
    val allWindowStream:AllWindowedStream[Int, GlobalWindow] = intStream.countWindowAll(3)

    // 每3条数据触发一次窗口数据的聚合
    val output:DataStream[Int] = allWindowStream.sum(0)

    output.print()

    env.execute("CountWindow")
  }
}
