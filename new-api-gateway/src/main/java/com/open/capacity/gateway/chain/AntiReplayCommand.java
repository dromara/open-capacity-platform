package com.open.capacity.gateway.chain;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.open.capacity.common.algorithm.AESUtil;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.gateway.context.SecurityContext;
import com.open.capacity.redis.repository.RedisRepository;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;

/**
 * 
 * 防重放
 */
@Component
public class AntiReplayCommand implements Command {

	private SecurityProperties securityProperties;
	private RedisRepository redisRepository;
	/**
	 * 时间戳属性
	 */
	public static final String TIMESTAMP = "timestamp";
	/**
	 * 签名属性
	 */
	public static final String SIGN = "sign";
	/**
	 * 盐
	 */
	public static final String NONCE = "nonce";

	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	// 防篡改 重放缓存key
	private static String REPLAY_KEY = CommonConstant.PROJECT_KEY + StrUtil.COLON + "replay" + StrUtil.COLON;

	public AntiReplayCommand(SecurityProperties securityProperties, RedisRepository redisRepository) {
		this.securityProperties = securityProperties;
		this.redisRepository = redisRepository;
	}

	@Override
	public boolean execute(Context context) throws Exception {

		if (securityProperties.getReplay().getEnable()) {
			SecurityContext securityContext = (SecurityContext) context;
			String requestURI = securityContext.getExchange().getRequest().getPath().toString();
			MultiValueMap<String, String> headers = securityContext.getExchange().getRequest().getHeaders();
			Map<String, String> headersMap = new HashMap<>();
			for (String key : headers.keySet()) {
				headersMap.put(key, headers.getFirst(key));
			}
			String nonce = MapUtils.getString(headersMap, NONCE);
			String sign = MapUtils.getString(headersMap, SIGN);
			String timestamp = MapUtils.getString(headersMap, TIMESTAMP);
			int flag = validateSign(nonce, sign, timestamp);
			if (flag > 0) {
				securityContext.setCode(HttpStatus.HTTP_UNAUTHORIZED);
				String msg = Match(flag).of(Case($(1), "非法请求,请核实请求参数是否正确！"), Case($(2), "非法请求,签名已过期！"),
						Case($(3), "非法请求,签名校验失败！"), Case($(4), "非法请求,请求禁止重放！" + requestURI));
				securityContext.setEntity(ResponseEntity.failed(msg));
				securityContext.setResult(true);
				return true;
			}
		}
		return false;
	}

	private int validateSign(String nonce, String sign, String timestamp) throws Exception {
		int flag = 0;
		if (StringUtils.isBlank(sign) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce)) {
			flag = 1;
			return flag;
		}
		long time = Long.parseLong(timestamp);
		if (System.currentTimeMillis() - time >= securityProperties.getReplay().getExpireTime() * 1000) {
			flag = 2;
			return flag;
		}
		String signString = timestamp + nonce;
		String encryptedSign = AESUtil.encrypt(signString, securityProperties.getReplay().getKey());
		if (!encryptedSign.equals(sign)) {
			flag = 3;
			return flag;
		} 
		String nonceKey = REPLAY_KEY + nonce;
		Boolean hasKey = redisRepository.hasKey(nonceKey);
		if (hasKey != null && hasKey) {
			flag = 4;
			return flag;
		}
		redisRepository.setExpire(nonceKey, signString, securityProperties.getReplay().getExpireTime());
		return flag;

	}

}
