package com.open.capacity.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;


/**
 * Serializer for serialize and deserialize.
 * @author someday
 * @date 2018/5/5
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public interface Serializer {
    /**
     * Encode object into bytes.
     * 
     * @param obj target object
     * @return serialized result
     */
    byte[] serialize(final Object obj) throws SerializationException;

    /**
     * Decode bytes into Object.
     * 
     * @param data serialized data
     * @param classOfT class of original data
     */
    <T> T deserialize(final byte[] data ) throws SerializationException;
}
