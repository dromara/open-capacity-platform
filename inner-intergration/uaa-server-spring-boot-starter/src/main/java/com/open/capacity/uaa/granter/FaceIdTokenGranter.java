package com.open.capacity.uaa.granter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.alibaba.fastjson.JSONObject;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.face.FaceLiveness;
import com.open.capacity.common.face.FaceQuality;
import com.open.capacity.common.face.FaceRecognitionV3Template;
import com.open.capacity.uaa.common.token.FaceIdAuthenticationToken;

/**
 * 人脸识别授权模式
 *
 * @author someday
 * @date 2018/7/11
 */
@SuppressWarnings("all")
public class FaceIdTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "faceId";

    private final AuthenticationManager authenticationManager;

    private FaceRecognitionV3Template faceRecognitionV3Template;	
    
    public FaceIdTokenGranter(FaceRecognitionV3Template faceRecognitionV3Template ,AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices
            , ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.faceRecognitionV3Template = faceRecognitionV3Template ;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String faceId = parameters.get("faceId");

        
        JSONObject param = faceRecognitionV3Template.search(faceId, CommonConstant.GROUP, FaceQuality.NONE,
				FaceLiveness.NONE);

        String userId = "1" ;
        //人脸用户绑定
        parameters.put("faceId", userId);
        
        //copy new tokenrequest 
        TokenRequest tokenRequestNew = new TokenRequest(parameters, tokenRequest.getClientId(), tokenRequest.getScope(), tokenRequest.getGrantType());
        
        Authentication userAuth = new FaceIdAuthenticationToken(userId);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        userAuth = authenticationManager.authenticate(userAuth);
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate faceId: " + faceId);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequestNew);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
