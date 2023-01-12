package com.open.capacity.uaa.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author someday
 */
@Setter
@Getter
public class TokenVo implements Serializable {
   private static final long serialVersionUID = -6656955957477645319L;
   /**
    * token的值
    */
   private String tokenValue;
   /**
    * 到期时间
    */
   private Date expiration;
   /**
    * 用户名
    */
   private String username;
   /**
    * 所属应用
    */
   private String clientId;
   /**
    * 授权类型
    */
   private String grantType;
   /**
    * 账号类型
    */
   private String accountType;
}
