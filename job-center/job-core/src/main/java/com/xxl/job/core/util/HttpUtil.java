package com.xxl.job.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.AdminBiz;

/**
 * @author someday
 */
public class HttpUtil {

    // 执行器执行接口
    public final static String executorApi = "/v1/executor/api";

    public final static String adminList = "/v1/executor/list";

    public final static String callBackApi = AdminBiz.MAPPING;

    /**
     * json to invoke
     * @param url address
     * @param regJson json object
     * @return invoke result
     * @throws Exception
     */
    public static String sendHttpPost(String url, Object regJson) throws Exception {
        String str = JSON.toJSONString(regJson);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String type = "application/json";

        return result(url, type, new StringEntity(str, "UTF-8"), httpClient);
    }

    /**
     * from param to invoke
     * @param url address
     * @param map param
     * @return invoke result
     * @throws Exception
     */
    public static String postFromParam(String url, Map<String, String> map) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        List<NameValuePair> basicNameValuePairList = new ArrayList<>(2);
        for (String key : map.keySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, map.get(key).toString());
            basicNameValuePairList.add(basicNameValuePair);
        }

        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(basicNameValuePairList);
        String type = "application/x-www-form-urlencoded;charset=UTF-8";

        return result(url, type, formEntity, httpClient);
    }

    /**
     * remote operation
     * @param url address
     * @param httpEntity request body
     * @param httpClient http client
     * @return remote result
     * @throws IOException
     */
    private static String result(String url, String type, HttpEntity httpEntity, CloseableHttpClient httpClient) throws IOException {
        // 设置请求和传输超时时间

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000) // 连接超时
				.setConnectionRequestTimeout(5000)// 请求超时
				.setSocketTimeout(5000).setRedirectsEnabled(true) // 允许自动重定向
				.build();
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", type);
        httpPost.setEntity(httpEntity);
        httpPost.setConfig(requestConfig);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");

        response.close();
        httpClient.close();
        return responseContent;
    }
}
