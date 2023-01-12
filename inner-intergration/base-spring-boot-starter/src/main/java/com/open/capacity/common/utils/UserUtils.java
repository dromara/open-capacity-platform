package com.open.capacity.common.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

/**
 * 获取当前登录人工具类
 * @author zlt
 * @version 1.0
 * @date 2018/6/26
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@UtilityClass
public class UserUtils {
    /**
     * 获取当前登录人
     */
    public  SysUser getCurrentUser(HttpServletRequest request, boolean isFull) {
        SysUser user = null;
        
        //强一致性校验
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            //客户端模式只返回一个clientId
            if (principal instanceof SysUser) {
                user = (SysUser)principal;
            }
        }
        //弱校验
        if (user == null) {
            String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
            String username = request.getHeader(SecurityConstants.USER_HEADER);
            String roles = request.getHeader(SecurityConstants.ROLE_HEADER);
            
            if (StrUtil.isAllNotBlank(username, userId)) {
                if (isFull) {
                	UserFeignClient userFeignClient = SpringUtil.getBean(UserFeignClient.class);
                    user = userFeignClient.selectByUsername(username);
                } else {
                    user = new SysUser();
                    user.setId(Long.valueOf(userId));
                    user.setUsername(username);
                }
                if (StrUtil.isNotBlank(roles)) {
                    List<SysRole> sysRoleList = new ArrayList<>();
                    Arrays.stream(roles.split(",")).forEach(role -> {
                        SysRole sysRole = new SysRole();
                        sysRole.setCode(role);
                        sysRoleList.add(sysRole);
                    });
                    user.setRoles(sysRoleList);
                }
            }
        }
        return user;
    }
    
    /**
     * 获取当前登录人
     */
    public  SysUser getCurrentUser(Map map, boolean isFull) {
        SysUser user = null;
        
        //强一致性校验
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            //客户端模式只返回一个clientId
            if (principal instanceof SysUser) {
                user = (SysUser)principal;
            }
        }
        //弱校验
        if (user == null) {
            String userId =  MapUtils.getString(map, SecurityConstants.USER_ID_HEADER) ;
            String username = MapUtils.getString(map, SecurityConstants.USER_HEADER) ;   
            String roles = MapUtils.getString(map, SecurityConstants.ROLE_HEADER) ;   
            if (StrUtil.isAllNotBlank(username, userId)) {
                if (isFull) {
                	UserFeignClient userFeignClient = SpringUtil.getBean(UserFeignClient.class);
                    user = userFeignClient.selectByUsername(username);
                } else {
                    user = new SysUser();
                    user.setId(Long.valueOf(userId));
                    user.setUsername(username);
                }
                if (StrUtil.isNotBlank(roles)) {
                    List<SysRole> sysRoleList = new ArrayList<>();
                    Arrays.stream(roles.split(",")).forEach(role -> {
                        SysRole sysRole = new SysRole();
                        sysRole.setCode(role);
                        sysRoleList.add(sysRole);
                    });
                    user.setRoles(sysRoleList);
                }
            }
        }
        return user;
    }
    
    
    
}