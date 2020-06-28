package clonegod.learn.flink

import com.sun.deploy.util.ParameterUtil
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
 * 1、先启动nc: nc -lk 7777
 * 2、args启动参数：--host localhost --port 7777
 */
object WordCountStream {
  def main(args: Array[String]): Unit = {
    // 创建流处理执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 最大并行度设置的值，不能超过flink管理页面上显示的"Total Task Slots"，否则会因为资源不够导致任务无法正常启动执行
    //env.setParallelism(1)
    //env.setMaxParallelism(16)

    // 读取启动参数
    val params = ParameterTool.fromArgs(args)
    val host = params.get("host") // localhost
    val port = params.getInt("port") // 7777

    // 接受的数据源是一个流
    // 命令行启动nc发送实时消息，提供流数据：nc 7777 -lk
    val inputDataStream: DataStream[String] =
          env.socketTextStream(host, port)

    import org.apache.flink.api.scala._
    val resultDataStream = inputDataStream
      .flatMap(_.split("\\s+"))
      .filter(_.nonEmpty)
      .map( (_,1) )
      .keyBy(0) // 按元组的第一个元素分组
      .sum(1) // 对元组的第二个元素进行sum

    // 输出结果设置为1个线程执行，就不会输出线程号了
    resultDataStream.print().setParallelism(1)

    // 启动任务，分配任务到执行worker
    env.execute("stream word count job")

  }
}
