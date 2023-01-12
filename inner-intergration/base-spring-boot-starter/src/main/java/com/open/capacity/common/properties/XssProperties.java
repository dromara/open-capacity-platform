package com.open.capacity.common.properties;

import lombok.Data;

@Data
public class XssProperties {
	 /**
     * 是否开启xss保护
     */
    private Boolean enable = false;
}
