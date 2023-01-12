package com.open.capacity.uaa.service;

import java.awt.image.BufferedImage;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.open.capacity.common.dto.ResponseEntity;

/**
 * @author zlt
 * @date 2018/12/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public interface IValidateCodeService {
    /**
     * 保存图形验证码
     * @param deviceId 前端唯一标识
     * @param imageCode 验证码
     */
    void saveImageCode(String deviceId, String imageCode);

    /**
     * 短信验证码
     * @param mobile
     * @return
     */
    ResponseEntity sendSmsCode(String mobile);
    
    /**
     * 动态令牌二维码
     * @param username
     * @return
     */
    BufferedImage createQrCode(String username) ;


    /**
     * 获取验证码
     * @param deviceId 前端唯一标识/手机号
     */
    String getCode(String deviceId);

    /**
     * 删除验证码
     * @param deviceId 前端唯一标识/手机号
     */
    void remove(String deviceId);

    /**
     * 验证验证码
     */
    void validate(String deviceId, String validCode);
    
    /**
     * 动态二维码
     * @param deviceId
     * @param secret
     * @return
     */
    BufferedImage createQrCode(String deviceId, String secret) ;
    /**
     * 保存谷歌动态令牌应用信息
     * @param secret
     */
    void saveQrSecret(String deviceId ,String secret);
    /**
     * 校验动态口令
     * @param secret
     */
    void validateDynamicToken(String deviceId, String validCode);

    /**
     *  服务端国密字符串保存
     * @param deviceId
     * @param publicKey
     * @param privateKey
     */
    ResponseEntity saveSmKey(String deviceId, String publicKey, String privateKey);

	/**
	 * 国密密码传输校验
	 * @param deviceId
	 * @param password
	 */
    String validateSmkey(String deviceId, String password);
    
}
