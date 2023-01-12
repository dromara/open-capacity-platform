package com.open.capacity.uaa.common.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.open.capacity.common.constant.CommonConstant;

import lombok.SneakyThrows;

/**
* 自定义异常序列化
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@SuppressWarnings("all")
public class OAuth2ExceptionSerializer extends StdSerializer<DefaultOAuth2Exception> {

	public OAuth2ExceptionSerializer() {
		super(DefaultOAuth2Exception.class);
	}

	@Override
	@SneakyThrows
	public void serialize(DefaultOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) {
		gen.writeStartObject();
		gen.writeObjectField(CommonConstant.STATUS, CommonConstant.FAIL);
		gen.writeStringField("msg", value.getMessage());
		gen.writeStringField("data", value.getErrorCode());
		gen.writeEndObject();
	}
}
