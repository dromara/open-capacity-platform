package com.open.capacity.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import lombok.Data;

/**
 * Token配置
 *
 * @author someday
 * @version 1.0
 * @date 2018/5/19
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@ConfigurationProperties(prefix = "ocp.oauth2.token.store")
@RefreshScope
public class TokenStoreProperties {
    /**
     * token存储类型(redis/db/jwt)
     */
    private String type = "redis";
}
