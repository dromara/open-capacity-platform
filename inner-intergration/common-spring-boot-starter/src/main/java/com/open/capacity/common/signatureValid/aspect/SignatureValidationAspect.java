package com.open.capacity.common.signatureValid.aspect;

import com.open.capacity.common.signatureValid.exception.SignatrueValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * @author xh
 * @comment 常量->混淆方法->加密算法
 */
@Aspect
@Component
@Slf4j
public class SignatureValidationAspect {
    /**
     * 时间戳请求最小限制(30s)
     * 设置的越小，安全系数越高，但是要注意一定的容错性
     */
    private static final long MAX_REQUEST = 20 * 1000L;
    /**
     * 秘钥
     */
    private static final String SECRET= "xh@!@kklsxxsyte";

    /**
     * 验签切点(完整的找到设置的文件地址)
     */
    @Pointcut("execution(@com.open.capacity.common.signatureValid.annotation.SignatureValidation * *(..))")
    private void verifyUserKey() {
    }

    /**
     * 开始验签
     */
    @Before("verifyUserKey()")
    public void doBasicProfiling() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("webToken");
        String timestamp = request.getHeader("webTm");
        try {
            Boolean check = checkToken(token, timestamp);
            if (!check) {
                throw new SignatrueValidationException("签名验证错误");
            }
        } catch (Throwable throwable) {
            throw new SignatrueValidationException("签名验证错误");
        }
    }

    /**
     * 校验token
     *
     * @param token     签名
     * @param timestamp 时间戳
     * @return 校验结果
     */
    private Boolean checkToken(String token, String timestamp) {
        if (StringUtils.isAnyBlank(token, timestamp)) {
            return false;
        }
        long now = System.currentTimeMillis();
        long time = Long.parseLong(timestamp);
        if (now - time > MAX_REQUEST) {
            log.error("时间戳已过期[{}][{}][{}]", now, time, (now - time));
            return false;
        }
        String crypt = md5(SECRET + timestamp);
        return StringUtils.equals(crypt, token);
    }

    /**
     * md5加密
     * @param str
     * @return
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return str;
    }
}
