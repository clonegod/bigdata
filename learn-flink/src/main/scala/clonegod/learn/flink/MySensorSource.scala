package clonegod.learn.flink

import java.lang
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

/**
 * 自定义Flink的输入数据源
 *
 * - 测试Flink数据处理逻辑时，可以模拟生产环境的数据
 */
class MySensorSource extends SourceFunction[SensorReading] {

  var running = true

  /**
   * 停止数据源的调用入口
   */
  override def cancel(): Unit = running = false

  /**
   * 提供生产数据的逻辑
   */
  override def run(sourceContext: SourceFunction.SourceContext[SensorReading]): Unit = {
    while(running) {
      var curTemp = 1.to(10).map(
        i => sourceContext.collect(
          SensorReading(s"sensor#$i", System.currentTimeMillis(), getTemperature())
        )
      )
      TimeUnit.SECONDS.sleep(3)
    }
  }

  def getTemperature():Double = {
    val rand = new Random()
    val str = new DecimalFormat("0.00").format(rand.nextDouble() * 50)
    new lang.Double(str)
  }
}
