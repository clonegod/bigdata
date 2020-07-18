package clonegod.learn.flink.richmapper

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.commons.io.IOUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}

/**
 * 从mysql关联活动的维度信息
 */
object ActivityApp {

  def main(args: Array[String]): Unit = {

    val kafkaServers = "localhost:9092"
    val topics = Lists.newArrayList("flink_topic")
    val groupId = "flink_group01"

    val dataStream = FlinkUtil.createKafkaSource(kafkaServers, topics, groupId)

    //val result = dataStream.map(new ActivityRichFunction())//.setParallelism(1)
    val result = dataStream.map(new ActivityRichFunction()).map(new GeoActivityRichFunction())

    result.print()

    FlinkUtil.env.execute("ActivityApp")

  }
}



// RichMapper
class ActivityRichFunction extends RichMapFunction[String, ActivityBean] {

  var conn:Connection = _
  var pstm_activity_name:PreparedStatement = _

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)

    // 每个subtask都会分别打开一个mysql的connection
    val subTask = getRuntimeContext.getIndexOfThisSubtask

    // 创建mysql连接
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useSSL=false", "root", "123456")
    pstm_activity_name = conn.prepareStatement("SELECT name FROM t_activities WHERE id = ?")

    println(subTask + " connect mysql successfully..." + conn)
  }

  override def map(line: String): ActivityBean = {
    val fields = line.split(",")
    val uid = fields(0)
    val aid = fields(1)
    val eventTime = fields(2)
    val eventType = fields(3).toInt
    val longitude = fields(4).toDouble
    val latitude = fields(5).toDouble

    // TODO: 根据aid从数据库/redis查询活动名称
    val activityName = queryActivityName(aid)

    new ActivityBean(uid, aid, eventTime, eventType, longitude, latitude, activityName, null)
  }

  def queryActivityName(aid: String):String = {
    pstm_activity_name.setString(1, aid)
    val rs = pstm_activity_name.executeQuery()
    if(rs.next()) {
      rs.getString(1)
    } else {
      "NotFound"
    }
  }

  override def close(): Unit = {
    super.close()
    conn.close()
    println("mysql conn closed")
  }


}


class GeoActivityRichFunction extends RichMapFunction[ActivityBean, ActivityBean] {

  @transient var httpClient:CloseableHttpClient = _

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    httpClient = HttpClients.createDefault()
  }

  override def map(in: ActivityBean): ActivityBean = {
    var httpRes:CloseableHttpResponse = null
    val locationQueryUrl = "http://www.baidu.com?"
    try {
      val httpGet = new HttpGet(locationQueryUrl)
      httpRes = httpClient.execute(httpGet)
      // 解析响应结果，获取经纬度对应的地址信息
      val location = parseRes(httpRes)
      new ActivityBean(in.uid, in.aid, in.eventTime, in.eventType,in.longitude, in.latitude, in.activityName, location)
    } finally {
      if(httpRes != null) httpRes.close()
    }
  }

  def parseRes(httpRes: CloseableHttpResponse):String = {
    val content = IOUtils.toString(httpRes.getEntity.getContent)
    println(content)
    if(Math.random() > 0.5) {
      "北京市"
    } else {
      "上海市"
    }
  }

  override def close(): Unit = {
    super.close()
    httpClient.close()
  }


}



