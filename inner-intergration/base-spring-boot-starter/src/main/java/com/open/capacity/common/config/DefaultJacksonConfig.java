package com.open.capacity.common.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * 处理LocalDate、LocalDateTime、Long 三种类型JSON序列化
 **/
@Configuration
public class DefaultJacksonConfig {
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer localDateCustomizer() {
		return builder -> builder.serializerByType(LocalDate.class,
				new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
		return builder -> builder.serializerByType(LocalDateTime.class,
				new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer longCustomizer() {
		return builder -> { 
			builder.serializerByType(Long.class, ToStringSerializer.instance) ;
			builder.modules(new JavaTimeModule());
		};
	}
}
