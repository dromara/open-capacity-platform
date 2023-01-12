package com.open.capacity.redis.serializer.jdk;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.open.capacity.redis.serializer.Serializer;

/**
 * Serializer for serialize and deserialize.
 * @author someday
 * @date 2018/5/5
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class JdkSerializer implements Serializer {

	private JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		return jdkSerializationRedisSerializer.serialize(obj);
	}

	@Override
	public <T> T deserialize(byte[] data) throws SerializationException {
		return (T) jdkSerializationRedisSerializer.deserialize(data);
	}

}
