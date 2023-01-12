package com.open.capacity.common.agora.interactive.resp;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudRecordingExtensionServiceState {

	/**
	 * 1、扩展服务类型。 
	 * "aliyun_vod_service" 代表阿里云视频点播服务
	 * "web_recorder_service" 代表页面录制
	 * "rtmp_publish_service" 代表页面录制并推流到 CDN
	 */
	@JsonProperty("serviceName")
	private String serviceName;
	
	/**
	 * 2、该扩展服务的状态信息
	 */
	@JsonProperty("payload")
	private JSONObject payload;
	
}