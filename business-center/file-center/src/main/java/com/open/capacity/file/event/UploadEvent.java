package com.open.capacity.file.event;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.file.constant.FileType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadEvent extends BaseEvent {
	
	/**
	 * 文件存储
	 */
	private FileType fileType ;
	
	/**
	 * 单个批量上传
	 */
	private String commandType ;
	
	/**
	 * 租户
	 */
	private String tenant ;
	
	/**
	 * 分片ID
	 */
	private String guid ;
	
	/**
	 * 当前分片
	 */
	private Integer chunk ;
	
	/**
	 * 总分片
	 */
	private Integer chunks ;
	
	/**
	 * 文件
	 */
	private List<MultipartFile>  files;

}
