package clonegod.learn.flink.richmapper;


import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class MyRedisSink implements RedisMapper<ActivityBean> {

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.HSET, "city_activity_count");
    }

    @Override
    public String getKeyFromData(ActivityBean activityBean) {
        return activityBean.city + "_" + activityBean.aid + "_" + activityBean.eventType;
    }

    @Override
    public String getValueFromData(ActivityBean activityBean) {
        return activityBean.count + "";
    }
}
