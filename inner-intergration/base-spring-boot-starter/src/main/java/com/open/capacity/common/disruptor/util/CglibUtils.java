package com.open.capacity.common.disruptor.util;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class CglibUtils {

    public static Class<?> filterCglibProxyClass(Class<?> clazz) {
        while (isCglibProxyClass(clazz)) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

}
