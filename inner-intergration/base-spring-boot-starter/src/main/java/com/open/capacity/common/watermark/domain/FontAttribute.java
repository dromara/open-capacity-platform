package com.open.capacity.common.watermark.domain;

import lombok.Builder;
import lombok.Data;

/**
 * 字体属性
 *
 * @author owen
 * @date 2022/09/29 14:59:25
 */
@Data
@Builder
public class FontAttribute {

    private int width;
    private int height;

}
