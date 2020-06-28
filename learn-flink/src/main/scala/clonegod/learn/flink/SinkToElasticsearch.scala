package clonegod.learn.flink

import java.net.InetAddress
import java.util

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch._
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.client.Requests

object SinkToElasticsearch {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 读取数据源
    val inputStream = env.readTextFile(
      "/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt")

    // 转换数据
    val dataStream:DataStream[SensorReading] = inputStream.map(
      data => {
        val dataArray = data.split(",")
        SensorReading(dataArray(0), dataArray(1).toLong, dataArray(2).toDouble)
      });

    // 写入数据到elastic
    val httpHosts = getEsHosts()
    val sinkFunc = getEsSinkFunc()
    dataStream.addSink(new ElasticsearchSink.Builder[SensorReading](httpHosts, sinkFunc).build())

    // 写入完成后，查询结果:
    // http://localhost:9200/sensor_temp/_search?pretty

    env.execute("sink to ElasticSearch")
  }

  // 1、定义连接es的地址
  def getEsHosts() = {
    // 注意List类型：java.util.List
    val hosts = new util.ArrayList[HttpHost]()
    hosts.add(new HttpHost("localhost", 9200))
    hosts
  }

  // 定义ElasticSearchSinkFunction
  def getEsSinkFunc(): ElasticsearchSinkFunction[SensorReading] = {
    new ElasticsearchSinkFunction[SensorReading] {
      override def process(t: SensorReading, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        // 定义数据对象的哪些内容写入es
        // 写入es的数据类型可以是json或者Map
        // 注意Map类型： java.util.Map
        val data = new util.HashMap[String,String]()
        data.put("sensor_id", t.id)
        data.put("temperature", t.temperature.toString)
        data.put("timestamp", t.timestamp.toString)

        // 创建一个index request - 建立索引
        val indexRequest = Requests.indexRequest()
                              .index("sensor_temp")
                              .`type`("sensor_temperature")
                              .source(data)

        // 用indexer发送http请求，将数据写入到es
        requestIndexer.add(indexRequest)

        println(t + " save to es successfully")
      }
    }
  }


}
