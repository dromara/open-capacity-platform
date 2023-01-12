package com.open.capacity.redis.serializer.snappy;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.redis.serializer.SerializationException;
import org.xerial.snappy.Snappy;

import com.open.capacity.redis.serializer.Serializer;

/**
 * Serializer for serialize and deserialize.
 * @author someday
 * @date 2018/5/5
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class SnappyRedisSerializer implements Serializer {

	private Serializer innerSerializer;

	public SnappyRedisSerializer() {

	}

	public SnappyRedisSerializer(Serializer innerSerializer) {
		this.innerSerializer = innerSerializer;
	}

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		try {
			byte[] bytes = innerSerializer != null ? innerSerializer.serialize(obj)
					: SerializationUtils.serialize((Serializable) obj);
			return Snappy.compress(bytes);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public <T> T deserialize(byte[] data) throws SerializationException {
		try {
			if (data == null || data.length <= 0) {
				return null;
			}
			byte[] bos = Snappy.uncompress(data);
			return (T) (innerSerializer != null ? innerSerializer.deserialize(bos)
					: SerializationUtils.deserialize(bos));
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

}
