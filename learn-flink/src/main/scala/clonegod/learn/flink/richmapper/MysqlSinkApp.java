package clonegod.learn.flink.richmapper;

import clonegod.learn.flink.util.FlinkUtil;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MysqlSinkApp {
    public static void main(String[] args) throws Exception {
        String kafkaServers = "localhost:9092";
        List<String> topics = Lists.newArrayList("flink_topic");
        String groupId = "flink_group01";

        DataStream dataStream = FlinkUtil.createKafkaSource(kafkaServers, topics, groupId);

        dataStream = dataStream.map(new ActivityRichFunction());

        dataStream = AsyncDataStream.unorderedWait(dataStream, new GeoAsyncActivityRichFunction(), 3000, TimeUnit.SECONDS, 100);

        // 统计不同城市的活动事件的发生次数
        dataStream = dataStream.keyBy("city", "aid", "eventType").sum("count");

        dataStream.print();

        dataStream.addSink(new MysqlSink());

        FlinkUtil.env.execute("MysqlSinkApp");
    }

}