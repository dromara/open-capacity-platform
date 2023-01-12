package com.open.capacity.uaa.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.open.capacity.uaa.common.interceptor.SocketChanelInterceptor;



/**
 * webSocket鉴权配置
 * @author owen
 * @version 1.0
 * @date 2018/5/8
 * blog: https://blog.51cto.com/13005375 
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@ConditionalOnClass(WebSocketMessage.class)
@EnableWebSocketMessageBroker
public class DefaultWebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private SocketChanelInterceptor socketChanelInterceptor;
	
 
	
	@Bean
	public SocketChanelInterceptor socketChanelInterceptor(  ) {
		return new SocketChanelInterceptor(   );
	}

	/**
	 * 注册stomp的端点
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 客户端连接端点
		registry.addEndpoint("/websocket")
				// .addInterceptors(new HttpHandShakeInterceptor())
				.setAllowedOriginPatterns("*").withSockJS();
	}

	/**
	 * 配置信息代理
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		registry.enableSimpleBroker("/topic/", "/queue/");
		registry.setUserDestinationPrefix("/queue/");
		registry.setApplicationDestinationPrefixes("/app");

//    	rabbitmq配置
//        registry.enableStompBrokerRelay("/topic/","/queue/")
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("guest")
//                .setClientPasscode("guest")
//                .setVirtualHost("/");
//        registry.setApplicationDestinationPrefixes("/app");
	}

	/**
	 * 配置客户端入站通道拦截器，用于传递从WebSocket客户端接收到的消息
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(socketChanelInterceptor);
	}

	/**
	 * 配置客户端出站通道拦截器，用于向WebSocket客户端发送服务器消息
	 */
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		// registration.interceptors(socketChanelInterceptor);
	}
}
