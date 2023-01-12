package com.open.capacity.redis.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

// 此时定义的序列化操作表示可以序列化所有类的对象，当然，这个对象所在的类一定要实现序列化接口
@SuppressWarnings("unchecked")
public class RedisObjectSerializer implements RedisSerializer<Object> {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0]; // 做一个空数组，不是null

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		if (obj == null) { // 这个时候没有要序列化的对象出现，所以返回的字节数组应该就是一个空数组
			return EMPTY_BYTE_ARRAY;
		}
		return SerializerManager.getSerializer(SerializerManager.SNAPPY_FST).serialize(obj); // 将对象变为字节数组
	}

	@Override
	public Object deserialize(byte[] data) throws SerializationException {
		if (data == null || data.length == 0) { // 此时没有对象的内容信息
			return null;
		}
		return SerializerManager.getSerializer(SerializerManager.SNAPPY_FST).deserialize(data);

	}

	public static RedisSerializer<Object> customer() {
		return new RedisObjectSerializer();
	}

}