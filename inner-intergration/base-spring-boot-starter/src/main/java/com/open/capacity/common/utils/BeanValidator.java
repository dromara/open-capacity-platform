package com.open.capacity.common.utils;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections4.MapUtils;

import lombok.experimental.UtilityClass;

/**
 * @author 作者 owen
 * @version 创建时间：2017年12月29日 下午2:13:07 hibernate-validator校验工具类
 *          参考文档：http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/
 *          类说明
 */
@UtilityClass
@SuppressWarnings("all")
public class BeanValidator {

	private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

	public <T> Map<String, String> validate(T t, Class... groups) {
		Validator validator = validatorFactory.getValidator();
		Set validateResult = validator.validate(t, groups);
		if (validateResult.isEmpty()) {
			return Collections.emptyMap();
		} else {
			LinkedHashMap errors = new LinkedHashMap<>();
			Iterator iterator = validateResult.iterator();
			while (iterator.hasNext()) {
				ConstraintViolation violation = (ConstraintViolation) iterator.next();
				errors.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			return errors;
		}
	}

	public Map<String, String> validateList(Collection<?> collection) {
		if (collection == null) {
			throw new NullPointerException();
		}
		Iterator iterator = collection.iterator();
		Map errors;

		do {
			if (!iterator.hasNext()) {
				return Collections.emptyMap();
			}
			Object object = iterator.next();
			errors = validate(object, new Class[0]);
		} while (errors.isEmpty());

		return errors;
	}

	public Map<String, String> validateObject(Object first, Object... objects) {

		if (objects != null && objects.length > 0) {

			List<Object> list = Arrays.asList(objects);
			list.add(0, first);

			return validateList(list);
		} else {
			return validate(first, new Class[0]);
		}
	}

	public void check(Object param) throws IllegalArgumentException {
		Map<String, String> map = BeanValidator.validateObject(param);
		if (MapUtils.isNotEmpty(map)) {
			throw new IllegalArgumentException(map.toString());
		}
	}

//	public static void main(String[] args) {
//		
//		SysUser user = new SysUser ();
//		user.setId(1L);
//		BeanValidator.check(user);
//		
//		
//	}
}
