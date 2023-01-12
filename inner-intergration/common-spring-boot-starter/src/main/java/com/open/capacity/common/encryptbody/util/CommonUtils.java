package com.open.capacity.common.encryptbody.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.encryptbody.bean.ISecurityInfo;
import com.open.capacity.common.encryptbody.exception.EncryptBodyFailException;
import com.open.capacity.common.encryptbody.exception.IllegalSecurityTypeException;
import com.open.capacity.common.encryptbody.exception.KeyNotConfiguredException;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * <p>工具类</p>
 *
 * @author licoy.cn
 * @version 2018/9/7
 * code:https://gitee.com/licoy/encrypt-body-spring-boot-starter
 */
public class CommonUtils {

    public static String checkAndGetKey(String k1, String k2, String keyName) {
        if (StrUtil.isEmpty(k1) && StrUtil.isEmpty(k2)) {
            throw new KeyNotConfiguredException(String.format("%s is not configured (未配置%s)", keyName, keyName));
        }
        if (k1 == null) {
            return k2;
        }
        return k1;
    }

    /**
     * 根据信息对象获取RSA实例
     *
     * @param info 信息
     * @return rsa
     */
    public static RSA infoBeanToRsaInstance(ISecurityInfo info) {
        RSA rsa;
        switch (info.getRsaKeyType()) {
            case PUBLIC:
                rsa = new RSA(null, SecureUtil.decode(info.getKey()));
                break;
            case PRIVATE:
                rsa = new RSA(SecureUtil.decode(info.getKey()), null);
                break;
            default:
                throw new IllegalSecurityTypeException();
        }
        return rsa;
    }

    /**
     * 是否转换为string
     *
     * @param clazz class
     * @return 是否
     */
    public static boolean isConvertToString(Class<?> clazz) {
        return clazz.equals(String.class) || ClassUtil.isPrimitiveWrapper(clazz);
    }

    /**
     * 转换为string
     *
     * @param val    数据
     * @param mapper jackson
     * @return string
     */
    public static String convertToStringOrJson(Object val, ObjectMapper mapper) {
        if (isConvertToString(val.getClass())) {
            return String.valueOf(val);
        }
        try {
            return mapper.writeValueAsString(val);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new EncryptBodyFailException(e.getMessage());
        }
    }

}
