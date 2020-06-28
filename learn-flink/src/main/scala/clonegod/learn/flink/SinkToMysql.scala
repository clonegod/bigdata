package clonegod.learn.flink

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._

object SinkToMysql {

  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 读取数据源
    val inputStream = env.readTextFile("/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/sensor.txt")

    // 转换数据
    val dataStream:DataStream[SensorReading] = inputStream.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0), dataArray(1).toLong, dataArray(2).toDouble)
    })

    // 将结果输出到mysql
    dataStream.addSink(new MyJDBCSink())

    dataStream.print()

    env.execute("sink to mysql")
  }


  /**
   * 自定义RichSinkFunction - 将结果写入MYSQL
   */
  class MyJDBCSink extends RichSinkFunction[SensorReading] {
    var conn: Connection  = _
    var insertStmt: PreparedStatement = _
    var updateStmt: PreparedStatement = _

    // 初始化JDBC连接，预编译SQL
    override def open(parameters: Configuration): Unit = {
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123456")
      insertStmt = conn.prepareStatement("insert into t_sensor(sensor_id, temperature) values (?,?)")
      updateStmt = conn.prepareStatement("update t_sensor set temperature = ? where sensor_id = ?")
    }

    // 执行SQL逻辑
    override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
      updateStmt.setDouble(1, value.temperature)
      updateStmt.setString(2, value.id )
      val n = updateStmt.executeUpdate()
      // 如果更新失败，说明不存在，则插入
      if(updateStmt.getUpdateCount == 0) {
        insertStmt.setString(1, value.id)
        insertStmt.setDouble(2, value.temperature)
        insertStmt.executeUpdate()
      }
    }


    // 释放JDBC连接资源
    override def close(): Unit = {
      updateStmt.close()
      insertStmt.close()
      conn.close()
    }
  }

}

