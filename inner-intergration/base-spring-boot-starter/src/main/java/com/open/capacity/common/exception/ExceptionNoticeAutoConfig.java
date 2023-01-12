package com.open.capacity.common.exception;

import com.open.capacity.common.properties.BlackListProperties;
import com.open.capacity.common.properties.ExceptionNoticeProperties;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.common.properties.TokenStoreProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ ExceptionNoticeProperties.class})
public class ExceptionNoticeAutoConfig {
}