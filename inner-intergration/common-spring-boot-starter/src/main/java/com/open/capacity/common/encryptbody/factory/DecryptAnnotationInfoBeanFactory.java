package com.open.capacity.common.encryptbody.factory;

import java.lang.reflect.AnnotatedElement;

import com.open.capacity.common.encryptbody.annotation.decrypt.AESDecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.DESDecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.DecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.RSADecryptBody;
import com.open.capacity.common.encryptbody.annotation.decrypt.SMDecryptBody;
import com.open.capacity.common.encryptbody.bean.DecryptAnnotationInfoBean;
import com.open.capacity.common.encryptbody.enums.DecryptBodyMethod;

/**
 * 解密工厂
 * 
 * @author licoy.cn
 * @version 2022/3/29
 *          code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class DecryptAnnotationInfoBeanFactory {

	public static DecryptAnnotationInfoBean getDecryptAnnotationInfoBean(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return null;
		}
		if (annotatedElement.isAnnotationPresent(DecryptBody.class)) {
			DecryptBody decryptBody = annotatedElement.getAnnotation(DecryptBody.class);
			if (decryptBody != null) {
				return DecryptAnnotationInfoBean.builder().decryptBodyMethod(decryptBody.value())
						.key(decryptBody.otherKey()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(DESDecryptBody.class)) {
			DESDecryptBody decryptBody = annotatedElement.getAnnotation(DESDecryptBody.class);
			if (decryptBody != null) {
				return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.DES)
						.key(decryptBody.key()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(AESDecryptBody.class)) {
			AESDecryptBody decryptBody = annotatedElement.getAnnotation(AESDecryptBody.class);
			if (decryptBody != null) {
				return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.AES)
						.key(decryptBody.key()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(RSADecryptBody.class)) {
			RSADecryptBody decryptBody = annotatedElement.getAnnotation(RSADecryptBody.class);
			if (decryptBody != null) {
				return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.RSA)
						.key(decryptBody.key()).rsaKeyType(decryptBody.type()).build();
			}
		}
		if (annotatedElement.isAnnotationPresent(SMDecryptBody.class)) {
			SMDecryptBody decryptBody = annotatedElement.getAnnotation(SMDecryptBody.class);
			if (decryptBody != null) {
				return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.SM)
						.key(decryptBody.key()).build();
			}
		}

		return null;
	}
}
