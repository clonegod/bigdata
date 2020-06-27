package clonegod.learn.flink

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.scala._

/**
 * 1、基本转换算子：
 * map, flatmap, filter
 *
 * 2、滚动聚合算子 Rolling Aggregation / KeyedStream
 * keyBy - 基于某个key的hashcode进行重分区/分组
 * sum, min, max
 * reduce
 *
 * 3、多流转换算子：切分 、分流、合并
 * split + select
 * connect + coMap
 * union
 */
object TransformExec {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // Source
    val file = "/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt"

    val stream:DataStream[String] = env.readTextFile(file)

    // 1、基本转换操作
    val dataStream:DataStream[SensorReading] = stream
        .map(line => {
          val dataArray = line.split(",")
          SensorReading(dataArray(0), dataArray(1).toLong, dataArray(2).toDouble)
        })
        .filter(_.temperature >= 10);

    // 2、分组聚合操作
    val aggStream:DataStream[SensorReading] = dataStream
        //.keyBy(0)
        //.keyBy("id")
        //.keyBy(_.id)
        .keyBy( new MyKeySelector() )  //  按key分组，求聚合结果
        //.max("temperature")
        .reduce(new MySensorReducer);

    // 3、分流+筛选
    val splitStream:SplitStream[SensorReading] = dataStream.split(data => {
      if(data.temperature > 11.0) Seq("hign")
      else Seq("low")
    })
    val hignTempStream = splitStream.select("hign") // 选择某个分流
    val lowTempStream = splitStream.select("low")
    val allTempStream = splitStream.select("hign", "low") // 选择多个分流
//    hignTempStream.print()
//    lowTempStream.print()
//    allTempStream.print()


    // 4、合流 + 转换(coMap-合并流需要提供两个转换函数)
    // connect可以连接2个类型不同的流
    // union可以连接多个流 ，但是要求流的数据类型一致
    val warningStream = hignTempStream.map(
      data => (data.id, data.temperature)
    )

    val connectedStream:ConnectedStreams[(String, Double), SensorReading] = warningStream.connect(lowTempStream)

    val resultStream:DataStream[Object] = connectedStream.map(
      warningData => (warningData._1, warningData._2, "hign temperature warning"),
      lowTempData => (lowTempData.id, lowTempData.temperature, "OK")
    )

    // union合并流
    hignTempStream.union(lowTempStream).print()

    // Sink
    resultStream.print()

    env.execute("source of file")
  }
}

class MyKeySelector extends KeySelector[SensorReading, String] {
  override def getKey(in: SensorReading): String = in.id
}

case class MySensorReducer() extends ReduceFunction[SensorReading] {
  // 返回最新的时间戳和最大的温度值
  override def reduce(cur: SensorReading, next: SensorReading): SensorReading = {
    SensorReading(
      cur.id,
      cur.timestamp.max(next.timestamp),
      cur.temperature.max(next.temperature))
  }
}