package com.open.capacity.common.encryptbody.advice;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.encryptbody.annotation.encrypt.AESEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.DESEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.EncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.MD5EncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.RSAEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.SHAEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.SMEncryptBody;
import com.open.capacity.common.encryptbody.bean.EncryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.config.EncryptBodyConfig;
import com.open.capacity.common.encryptbody.enums.EncryptBodyMethod;
import com.open.capacity.common.encryptbody.exception.EncryptBodyFailException;
import com.open.capacity.common.encryptbody.exception.EncryptMethodNotFoundException;
import com.open.capacity.common.encryptbody.factory.EncryptAnnotationInfoBeanFactory;
import com.open.capacity.common.encryptbody.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 响应数据的加密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 * 或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 * 以及package为<strong><code>com.open.capacity</code></strong>下的注解有效
 *
 * @author licoy.cn
 * @version 2018/9/4
 * @see ResponseBodyAdvice
 *      code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {

	private final ObjectMapper objectMapper;

	private final EncryptBodyConfig config;

	@Autowired
	public EncryptResponseBodyAdvice(ObjectMapper objectMapper, EncryptBodyConfig config) {
		this.objectMapper = objectMapper;
		this.config = config;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {
		Class<?> declaringClass = returnType.getDeclaringClass();
		if (this.hasEncryptAnnotation(declaringClass)) {
			return true;
		}
		Method method = returnType.getMethod();
		if (method != null) {
			Class<?> returnValueType = method.getReturnType();
			return this.hasEncryptAnnotation(method) || this.hasEncryptAnnotation(returnValueType);
		}
		return false;
	}

	private boolean hasEncryptAnnotation(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return false;
		}
		return annotatedElement.isAnnotationPresent(EncryptBody.class)
				|| annotatedElement.isAnnotationPresent(AESEncryptBody.class)
				|| annotatedElement.isAnnotationPresent(DESEncryptBody.class)
				|| annotatedElement.isAnnotationPresent(RSAEncryptBody.class)
				|| annotatedElement.isAnnotationPresent(MD5EncryptBody.class)
				|| annotatedElement.isAnnotationPresent(SHAEncryptBody.class)
				|| annotatedElement.isAnnotationPresent(SMEncryptBody.class);
	}

	@Override
	public String beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (body == null) {
			return null;
		}
		String str = CommonUtils.convertToStringOrJson(body, objectMapper);
		response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
		Method method = returnType.getMethod();
		if (method != null) {
			// 从方法上
			EncryptAnnotationInfoBean methodAnnotation = this.getEncryptAnnotation(method);
			if (methodAnnotation != null) {
				return switchEncrypt(str, methodAnnotation);
			}
			// 从方法返回值上
			Class<?> methodReturnType = method.getReturnType();
			EncryptAnnotationInfoBean returnTypeClassAnnotation = this.getEncryptAnnotation(methodReturnType);
			if (returnTypeClassAnnotation != null) {
				return switchEncrypt(str, returnTypeClassAnnotation);
			}
		}
		// 从声明类上
		EncryptAnnotationInfoBean classAnnotation = this.getEncryptAnnotation(returnType.getDeclaringClass());
		if (classAnnotation != null) {
			return switchEncrypt(str, classAnnotation);
		}
		throw new EncryptBodyFailException();
	}

	/**
	 * 获取加密注解的数据
	 *
	 * @param annotatedElement 注解元素
	 * @return 加密注解组装数据
	 */
	private EncryptAnnotationInfoBean getEncryptAnnotation(AnnotatedElement annotatedElement) {
		return EncryptAnnotationInfoBeanFactory.getEncryptAnnotationInfoBean(annotatedElement);
	}

	/**
	 * 选择加密方式并进行加密
	 * 
	 * @param formatStringBody 目标加密字符串
	 * @param infoBean         加密信息
	 * @return 加密结果
	 */
	private String switchEncrypt(String formatStringBody, EncryptAnnotationInfoBean infoBean) {
		EncryptBodyMethod method = infoBean.getEncryptBodyMethod();
		if (method == null) {
			throw new EncryptMethodNotFoundException();
		}
		String encrypt = EncryptBodyMethod.valueOf(method.toString()).getEncryptHandler().encrypt(infoBean, config,
				formatStringBody);
		return encrypt;
	}

}
