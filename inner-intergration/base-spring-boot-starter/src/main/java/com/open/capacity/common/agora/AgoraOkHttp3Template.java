package com.open.capacity.common.agora;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.agora.interactive.resp.AgoraResponse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttp3 常规请求模板
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
@Slf4j
public class AgoraOkHttp3Template implements InitializingBean {

	public final static String APPLICATION_JSON_VALUE = "application/json";
	public final static String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
	public final static MediaType APPLICATION_JSON = MediaType.parse(APPLICATION_JSON_VALUE);
	public final static MediaType APPLICATION_JSON_UTF8 = MediaType.parse(APPLICATION_JSON_UTF8_VALUE);

	protected OkHttpClient okhttp3Client;
	protected ObjectMapper objectMapper;
	protected AgoraProperties agoraProperties;

	public AgoraOkHttp3Template(OkHttpClient okhttp3Client, ObjectMapper objectMapper, AgoraProperties agoraProperties) {
		this.okhttp3Client = okhttp3Client;
		this.objectMapper = objectMapper;
		this.agoraProperties = agoraProperties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 请求编码，默认：UTF-8
		// String charset = properties.getProperty(HTTP_CHARSET, "UTF-8");
		if (okhttp3Client == null) {
			// 1.创建OkHttpClient对象
			okhttp3Client = new OkHttpClient().newBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS)
					// .hostnameVerifier(okhttpHostnameVerifier)
					// .followRedirects(properties.isFollowRedirects())
					// .followSslRedirects(properties.isFollowSslRedirects())
					.pingInterval(1, TimeUnit.MILLISECONDS).readTimeout(3000, TimeUnit.MILLISECONDS)
					.retryOnConnectionFailure(true)
					// .sslSocketFactory(trustedSSLSocketFactory, trustManager)
					.writeTimeout(3, TimeUnit.SECONDS)
					// Application Interceptors、Network Interceptors :
					// https://segmentfault.com/a/1190000013164260
					// .addNetworkInterceptor(loggingInterceptor)
					// .addInterceptor(headerInterceptor)
					.build();
		}
	}

	/**
	 * restful请求认证
	 */
	private String getAuthorizationHeader() {
		// 1、拼接客户 ID 和客户密钥并使用 base64 编码
		String plainCredentials = agoraProperties.getLoginKey() + ":" + agoraProperties.getLoginSecret();
		String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
		// 2、创建 authorization header
		return "Basic " + base64Credentials;
	}

	public <T extends AgoraResponse> T post(String url,  Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.POST, null, null, null, rtClass);
	}

	public <T extends AgoraResponse> T post(String url, Map<String, Object> params, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.POST, null, params, null, rtClass);
	}

	public <T extends AgoraResponse> T post(String url, Map<String, Object> headers, Map<String, Object> params, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.POST, headers, params, null, rtClass);
	}

	public <T extends AgoraResponse> T post(String url, Map<String, Object> headers, Map<String, Object> params, Map<String, Object> bodyContent, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.POST, headers, params, bodyContent, rtClass);
	}

	public <T extends AgoraResponse> T get(String url, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.GET, null, null, null, rtClass);
	}

	public <T extends AgoraResponse> T get(String url, Map<String, Object> params, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.GET, null, params, null, rtClass);
	}

	public <T extends AgoraResponse> T get(String url, Map<String, Object> headers, Map<String, Object> params, Class<T> rtClass) throws IOException {
		return this.doRequest(url, HttpMethod.GET, headers, params, null, rtClass);
	}

	public <T extends AgoraResponse> T doRequest(
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Map<String, Object> bodyContent,
			Class<T> rtClass) throws IOException {
		long startTime = System.currentTimeMillis();
		// 1.创建Request对象，设置一个url地址,设置请求方式。
		HttpUrl httpUrl = this.getHttpUrl(url, queryParams);
		return this.doRequest(startTime, httpUrl, method, headers, bodyContent, rtClass);
	}

	public <T extends AgoraResponse> T doRequest(
			long startTime,
			HttpUrl httpUrl,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> bodyContent,
			Class<T> rtClass) throws IOException {
		// 2.创建一个call对象,参数就是Request请求对象
		Response response = this.doRequest(startTime, httpUrl, method, headers, bodyContent);
		T res = null;
		try {
			if (response.isSuccessful()) {
				String body = response.body().string();
				res = this.readValue(body, rtClass);
				res.setCode(response.code());
			} else {
				res = BeanUtils.instantiateClass(rtClass);
				res.setCode(response.code());
			}
		} catch (Exception e) {
			log.error("Agora >> Async Request Error : {}, use time : {}", e.getMessage(), System.currentTimeMillis() - startTime);
			res = BeanUtils.instantiateClass(rtClass);
		}
		return res;
	}

	public Response doRequest(
			String url,
			HttpMethod method) throws IOException {
		return this.doRequest(url, method, null);
	}

	public Response doRequest(
			String url,
			HttpMethod method,
			Map<String, Object> queryParams) throws IOException {
		return this.doRequest(url, method, null, queryParams);
	}

	public Response doRequest(
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams) throws IOException {
		return this.doRequest(url, method, headers, queryParams, null);
	}

	public Response doRequest(
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Map<String, Object> bodyContent) throws IOException {
		long startTime = System.currentTimeMillis();
		return this.doRequest(startTime, url, method, headers, queryParams, bodyContent);
	}

	public Response doRequest(
			long startTime,
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Map<String, Object> bodyContent) throws IOException {
		// 1.创建Request对象，设置一个url地址,设置请求方式。
		HttpUrl httpUrl = this.getHttpUrl(url, queryParams);
		return this.doRequest(startTime, httpUrl, method, headers, bodyContent);
	}

	public Response doRequest(
			long startTime,
			HttpUrl httpUrl,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> bodyContent) throws IOException {
		// 1、创建Request.Builder对象
		Request.Builder builder = this.createRequestBuilder(httpUrl, method, headers, bodyContent);
		// 2.创建一个call对象, 参数就是Request请求对象
		try {
			Response response = okhttp3Client.newCall(builder.build()).execute();
			if (response.isSuccessful()) {
				log.info("Agora >> Request Success : code : {}, use time : {} ", response.code(), System.currentTimeMillis() - startTime);
			} else {
				log.error("Agora >> Request Failure : code : {}, message : {}, use time : {} ", response.code(), response.message(), System.currentTimeMillis() - startTime);
			}
			return response;
		} catch (Exception e) {
			log.error("OkHttp3 Request Error : {}, use time : {}", e.getMessage(), System.currentTimeMillis() - startTime);
		}
		return null;
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			String url,
			HttpMethod method,
			Consumer<T> success,
			Class<T> rtClass) throws IOException {
		this.doAsyncRequest(url, method, success, null, rtClass);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			String url,
			HttpMethod method,
			Consumer<T> success,
			BiFunction<Call, IOException, Boolean> failure,
			Class<T> rtClass) throws IOException {
		this.doAsyncRequest(url, method, null, success, failure, rtClass);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			String url,
			HttpMethod method,
			Map<String, Object> queryParams,
			Consumer<T> success,
			BiFunction<Call, IOException, Boolean> failure,
			Class<T> rtClass) throws IOException {
		this.doAsyncRequest(url, method, null, queryParams, success, failure, rtClass);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Consumer<T> success,
			BiFunction<Call, IOException, Boolean> failure,
			Class<T> rtClass) throws IOException {
		this.doAsyncRequest(url, method, headers, queryParams, null, success, failure, rtClass);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Map<String, Object> bodyContent,
			Consumer<T> success,
			BiFunction<Call, IOException, Boolean> failure,
			Class<T> rtClass) throws IOException {
		long startTime = System.currentTimeMillis();
		// 1.创建Request对象，设置一个url地址,设置请求方式。
		HttpUrl httpUrl = this.getHttpUrl(url, queryParams);
		this.doAsyncRequest(startTime, httpUrl, method, headers, bodyContent, success, failure, rtClass);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			long startTime,
			HttpUrl httpUrl,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> bodyContent,
			Consumer<T> success,
			BiFunction<Call, IOException, Boolean> failure,
			Class<T> rtClass) throws IOException {
		// 2.创建一个call对象,参数就是Request请求对象
		this.doAsyncRequest(startTime, httpUrl, method, headers, bodyContent, (call, response) -> {
			T res;
			try {
				if (response.isSuccessful()) {
					String body = response.body().string();
					res = this.readValue(body, rtClass);
					res.setCode(response.code());
				} else {
					res = BeanUtils.instantiateClass(rtClass);
					res.setCode(response.code());
				}
			} catch (Exception e) {
				log.error("Agora >> Async Request Error : {}, use time : {}", e.getMessage(), System.currentTimeMillis() - startTime);
				res = BeanUtils.instantiateClass(rtClass);
			}
			success.accept(res);
			return res;
		}, failure);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			long startTime,
			String url,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> queryParams,
			Map<String, Object> bodyContent,
			BiFunction<Call, Response, T> success,
			BiFunction<Call, IOException, Boolean> failure) throws IOException {
		// 1.创建Request对象，设置一个url地址,设置请求方式。
		HttpUrl httpUrl = this.getHttpUrl(url, queryParams);
		this.doAsyncRequest(startTime, httpUrl, method, headers, bodyContent, success, failure);
	}

	public <T extends AgoraResponse> void doAsyncRequest(
			long startTime,
			HttpUrl httpUrl,
			HttpMethod method,
			Map<String, Object> headers,
			Map<String, Object> bodyContent,
			BiFunction<Call, Response, T> success,
			BiFunction<Call, IOException, Boolean> failure) throws IOException {
		// 1、创建Request.Builder对象
		Request.Builder builder = this.createRequestBuilder(httpUrl, method, headers, bodyContent);
		// 2.创建一个call对象,参数就是Request请求对象
		okhttp3Client.newCall(builder.build()).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				log.error("Agora >> Async Request Failure : {}, use time : {} ", e.getMessage(), System.currentTimeMillis() - startTime);
				if (Objects.nonNull(failure)) {
					failure.apply(call, e);
				}
			}

			@Override
			public void onResponse(Call call, Response response) {
				if (response.isSuccessful()) {
					log.info("Agora >> Async Request Success : code : {}, use time : {} ", response.code(), System.currentTimeMillis() - startTime);
				} else {
					log.error("Agora >> Async Request Failure : code : {}, message : {}, use time : {} ", response.code(), response.message(), System.currentTimeMillis() - startTime);
				}
				if (Objects.nonNull(success)) {
					success.apply(call, response);
				}
			}

		});
	}

	public HttpUrl getHttpUrl(String httpUrl, Map<String, Object> params) {
		log.info("Agora >> Request Url : {}", httpUrl);
		HttpUrl.Builder urlBuilder = HttpUrl.parse(httpUrl).newBuilder();
		if (CollectionUtils.isEmpty(params)) {
			return urlBuilder.build();
		}
		if (!CollectionUtils.isEmpty(params)) {
			log.info("Agora >> Request Params : {}", params);
			Iterator<Entry<String, Object>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				urlBuilder.addQueryParameter(entry.getKey(), Objects.isNull(entry.getValue()) ? "" : entry.getValue().toString());
			}
		}
		return urlBuilder.build();
	}

	public Request.Builder createRequestBuilder(HttpUrl httpUrl,
												  HttpMethod method,
												  Map<String, Object> headers,
												  Map<String, Object> bodyContent) throws IOException{
		log.info("Agora >> Request Query Url : {} , Method : {}", httpUrl.query() , method.getName());
		// 1、创建Request.Builder对象
		Request.Builder builder = new Request.Builder().url(httpUrl);
		// 2、添加请求头
		String authorizationHeader = getAuthorizationHeader();
		log.info("Agora >> Request Authorization : {}", authorizationHeader);
		builder = builder.header("Authorization", authorizationHeader)
				.header("Content-Type", APPLICATION_JSON_VALUE);
		if(Objects.nonNull(headers)) {
			log.info("Agora >> Request Headers : {}", headers);
			for (Entry<String, Object> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		// 3、添加请求体
		if(Objects.nonNull(bodyContent)) {
			String bodyStr = objectMapper.writeValueAsString(bodyContent);
			log.info("Agora >> Request Body : {}", bodyStr);
			builder = method.apply(builder, bodyStr);
		} else {
			builder = method.apply(builder);
		}
		return builder;
	}

	public <T extends AgoraResponse> T readValue(String json, Class<T> cls) {
		try {
			return JSONObject.parseObject(json, cls);
			//return objectMapper.readValue(json, cls);
		} catch (Exception e) {
			log.error(e.getMessage());
			return BeanUtils.instantiateClass(cls);
		}
	}

	public static enum HttpMethod {

		/**
		 * get request.
		 */
		GET("GET", (builder, bodyStr)->{
			return builder.get();
		}),
		/**
		 * head request.
		 */
		HEAD("HEAD", (builder, bodyStr)->{
			return builder.head();
		}),
		/**
		 * post request.
		 */
		POST("POST", (builder, bodyStr)->{
			return builder.post(RequestBody.create(APPLICATION_JSON_UTF8, bodyStr));
		}),
		/**
		 * put request.
		 */
		PUT("PUT", (builder, bodyStr)->{
			return builder.put(RequestBody.create(APPLICATION_JSON_UTF8, bodyStr));
		}),
		/**
		 * patch request.
		 */
		PATCH("PATCH", (builder, bodyStr)->{
			return builder.patch(RequestBody.create(APPLICATION_JSON_UTF8, bodyStr));
		}),
		/**
		 * delete request.
		 */
		DELETE("DELETE", (builder, bodyStr)->{
			return StringUtils.hasText(bodyStr) ? builder.delete(RequestBody.create(APPLICATION_JSON_UTF8, bodyStr)) : builder.delete();
		}),
		/**
		 * options request.
		 */
		OPTIONS("OPTIONS", (builder, bodyStr)->{
			return builder;
		}),
		/**
		 * trace request.
		 */
		TRACE("TRACE", (builder, bodyStr)->{
			return builder;
		});

		private String name;
		private BiFunction<Request.Builder, String, Request.Builder> function;

		HttpMethod(String name, BiFunction<Request.Builder, String, Request.Builder> function) {
			this.name = name;
			this.function = function;
		}

		public String getName() {
			return name;
		}

		public Request.Builder apply(Request.Builder builder, String bodyStr){
			return function.apply(builder, bodyStr);
		}

		public Request.Builder apply(Request.Builder builder){
			return function.apply(builder, null);
		}

		public static HttpMethod getByName(int name) {
			for (HttpMethod type : HttpMethod.values()) {
				if (type.getName().equals(name)) {
					return type;
				}
			}
			return null;
		}

	}


}
