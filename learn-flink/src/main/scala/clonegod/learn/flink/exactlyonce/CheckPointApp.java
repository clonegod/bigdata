package clonegod.learn.flink.exactlyonce;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * FlinK Exactly-Once机制：
 *  Exactly-Once指的是Flink保证Source数据源只被"正确处理一次"。
 *  当从Kafka消费一条消息时，只有当Sink操作也成功执行，Flink才会确认这条消息被消费。
 *  如果在算子的计算过程中，或者Sink操作中出现失败，则Flink不会确认这条消息。
 *  在修复程序bug之后，重启Job的执行时，通过checkPoint恢复到最近一次的状态数据，继续执行任务。
 *  此时，Flink会重新拉去kafka上未被确认消费的消息，再次进行处理。。。
 *
 *
 * Checkpoint：JobManager定时通知TaskManager对内存中对数据进行快照保存
 * State：OperatorState, KeyedState
 * StateBackend：State状态数据的后端存储方式和存储路径
 * RestartStrategy：实现故障发生时任务的自动恢复
 *
 * 1、中间数据/状态数据的定时保存：元数据（kafka消息的偏移量等），中间计算结果（subtask计算得到的结果）
 * 2、StateBackend 提供状态数据的存储。存储系统可以选择：内存、本地文件、hdfs分布式文件系统、RocksDB+hdfs实现增量式保存等
 * 3、任务失败之后，再次启动任务时，需要设置SavePoint从哪个路径下加载之前保存的checkPoint状态数据，从而恢复到上一次的状态。
 */
public class CheckPointApp {
    public static void main(String[] args) throws  Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /** 配置开启CheckPointing */
        // 1、开启CheckPointing(只有开启了Checkpoint，才会有重启策略)
        env.enableCheckpointing(10000); // 10s
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        // 2、设置checkpoint的存储方式：内存（默认）、文件系统、hdfs
        // 建议通过flink配置文件统一设置checkPoint的存储方式和路径
        //env.setStateBackend(new FsStateBackend("file:///Users/huangqihang/workspace_bigdata/learn-flink/target/flink_checkpoint"));
        //env.setStateBackend(new FsStateBackend("hdfs://localhost:8020/flink_checkpoint"));

        // 3、程序异常退出或者任务被cancel的时候，是否删除checkpoint
        // RETAIN_ON_CANCELLATION - 保留历史的Checkpoint
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        /** 配置RestartStrategy */
        // 4、默认的重启策略：固定延迟时间的无限次重启
        // 设置重启策略：最多重启3次，每次重启延迟5s
        // 前3次发生错误，flink会都静默处理掉异常。当超过配置当重试次数之后，程序抛出异常并结束。
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 5000));

        // ============================================================================= //
        // Source
        DataStream<String> dataStream = env.socketTextStream("localhost", 7777);

        // Transformation
        DataStream<Tuple2<String,Integer>> wordAndOne = dataStream.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String word) throws Exception {
                if(word.contains("x")) throw new RuntimeException("bad input");
                return Tuple2.of(word, 1);
            }
        });

        DataStream result = wordAndOne.keyBy(0).sum(1);

        // Sink
        result.print();

        env.execute("CheckPointApp");

    }
}
