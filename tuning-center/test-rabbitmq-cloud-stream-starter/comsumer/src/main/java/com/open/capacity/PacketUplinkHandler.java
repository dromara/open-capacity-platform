package com.open.capacity;



import java.io.IOException;

import com.rabbitmq.client.Channel;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public   class PacketUplinkHandler {

    @StreamListener("waterLevelInput")
    public void handleWaterLevelPacket(PacketModel model ,
            @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws  IOException {
        System.out.println("waterLevelInput==========="+model);
        channel.basicAck(deliveryTag, false);
    }
    
 
 
    @StreamListener("temperatureInput")
    public void handleTemperaturePacket(PacketModel model,
            @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws IOException {
    	
    	System.out.println("temperatureInput========="+model);
    	
		System.out.println(1/0);
		
    	channel.basicAck(deliveryTag, false);
    }
    
    
    @StreamListener("temperatureInputError")
    public void handleTemperatureInputError(PacketModel model ) throws IOException {
    	
    	System.out.println("temperatureInputError=========");
    	
    }
 
//    @ServiceActivator(inputChannel = "packetUplinkTopic.temperature.errors")
//    public void handleTemperaturePacketErrors(Message<?> message) {
//    	System.out.println("1111111111111");
//    }
    		
    
    /**
     * 处理全局异常的方法
     * @param errorMessage 异常消息对象
     */
	@StreamListener(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
	public void handleError(ErrorMessage errorMessage) {
		//配置全局error自动流转到私信队列，自定义@ServiceActivator(inputChannel = "packetUplinkTopic.temperature.errors") 的不会
		System.out.println("发生异常. errorMessage = {" + errorMessage + "}");
	}
  
	
	
	
} 