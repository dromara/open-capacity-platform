package com.open.capacity.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 作者 gitgeek 
* @version 创建时间：2018-08-06 21:29
* 类说明  应用实体
*/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_client_details")
public class Client extends BaseEntity<Client> {
   private static final long serialVersionUID = -8185413579135897885L;
   private String clientId;
   /**
    * 应用名称
    */
   private String clientName;
   private String resourceIds = "";
   private String clientSecret;
   private String clientSecretStr;
   private String scope = "all";
   private String authorizedGrantTypes = "authorization_code,password,refresh_token,client_credentials";
   private String webServerRedirectUri;
   private String authorities = "";
   @TableField(value = "access_token_validity")
   private Integer accessTokenValiditySeconds = 18000;
   @TableField(value = "refresh_token_validity")
   private Integer refreshTokenValiditySeconds = 18000;
   private String additionalInformation = "{}";
   private String autoapprove = "true";
   /**
    * 是否支持id_token
    */
   private Boolean supportIdToken = true;
   /**
    * id_token有效时间(s)
    */
   @TableField(value = "id_token_validity")
   private Integer idTokenValiditySeconds = 60;
   
   private Integer ifLimit ;
   
   private Long limitCount=10000L ;
   
}
