package com.open.capacity.user.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.open.capacity.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户导出实例
 * @Author: someday
 */
@Data
public class SysUserExcel implements Serializable {

	private static final long serialVersionUID = -2753486089127204660L;

	@Excel( needMerge = true, name = "用户姓名", height = 20, width = 30, isImportField = "true")
    private String username;

    @Excel( needMerge = true,name = "用户昵称", height = 20, width = 30, isImportField = "true")
    private String nickname;

    @Excel(needMerge = true, name = "手机号码", height = 20, width = 30, isImportField = "true")
    private String mobile;

    @Excel(needMerge = true, name = "性别", replace = { "男_0", "女_1" }, isImportField = "true")
    private Integer sex;

    @Excel(needMerge = true, name = "创建时间", format = CommonConstant.DATETIME_FORMAT, isImportField = "true", width = 20)
    private Date createTime;

    @Excel(needMerge = true,name = "修改时间", format = CommonConstant.DATETIME_FORMAT, isImportField = "true", width = 20)
    private Date updateTime;
}
