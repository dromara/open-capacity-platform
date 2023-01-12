package com.open.capacity.oss.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class OssUploadEntity {
    @ApiModelProperty("图片的base64数据")
    private String base64Data;
    @ApiModelProperty("是否使用oss")
    private boolean oss;
}