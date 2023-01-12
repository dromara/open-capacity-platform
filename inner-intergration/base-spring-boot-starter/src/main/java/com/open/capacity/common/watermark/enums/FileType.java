package com.open.capacity.common.watermark.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型
 * @author owen
 * @date 2022/09/29 14:59:25
 */
@Getter
@AllArgsConstructor
public enum FileType {

    // word(.doc)
    DOC("application/msword"),

    // powerpoint(.ppt)
    PPT("application/vnd.ms-powerpoint"),

    // excel(.xls)
    XLS("application/vnd.ms-excel"),

    // excel(.xls)
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    // powerpoint(.pptx)
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    // excel(.xlsx)
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    // rar
    RAR("application/x-rar-compressed"),

    // zip
    ZIP("application/zip"),

    // pdf
    PDF("application/pdf"),

    // rtf
    RTF("application/rtf"),

    // 视频文件
    VIDEO("video/.*"),

    // 音频文件
    AUDIO("audio/.*"),

    // 图片文件
    IMAGE("image/.*"),

    // 	纯文本
    PLAIN("text/plain"),

    // 	css文件
    CSS("text/css"),

    // html文件
    HTML("text/html"),

    // java源代码
    JAVA("text/x-java-source"),

    // c源代码
    CSRC("text/x-csrc"),

    // 	c++源代码
    CPLUSSRC("text/x-c++src"),


    ;

    private String value;

















}
