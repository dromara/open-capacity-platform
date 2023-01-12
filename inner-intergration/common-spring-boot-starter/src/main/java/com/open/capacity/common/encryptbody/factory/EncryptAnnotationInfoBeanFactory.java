package com.open.capacity.common.encryptbody.factory;

import java.lang.reflect.AnnotatedElement;

import com.open.capacity.common.encryptbody.annotation.encrypt.AESEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.DESEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.EncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.MD5EncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.RSAEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.SHAEncryptBody;
import com.open.capacity.common.encryptbody.annotation.encrypt.SMEncryptBody;
import com.open.capacity.common.encryptbody.bean.EncryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.enums.EncryptBodyMethod;

/**
 *加密工程
 * 
 * @author licoy.cn
 * @version 2022/3/29
 *          code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class EncryptAnnotationInfoBeanFactory {

	public static EncryptAnnotationInfoBean getEncryptAnnotationInfoBean(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return null;
		}
		if (annotatedElement.isAnnotationPresent(EncryptBody.class)) {
			EncryptBody encryptBody = annotatedElement.getAnnotation(EncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(encryptBody.value())
						.key(encryptBody.otherKey()).shaEncryptType(encryptBody.shaType()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(MD5EncryptBody.class)) {
			return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.MD5).build();
		}
		if (annotatedElement.isAnnotationPresent(SHAEncryptBody.class)) {
			SHAEncryptBody encryptBody = annotatedElement.getAnnotation(SHAEncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.SHA)
						.shaEncryptType(encryptBody.value()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(DESEncryptBody.class)) {
			DESEncryptBody encryptBody = annotatedElement.getAnnotation(DESEncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.DES)
						.key(encryptBody.key()).build();
			}

		}
		if (annotatedElement.isAnnotationPresent(AESEncryptBody.class)) {
			AESEncryptBody encryptBody = annotatedElement.getAnnotation(AESEncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.AES)
						.key(encryptBody.key()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(RSAEncryptBody.class)) {
			RSAEncryptBody encryptBody = annotatedElement.getAnnotation(RSAEncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.RSA)
						.key(encryptBody.key()).rsaKeyType(encryptBody.type()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(SMEncryptBody.class)) {
			SMEncryptBody encryptBody = annotatedElement.getAnnotation(SMEncryptBody.class);
			if (encryptBody != null) {
				return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.SM)
						.key(encryptBody.key()).build();
			}
		}
		return null;
	}
}
