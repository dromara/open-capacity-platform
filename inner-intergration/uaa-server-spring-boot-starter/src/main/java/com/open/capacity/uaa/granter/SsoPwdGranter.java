package com.open.capacity.uaa.granter;

import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.uaa.common.token.SsoSysAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xhq
 * @title SsoPwdGranter 自定义单点登录模式
 * @create 20:33 2022/9/18
 */
@SuppressWarnings("all")
public class SsoPwdGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "password_sso";
    private final AuthenticationManager authenticationManager;

    public SsoPwdGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> paramters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = paramters.get("txt3"); // 用户名
        String password = paramters.get("txt4"); // 密码
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            throw new InvalidGrantException("用户名或密码异常：" + username);
        }
        // 存放自定义参数，可以在Provider中使用
        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("username", username);
        tokenParams.put("password", password);
        Authentication userAuth = new SsoSysAuthenticationToken(null, username, tokenParams);
        ((AbstractAuthenticationToken) userAuth).setDetails(paramters);
        // 调用Provider
        userAuth = authenticationManager.authenticate(userAuth);
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate sso Login: " + username);
        }
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
