package clonegod.learn.flink

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer}

/**
 * canal监控mysql下的flink库下的binlog,将日志发送到Kafka的topic上。
 * topic名称为：flink
 *
 */
object SourceOfKafka {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // Source
    val topic = "flink"
    val deSerSchema = new SimpleStringSchema()
    val kafkaProps = getKafkaProperties()

    // 消费kafka上的数据：指定topic, 反序列化机制，连接kafka的配置等
    val stream = env.addSource(new FlinkKafkaConsumer[String](topic, deSerSchema, kafkaProps))

    // Sink
    stream.print()

    env.execute("source of kafka")
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
