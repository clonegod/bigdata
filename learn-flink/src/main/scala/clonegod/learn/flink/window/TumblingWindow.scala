package clonegod.learn.flink.window

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 滚动窗口-无分组
 */
object TumblingWindow {
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

    // 按时间划分窗口，每5秒统计一次
    val timeWindow = intStream.timeWindowAll(Time.seconds(5))

    val output:DataStream[Int] = timeWindow.sum(0)

    output.print()

    env.execute("TimeWindow")

  }
}
