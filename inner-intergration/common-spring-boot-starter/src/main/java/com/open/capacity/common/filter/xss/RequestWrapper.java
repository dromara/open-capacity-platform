package com.open.capacity.common.filter.xss;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @date 2019-08-13 1. XSS过滤 2. body重复读
 */
@Slf4j
@SuppressWarnings(value = { "all" })
public class RequestWrapper extends HttpServletRequestWrapper {

	private static final Log LOG = LogFactory.getLog(RequestWrapper.class);
	private static final String ANTISAMY_SLASHDOT_XML = "antisamy-ocp.xml";
	// AntiSamy使用的策略文件
	private static Policy policy = null;
	static {
		try (InputStream inputStream = new ClassPathResource(ANTISAMY_SLASHDOT_XML).getInputStream();) {
			policy = Policy.getInstance(inputStream);
		} catch (PolicyException var12) {
			LOG.error(var12.getMessage());
		} catch (IOException var13) {
			LOG.error(var13.getMessage());
		}

	}

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> requestMap = super.getParameterMap();
		Iterator iterator = requestMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry me = (Entry) iterator.next();
			String[] values = (String[]) ((String[]) me.getValue());
			for (int i = 0; i < values.length; ++i) {
				values[i] = xssClean(values[i]);
			}
		}
		return requestMap;
	}

	public String[] getParameterValues(String paramString) {
		String[] arrayOfString1 = super.getParameterValues(paramString);
		if (arrayOfString1 == null) {
			return null;
		} else {
			int i = arrayOfString1.length;
			String[] arrayOfString2 = new String[i];
			for (int j = 0; j < i; ++j) {
				arrayOfString2[j] = xssClean(arrayOfString1[j]);
			}
			return arrayOfString2;
		}
	}

	public String getParameter(String paramString) {
		String str = super.getParameter(paramString);
		if (str == null) {
			return null;
		} else {
			return paramString.equals("logoutRequest") ? str : xssClean(str);
		}
	}

	public String getHeader(String paramString) {
		String str = super.getHeader(paramString);
		return str == null ? null : xssClean(str);
	}

	public String getQueryString(String paramString) {
		String str = super.getQueryString();
		return str == null ? null : xssClean(str);
	}

	public static String xssClean(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		} else {
			String returnValue = value;
			AntiSamy antiSamy = new AntiSamy();
			try {
				CleanResults cr = antiSamy.scan(returnValue, policy);
				returnValue = cr.getCleanHTML();
				returnValue = StringEscapeUtils.unescapeHtml4(returnValue);
				return returnValue.replaceAll(antiSamy.scan("&nbsp;", policy).getCleanHTML(), "&")
						.replaceAll("＼\"", "“").replaceAll("& #40;", "&#40;").replaceAll("& #41;", "&#41;");
			} catch (Exception var4) {
				LOG.error("xssClean error", var4);
				return value;
			}
		}
	}
}
