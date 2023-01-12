package com.open.capacity.uaa.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.open.capacity.uaa.common.token.TenantUsernamePasswordAuthenticationToken;
import com.open.capacity.uaa.password.PasswordAuthenticationProvider;

/**
 * 增加租户id，解决不同租户单点登录时角色没变化
 *
 * @author someday
 * @date 2018/6/10
 */
public class TenantAuthenticationProvider extends PasswordAuthenticationProvider {
    @Override
    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        TenantUsernamePasswordAuthenticationToken authenticationToken = (TenantUsernamePasswordAuthenticationToken) authentication;
        TenantUsernamePasswordAuthenticationToken result = new TenantUsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(), user.getAuthorities(), authenticationToken.getClientId());
        result.setDetails(authenticationToken.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TenantUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
