package com.open.capacity.jpush;

import java.util.LinkedList;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.jiguang.common.connection.HttpProxy;
import lombok.Data;

@Data
@ConfigurationProperties(JPushProperties.PREFIX)
public class JPushProperties {

	public static final String PREFIX = "jpush";

	/**
	 * The KEY of one application on JPush.
	 */
	private String appKey;

	/**
	 *  API access secret of the appKey.
	 */
    private String masterSecret;
    
    /**
     * The proxy, if there is no proxy, should be null.
     */
    private HttpProxy proxy;

    /**
     * 	推送开发还是生产环境(设置ios平台环境，true表示推送生产环境，false表示要推送开发环境)
     */
    private boolean production = true;
    
    /**
     * 	
     */
    private LinkedList<JPushSlaveClientConfig> slaves = new LinkedList<>();

    @Data
    public static class JPushSlaveClientConfig {
    	
    	/**
    	 * The ID of one application on Local System.
    	 */
    	private String appId;
    	
    	/**
    	 * The KEY of one application on JPush.
    	 */
    	private String appKey;

    	/**
    	 *  API access secret of the appKey.
    	 */
        private String appSecret;
        
        /**
         * The proxy, if there is no proxy, should be null.
         */
        private HttpProxy proxy;

        /**
         * 	推送开发还是生产环境(设置ios平台环境，true表示推送生产环境，false表示要推送开发环境)
         */
        private boolean production = true;
    	
    }
    

}