package com.open.capacity.uaa.openid;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.open.capacity.uaa.common.token.OpenIdAuthenticationToken;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.utils.PreAuthenticationChecks;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author someday
 */
@Setter
@Getter
public class OpenIdAuthenticationProvider implements AuthenticationProvider {
    private UserDetailServiceFactory userDetailsServiceFactory;
    private UserDetailsChecker detailsChecker = new PreAuthenticationChecks();
    
    @Override
    public Authentication authenticate(Authentication authentication) {
        OpenIdAuthenticationToken authenticationToken = (OpenIdAuthenticationToken) authentication;
        String openId =  ObjectUtils.nullSafeToString(authenticationToken.getPrincipal())   ;
        UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByOpenId(openId);
        detailsChecker.check(user);
        OpenIdAuthenticationToken authenticationResult = new OpenIdAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OpenIdAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
