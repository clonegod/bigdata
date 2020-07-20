package clonegod.learn.flink.util;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.List;
import java.util.Properties;

public class FlinkUtil {

    public static final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    public static DataStream<String> createKafkaSource(String serverUrl, List<String> topics, String groupId) {
        Properties props = getKafkaProperties(serverUrl, groupId);
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), props);
        // Checkpoint 保存成功之后，才向kafka提交消息偏移量
        kafkaConsumer.setCommitOffsetsOnCheckpoints(true);

        return env.addSource(kafkaConsumer);
    }

    private static Properties getKafkaProperties(String serverUrl, String groupId) {
        Properties props = new Properties();
        // "127.0.0.1:9092"
        props.setProperty("bootstrap.servers", serverUrl);
        // "consumer-group-flink"
        props.setProperty("group.id", groupId);
        props.setProperty("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        // earliest|latest|none
        props.setProperty("auto.offset.reset", "latest");
        // 消费者不自动提交消息的偏移量
        props.setProperty("enable.auto.commit", "false");
        return props;
    }

}
