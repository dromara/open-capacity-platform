package com.open.capacity.redis.serializer.fury;

import org.springframework.data.redis.serializer.SerializationException;

import com.open.capacity.redis.serializer.Serializer;

import io.fury.Fury;
import io.fury.Language;
import io.fury.ThreadSafeFury;
import io.fury.serializer.CompatibleMode;

/**
 * Serializer for serialize and deserialize.
 * 
 * @author someday
 * @date 2018/5/5 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class FuryRedisSerializer implements Serializer {
	private static ThreadSafeFury fury = Fury.builder().withLanguage(Language.JAVA)
			// 开启共享引用/循环引用支持，不需要的话建议关闭，性能更快
			.withRefTracking(true)
			// 允许序列化未注册类型
			.requireClassRegistration(false)
			.withCompatibleMode(CompatibleMode.SCHEMA_CONSISTENT)
			// 开启异步多线程编译
			.withAsyncCompilationEnabled(true).buildThreadSafeFury();

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		return fury.serialize(obj);
	}

	@Override
	public <T> T deserialize(byte[] data) throws SerializationException {
		return (T) fury.deserialize(data);
	}
}
