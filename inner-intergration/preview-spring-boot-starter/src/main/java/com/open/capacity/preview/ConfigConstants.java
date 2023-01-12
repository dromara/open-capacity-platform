package com.open.capacity.preview;

import com.open.capacity.preview.exception.KKFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class ConfigConstants {

    static {
        //pdfbox兼容低版本jdk
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    private static Boolean cacheEnabled;

    private static String fileDir = ConfigUtils.getHomePath(null) + File.separator + "file" + File.separator;

    private static String convertDir = null;


    public static final String DEFAULT_CACHE_ENABLED = "true";
    public static final String DEFAULT_FTP_CONTROL_ENCODING = "UTF-8";
    public static final String DEFAULT_FILE_DIR_VALUE = "default";

    public static Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    @Value("${office.cache.enabled:true}")
    public void setCacheEnabled(String cacheEnabled) {
        setCacheEnabledValueValue(Boolean.parseBoolean(cacheEnabled));
    }

    public static void setCacheEnabledValueValue(Boolean cacheEnabled) {
        ConfigConstants.cacheEnabled = cacheEnabled;
    }

    public static String getFileDir() {
        return fileDir;
    }

    /**
     * 获取直接装换文档格式的保存目录
     */
    public static String getConvertDir() {
        if (convertDir != null) {
            return convertDir;
        }
        boolean endsWith = fileDir.endsWith(File.separator);
        String convertDir = ConfigUtils.CONVERT_DIRECTORY_NAME + File.separator;
        return endsWith ? fileDir + convertDir : fileDir + File.separator + convertDir;
    }

    @Value("${office.cache.file.dir:default}")
    public void setFileDir(String fileDir) {
        setFileDirValue(fileDir);
        File file = new File(ConfigConstants.fileDir);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new KKFileException("office.cache.file.dir 路径错误");
            }
        }
    }

    public static void setFileDirValue(String fileDir) {
        if (!DEFAULT_FILE_DIR_VALUE.equalsIgnoreCase(fileDir)) {
            boolean absolutePath = KkFileUtils.isAbsolutePath(fileDir);
            if (absolutePath) {
                ConfigConstants.fileDir = fileDir;
            } else {
                if (fileDir.endsWith(File.separator)) {
                    fileDir = fileDir.substring(0, fileDir.length() - 1);
                }
                ConfigConstants.fileDir = ConfigUtils.getHomePath(fileDir) + File.separator + "file" + File.separator;
            }
        }
    }

}
