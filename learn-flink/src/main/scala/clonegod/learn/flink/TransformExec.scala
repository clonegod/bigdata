package clonegod.learn.flink

import org.apache.flink.api.common.functions.{IterationRuntimeContext, MapFunction, ReduceFunction, RichMapFunction, RuntimeContext}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
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
 *
 * ---
 * 1、keyBy是什么
 * 基于key的hash code重分区
 * 同一个key只能在一个分区里处理，一个分区内可以存在不同key的数据
 * keyBy之后的所有操作，针对的作用域都只是当前的key
 *
 * 2、滚动聚合操作是什么
 * DataStream没有聚合操作，目前所有聚合操作都是基于KeyedStream来进行的。
 * 也就是说聚合操作API必需是经过分组后的
 *
 * 3、多流转换算子是什么
 * split-select, connect-map/flatmap
 * 它们都是成对进行使用的
 * 先转换为 SplitStream/ConnectedStream, 然后再通过select/comap操作就转换回DataStream
 * 所谓coMap,就是基于ConnectedStream的map方法，需要同时提供两个转换函数，这两个函数就叫做coMapFunction
 *
 * 4、富函数
 * 普通函数的增强版，提供了生命周期相关的方法，还可以获取到运行时上下文
 * 在运行时上下文可以对state进行操作
 * flink有状态的流式计算，叫做状态编程，就是基于类似RichFunction机制来实现的
 *
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
      if(data.temperature > 11.0) Seq("high") // 对不同的流打上标签
      else Seq("low")
    })
    val highTempStream = splitStream.select("high") // 通过标签选择某个分流
    val lowTempStream = splitStream.select("low")
    val allTempStream = splitStream.select("high", "low") // 选择多个分流
//    hignTempStream.print()
//    lowTempStream.print()
//    allTempStream.print()


    // 4、合流 + 转换(coMap-合并流需要提供两个转换函数)
    // connect可以连接2个类型不同的流
    // union可以连接多个流 ，但是要求流的数据类型一致
    val warningStream = hignTempStream.map(
      //data => (data.id, data.temperature)
      new MyMapper()
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

class MyMapper extends MapFunction[SensorReading, (String,Double)] {
  override def map(t: SensorReading): (String, Double) = (t.id, t.temperature)
}

/**
 * 富函数
 */
class MyRichFunction extends RichMapFunction {

  override def setRuntimeContext(t: RuntimeContext): Unit = super.setRuntimeContext(t)

  override def getRuntimeContext: RuntimeContext = super.getRuntimeContext

  override def getIterationRuntimeContext: IterationRuntimeContext = super.getIterationRuntimeContext

  // 初始化
  override def open(parameters: Configuration): Unit = super.open(parameters)

  // 每一次map操作都会调用
  override def map(in: Nothing): Nothing = ???

  // 结束
  override def close(): Unit = super.close()


}