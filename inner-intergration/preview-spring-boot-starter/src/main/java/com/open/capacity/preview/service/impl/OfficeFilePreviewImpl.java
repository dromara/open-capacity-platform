package com.open.capacity.preview.service.impl;

import com.open.capacity.preview.ConfigConstants;
import com.open.capacity.preview.FileAttribute;
import com.open.capacity.preview.service.FileHandlerService;
import com.open.capacity.preview.service.FilePreview;
import com.open.capacity.preview.service.OfficeToPdfService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author luowj
 * @since copy from cn.keking.service.impl.OfficeFilePreviewImpl
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    private static final String CONVERT_DIR = ConfigConstants.getConvertDir();

    private final FileHandlerService fileHandlerService;
    private final OfficeToPdfService officeToPdfService;

    public OfficeFilePreviewImpl(FileHandlerService fileHandlerService, OfficeToPdfService officeToPdfService) {
        this.fileHandlerService = fileHandlerService;
        this.officeToPdfService = officeToPdfService;
    }

    @Override
    public File convertToViewFile(File file, FileAttribute fileAttribute) {
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        boolean isHtml = suffix.equalsIgnoreCase(XLS) || suffix.equalsIgnoreCase(XLSX);
        String fileNo = fileName.substring(0, fileName.lastIndexOf("."));
        String convertFileName = fileNo + (isHtml ? ".html" : ".pdf");
        String outFilePath = CONVERT_DIR + convertFileName;
        // 判断之前是否已转换过，如果转换过，直接返回，否则执行转换
        if (!fileHandlerService.listConvertedFiles().containsKey(fileNo)) {
            String filePath = file.getPath();
            if (StringUtils.isNotBlank(outFilePath)) {
                officeToPdfService.openOfficeToPDF(filePath, outFilePath);
                if (isHtml) {
                    // 对转换后的文件进行操作(改变编码方式)
                    fileHandlerService.doActionConvertedFile(outFilePath);
                }
            }
        }
        File resultFile = new File(outFilePath);
        if (resultFile.exists()) {
            return resultFile;
        }
        return null;
    }

    @Override
    public File filePreviewHandleToFile(File file, FileAttribute fileAttribute) {
        String suffix = fileAttribute.getSuffix();
        boolean isHtml = suffix.equalsIgnoreCase(XLS) || suffix.equalsIgnoreCase(XLSX);
        String fileNo = fileAttribute.getPrefix();
        String pdfName = fileAttribute.getPrefix() + (isHtml ? ".html" : ".pdf");
        String outFilePath = ConfigConstants.getFileDir() + pdfName;
        // 判断之前是否已转换过，如果转换过，直接返回，否则执行转换
        if (!fileHandlerService.listConvertedFiles().containsKey(fileNo)) {
            String filePath = file.getPath();
            if (StringUtils.isNotBlank(outFilePath)) {
                officeToPdfService.openOfficeToPDF(filePath, outFilePath);
                if (isHtml) {
                    // 对转换后的文件进行操作(改变编码方式)
                    fileHandlerService.doActionConvertedFile(outFilePath);
                }
                if (Boolean.TRUE.equals(ConfigConstants.isCacheEnabled())) {
                    // 加入缓存
                    fileHandlerService.addConvertedFile(fileNo, fileHandlerService.getRelativePath(outFilePath));
                }
            }
        }
        File resultFile = new File(outFilePath);
        if (resultFile.exists()) {
            return resultFile;
        }
        return null;
    }

    @Override
    public File getCacheFile(String fileNo) {
        if (StringUtils.isEmpty(fileNo)) {
            return null;
        }
        return fileHandlerService.getCacheFile(fileNo);
    }

}
