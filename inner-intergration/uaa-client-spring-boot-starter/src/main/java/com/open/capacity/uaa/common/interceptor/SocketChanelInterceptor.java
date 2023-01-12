package com.open.capacity.uaa.common.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.uaa.common.util.AuthUtils;

import lombok.AllArgsConstructor;

/**
* 功能:频道拦截器,类似管道,获取消息的一些meta数据
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@SuppressWarnings("all")
@AllArgsConstructor
public class SocketChanelInterceptor implements ChannelInterceptor {

	/**
	 * 实际消息发送到频道之前调用
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String token = accessor.getFirstNativeHeader(CommonConstant.ACCESS_TOKEN);
			if (token == null) {
				throw new IllegalArgumentException("抱歉，您没有访问权限");
			}
			String username = AuthUtils.checkAccessToken(token).getUsername();
			if (username != null) {
				accessor.setUser(SecurityContextHolder.getContext().getAuthentication());
			} else {
				throw new IllegalArgumentException("抱歉，您没有访问权限");
			}
		} else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
			String topic = accessor.getDestination().toString();
			Authentication authentication = (Authentication) accessor.getUser();

			boolean result = checkPermission(authentication, topic);

			if (!result) {
				// 这里返回一个自定义提示消息，由于没有指令和相关参数，所以不会真正执行订阅
				Message<String> msg = new Message<String>() {
					@Override
					public String getPayload() {
						return "主题订阅失败";
					}
					@Override
					public MessageHeaders getHeaders() {
						return null;
					}
				};
				return msg;
				// 如果抛出异常，则会导致前端面页连接也会断开，不断重连
				// throw new IllegalArgumentException("抱歉，没有权限订阅该主题");
			}
		} else if (StompCommand.SEND.equals(accessor.getCommand())) {
			String topic = accessor.getDestination().toString();
			Authentication authentication = (Authentication) accessor.getUser();
			boolean result = checkPermission(authentication, topic);

			if (!result) {
				throw new IllegalArgumentException("抱歉，没有权限发送消息到该主题");
			}
		}
		return message;
	}

	/**
	 * 验证当前用户是否有权限订阅主题
	 */
	private boolean checkPermission(Authentication authentication, String topic) {
		
		SysUser user = AuthUtils.getUser(authentication) ;
		 
		return true;
	}

}
