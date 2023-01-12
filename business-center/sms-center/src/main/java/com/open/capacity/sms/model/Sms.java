package com.open.capacity.sms.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.open.capacity.common.model.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 作者 : someday 
 * 模块 : 短信中心 
 * 描述 : 短信model 
 * 备注 : version20180709001
 * 修改历史 序号 日期 修改人 修改原因
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_sms")
public class Sms extends BaseEntity<Sms> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4648169168575518593L;
	private Long id;
	private String phone;
	private String signName;
	private String templateCode;
	private String params;
	private String bizId;
	private String code;
	private String message;
	private Date day;
	private Date createTime;
	private Date updateTime;
}
