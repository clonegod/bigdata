package clonegod.learn.flink.exactlyonce;

import clonegod.learn.flink.util.FlinkUtilV2;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

public class FlinkKafkaSourceToRedis {

    public static void main(String[] args) throws Exception {
        String configPath = "/Users/huangqihang/workspace_bigdata/learn-flink/src/main/resources/app.properties";
        ParameterTool parameterTool = ParameterTool.fromPropertiesFile(configPath);

        DataStream<String> kafakStream = FlinkUtilV2.createKafakStream(parameterTool, SimpleStringSchema.class);

        SingleOutputStreamOperator<Tuple2<String, Integer>> dataStream = process(kafakStream);

        sink(dataStream);

        FlinkUtilV2.getEnv().execute("FlinkKafkaSourceToRedis");
    }

    private static SingleOutputStreamOperator<Tuple2<String, Integer>> process(DataStream<String> dataStream) {

        SingleOutputStreamOperator<Tuple2<String, Integer>> result = dataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String line, Collector<String> collector) throws Exception {
                for (String word : line.split(" ")) {
                    collector.collect(word);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String word) throws Exception {
                return Tuple2.of(word, 1);
            }
        }).keyBy(0).sum(1);

        return result;
    }

    /**
     * 要模拟kafak-to-redis 的Exactly-Once，将reids停掉，再重启redis。
     * 如果重启redis之后，再往kafka写入消息，计算结果是累加之前的历史数据的，那就说明Exactly-Once测试成功！
     */
    private static void sink(DataStream<Tuple2<String, Integer>> result) {
        result.print();

        String hashKey = "kafak_to_redis";

        // 将上游数据转换为自定义RedisSink需要的格式: Tuple3<String, String, String>
        result.map(new MapFunction<Tuple2<String, Integer>, Tuple3<String, String, String>>() {
            @Override
            public Tuple3<String, String, String> map(Tuple2<String, Integer> tuple2) throws Exception {
                return Tuple3.of(hashKey, tuple2.f0, tuple2.f1+"");
            }
        }).addSink(new MyRedisSink());
    }

}
