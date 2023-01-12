package com.open.capacity.redis.serializer.kryo;

import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.open.capacity.redis.serializer.Serializer;

/**
 * Serializer for serialize and deserialize.
 * 
 * @author someday
 * @date 2018/5/5 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class KryoSerializer implements Serializer {

	private static final int BUFFER_SIZE = 4096;
	private static final int MAX_BUFFERED_SIZE = 10240;
	private static final ThreadLocal<Kryo> KRYOS = ThreadLocal.withInitial(Kryo::new);

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		try (Output output = new Output(BUFFER_SIZE, MAX_BUFFERED_SIZE);) {
			final Kryo kryo = KRYOS.get();
			kryo.register(Object.class);
			kryo.writeClassAndObject(output, obj);
			return output.toBytes();
		}
	}

	@Override
	public <T> T deserialize(byte[] data) throws SerializationException {
		try (Input input = new Input(data);) {
			final Kryo kryo = KRYOS.get();
			return (T) kryo.readClassAndObject(input);
		}
	}
}
