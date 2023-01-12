package com.open.capacity.uaa.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.util.NamedFeature;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.uaa.common.service.IFeatureUserService;;

/**
 * 个性用户特权开关
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform  
 */
@Service
public class FeatureUserServiceImpl implements IFeatureUserService {

	@Autowired
	private FeatureManager manager;
	public static final Feature GRAY_USER = new NamedFeature(SecurityConstants.GRAY_USER);
	
	@Override
	public boolean isActive() {
		// 特权工号不需要校验
		if (manager.isActive(GRAY_USER)) {
			return true;
		}
		return false;
	}

}
