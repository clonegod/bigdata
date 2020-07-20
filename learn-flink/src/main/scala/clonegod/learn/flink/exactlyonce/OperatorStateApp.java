package clonegod.learn.flink.exactlyonce;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * State的分类：
 * 按照是否通过 key 进行分区，分为：OperatorState 和 KeyedState
 *
 * OperatorState:
 *  维护Sources 数据源操作的状态数据，比如从kafka消费的消息在partition上的偏移量
 *
 * KeyedState:
 *  维护与key相关的一种state，只能用于KeyedStateStream类型数据集对应的Functions和Operators之上
 *  其中的key是类似SQL语句中对应的GroupBy/PartitionBy字段
 *  --- GroupBy 的字段，可延伸为一个维度表
 */
public class OperatorStateApp {

    // OperatorState - 记录文件读取的偏移量，这个偏移量就是状态数据
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.setStateBackend(new FsStateBackend("file:///tmp"));
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


        // 并行读取本地文件，实现Exactly-Once
        env.addSource(new MyExactlyOnceParFileSource("/tmp"))
                .setParallelism(2)
                .print();

        env.execute("OperatorStateApp");
    }

    private static class MyExactlyOnceParFileSource extends RichParallelSourceFunction<Tuple2<String, String>>
        implements CheckpointedFunction {

        private volatile boolean runningFlag = true;

        private String path;

        private Long offset = 0L;

        private transient ListState<Long> offsetState;

        public MyExactlyOnceParFileSource(String path) {
            this.path = path; // /tmp/data0.txt
        }

        /**
         * 对OperatorState记录快照： 对文件已读取进度/偏移量记录快照
         */
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            offsetState.clear(); // 清除历史状态数据
            offsetState.add(offset); // 设置新对状态数据
        }

        /**
         * 初始化OperatorState ： 初始化读取文件的偏移量
         */
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            ListStateDescriptor<Long> stateDescriptor = new ListStateDescriptor<Long>(
                    "file_offset_state",
                    //TypeInformation.of(new TypeHint<Long>() {})
                    //Long.class
                    Types.LONG
            );
            offsetState = context.getOperatorStateStore().getListState(stateDescriptor);
        }

        @Override
        public void run(SourceContext<Tuple2<String, String>> context) throws Exception {
            int subTaskIndex = getRuntimeContext().getIndexOfThisSubtask();

            // 让不同的subTask分别读取不同的本地文件
            File file = new File(path + "/data" + subTaskIndex + ".txt");
            if(! file.exists()) file.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // 获取历史状态offset偏移量
            for (Long aLong : offsetState.get()) {
                offset = aLong;
            }

            // 设置随机读取的偏移量
            raf.seek(offset);

            while (runningFlag) {
                String line = raf.readLine();
                if(line != null) {
                    line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                    synchronized (context.getCheckpointLock()) {
                        offset = raf.getFilePointer(); // 更新偏移量
                        context.collect(Tuple2.of(subTaskIndex + "", line));
                    }
                } else {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        }

        @Override
        public void cancel() {
            runningFlag = false;
        }
    }



}
