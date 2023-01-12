package com.ulisesbocchio.jasyptspringboot.detector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import org.springframework.util.Assert;

/**
 * Default property detector that detects encrypted property values with the
 * format "$prefix$encrypted_value$suffix" Default values are "ENC(" and ")"
 * respectively.${PASSWORD:ENC(encodePassword)}
 * @author Ulises Bocchio
 */
public class DefaultPropertyDetector implements EncryptablePropertyDetector {

	private String prefix = "ENC(";
	private String suffix = ")";
	// 匹配正则
	private static final String REG_EXP_PATTERN = "\\$\\{[A-Z0-9_]+:ENC\\(\\S+\\)\\}";
	// 获取ENC(x)
	private static final Pattern compile = Pattern.compile("ENC\\(\\S+\\)");

	public DefaultPropertyDetector() {
	}

	public DefaultPropertyDetector(String prefix, String suffix) {
		Assert.notNull(prefix, "Prefix can't be null");
		Assert.notNull(suffix, "Suffix can't be null");
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public boolean isEncrypted(String property) {
		if (property == null) {
			return false;
		}
		final String trimmedValue = property.trim();
		if (Pattern.matches(REG_EXP_PATTERN, property)) {
			return true;
		}
		return (trimmedValue.startsWith(prefix) && trimmedValue.endsWith(suffix));
	}

	@Override
	public String unwrapEncryptedValue(String property) {
		if (property.matches(REG_EXP_PATTERN)) {
			Matcher matcher = compile.matcher(property);
			if (matcher.find()) {
				property = matcher.group();
			}
		}

		return property.substring(prefix.length(), (property.length() - suffix.length()));
	}
}
