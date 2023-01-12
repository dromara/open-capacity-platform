package com.open.capacity.uaa.common.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author xhq
 * @title SsoSysAuthenticationToken 自定义sso		
 * @create 9:15 2022/9/9
 */
@SuppressWarnings("all")
public class SsoSysAuthenticationToken extends AbstractAuthenticationToken implements Serializable {
    private static final long serialVersionUID = -1393573194444272677L;
    private final Object principal;
    private Map<String, Object> map; //自定义参数

    public SsoSysAuthenticationToken(String username) {
        super(null);
        this.principal = username;
        setAuthenticated(false);
    }

    public SsoSysAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Map<String, Object> map) {
        super(authorities);
        this.principal = principal;
        this.map = map;
        super.setAuthenticated(true); // must use super, as we override
    }

    public SsoSysAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Map<String, Object> getParams() {
        return map;
    }
}
