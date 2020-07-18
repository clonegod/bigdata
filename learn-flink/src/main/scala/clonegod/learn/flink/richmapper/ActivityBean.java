package clonegod.learn.flink.richmapper;

public class ActivityBean {
    public String uid;
    public String aid;
    public String eventTime;
    public Integer eventType;
    public Double longitude;
    public Double latitude;
    public String activityName;
    public String city;
    public Integer count = 1;

    public ActivityBean() {
    }

    public ActivityBean(String uid, String aid, String eventTime, Integer eventType, Double longitude, Double latitude, String activityName, String city) {
        this.uid = uid;
        this.aid = aid;
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.activityName = activityName;
        this.city = city;
    }

    @Override
    public String toString() {
        return "ActivityBean{" +
                "uid='" + uid + '\'' +
                ", aid='" + aid + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", eventType=" + eventType +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", activityName='" + activityName + '\'' +
                ", city='" + city + '\'' +
                ", count=" + count +
                '}';
    }
}