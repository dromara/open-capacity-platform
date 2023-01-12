package com.open.capacity.common.config;

import java.util.Arrays;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义密码规则校验器
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
public class DefaultPasswordValidator {

	@Bean
	@ConditionalOnMissingBean
	public PasswordValidator passwordValidator() {

		PasswordValidator validator = new PasswordValidator(Arrays.asList(
				// 包含用户名
				new UsernameRule(), 
				// 长度规则，8-30
				new LengthRule(8, 30),
				// 字符规则 至少有一个大写字母
				new CharacterRule(EnglishCharacterData.UpperCase, 1),
				// 字符规则 至少有一个小写字母
				new CharacterRule(EnglishCharacterData.LowerCase, 1),
				// 字符规则 至少有一个特殊字符
				new CharacterRule(EnglishCharacterData.Special, 1),
				// 非法顺序规则 不允许有5个连续字母表顺序的字母，比如不允许abcde
				new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
				// 非法顺序规则 不允许有5个连续数字顺序的数字 比如不允许12345
				new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
				// 非法顺序规则 不允许有5个连续键盘顺序的字母 比如不允许asdfg
				new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
				new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
				// 空格规则,不能有空格
				new WhitespaceRule()));

		return validator;

	}
	
//	
//	public boolean validatePassword(String username, String password) {
//		PasswordValidator passwordValidator = new PasswordValidator(
//				Arrays.asList(new LengthRule(5, 18), new WhitespaceRule()));
//		RuleResult ResponseEntity = passwordValidator.validate(new PasswordData(username, password));
//		return ResponseEntity.isValid();
//	}
}
