package clonegod.learn.flink.exactlyonce;

import clonegod.learn.flink.util.FlinkUtil;
import jdk.nashorn.internal.codegen.types.Type;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class KeyedStateApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.env;

        // 配置checkpoint 和重启策略
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.setStateBackend(new FsStateBackend("file:///Users/huangqihang/workspace_bigdata/learn-flink/target/flink_checkpoint"));
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 5000));

        // KafkaSource
        DataStream<String> kafaSource = FlinkUtil.createKafkaSource(
                "localhost:9092",
                Lists.newArrayList("flink_topic_KeyedStateApp"),
                "flink-g1");

        // transformation
        SingleOutputStreamOperator wordStream = kafaSource.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                for (String word : s.split(" ")) {
                    collector.collect(word);
                }
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOne = wordStream.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return Tuple2.of(s, 1);
            }
        });

        KeyedStream<Tuple2<String, Integer>, Tuple> keyedStream = wordAndOne.keyBy(0);

        // Sink
        keyedStream.print();



        /** 自定义对keyedStream的操作，模拟实现sum聚合的功能 */
        keyedStream.map(new CusKeyedStateMap())
                .print();


        env.execute("KeyedStateApp");
    }


    private static class CusKeyedStateMap extends RichMapFunction<Tuple2<String, Integer>, Tuple2<String, Integer>> {
        private ValueState<Tuple2<String,Integer>> valueState;
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            // 定义状态描述器，初始化或恢复历史状态数据
            String stateName = "word_sum_keyed_state";
            //TypeInformation valueType = Types.TUPLE(Types.STRING, Types.INT);
            TypeInformation<Tuple2<String, Integer>> valueType = TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {});
            ValueStateDescriptor<Tuple2<String, Integer>> descriptor = new ValueStateDescriptor<>(stateName, valueType);
            valueState = getRuntimeContext().getState(descriptor);
        }

        @Override
        public Tuple2<String, Integer> map(Tuple2<String, Integer> value) throws Exception {
            // 输入的数据

            // 历史状态数据
            Tuple2<String, Integer> stateValue = valueState.value();

            // 计算
            Integer newValue = null;
            if(stateValue == null) {
                // 首次，历史状态为空
                valueState.update(value);
                return value;
            } else {
                // 更新状态
                stateValue.f1 += value.f1;
                valueState.update(stateValue);
                return stateValue;
            }
        }

        @Override
        public void close() throws Exception {
            super.close();
        }
    }


}
