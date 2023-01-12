package com.open.capacity.jpush;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import cn.jiguang.common.DeviceType;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;

public class JPushTemplate implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(JPushTemplate.class);
    private JPushClient jPushClient;
    private ConcurrentHashMap<String, JPushClientExt> jPushClientMap;
    private boolean production;

    public JPushTemplate(JPushClient jPushClient, List<JPushClientExt> clients, boolean prod) {
        this.jPushClient = jPushClient;
        this.jPushClientMap = new ConcurrentHashMap<>();
		for (JPushClientExt jPushClientExt : clients) {
			this.jPushClientMap.put(jPushClientExt.getAppId(), jPushClientExt);
		}
        this.production = prod;
    }

    public JPushClient getjPushClient() {
        return jPushClient;
    }
    
	public Map<String, JPushClientExt> getjPushClientMap() {
		return jPushClientMap;
	}
    
    public boolean sendPush(PushObject pushObject) {
		return this.sendPush(Audience.all(), pushObject);
    }
    
    public boolean sendPush(List<String> alias, PushObject pushObject) {
		return this.sendPush(Audience.alias(alias), pushObject);
    }
    
    public boolean sendPushByTag(List<String> tags, PushObject pushObject) {
		return this.sendPush(Audience.tag(tags), pushObject);
	}
    
    public boolean sendPush(Audience audience, PushObject pushObject) {
    	
        PushPayload payload = JPushNotifications.buildPushPayloadForAndroidAndIos(production, audience, pushObject);
        try {
            PushResult result = jPushClient.sendPush(payload);
            return result.isResultOK();
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        }
        return false;
    }
    
    public boolean sendPush(String appId, PushObject pushObject) {
		return this.sendPush(appId, Audience.all(), pushObject);
    }
    
    public boolean sendPush(String appId, List<String> alias, PushObject pushObject) {
		return this.sendPush(appId, Audience.alias(alias), pushObject);
    }
    
    public boolean sendPushByTag(String appId, List<String> tags, PushObject pushObject) {
		return this.sendPush(appId, Audience.tag(tags), pushObject);
	}
    
    public boolean sendPush(String appId, Audience audience, PushObject pushObject) {
    	JPushClient jPushClient = jPushClientMap.get(appId);
    	if(Objects.nonNull(jPushClient)) {
    		PushPayload payload = JPushNotifications.buildPushPayloadForAndroidAndIos(production, audience, pushObject);
	        try {
	            PushResult result = jPushClient.sendPush(payload);
	            return result.isResultOK();
	        } catch (APIConnectionException e) {
	            LOG.error("Connection error. Should retry later. ", e);
	            LOG.error("Sendno: " + payload.getSendno());
	        } catch (APIRequestException e) {
	            LOG.error("Error response from JPush server. Should review and fix it. ", e);
	            LOG.info("HTTP Status: " + e.getStatus());
	            LOG.info("Error Code: " + e.getErrorCode());
	            LOG.info("Error Message: " + e.getErrorMessage());
	            LOG.info("Msg ID: " + e.getMsgId());
	            LOG.error("Sendno: " + payload.getSendno());
	        }
    	}
        return false;
    }

    public void clearAlias(String alias) {
        try {
            jPushClient.deleteAlias(alias, DeviceType.Android.value());
            jPushClient.deleteAlias(alias, DeviceType.IOS.value());
        } catch (APIConnectionException e) {
            LOG.error("清理Alias异常", e);
        } catch (APIRequestException e) {
            LOG.error("清理Alias异常", e);
        }
    }
    
    public void clearAlias(String appId, String alias) {
        try {
        	JPushClient jPushClient = jPushClientMap.get(appId);
        	if(Objects.nonNull(jPushClient)) {
        		jPushClient.deleteAlias(alias, DeviceType.Android.value());
            	jPushClient.deleteAlias(alias, DeviceType.IOS.value());
        	}
        } catch (APIConnectionException e) {
            LOG.error("清理Alias异常", e);
        } catch (APIRequestException e) {
            LOG.error("清理Alias异常", e);
        }
    }

	@Override
	public void destroy() throws Exception {
		try {
			jPushClient.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (JPushClientExt jPushClientExt : jPushClientMap.values()) {
			try {
				jPushClientExt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
}
