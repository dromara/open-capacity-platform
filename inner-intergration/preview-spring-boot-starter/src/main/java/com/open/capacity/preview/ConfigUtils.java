package com.open.capacity.preview;

import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author : kl
 * https://gitee.com/kekingcn/file-online-preview
 **/
public class ConfigUtils {

    /**
     * 装换文件保存的文件夹名称
     */
    private static final String MAIN_DIRECTORY_NAME = "office-file";

    /**
     * 直接转换文件的保存文件夹名称
     */
    public static final String CONVERT_DIRECTORY_NAME = "convert";

    public static String getHomePath(String path) {
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            userDir = System.getProperty("user.dir");
        }
        if (StringUtils.isEmpty(path)) {
            path = MAIN_DIRECTORY_NAME;
        }
        if (userDir.endsWith("bin")) {
            userDir = userDir.substring(0, userDir.length() - 4);
        } else {
            userDir = userDir + File.separator + path;
        }
        return userDir;
    }

}
