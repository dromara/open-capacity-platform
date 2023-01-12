package com.open.capacity.common.mail;


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.open.capacity.common.mail.core.JavaMailTemplate;
import com.open.capacity.common.mail.core.MailTemplate;

/**
 * 邮件配置
 *  @someday
 *  @2018-12-22
 */
@Configuration
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class MailAutoConfiguration {

	@Bean
	@ConditionalOnBean({MailProperties.class, JavaMailSender.class})
	public MailTemplate mailTemplate(JavaMailSender mailSender,MailProperties mailProperties) {
		return new JavaMailTemplate(mailSender,mailProperties);
	}
}