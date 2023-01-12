package com.open.capacity.common.resolver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.open.capacity.common.annotation.Permission;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.utils.MessageSourceUtil;
import com.open.capacity.common.utils.SpringUtil;
import com.open.capacity.common.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * 接口鉴权
 * 
 * @author someday
 * @date 2018/12/21
 */
@Aspect
@AllArgsConstructor
@ConditionalOnWebApplication(type = Type.SERVLET)
public class PermissionAspectResolver {

	/**
	 * 判断接口是否有xxx:xxx权限
	 * @param permission 权限
	 * @return {boolean}
	 */

	@SneakyThrows
	@Around("@annotation(permission)")
	public Object hasPermission(ProceedingJoinPoint point, Permission permission) {

		String permissionCode = permission.value(); // 按钮标识
		if (StringUtil.isNoneBlank(permissionCode)) {

			String username = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
					.getHeader(SecurityConstants.USER_HEADER);

			if (StringUtil.isNoneBlank(username)) {
				UserFeignClient userFeignClient = SpringUtil.getBean(UserFeignClient.class);
				LoginAppUser appUser = userFeignClient.findByUsername(username);
				boolean flag = appUser.getPermissions().stream().filter ( StringUtils::hasText)
						.anyMatch(x -> PatternMatchUtils.simpleMatch(permission.value(), x));
				if (!flag) {
					throw new AccessDeniedException(
							MessageSourceUtil.getAccessor().getMessage("AbstractAccessDecisionManager.accessDenied",
									new Object[] { permission.value() }, "access denied"));
				}
			}
		}

		return point.proceed();
	}

}
