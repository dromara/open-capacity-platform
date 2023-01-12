package com.open.capacity.common.idempotent.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.idempotent.annotation.RepeatSubmit;
import com.open.capacity.common.utils.AddrUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @date 2018/9/25
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 **/

@Aspect
@Slf4j
public class RepeatSubmitAspect {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(repeatSubmit)")
    public void pointCutNoRepeatSubmit(RepeatSubmit repeatSubmit) {

    }

    /**
     * 环绕通知, 围绕着方法执行
     * 两种方式
     * 方式一：加锁 固定时间内不能重复提交
     *  code: https://gitee.com/owenwangwen/open-capacity-platform
     * 方式二：先请求获取token，这边再删除token,删除成功则是第一次提交
     */
    @Around("pointCutNoRepeatSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String serviceId =  repeatSubmit.serviceId() ;
        //用于记录成功或者失败
        boolean res = false;
        //防重提交类型
        String type = repeatSubmit.limitType().name();
        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())) {
            //方式一，参数形式防重提交
            long lockTime = repeatSubmit.lockTime();
            String ipAddr = AddrUtil.getRemoteAddr(request) ;
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String className = method.getDeclaringClass().getName();
            String key = repeatSubmit.serviceId() + ":repeat_submit:"+AddrUtil.MD5(String.format("%s-%s-%s-%s",ipAddr,className,method,serviceId));

            RLock lock = redissonClient.getLock(key);
            // 尝试加锁，最多等待0秒，上锁以后5秒自动解锁 [lockTime默认为5s, 可以自定义]
            res = lock.tryLock(0,lockTime,TimeUnit.SECONDS);

        } else {
            //方式二，令牌形式防重提交
            String requestToken = request.getHeader("request-token");
            if (StringUtils.isBlank(requestToken)) {
                throw new RuntimeException("请求未包含令牌");
            }
            String key = String.format(CommonConstant.SERVICE_SUBMIT_TOKEN_KEY, repeatSubmit.serviceId(), requestToken);
            res = redisTemplate.delete(key);

        }
        if (!res) {
            log.error("请求重复提交");
            return null;
        }
        log.info("环绕通知执行前");

        Object obj = joinPoint.proceed();

        log.info("环绕通知执行后");

        return obj;

    }


}
