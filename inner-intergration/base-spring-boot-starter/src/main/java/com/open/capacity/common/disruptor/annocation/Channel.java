package com.open.capacity.common.disruptor.annocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author someday
 * 管道定义注解，用于处理消费者
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Channel {
    String value();
    
    /**
	 * 值越低,优先级越高
	 *
	 * @return
	 */
	int order() default LOWEST_ORDER;

	int LOWEST_ORDER = Integer.MAX_VALUE;
	int HIGHEST_ORDER = Integer.MIN_VALUE;
}
