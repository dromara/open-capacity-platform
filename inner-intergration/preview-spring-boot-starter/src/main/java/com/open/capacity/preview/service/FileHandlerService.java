package com.open.capacity.preview.service;

import com.open.capacity.preview.ConfigConstants;
import com.open.capacity.preview.FileAttribute;
import com.open.capacity.preview.FileType;
import com.open.capacity.preview.KkFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yudian-it
 * @date 2017/11/13
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
@Slf4j
@Component
public class FileHandlerService {

    private static final String DEFAULT_CONVERTER_CHARSET = System.getProperty("sun.jnu.encoding");
    private final CacheService cacheService;

    public FileHandlerService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * @return 已转换过的文件集合(缓存)
     */
    public Map<String, String> listConvertedFiles() {
        return cacheService.getCache();
    }

    /**
     * @return 已转换过的文件，根据文件名获取
     */
    public String getConvertedFile(String key) {
        return cacheService.getCache(key);
    }

    /**
     * 从路径中获取文件负
     *
     * @param path 类似这种：C:\Users\yudian-it\Downloads
     * @return 文件名
     */
    public String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取相对路径
     *
     * @param absolutePath 绝对路径
     * @return 相对路径
     */
    public String getRelativePath(String absolutePath) {
        return absolutePath.substring(ConfigConstants.getFileDir().length());
    }

    /**
     * 添加转换后PDF缓存
     *
     * @param fileName pdf文件名
     * @param value    缓存相对路径
     */
    public void addConvertedFile(String fileName, String value) {
        cacheService.putCache(fileName, value);
    }

    /**
     * 对转换后的文件进行操作(改变编码方式)
     *
     * @param outFilePath 文件绝对路径
     */
    public void doActionConvertedFile(String outFilePath) {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(outFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CONVERTER_CHARSET))) {
            String line;
            while (null != (line = reader.readLine())) {
                if (line.contains("charset=gb2312")) {
                    line = line.replace("charset=gb2312", "charset=utf-8");
                }
                sb.append(line);
            }
//            // 添加sheet控制头
//            sb.append("<script src=\"js/jquery-3.0.0.min.js\" type=\"text/javascript\"></script>");
//            sb.append("<script src=\"js/excel.header.js\" type=\"text/javascript\"></script>");
//            sb.append("<link rel=\"stylesheet\" href=\"bootstrap/css/bootstrap.min.css\">");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 重新写入文件
        try (FileOutputStream fos = new FileOutputStream(outFilePath);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public File getCacheFile(String fileNo) {
        String cache = cacheService.getCache(fileNo);
        if (StringUtils.isEmpty(cache)) {
            return null;
        }
        File resultFile = new File(ConfigConstants.getFileDir() + cache);
        return resultFile.exists() ? resultFile : null;
    }

    /**
     * 获取文件属性
     *
     * @return 文件属性
     */
    public FileAttribute getFileAttribute(String fileName) {
        FileType type = FileType.typeFromFileName(fileName);
        if (type == null) {
            return null;
        }
        FileAttribute attribute = new FileAttribute();
        String suffix = KkFileUtils.suffixFromFileName(fileName);
        String prefix = KkFileUtils.getPrefix(fileName);
        attribute.setType(type);
        attribute.setName(fileName);
        attribute.setSuffix(suffix);
        attribute.setPrefix(prefix);
        return attribute;
    }

}
