package com.open.capacity.file.event;

import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.file.constant.FileType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadEvent extends BaseEvent {
	
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
	 * 文件
	 */
	private String fileId ;
 
	/**
	 * 断点下载
	 */
	private String range ;

}
