package com.open.capacity.file.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.open.capacity.common.model.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 file实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info")
public class FileInfo extends BaseEntity<FileInfo> {

	private static final long serialVersionUID = -1438078028040922174L;

	private String md5;
//  原始文件名
	private String name;
//	是否图片
	private Boolean isImg;
//	上传文件类型
	private String contentType;
//	文件大小
	private long size;
//  冗余字段
	private String path;
//	oss访问路径 oss需要设置公共读
	private String url;
 
	private Date createTime;
	/**
	 * 目录磁盘地址
	 */
	@TableField(exist = false)
	private String pathDir;
}
