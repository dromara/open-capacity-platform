package com.open.capacity.uaa.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * 认证服务器使用数据库存取令牌
 *
 * @author someday
 * @date 2018/7/24 16:23
 */
@Configuration
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "ocp.oauth2.token.store", name = "type", havingValue = "db")
public class DefaultDbTokenStoreConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore(){
    	//oauth_access_token oauth_refresh_token 创建两张表
        return new JdbcTokenStore(dataSource);
    }
}
