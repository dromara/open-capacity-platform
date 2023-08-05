package com.open.capacity.gateway.auth;

import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.properties.SecurityProperties;

import reactor.core.publisher.Mono;

/**
 * 自定义 ServerWebExchangeMatcher
 * 解决只要请求携带access_token，排除鉴权的url依然会被拦截
 *
 * @author zlt
 * @version 1.0
 * @date 018/6/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomServerWebExchangeMatchers implements ServerWebExchangeMatcher {
    private final SecurityProperties securityProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public CustomServerWebExchangeMatchers(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * 解决只要请求携带access_token，排除鉴权的url依然会被拦截
     */
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        for (String url : securityProperties.getIgnore().getUrls()) {
            if (antPathMatcher.match(url, exchange.getRequest().getURI().getPath())) {
                return MatchResult.notMatch();
            }
        }
        return MatchResult.match();
    }
}
