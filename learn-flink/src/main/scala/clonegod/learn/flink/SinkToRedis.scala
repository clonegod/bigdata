package clonegod.learn.flink

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object SinkToRedis {
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
            SensorReading(dataArray(0),
                          dataArray(1).toLong,
                          dataArray(2).toDouble)
          });

    // 写入数据到redis
    val redisConf = getRedisConfig()
    val redisMapper = getRedisMapper()
    dataStream.addSink(new RedisSink(redisConf, redisMapper))

    // 写入完成后，在redis查询结果:
    // hgetall sensor_data

    env.execute("sink to redis")
  }

  // 定义redis连接配置
  def getRedisConfig():FlinkJedisPoolConfig = {
    new FlinkJedisPoolConfig.Builder()
      .setHost("localhost")
      .setPort(6379)
      .build()
  }

  // 定义redis的写入方式
  def getRedisMapper(): RedisMapper[SensorReading] = {
    new RedisMapper[SensorReading] {
      // 定义保存到redis的命令：hset key field value
      override def getCommandDescription: RedisCommandDescription = {
        new RedisCommandDescription(RedisCommand.HSET, "sensor_data")
      }

      // 设置要写入数据的key。从当前数据对象中取哪个属性作为key
      override def getKeyFromData(t: SensorReading): String = t.id.toString

      // 设置要写入数据的value。从当前数据对象中取哪个属性作为value
      override def getValueFromData(t: SensorReading): String = t.temperature.toString
    }
  }


}
