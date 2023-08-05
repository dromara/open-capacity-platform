package com.open.capacity.common.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.open.capacity.common.annotation.LoginClient;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * head中的应用参数注入clientId中
 *
 * @author zlt
 * @date 2019/7/10
 */
@Slf4j
public class ClientArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 入参筛选
     *
     * @param methodParameter 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(LoginClient.class) && methodParameter.getParameterType().equals(String.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String clientId = request.getHeader(SecurityConstants.TENANT_HEADER);
        if (StringUtil.isBlank(clientId)) {
            log.warn("resolveArgument error clientId is empty");
        }
        return clientId;
    }
}
