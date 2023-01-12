package com.open.capacity.uaa.common.authorize.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.uaa.common.authorize.AuthorizeConfigProvider;

/**
 * @author 作者 owen 
 * @version 创建时间：2018年1月31日 下午9:11:36 类说明 白名单
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Component
@Order(Integer.MAX_VALUE - 1)
public class AuthAuthorizeConfigProvider implements AuthorizeConfigProvider {
	
	@Autowired
	private SecurityProperties securityProperties;
	

	@Override
	public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		// 免token登录设置
		config.antMatchers(securityProperties.getIgnore().getUrls()).permitAll();
		//监控断点放权
		config.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll() ;
		//前后分离时需要带上
		config.antMatchers(HttpMethod.OPTIONS).permitAll();
        //Trace方法拦截
		config.antMatchers(HttpMethod.TRACE).denyAll();
		
		return true;
	}

}
