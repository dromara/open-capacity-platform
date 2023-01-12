package com.open.capacity.redis.serializer.fst;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.SerializationException;

import com.open.capacity.redis.serializer.Serializer;

/**
 * Serializer for serialize and deserialize.
 * 
 * @author someday
 * @date 2018/5/5 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class FSTRedisSerializer implements Serializer {
	private static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();
	
//	static {
//		 fstConfiguration.setForceSerializable(true);
//	}

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		return fstConfiguration.asByteArray(obj);
	}

	@Override
	public <T> T deserialize(byte[] data) throws SerializationException {
		return (T) fstConfiguration.asObject(data);
	}
}
