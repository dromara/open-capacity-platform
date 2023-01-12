package com.open.capacity.jpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.HttpProxy;
import cn.jpush.api.JPushClient;

public class JPushClientExt extends JPushClient {

	private final String appId;
	
	public JPushClientExt(String appId, String appKey, String masterSecret) {
		super(masterSecret, appKey);
		this.appId = appId;
	}
	
	public JPushClientExt(String appId, String appKey, String masterSecret, HttpProxy proxy, ClientConfig conf) {
		super(masterSecret, appKey, proxy, conf);
		this.appId = appId;
	}
	
	public String getAppId() {
		return appId;
	}
	
}
