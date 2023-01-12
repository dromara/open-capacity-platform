package com.open.capacity.preview.service;

import com.open.capacity.preview.FileAttribute;

import java.io.File;

/**
 * <p>
 * 文件预览接口
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/5/9 11:05
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
public interface FilePreview {

    String PDF_FILE_PREVIEW_PAGE = "pdf";
    String PPT_FILE_PREVIEW_PAGE = "ppt";

    String NOT_SUPPORTED_FILE_PAGE = "fileNotSupported";

    /**
     * 直接转换成可以预览的文件（不加入缓存）
     *
     * @param file          需要转换的文件
     * @param fileAttribute 文件属性
     * @return java.io.File 装换之后的文件
     */
    File convertToViewFile(File file, FileAttribute fileAttribute);

    /**
     * 文件预览 返回文件
     *
     * @param file          文件
     * @param fileAttribute 文件属性
     * @return java.io.File
     */
    File filePreviewHandleToFile(File file, FileAttribute fileAttribute);

    /**
     * 根据文件名获取缓存问题，如果没有返回null
     *
     * @param fileName 文件名称
     * @return java.io.File
     */
    default File getCacheFile(String fileName) {
        return null;
    }
}
