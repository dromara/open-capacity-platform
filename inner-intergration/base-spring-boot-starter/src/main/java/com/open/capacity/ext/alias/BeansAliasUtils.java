/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.alias;

import com.open.capacity.ext.util.SpringBeanUtils;

public class BeansAliasUtils {
    public static final String DEF_GROUP_NAME="def";
    private static String ALIAS_BEAN_NAME_TEMP = "#A:%s:%s";
    public static String createAliasBeanName(String groupName,String alias){
        return String.format(ALIAS_BEAN_NAME_TEMP,groupName,alias);
    }

    public static <T> T getBean(String groupName,String alias,Class<T> clazz){
        return SpringBeanUtils.getBean(createAliasBeanName(groupName,alias),clazz);
    }
    public static <T> T getBean(String groupName,String alias){
        return (T)SpringBeanUtils.getBean(createAliasBeanName(groupName,alias));
    }

    public static <T> T getBean(String alias,Class<T> clazz){
        return SpringBeanUtils.getBean(createAliasBeanName(DEF_GROUP_NAME,alias),clazz);
    }
    public static <T> T getBean(String alias){
        return (T)SpringBeanUtils.getBean(createAliasBeanName(DEF_GROUP_NAME,alias));
    }
}
