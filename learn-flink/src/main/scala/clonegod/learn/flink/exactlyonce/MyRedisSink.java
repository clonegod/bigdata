package clonegod.learn.flink.exactlyonce;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyRedisSink extends RichSinkFunction<Tuple3<String,String,String>> {

    private JedisPool jedisPool;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ParameterTool globalJobParameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

        JedisPoolConfig poolConfig =new JedisPoolConfig();
        poolConfig.setMaxTotal(globalJobParameters.getInt("redis.pool.max.total", 10));
        poolConfig.setMaxIdle(globalJobParameters.getInt("redis.pool.max.idle", 1));
        poolConfig.setTestWhileIdle(true);

        String host = globalJobParameters.get("redis.host", "127.0.0.1");
        int port = globalJobParameters.getInt("redis.post", 6379);
        int connectionTimeout = globalJobParameters.getInt("redis.timeout.ms", 3000);
        jedisPool = new JedisPool(poolConfig, host, port, connectionTimeout);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @Override
    public void invoke(Tuple3<String, String, String> value, Context context) throws Exception {
        Jedis jedis = getJedis();

        if(! jedis.isConnected()) {
            jedis.connect();
        }

        jedis.hset(value.f0, value.f1, value.f2);

        jedis.close();
    }

    @Override
    public void close() throws Exception {
        super.close();
        jedisPool.close();
    }
}
