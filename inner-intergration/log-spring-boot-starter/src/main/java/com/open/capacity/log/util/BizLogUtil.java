package com.open.capacity.log.util;

import org.slf4j.MDC;

import com.open.capacity.log.monitor.BizLog;

import lombok.experimental.UtilityClass;

/**
 * logback tag 业务日志隔离
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@UtilityClass
public class BizLogUtil {

	private static final String FLAG = "tag" ;
	public void info(String tag, String message, Object... params) {
		MDC.put(FLAG, tag);
		BizLog.info(message, params);
		MDC.remove(tag);
	}

	public void debug(String tag, String message, Object... params) {
		MDC.put(FLAG, tag);
		BizLog.debug(message, params);
		MDC.remove(tag);
	}

	public void trace(String tag, String message, Object... params) {
		MDC.put(FLAG, tag);
		BizLog.trace(message, params);
		MDC.remove(FLAG);
	}

}
