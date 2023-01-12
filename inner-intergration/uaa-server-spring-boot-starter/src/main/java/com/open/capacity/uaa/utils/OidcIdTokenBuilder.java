package com.open.capacity.uaa.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.util.Assert;

import com.open.capacity.common.utils.JsonUtil;
import com.open.capacity.uaa.common.constants.IdTokenClaimNames;
import com.open.capacity.uaa.common.util.JwtUtils;

import lombok.Getter;

/**
 * Oidc协议的IdToken
 * @author zlt
 * @version 1.0
 * @date 2018/4/21
 */
@Getter
public class OidcIdTokenBuilder {
    private final Map<String, Object> claims;

    private OidcIdTokenBuilder(KeyProperties keyProperties) {
        this.claims = new LinkedHashMap<>();
    }

    public static OidcIdTokenBuilder builder(KeyProperties keyProperties) {
        Assert.notNull(keyProperties, "keyProperties required");
        return new OidcIdTokenBuilder(keyProperties);
    }

    /**
     * Use this claim in the resulting {@link OidcIdTokenBuilder}
     *
     * @param name The claim name
     * @param value The claim value
     */
    public OidcIdTokenBuilder claim(String name, Object value) {
        this.claims.put(name, value);
        return this;
    }

    /**
     * Use this audience in the resulting {@link OidcIdTokenBuilder}
     *
     * @param audience The audience to use
     */
    public OidcIdTokenBuilder audience(String audience) {
        return claim(IdTokenClaimNames.AUD, audience);
    }

    /**
     * Use this expiration in the resulting {@link OidcIdTokenBuilder}
     *
     * @param expiresAt The expiration to use
     */
    public OidcIdTokenBuilder expiresAt(long expiresAt) {
        return this.claim(IdTokenClaimNames.EXP, expiresAt);
    }

    /**
     * Use this issued-at timestamp in the resulting {@link OidcIdTokenBuilder}
     *
     * @param issuedAt The issued-at timestamp to use
     */
    public OidcIdTokenBuilder issuedAt(long issuedAt) {
        return this.claim(IdTokenClaimNames.IAT, issuedAt);
    }

    /**
     * Use this issuer in the resulting {@link OidcIdTokenBuilder}
     *
     * @param issuer The issuer to use
     */
    public OidcIdTokenBuilder issuer(String issuer) {
        return this.claim(IdTokenClaimNames.ISS, issuer);
    }

    /**
     * Use this nonce in the resulting {@link OidcIdTokenBuilder}
     *
     * @param nonce The nonce to use
     */
    public OidcIdTokenBuilder nonce(String nonce) {
        return this.claim(IdTokenClaimNames.NONCE, nonce);
    }

    /**
     * Use this subject in the resulting {@link OidcIdTokenBuilder}
     *
     * @param subject The subject to use
     */
    public OidcIdTokenBuilder subject(String subject) {
        return this.claim(IdTokenClaimNames.SUB, subject);
    }

    /**
     * 赋值用户姓名
     */
    public OidcIdTokenBuilder name(String name) {
        return this.claim(IdTokenClaimNames.NAME, name);
    }

    /**
     * 赋值用户登录名
     */
    public OidcIdTokenBuilder loginName(String loginName) {
        return this.claim(IdTokenClaimNames.L_NAME, loginName);
    }

    /**
     * 赋值用户头像
     */
    public OidcIdTokenBuilder picture(String picture) {
        return this.claim(IdTokenClaimNames.PIC, picture);
    }

    /**
     * Build the {@link OidcIdTokenBuilder}
     *
     * @return The constructed {@link OidcIdTokenBuilder}
     */
    public String build() {
        return JwtUtils.encodeStr(JsonUtil.toJSONString(claims) );
    }
}
