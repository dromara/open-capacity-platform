package com.open.capacity.common.agora;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface AgoraUserIdProvider {

	default String getUserIdByChannel(String appid, String channel)  {
		return channel;
	}
	
	default String getChannelByUserId(String appid, String userId) {
		return userId;
	}

}
