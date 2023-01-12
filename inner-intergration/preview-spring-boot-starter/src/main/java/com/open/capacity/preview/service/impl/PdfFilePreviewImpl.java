package com.open.capacity.preview.service.impl;

import com.open.capacity.preview.FileAttribute;
import com.open.capacity.preview.service.FileHandlerService;
import com.open.capacity.preview.service.FilePreview;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * <p>
 * pdf直接不处理，可以直接预览
 * <p/>
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 * @since 2022/7/7 16:26
 */
@Service
public class PdfFilePreviewImpl implements FilePreview {

    private final FileHandlerService fileHandlerService;

    public PdfFilePreviewImpl(FileHandlerService fileHandlerService) {
        this.fileHandlerService = fileHandlerService;
    }

    @Override
    public File convertToViewFile(File file, FileAttribute fileAttribute) {
        return file;
    }

    @Override
    public File filePreviewHandleToFile(File file, FileAttribute fileAttribute) {
        return file;
    }
}
