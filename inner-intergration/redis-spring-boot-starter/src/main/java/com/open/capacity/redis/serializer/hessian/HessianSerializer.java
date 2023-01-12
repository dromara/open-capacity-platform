package com.open.capacity.redis.serializer.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.data.redis.serializer.SerializationException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.open.capacity.redis.serializer.Serializer;

import lombok.Cleanup;
import lombok.SneakyThrows;


/**
 * Serializer for serialize and deserialize.
 * @author someday
 * @date 2018/5/5
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("unchecked")
public class HessianSerializer implements Serializer {

    private SerializerFactory serializerFactory = new SerializerFactory();
   
    @Override
    @SneakyThrows
    public byte[] serialize(Object obj) throws SerializationException {
    	@Cleanup  ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(obj);
            
        } catch (IOException e) {
            throw new SerializationException("IOException occurred when Hessian serializer encode!", e);
        }finally {
        	try {
        		if(output!=null) {
        			output.close();
        		}
			} catch (IOException e) {
			}
		}
        return byteArray.toByteArray();
    }

   
    @Override
    @SneakyThrows
    public <T> T deserialize(byte[] data ) throws SerializationException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject = null ;
        try {
            resultObject = input.readObject();
        } catch (IOException e) {
            throw new SerializationException("IOException occurred when Hessian serializer decode!", e);
        }finally {
        	try {
        		if(input!=null) {
        			input.close();
        		}
			} catch (IOException e) {
			}
		}
        return (T) resultObject;
    }

}
