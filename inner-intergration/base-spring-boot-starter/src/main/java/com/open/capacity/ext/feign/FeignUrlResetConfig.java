/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.feign;

import lombok.Data;

@Data
public class FeignUrlResetConfig {
    private String type;
    private String value;
    private String newurl;
}