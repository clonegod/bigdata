package clonegod;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.scala.DataStream;
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


public class StreamWorldCount {

//    public static void main(String[] args) {
//        // 创建执行环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
//
//        // 使用StreamExecutionEnvironment创建DataStream
//        // 连接Source
//        DataStream<String> lineStream = env.socketTextStream("localhost", 8888);
//
//        // Transformation
//        lineStream.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public void flatMap(String line, Collector<String> collector) throws Exception {
//                for (String word : line.split(" ")) {
//                    collector.collect(word);
//                }
//            }
//        });
//
//        // 调用Sink，输出结果
//
//
//        env.execute("StreamWorldCount");
//    }
}
