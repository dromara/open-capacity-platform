package com.open.capacity.gateway.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.model.Client;
import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.gateway.service.IRateLimitService;
import com.open.capacity.sentinel.util.RedisLimiterUtils;
import com.open.capacity.uaa.common.service.IClientService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 程序名 : ClientRateLimitServiceImpl 建立日期: 2018-09-09 作者 : someday 模块 : 网关 描述 :
 * 根据应用限流 version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements IRateLimitService {
	// url匹配器
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Autowired
	private RedisLimiterUtils redisLimiterUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TokenStore tokenStore;

	@Resource
	IClientService sysClientService;

	/**
	 * 1. 判断token是否有效 2. 如果token有对应clientId 2.1 判断clientId是否有效 2.2
	 * 判断请求的服务service是否有效 2.3 判断clientId是否有权限访问service 3. 判断 clientId+service 每日限流
	 *
	 * @param exchange
	 * @param accessToken
	 * @return
	 */
	@SneakyThrows
	@Override
	public boolean checkRateLimit(String reqUrl, String accessToken) {
		if (StringUtil.isNotBlank(accessToken)) {
			// 1. 按accessToken查找对应的clientId
			OAuth2Authentication oauth2Authentication = tokenStore.readAuthentication(accessToken);
			if (oauth2Authentication != null) {
				String clientId = oauth2Authentication.getOAuth2Request().getClientId();
				// 根据应用 url 限流
				// oauth_client_details if_limit 限流开关
				// limit_count 阈值
				Client client = sysClientService.loadClientByClientId(clientId);
				if (client != null) {
					Integer flag = client.getIfLimit();

					if (1 == flag) {
						Long accessLimitCount = client.getLimitCount();
						ResponseEntity result = redisLimiterUtils.rateLimitOfDay(clientId, reqUrl, accessLimitCount);
						if (-1 == result.getStatusCodeValue()) {
							log.trace("token: {} , limitCount: {} , desc: {} ", accessToken, accessLimitCount,
									result.getMsg());
							return true;
						}
					}
				}
			}

		}
		return false;
	}

}
