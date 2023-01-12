package com.open.capacity.common.model;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.open.capacity.common.sensitive.Sensitive;
import com.open.capacity.common.sensitive.SensitiveTypeEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * 类说明 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser extends BaseEntity<SysUser> {
	private static final long serialVersionUID = -5886012896705137070L;

	private String username;
	private String password;
	@Sensitive(type = SensitiveTypeEnum.CHINESE_NAME)
	private String nickname;
	private String headImgUrl;
	private String mobile;
	private Integer sex;
	private Boolean enabled;
	private String type;
	private String openId;
	@TableLogic
	private boolean isDel;

	@TableField(exist = false)
	private List<SysRole> roles;
	@TableField(exist = false)
	private String roleId;
	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String newPassword;
}
