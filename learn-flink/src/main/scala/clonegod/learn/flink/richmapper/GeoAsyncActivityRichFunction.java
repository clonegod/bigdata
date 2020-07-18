package clonegod.learn.flink.richmapper;

import org.apache.commons.io.IOUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

// 异步非阻塞的Http请求
public class GeoAsyncActivityRichFunction extends RichAsyncFunction<ActivityBean, ActivityBean> {

  private CloseableHttpAsyncClient httpAsyncClient = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
        httpAsyncClient = HttpAsyncClients.custom().setMaxConnTotal(20).setDefaultRequestConfig(requestConfig).build();
        httpAsyncClient.start();
    }

    @Override
    public void asyncInvoke(ActivityBean in, ResultFuture<ActivityBean> resultFuture) {
        String locationQueryUrl = "http://www.baidu.com?";
        HttpGet httpGet = new HttpGet(locationQueryUrl);
        Future<HttpResponse> futureHttpRes = httpAsyncClient.execute(httpGet, null);

        CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse res = futureHttpRes.get();
                // 解析响应结果，获取经纬度对应的地址信息
                return parseRes(res, in);
            } catch (Exception e) {
                return "";
            }
        }).thenAccept((location) -> resultFuture.complete(Collections.singleton(
            new ActivityBean(in.uid, in.aid, in.eventTime, in.eventType,
                    in.longitude, in.latitude, in.activityName, location)
        )));
    }

    private String parseRes(HttpResponse httpRes, ActivityBean bean) throws Exception {
        String content = IOUtils.toString(httpRes.getEntity().getContent());
        //System.out.println(content);
        if(bean.longitude >= 100) return "北京市";
        return "深圳市";
    }

    @Override
    public void close() throws Exception {
        super.close();
        httpAsyncClient.close();
    }

}