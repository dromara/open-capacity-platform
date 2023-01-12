package com.open.capacity.common.utils;

import java.util.Locale;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

 
/**
 * @author someday
 * @date 2018/1/6
 * spring security 异常国际化
 */
public class MessageSourceUtil extends ReloadableResourceBundleMessageSource {
	 
	public MessageSourceUtil() {
		setBasename("messages/messages");
		setDefaultLocale(Locale.CHINA);
	}

	public static MessageSourceAccessor getAccessor() {
		return new MessageSourceAccessor(new MessageSourceUtil());
	}

}
