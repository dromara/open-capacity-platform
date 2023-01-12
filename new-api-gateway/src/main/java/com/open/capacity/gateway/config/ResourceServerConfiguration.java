package com.open.capacity.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;

import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.gateway.auth.CustomAuthenticationManager;
import com.open.capacity.gateway.auth.CustomServerWebExchangeMatchers;
import com.open.capacity.gateway.auth.CustomerAccessManager;
import com.open.capacity.gateway.error.CustomerSecurityProblemSupport;
import com.open.capacity.gateway.handler.Oauth2AuthSuccessHandler;

/**
 * 资源服务器配置
 *
 * @author zlt
 * @date 2019/10/5
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@SuppressWarnings("all")
@Import(CustomerSecurityProblemSupport.class)
public class ResourceServerConfiguration {
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private CustomerAccessManager customerAccessManager;
    
    @Autowired
    private  CustomerSecurityProblemSupport problemSupport;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //认证处理器
        ReactiveAuthenticationManager customAuthenticationManager = new CustomAuthenticationManager(tokenStore);
        //token转换器
        ServerBearerTokenAuthenticationConverter tokenAuthenticationConverter = new ServerBearerTokenAuthenticationConverter();
        tokenAuthenticationConverter.setAllowUriQueryParameter(true);
        //oauth2认证过滤器
        AuthenticationWebFilter oauth2Filter = new AuthenticationWebFilter(customAuthenticationManager);
        oauth2Filter.setServerAuthenticationConverter(tokenAuthenticationConverter);
        oauth2Filter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(problemSupport));
        oauth2Filter.setAuthenticationSuccessHandler(new Oauth2AuthSuccessHandler());
        oauth2Filter.setRequiresAuthenticationMatcher(new CustomServerWebExchangeMatchers(securityProperties));
        http.addFilterAt(oauth2Filter, SecurityWebFiltersOrder.AUTHENTICATION);

        
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchange = http.authorizeExchange();
        if (securityProperties.getAuth().getHttpUrls().length > 0) {
            authorizeExchange.pathMatchers(securityProperties.getAuth().getHttpUrls()).authenticated();
        }
        if (securityProperties.getIgnore().getUrls().length > 0) {
            authorizeExchange.pathMatchers(securityProperties.getIgnore().getUrls()).permitAll();
        }
        authorizeExchange
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange()
                    .access(customerAccessManager)
                .and()
                    .exceptionHandling()
                    	// 无权限访问处理器
                        .accessDeniedHandler( problemSupport)
                        //验证失败处理器
                        .authenticationEntryPoint(problemSupport)
                .and()
                    .headers()
                        .frameOptions()
                        .disable()
                .and()
                    .httpBasic().disable()
                    .csrf().disable();
        return http.build();
    }
}
