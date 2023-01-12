package com.open.capacity.common.encryptbody.advice;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.open.capacity.common.encryptbody.annotation.decrypt.AESDecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.DESDecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.DecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.RSADecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.SMDecryptBody;
import com.open.capacity.common.encryptbody.bean.DecryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.bean.DecryptHttpInputMessage;
import com.open.capacity.common.encryptbody.config.EncryptBodyConfig;
import com.open.capacity.common.encryptbody.enums.DecryptBodyMethod;
import com.open.capacity.common.encryptbody.exception.DecryptBodyFailException;
import com.open.capacity.common.encryptbody.exception.DecryptMethodNotFoundException;
import com.open.capacity.common.encryptbody.factory.DecryptAnnotationInfoBeanFactory;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求数据的加密信息解密处理<br>
 * Body}</strong>
 * 以及package为<strong><code>com.open.capacity</code></strong>下的注解有效
 *
 * @author licoy.cn
 * @version 2018/9/7
 * @see RequestBodyAdvice
 *      code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

	private final EncryptBodyConfig config;

	public DecryptRequestBodyAdvice(EncryptBodyConfig config) {
		this.config = config;
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		if (this.hasDecryptAnnotation(methodParameter.getDeclaringClass())) {
			return true;
		}
		Method method = methodParameter.getMethod();
		if (method != null) {
			if (this.hasDecryptAnnotation(method)) {
				return true;
			}
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> parameterType : parameterTypes) {
				if (this.hasDecryptAnnotation(parameterType)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasDecryptAnnotation(AnnotatedElement annotatedElement) {
		return annotatedElement.isAnnotationPresent(DecryptBody.class)
				|| annotatedElement.isAnnotationPresent(AESDecryptBody.class)
				|| annotatedElement.isAnnotationPresent(DESDecryptBody.class)
				|| annotatedElement.isAnnotationPresent(RSADecryptBody.class)
				|| annotatedElement.isAnnotationPresent(SMDecryptBody.class);
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		String body;
		try {
			body = IoUtil.read(inputMessage.getBody(), config.getEncoding());
		} catch (Exception e) {
			throw new DecryptBodyFailException("Unable to get request body data,"
					+ " please check if the sending data body or request method is in compliance with the specification."
					+ " (无法获取请求正文数据，请检查发送数据体或请求方法是否符合规范。)");
		}
		if (body == null || StrUtil.isEmpty(body)) {
			throw new DecryptBodyFailException("The request body is NULL or an empty string, so the decryption failed."
					+ " (请求正文为NULL或为空字符串，因此解密失败。)");
		}
		Class<?> targetTypeClass;
		try {
			targetTypeClass = Class.forName(targetType.getTypeName());
		} catch (ClassNotFoundException e) {
			throw new DecryptBodyFailException(e.getMessage());
		}
		String decryptBody = null;
		DecryptAnnotationInfoBean methodAnnotation = this.getDecryptAnnotation(parameter.getMethod());
		if (methodAnnotation != null) {
			decryptBody = switchDecrypt(body, methodAnnotation);
		} else if (this.hasDecryptAnnotation(targetTypeClass)) {

			DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(targetTypeClass);
			if (classAnnotation != null) {
				decryptBody = switchDecrypt(body, classAnnotation);
			}
		} else {
			DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(parameter.getDeclaringClass());
			if (classAnnotation != null) {
				decryptBody = switchDecrypt(body, classAnnotation);
			}
		}
		if (decryptBody == null) {
			throw new DecryptBodyFailException(
					"Decryption error, " + "please check if the selected source data is encrypted correctly."
							+ " (解密错误，请检查选择的源数据的加密方式是否正确。)");
		}
		try {
			return new DecryptHttpInputMessage(IoUtil.toStream(decryptBody, config.getEncoding()),
					inputMessage.getHeaders());
		} catch (Exception e) {
			throw new DecryptBodyFailException("The string is converted to a stream format exception."
					+ " Please check if the format such as encoding is correct." + " (字符串转换成流格式异常，请检查编码等格式是否正确。)");
		}
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	/**
	 * 获取解密注解的数据
	 *
	 * @param annotatedElement 注解元素
	 * @return 解密注解组装数据
	 */
	private DecryptAnnotationInfoBean getDecryptAnnotation(AnnotatedElement annotatedElement) {
		return DecryptAnnotationInfoBeanFactory.getDecryptAnnotationInfoBean(annotatedElement);
	}

	/**
	 * 选择加密方式并进行解密
	 *
	 * @param formatStringBody 目标解密字符串
	 * @param infoBean         加密信息
	 * @return 解密结果
	 */
	private String switchDecrypt(String formatStringBody, DecryptAnnotationInfoBean infoBean) {
		DecryptBodyMethod method = infoBean.getDecryptBodyMethod();
		if (method == null) {
			throw new DecryptMethodNotFoundException();
		}
		String decrypt = DecryptBodyMethod.valueOf(method.toString()).getDecryptHandler().decrypt(infoBean, config,
				formatStringBody);
		;

		return decrypt;

	}
}
