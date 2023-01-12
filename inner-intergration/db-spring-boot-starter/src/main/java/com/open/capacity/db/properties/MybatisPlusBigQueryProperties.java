package com.open.capacity.db.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * mybatis-plus 配置
 *
 * @author someday
 * @date 2020/4/5
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "ocp.mybatis-plus.result-set-threshold")
@RefreshScope
public class MybatisPlusBigQueryProperties {
    /**
     * 是否开启大查询监控
     */
    private Boolean enabled = true;
    /**
     * 最大阈值
     */
    private Integer size = 1000;
}
