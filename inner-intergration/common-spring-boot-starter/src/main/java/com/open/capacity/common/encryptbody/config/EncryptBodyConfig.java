package com.open.capacity.common.encryptbody.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * <p>加密数据配置读取类</p>
 * <p>在SpringBoot项目中的application.yml中添加配置信息即可</p>
 * <pre>
 *     encrypt:
 *      body:
 *       aes-key: 12345678 # AES加密秘钥
 *       des-key: 12345678 # DES加密秘钥
 *       sm-key: 0123456789abcdeffedcba9876543210  # SM加密秘钥
 * </pre>
 * @author licoy.cn
 * @version 2018/9/6
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
@ConfigurationProperties(prefix = "encrypt.body")
@Configuration
@Data
@RefreshScope
public class EncryptBodyConfig {

    private String aesKey;

    private String desKey;
    
    public String smKey ;

    private Charset encoding = StandardCharsets.UTF_8;

	

}
