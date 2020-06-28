package clonegod.learn.flink

import org.apache.flink.api.common.serialization.{SimpleStringEncoder}
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala._

object SinkToFileSystem {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 读取数据源
    val inputStream = env.readTextFile("/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt")

    // 转换数据
    val dataStream:DataStream[String] = inputStream.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0), dataArray(1).toLong, dataArray(2).toDouble).toString
    })

    // 将结果输出到文件系统: 本地/hdfs文件系统
    val fileSink = StreamingFileSink.forRowFormat(
                    new Path("target/sink"),
                    //new Path("hdfs://localhost:8020/testdata/sink"),
                    new SimpleStringEncoder[String]("UTF-8")
                  ).build();

    dataStream.addSink(fileSink)

    dataStream.print()

    env.execute("sink to file system")
  }
}
