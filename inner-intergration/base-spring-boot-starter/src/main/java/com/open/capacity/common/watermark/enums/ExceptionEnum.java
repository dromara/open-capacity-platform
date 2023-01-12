package com.open.capacity.common.watermark.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常枚举
 *
 * @author owen
 * @date 2022/09/29 14:59:25
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    // 必须是xxx类型文件
    THE_FILE_MUST_BE_OF_TYPE_XXX("The file must be of type %s") ;

    private String value;


}
