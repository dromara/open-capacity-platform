package com.open.capacity.jpush;

import java.util.Map;

import com.google.gson.JsonObject;

import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;

public class JPushNotifications {

	public static Notification buildNotification(Object alert, PlatformNotification... notifications) {
		Notification.Builder builder = Notification.newBuilder().setAlert(alert);
		for (PlatformNotification platformNotification : notifications) {
			builder = builder.addPlatformNotification(platformNotification);
		}
		return builder.build();
    }

    public static PushPayload buildPushPayloadForAndroidAndIos(boolean production, Audience audience,PushObject pushObject) {
        AndroidNotification.Builder androidBuilder = AndroidNotification.newBuilder();
        IosNotification.Builder iosBuilder = IosNotification.newBuilder();
        if (pushObject.getExtras() != null && pushObject.getExtras().size() > 0) {
            for (Map.Entry<String, Object> entry : pushObject.getExtras().entrySet()) {
                if (entry.getValue() instanceof Number) {
                    Number value = (Number) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof String) {
                    String value = (String) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof Boolean) {
                    Boolean value = (Boolean) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof JsonObject) {
                    JsonObject value = (JsonObject) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else {
                    //ignore ...
                }
            }
        }
        
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios()) //推送平台
                .setAudience(audience) //推送目标
                .setNotification(Notification.newBuilder()
                        .setAlert(pushObject.getAlert()) //通知信息
                        .addPlatformNotification(androidBuilder.build())
                        .addPlatformNotification(iosBuilder
                                .incrBadge(1) //角标数字加 1
                                .setSound(pushObject.getSound()) //通知声音为 "happy"
                                .build())
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(pushObject.getMsgContent()) //消息内容
                        .build())
                .setOptions(Options.newBuilder()
                        //设置ios平台环境，true表示推送生产环境，false表示要推送开发环境
                        .setApnsProduction(production)
                        .build())
                .build();
        return payload;
    }
	
}
