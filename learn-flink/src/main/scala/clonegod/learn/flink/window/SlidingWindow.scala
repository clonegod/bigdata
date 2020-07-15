package clonegod.learn.flink.window

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 滑动窗口-无分组
 */
object SlidingWindow {
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

    // 窗口长度为10s，步长为5秒滑动一次
    val timeWindow = intStream.timeWindowAll(Time.seconds(10), Time.seconds(5))

    val output:DataStream[Int] = timeWindow.sum(0)

    output.print()

    env.execute("SlidingWindow")

  }
}
