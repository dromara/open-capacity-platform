/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.feign;

import lombok.Data;

import java.util.List;

@Data
public class FeignUrlResetProperties {
    public static String FEIGN_URL_RESET_PREFIX="hill4j.feign.reset";
    private List<FeignUrlResetConfig> configs;

    public enum ResetType{
        NAME("name","feign接口应用名称"),
        CONTEXT_ID("contextId","feign接口contextId"),
        PACKAGE("package","feign接口所在包名");
        public final String type;
        public final String desc;

        ResetType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }
}
