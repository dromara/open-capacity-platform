package com.open.capacity.uaa.common.token;



import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * 百度认证
 * @author owen	
 * @Date: 2020/03/01/12:45
 *
 */
@SuppressWarnings("all")
public class BaiduAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Object code;
    private Object openId;

    public BaiduAuthenticationToken(Object code, Object openId) {
        super((Collection)null);
        this.code = code;
        this.openId = openId;
        this.setAuthenticated(false);
    }

    public BaiduAuthenticationToken(Object code, Object openId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        this.openId = openId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.openId;
    }

    @Override
    public Object getPrincipal() {
        return this.code;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this openId to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

}
