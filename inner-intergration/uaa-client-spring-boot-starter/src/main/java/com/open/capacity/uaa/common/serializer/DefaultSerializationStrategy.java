package com.open.capacity.uaa.common.serializer;

import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import com.open.capacity.redis.serializer.SerializerManager;

/**
 * 序列化策略
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform  
 */
@SuppressWarnings("all")
public class DefaultSerializationStrategy extends StandardStringSerializationStrategy {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0]; // 做一个空数组，不是null
	@Override
	@SuppressWarnings("unchecked")
	protected <T> T deserializeInternal(byte[] data, Class<T> clazz) {
		if (data == null || data.length == 0) { // 此时没有对象的内容信息
			return null;
		}
		return (T) SerializerManager.getSerializer(SerializerManager.SNAPPY_FST).deserialize(data);
	}

	@Override
	protected byte[] serializeInternal(Object obj) {
		if (obj == null) { // 这个时候没有要序列化的对象出现，所以返回的字节数组应该就是一个空数组
			return EMPTY_BYTE_ARRAY;
		}
		return SerializerManager.getSerializer(SerializerManager.SNAPPY_FST).serialize(obj);
	}
}