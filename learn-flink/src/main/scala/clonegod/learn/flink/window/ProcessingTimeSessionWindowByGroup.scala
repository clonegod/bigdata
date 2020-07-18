package clonegod.learn.flink.window

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows

/**
 * 会话窗口- 有分组
 */
object ProcessingTimeSessionWindowByGroup {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /**
     * nc client input:
     * hadoop,1
     * scala,1
     * flink,1
     * hadoop,2
     * hadoop,3
     * flink,2
     * scala,2
     * scala,3
     * flink,4
     */
    val dataStream:DataStream[String] = env.socketTextStream("localhost", 7777)

    val wordsStream = dataStream.map(line => {
      val arr = line.split(",")
      Tuple2(arr(0), arr(1).toInt)
    })

    // 先分组
    // 会话窗口5秒内没有数据达到，触发窗口执行
    val windowStream = wordsStream.keyBy(_._1).window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)))

    val output = windowStream.sum(1)

    output.print()

    env.execute("ProcessingTimeSessionWindowByGroup")
  }
}
