package com.open.capacity.common.properties;

import lombok.Data;

/**
 * 验证码配置
 *
 * @author zlt
 * @date 2018/1/4
 */
@Data
public class ValidateCodeProperties {
    /**
     * 设置认证通时不需要验证码的clientId
     */
    private String[] ignoreClientCode = {};
}
