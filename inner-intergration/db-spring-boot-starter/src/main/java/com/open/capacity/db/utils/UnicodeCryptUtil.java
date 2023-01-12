package com.open.capacity.db.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author xh
 * 存储加密算法
 */
@Slf4j
public class UnicodeCryptUtil {

    private final static char SALT = 'o' + 'c' + 'p';

    /**
     *  转 unicode 编码
     */
    public static String encodeUicode(final String data) {
        char[] utfBytes = data.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char utfByte : utfBytes) {
            //转Unicode
            String str = Integer.toHexString((((utfByte + SALT) & 0x0000FFFF) | 0xFFFF0000)).substring(4);
            //倒序
            String reverse = reverse(str);
            sb.append("u").append(reverse);
        }
        return sb.toString();
    }

    /**
     *  解析 unicode 编码
     */
    public static String decodeUnicode(final String dataStr) {
        if(!StringUtils.isNotBlank(dataStr)){
            return dataStr;
        }
        if (!isEncrypt(dataStr)) {
            return dataStr;
        }
        StringBuilder sb = new StringBuilder();
        String[] split = StringUtils.split(dataStr,"u");
        Arrays.stream(split).forEach(s -> {
            String reverse = reverse(s);
            sb.append((char) (Integer.parseInt(reverse, 16) - SALT));
        });
        return sb.toString();
    }

    private static String reverse(String str) {
        char[] chars = str.toCharArray();
        int n = chars.length - 1;
        for (int i = 0; i < chars.length / 2; i++) {
            char temp = chars[i];
            chars[i] = chars[n - i];
            chars[n - i] = temp;
        }
        return new String(chars);
    }

    private static boolean isEncrypt(String dataStr) {
        return dataStr.contains("u");
    }
}