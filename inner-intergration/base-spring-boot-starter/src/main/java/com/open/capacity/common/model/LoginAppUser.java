package com.open.capacity.common.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 用户实体绑定spring security
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
public class LoginAppUser extends SysUser implements SocialUserDetails {
	
	private static final long serialVersionUID = -3685249101751401211L;

	private Set<String> permissions;

	/***
	 * 权限重写
	 */
	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = SynchronizedCollection.synchronizedCollection(new HashSet<>());
		if (!CollectionUtils.isEmpty(super.getRoles())) {
			super.getRoles().parallelStream()
					.forEach(role -> collection.add(new SimpleGrantedAuthority(role.getCode())));
		}
		return collection;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return getEnabled();
	}

	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
	}

	@Override
	public String getUserId() {
		return getOpenId();
	}
}
