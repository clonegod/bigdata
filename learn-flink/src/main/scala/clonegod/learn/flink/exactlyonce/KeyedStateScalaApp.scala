package clonegod.learn.flink.exactlyonce

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala._

object KeyedStateScalaApp {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.enableCheckpointing(5000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    env.setStateBackend(new FsStateBackend("file:///Users/huangqihang/workspace_bigdata/learn-flink/target/flink_checkpoint"))
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 3000))

    val lines = env.socketTextStream("localhost", 7777)
    val keyed = lines.flatMap(_.split(" ")).map(word => {
      if(word.contains("error")) throw new RuntimeException("input error")
      (word, 1)
    }).keyBy(0)

    val wordCountStream = keyed.mapWithState((input:(String, Int), state:Option[Int]) => {
      state match {
        case Some(count) => {
          val key = input._1
          val value = input._2
          val newValue = value + count
          ((key, newValue), Some(newValue))
        }
        case None => {
          (input, Some(input._2))
        }
      }
    })

    wordCountStream.print()

    env.execute("KeyedStateScalaApp")
  }
}
