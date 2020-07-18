package clonegod.learn.flink.window

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

/**
 * 使用EventTime，划分滚动窗口。
 *
 * WaterMark： 控制乱序数据允许迟到的时间，让延迟在可接受范围内的数据在原来的窗口中执行。
 *
 * WaterMark：窗口的最大边界 + 允许的延迟时间
 *
 * WaterMark： Flink中用来延迟触发窗口执行的机制。
 *
 * 数据时间 - 延迟时间 = waterMark, 当 waterMark >= 窗口最大边界时间， 则窗口执行。
 *
 */
object EventTimeOfTumblingWindowWaterMark {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val timeAllowedDelay = Time.seconds(3)
    // 处理无序到达的数据，并设置一个允许的延迟时间
    val tsExtractor = new BoundedOutOfOrdernessTimestampExtractor[String](timeAllowedDelay) {
      // 定义时间提取器，使用EventTime作为窗口计算的基准时间
      override def extractTimestamp(t: String): Long = t.split(",")(0).toLong
    }

    // 并行度为1的Source
    val dataStream = env.socketTextStream("localhost", 7777).assignTimestampsAndWatermarks(tsExtractor);
    println("socket parallelism=" + dataStream.parallelism)

    // transformation
    val wordsStream = dataStream.map(line => {
      val arr = line.split(",")
      Tuple2(arr(1), arr(2).toInt)
    })

    // 先分组
    // 基于数据的发生时间，滑动窗口每5s向后翻滚一次
    // flink划分窗口，窗口的时间精度是ms，窗口是'前闭后开'的。
    // 第1个滚动窗口的时间边界：[0,5000) -> eventTime 在 0 - 4999ms，是第一个窗口的时间边界
    // 第2个滚动窗口的时间边界：[5000, 10000) -> eventTime 在 5000 - 9999ms，是第二个窗口的时间边界
    // 结合waterMark设置的最大允许延迟时间3s
    val windowStream = wordsStream.keyBy(_._1).window(TumblingEventTimeWindows.of(Time.seconds(5)))

    val output = windowStream.sum(1)

    // Sink
    output.print()

    env.execute("EventTimeOfTumblingWindowWaterMark")
  }

  def getKafkaProperties():Properties = {
    val props = new Properties()
    props.setProperty("bootstrap.servers", "127.0.0.1:9092")
    props.setProperty("group.id", "consumer-group-flink")
    props.setProperty("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    props.setProperty("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    // earliest|latest|none
    props.setProperty("auto.offset.reset", "latest")
    props
  }
}
