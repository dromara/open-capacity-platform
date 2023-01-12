package com.open.capacity.uaa.granter;

import com.alibaba.fastjson.JSONObject;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.face.FaceLiveness;
import com.open.capacity.common.face.FaceQuality;
import com.open.capacity.uaa.service.IValidateCodeService;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * password添加国密密码key模式
 * @author owen
 * @date 2020/7/11
 */
@SuppressWarnings("all")
public class PwdSmKeyTokenGranter extends ResourceOwnerPasswordTokenGranter {
    private static final String GRANT_TYPE = "password_smkey";

    private final IValidateCodeService validateCodeService;

    public PwdSmKeyTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices
            , ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, IValidateCodeService validateCodeService) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.validateCodeService = validateCodeService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String deviceId = parameters.get("deviceId");
        String password = parameters.get("password");
        //服务端解密
        String realPass =  validateCodeService.validateSmkey(deviceId, password);
        parameters.put("password", realPass);
        //copy new tokenrequest 
        TokenRequest tokenRequestNew = new TokenRequest(parameters, tokenRequest.getClientId(), tokenRequest.getScope(), tokenRequest.getGrantType());
        return   super.getOAuth2Authentication(client, tokenRequestNew);
    }
}
