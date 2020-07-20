package clonegod.learn.flink.util;

import com.esotericsoftware.kryo.DefaultSerializer;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.List;
import java.util.Properties;

public class FlinkUtilV2 {

    private static final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    public static StreamExecutionEnvironment getEnv() {
        return env;
    }

    public static <T> DataStream<T> createKafakStream(ParameterTool parameterTool,
                                                      Class<? extends DeserializationSchema> defaultSerializer) throws Exception {

        // 设置全局的参数对象，以便在之后的算子中获取配置参数
        env.getConfig().setGlobalJobParameters(parameterTool);

        // 开启checkpoint，指定保存状态数据的间隔时间
        env.enableCheckpointing(parameterTool.getInt("checkpoint.interval.ms", 5000));
        // 设置checkpoint要支持的模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 取消任务不删除checkpoint数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 设置重启次数和延迟间隔
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                parameterTool.getInt("restart.max.times", 3), parameterTool.getInt("restart.delay.ms", 5000)));
        // StateBackend，建议在flink配置文件中进行统一设置
        env.setStateBackend(new FsStateBackend("file:///tmp/flink_checkpoints"));
        //env.setStateBackend(new FsStateBackend("hdfs://localhost:8020/flink_checkpoints"));

        String topics = parameterTool.getRequired("kafak.topics");
        List<String> topicList = Lists.newArrayList(topics.split(","));

        Properties kafkaProperties = getKafakProperties(parameterTool);

        FlinkKafkaConsumer<T> flinkKafkaConsumer = new FlinkKafkaConsumer<T>(
                topicList,
                defaultSerializer.newInstance(),
                kafkaProperties
        );

        // 保存CheckPoint成功之后，也需要更新偏移量到Kafka的__consumer_offset topic中。
        flinkKafkaConsumer.setCommitOffsetsOnCheckpoints(true);

        // 设置kafkaSource到env
        DataStream<T> dataStream = env.addSource(flinkKafkaConsumer);

        return dataStream;
    }


    /**
     * 比较：earliest 和 latest
     * earliest：当对应partition上没有记录offset，则从最早的那条消息开始消费
     * latest：当对应partition上没有记录offset，则等待新产生的消息进行消息
     *
     * @param parameterTool
     * @return
     */
    private static Properties getKafakProperties(ParameterTool parameterTool) {
        Properties props = new Properties();

        props.setProperty("bootstrap.servers", parameterTool.getRequired("kafak.server.address"));
        props.setProperty("group.id", parameterTool.getRequired("kafak.consumer.group.id"));
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // earliest|latest|none
        props.setProperty("auto.offset.reset", "latest");
        // 消费者不自动提交消息的偏移量
        props.setProperty("enable.auto.commit", "false");

        return props;
    }

}
