package com.open.capacity.uaa.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.uaa.common.store.CustomRedisTokenStore;

/**
 * 认证服务器使用Redis存取令牌
 * 注意: 需要配置redis参数
 *
 * @author someday
 * @date 2018/7/25 9:36
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "ocp.oauth2.token.store", name = "type", havingValue = "redis", matchIfMissing = true)
public class DefaultRedisTokenStoreConfig {
    @Bean
    public TokenStore tokenStore(RedisConnectionFactory connectionFactory, SecurityProperties securityProperties, RedisSerializer<Object> redisValueSerializer) {
        return new CustomRedisTokenStore(connectionFactory, securityProperties, redisValueSerializer);
    }
}
