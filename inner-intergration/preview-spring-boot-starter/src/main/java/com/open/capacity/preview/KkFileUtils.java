package com.open.capacity.preview;

import jodd.io.FileNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;

import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author luowj
 * @since copy from cn.keking.utils.KkFileUtils
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
@Slf4j
public class KkFileUtils {

    private KkFileUtils() {

    }

    private static final List<String> SPECIAL_SUFFIX = Arrays.asList("tar.bz2", "tar.Z", "tar.gz", "tar.xz");

    public static final String DEFAULT_FILE_ENCODING = "UTF-8";

    private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?");

    /**
     * 判断url是否是http资源
     *
     * @param url url
     * @return 是否http
     */
    public static boolean isHttpUrl(URL url) {
        return url.getProtocol().toLowerCase().startsWith("file") || url.getProtocol().toLowerCase().startsWith("http");
    }

    /**
     * 判断url是否是ftp资源
     *
     * @param url url
     * @return 是否ftp
     */
    public static boolean isFtpUrl(URL url) {
        return "ftp".equalsIgnoreCase(url.getProtocol());
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFileByName(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.info("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                log.info("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            log.info("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 通过文件名获取文件后缀
     *
     * @param fileName 文件名称
     * @return 文件后缀
     */
    public static String suffixFromFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 通过文件名获取文件前缀
     *
     * @param fileName 文件名称
     * @return 文件前缀
     */
    public static String getPrefix(String fileName) {
        return FileNameUtil.getPrefix(fileName);
    }

    /**
     * 根据文件路径删除文件
     *
     * @param filePath 绝对路径
     */
    public static void deleteFileByPath(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            log.warn("压缩包源文件删除失败:{}！", filePath);
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            log.info("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = KkFileUtils.deleteFileByName(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else if (files[i].isDirectory()) {
                // 删除子目录
                flag = KkFileUtils.deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!dirFile.delete() || !flag) {
            log.info("删除目录失败！");
            return false;
        }
        return true;
    }

    public static boolean exist(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static File mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            mkdirsSafely(file, 5, 1L);
        }
        return file;
    }

    private static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        } else if (dir.isDirectory()) {
            return true;
        } else {
            for (int i = 1; i <= tryCount; ++i) {
                dir.mkdirs();
                if (dir.exists()) {
                    return true;
                }
                try {
                    ThreadUtils.sleep(Duration.ofMillis(sleepMillis));
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
            return dir.exists();
        }
    }

    public static String getSuffix(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            int index = fileName.lastIndexOf(".");
            if (index == -1) {
                return "";
            } else {
                int secondToLastIndex = fileName.substring(0, index).lastIndexOf(".");
                String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
                if (SPECIAL_SUFFIX.contains(substr)) {
                    return substr;
                } else {
                    String ext = fileName.substring(index + 1);
                    boolean endsWith = ext.endsWith(File.separator);
                    return endsWith ? "" : ext;
                }
            }
        }
    }

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception var2) {
            }
        }
    }

    public static boolean isAbsolutePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        } else {
            return '/' == path.charAt(0) || PATTERN_PATH_ABSOLUTE.matcher(path).matches();
        }
    }
}
