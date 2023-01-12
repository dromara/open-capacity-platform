package com.open.capacity.preview;

import com.open.capacity.preview.exception.KKFileException;
import com.open.capacity.preview.service.FileHandlerService;
import com.open.capacity.preview.service.FilePreview;
import com.open.capacity.preview.service.FilePreviewFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 文件预览类
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/7/13 14:45
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
@Slf4j
@Component
public class KKFileViewComponent {

    @Resource
    private FileHandlerService fileHandlerService;

    @Resource
    private FilePreviewFactory previewFactory;

    /**
     * 支持预览的文件类型
     */
    private static final List<String> ALLOW_FILE_TYPE = Arrays.asList(".doc", ".docx", ".pdf", ".xlsx", ".xls", ".pptx", ".ppt");

    /**
     * 直接转换成可预览的文件，目前类型为pdf和html
     *
     * @param file 需要预览的文件
     * @return java.io.File 转换之后的文件
     */
    public File convertViewFile(File file) {
        FileAttribute fileAttribute = fileHandlerService.getFileAttribute(file.getName());
        if (!allowFile(fileAttribute.getSuffix())) {
            throw new KKFileException("不支持的文件类型");
        }
        FilePreview filePreview = previewFactory.get(fileAttribute);
        if (filePreview == null) {
            return null;
        }
        return filePreview.convertToViewFile(file, fileAttribute);
    }

    /**
     * 从缓存中读取文件预览
     *
     * @param response http响应对象
     * @param fileNo   存入缓存的文件编号
     */
    public void viewCacheFile(HttpServletResponse response, String fileNo) {
        File cacheFile = fileHandlerService.getCacheFile(fileNo);
        if (cacheFile == null) {
            throw new KKFileException("文件不存在");
        }
        InputStream inputStream = null;
        FileInputStream fin = null;
        ServletOutputStream outputStream = null;
        try {
            String name = cacheFile.getName();
            String fileName = name.substring(0, name.lastIndexOf('.')) + name.substring(name.lastIndexOf('.'));
            String suffix = KkFileUtils.getSuffix(name);
            response.setContentType("application/force-download; charset=UTF-8");
            response.addHeader("access-control-expose-headers", "file-type");
            response.setHeader("file-type", suffix);
            response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "utf-8"));

            byte[] buff = new byte[1024];
            // 读取文件
            fin = new FileInputStream(cacheFile);

            outputStream = response.getOutputStream();

            int len;
            while ((len = fin.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new KKFileException("预览失败");
        } finally {
            KkFileUtils.close(inputStream);
            KkFileUtils.close(outputStream);
            KkFileUtils.close(fin);
        }

    }

    /**
     * 添加文件到缓存，添加成功之后就直接转成可以预览的文件了
     *
     * @param file 需要预览的文件
     * @return java.lang.String 返回文件编号
     */
    public String addFileToCache(File file) {
        FileAttribute fileAttribute = fileHandlerService.getFileAttribute(file.getName());
        String fileNo = HutoolUUID.randomUUID().toString(true);
        FilePreview filePreview = previewFactory.get(fileAttribute);
        if (filePreview == null) {
            return null;
        }
        this.addFileToCache(file, fileNo);
        return fileNo;
    }

    /**
     * 添加文件到缓存并指定文件编号，添加成功之后就直接转成可以预览的文件了
     *
     * @param file   需要预览的文件
     * @param fileNo 文件编号
     */
    public void addFileToCache(File file, String fileNo) {
        boolean existFile = cacheExistFile(fileNo);
        if (existFile) {
            throw new KKFileException("文件编号已经存在,请尝试其他编号");
        }
        FileAttribute fileAttribute = fileHandlerService.getFileAttribute(file.getName());
        FilePreview filePreview = previewFactory.get(fileAttribute);
        if (filePreview == null) {
            throw new KKFileException("添加缓存失败");
        }
        fileAttribute.setPrefix(fileNo);
        fileAttribute.setName(fileNo + fileAttribute.getSuffix());
        File handleToFile = filePreview.filePreviewHandleToFile(file, fileAttribute);
        if (handleToFile == null) {
            throw new KKFileException("转换失败");
        }
    }

    /**
     * 判断文件是否已经有缓存
     *
     * @param fileNo 文件编号
     * @return boolean
     */
    public boolean cacheExistFile(String fileNo) {
        String convertedFile = fileHandlerService.getConvertedFile(fileNo);
        return convertedFile != null;
    }


    private boolean allowFile(String fileType) {
        return !ALLOW_FILE_TYPE.contains(fileType);
    }

}
