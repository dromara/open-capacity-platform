package com.open.capacity.uaa.filter;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

/**
 * oauth-token拦截器
 * 1. 赋值租户
 * 2. 统一返回token格式
 *
 * @author zlt
 * @date 2018/3/29
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings("all")
public class OauthTokenAspect {
    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();
            Principal principal = (Principal) args[0];
            if (!(principal instanceof Authentication)) {
                throw new InsufficientAuthenticationException(
                        "There is no client authentication. Try adding an appropriate authentication filter.");
            }
            String clientId = getClientId(principal);
            Map<String, String> parameters = (Map<String, String>) args[1];
            String grantType = parameters.get(OAuth2Utils.GRANT_TYPE);
            if (!parameters.containsKey(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME)) {
                parameters.put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, SecurityConstants.DEF_ACCOUNT_TYPE);
            }

            //保存租户id
            TenantContextHolder.setTenant(clientId);
            Object proceed = joinPoint.proceed(args);
            if (SecurityConstants.AUTHORIZATION_CODE.equals(grantType)) {
                return proceed;
            } else {
                ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
                OAuth2AccessToken body = responseEntity.getBody();
                return ResponseEntity.status(HttpStatus.OK).body(com.open.capacity.common.dto.ResponseEntity.succeed(body));
            }
        } finally {
            TenantContextHolder.clear();
        }
    }

    private String getClientId(Principal principal) {
        Authentication client = (Authentication) principal;
        if (!client.isAuthenticated()) {
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        }
        String clientId = client.getName();
        if (client instanceof OAuth2Authentication) {
            clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
        }
        return clientId;
    }
}
