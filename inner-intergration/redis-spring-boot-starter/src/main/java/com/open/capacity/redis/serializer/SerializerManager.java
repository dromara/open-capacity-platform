package com.open.capacity.redis.serializer;

import com.open.capacity.redis.serializer.fst.FSTRedisSerializer;
import com.open.capacity.redis.serializer.fury.FuryRedisSerializer;
import com.open.capacity.redis.serializer.hessian.HessianSerializer;
import com.open.capacity.redis.serializer.jdk.JdkSerializer;
import com.open.capacity.redis.serializer.kryo.KryoSerializer;
import com.open.capacity.redis.serializer.snappy.SnappyRedisSerializer;

/**
 * Serializer for serialize and deserialize.
 * 
 * @author someday
 * @date 2018/5/5 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class SerializerManager {

	private static Serializer[] serializers = new Serializer[10];

	public static final byte JDK = 0;
	public static final byte SNAPPY_JDK = 1;
	public static final byte HESSIAN2 = 2;
	public static final byte SNAPPY_HESSIAN2 = 3;
	public static final byte FST = 4;
	public static final byte SNAPPY_FST = 5;
	public static final byte KRYO = 6;
	public static final byte SNAPPY_KRYO = 7;
	public static final byte FURY = 8;
	public static final byte SNAPPY_FURY = 9;

	static {
		addSerializer(JDK, new JdkSerializer());
		addSerializer(SNAPPY_JDK, new SnappyRedisSerializer(new JdkSerializer()));
		addSerializer(HESSIAN2, new HessianSerializer());
		addSerializer(SNAPPY_HESSIAN2, new SnappyRedisSerializer(new HessianSerializer()));
		addSerializer(FST, new FSTRedisSerializer());

		addSerializer(SNAPPY_FST, new SnappyRedisSerializer(new FSTRedisSerializer()));
		addSerializer(KRYO, new KryoSerializer());
		addSerializer(SNAPPY_KRYO, new SnappyRedisSerializer(new KryoSerializer()));
		addSerializer(FURY, new FuryRedisSerializer());
		addSerializer(SNAPPY_FURY, new SnappyRedisSerializer(new FuryRedisSerializer()));

	}

	public static Serializer getSerializer(int idx) {
		return serializers[idx];
	}

	public static void addSerializer(int idx, Serializer serializer) {
		if (serializers.length <= idx) {
			Serializer[] newSerializers = new Serializer[idx + 10];
			System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
			serializers = newSerializers;
		}
		serializers[idx] = serializer;
	}
}
