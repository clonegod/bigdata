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
 * 如果使用的是并行的Source，比如KafkaSource。
 * 当Kafka的Topic有多个分区时，每个Source所对应的partition上的数据，全部到达窗口边界时间之后，整个窗口才会被触发！
 *
 *
 * 基于数据的eventTime作为窗口边界，当存在分组的时候，不同组的窗口的触发行为如何？
 * 1、对于单一并行度的source，比如socketTextStream，分组后即使只有1个分组的时间满足触发条件，其它分组也会被一起执行。
 * 2、对于并行度大于1的source，比如kafkaSource，分组后，只有当所有的"管道"上的时间区间都满足被触发条件，窗口才会进行触发。
 *  --- 这种情况有个坑，比如Kafka3个分区，但总是有1个分区没有数据，那整个窗口的执行都会受到影响。因此，必需保证每个partition数据到达是均匀的。
 *  --- Kafka多分区的情况下，flink消费Kafka的数据，跟keyBy分组的个数没有关系，主要是看Kafka的topic下每个partition的数据时间是否满足窗口边界。
 *  eg:
 * > 1000,a,1
 * > 1000,b,1
 * > 1000,c,1
 * > 1000,d,1 ---- d这条数据，会在3个partition都达到窗口边界时，一起被输出
 * > 4999,a,2 -> partition1 达到窗口边界
 * > 4999,b,2 -> partition2 达到窗口边界
 * > 4999,c,2 -> partition3 达到窗口边界(3个partition上的数据时间满足窗口时间要求，因此触发窗口内数据的计算)
 * 2> (b,3)
 * 4> (c,3)
 * 6> (a,3)
 * 5> (d,1)
 */
object KafkaSourceEventTimeOfTumblingWindow {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val tsExtractor = new BoundedOutOfOrdernessTimestampExtractor[String](Time.seconds(0)) {
      // 定义时间提取器，使用EventTime作为窗口计算的基准时间
      override def extractTimestamp(t: String): Long = t.split(",")(0).toLong
    }


    // 并行度为1的Source
//    val dataStream = env.socketTextStream("localhost", 7777).assignTimestampsAndWatermarks(tsExtractor);
//    println("socket parallelism=" + dataStream.parallelism)



    // KafkaSource (并行度大于1的Source）
    // flink启动之后，多个TaskManager下的subtask由于属于同一个group，因此这些subtask作为同组下的consumer，将同时消费不同的partition上的数据
    val topic = "flink_tumbling_window"
    val deSerSchema = new SimpleStringSchema()
    val kafkaProps = getKafkaProperties()
    val dataStream = env.addSource(new FlinkKafkaConsumer[String](topic, deSerSchema, kafkaProps)).assignTimestampsAndWatermarks(tsExtractor);
    println("kafkaSource parallelism=" + dataStream.parallelism)


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
    val windowStream = wordsStream.keyBy(_._1).window(TumblingEventTimeWindows.of(Time.seconds(5)))

    val output = windowStream.sum(1)

    // Sink
    output.print()

    env.execute("KafkaSourceEventTimeOfTumblingWindow")
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
