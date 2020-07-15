package clonegod.learn.flink.window

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor

/**
 * 会话窗口- 有分组
 * 按数据所携带的时间为基准，进行窗口的划分
 */
object EventTimeSessionWindowByGroup {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /** 设置时间基准为数据所携带的时间，而非默认的ProcessingEvent Time */
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    /**
     * nc client input:
     * 1594829801000,a,1
     * 1594829802000,b,1
     * 1594829801000,a,2
     * 1594829802000,b,3
     * 1594829807000,a,1 ---> 与上一条时间间隔gap达到5秒，窗口被触发
     */
    val dataStream = env.socketTextStream("localhost", 7777)

    // 定义时间提取器，使用EventTime作为窗口计算的基准时间
    val timestampExtractor = new BoundedOutOfOrdernessTimestampExtractor[String](Time.seconds(0)) {
      override def extractTimestamp(t: String): Long = t.split(",")(0).toLong
    }

    val eventTimeDataStream = dataStream.assignTimestampsAndWatermarks(timestampExtractor)

    val wordsStream = eventTimeDataStream.map(line => {
      val arr = line.split(",")
      Tuple2(arr(1), arr(2).toInt)
    })

    // 先分组
    // 会话窗口5秒内没有数据达到，触发窗口执行
    val windowStream = wordsStream.keyBy(_._1).window(EventTimeSessionWindows.withGap(Time.seconds(5)))

    val output = windowStream.sum(1)

    output.print()

    env.execute("CountWindowByGroup")
  }
}
