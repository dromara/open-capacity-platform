package com.open.capacity.common.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.open.capacity.common.annotation.LoginUser;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.UserUtils;

/**
 * Token转化SysUser
 *
 * @author zlt
 * @date 2018/12/21
 */
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

	private UserFeignClient userFeignClient;

	public TokenArgumentResolver(UserFeignClient userFeignClient) {
		this.userFeignClient = userFeignClient;
	}

	/**
	 * 入参筛选
	 *
	 * @param methodParameter 参数集合
	 * @return 格式化后的参数
	 */
	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.hasParameterAnnotation(LoginUser.class)
				&& methodParameter.getParameterType().equals(SysUser.class);
	}

	/**
	 * @param methodParameter       入参集合
	 * @param modelAndViewContainer model 和 view
	 * @param nativeWebRequest      web相关
	 * @param webDataBinderFactory  入参解析
	 * @return 包装对象
	 */
	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
		//解析注解
		LoginUser loginUser = methodParameter.getParameterAnnotation(LoginUser.class);
		boolean isFull = loginUser.isFull();
		HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		// 账号类型
		String accountType = request.getHeader(SecurityConstants.ACCOUNT_TYPE_HEADER);

		return UserUtils.getCurrentUser(request, isFull);
	}
}
