package com.open.capacity.file.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.open.capacity.common.model.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件分片表
 * 
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 file实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info_part")
public class FileInfoPart extends BaseEntity<FileInfoPart> {

	private static final long serialVersionUID = 1L;

	private String md5;
	/**
	 * 文件分片id
	 */
	private String guid;

	// s3 upload id
	private String uploadId;
	// 原始文件名
	private String name;
	// 是否图片
	private Boolean isImg;
	// 上传文件类型
	private String contentType;
	// 文件大小
	private Long size;

	/**
	 * 物理路径
	 */
	private String path;

	private String fileId;

	private String eTag;

	private Integer part;

	private Date createTime;
	/**
	 * 目录磁盘地址
	 */
	@TableField(exist = false)
	private String pathDir;

//	@TableField(value = "max(upload_id)" ,insertStrategy = FieldStrategy.NEVER,updateStrategy =   FieldStrategy.NEVER )
//	private String maxUploadId ;
}
