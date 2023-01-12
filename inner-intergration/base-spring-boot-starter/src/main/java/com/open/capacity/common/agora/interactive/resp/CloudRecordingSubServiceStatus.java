package com.open.capacity.common.agora.interactive.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudRecordingSubServiceStatus {

	/**
	 * 1、订阅模块的状态
	 */
	@JsonProperty("recordingService")
	private String recordingService;
	
}