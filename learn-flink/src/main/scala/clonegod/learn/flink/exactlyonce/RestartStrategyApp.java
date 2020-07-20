package clonegod.learn.flink.exactlyonce;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RestartStrategyApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.setStateBackend(new FsStateBackend("file:///tmp/RestartStrategyApp"));
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        // 外部触发程序异常
        env.socketTextStream("localhost", 7777).map(new MapFunction<String, String>() {
            @Override
            public String map(String line) throws Exception {
                System.out.println(line);
                if(line.contains("restart")) throw  new RuntimeException("收到重启命令");
                return line;
            }
        }).print();

        env.execute("RestartStretegyApp");
    }
}
