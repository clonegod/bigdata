package clonegod.learn.flink.richmapper;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

class MysqlSink extends RichSinkFunction<ActivityBean> {
    private transient Connection conn = null;
    private transient PreparedStatement pstm = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useSSL=false&characterEncoding=UTF-8", "root", "123456");
        // 建立唯一索引: city + aid + event_type
        // 无则插入，有则更新
        pstm = conn.prepareStatement("INSERT INTO t_activity_count (aid, event_type, city, count) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE count = ?");
    }

    @Override
    public void invoke(ActivityBean value, Context context) throws Exception {
        pstm.setString(1, value.aid);
        pstm.setInt(2, value.eventType);
        pstm.setString(3, value.city);
        pstm.setInt(4, value.count);
        pstm.setInt(5, value.count); // for update

        pstm.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        super.close();
        if(pstm != null) pstm.close();
        if(conn != null) conn.close();
    }
}