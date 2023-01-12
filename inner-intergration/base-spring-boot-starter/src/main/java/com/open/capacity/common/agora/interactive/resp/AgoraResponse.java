package com.open.capacity.common.agora.interactive.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class AgoraResponse {

	/**
	 * 响应状态码，200表示成功，非200表示失败
	 * https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful#status
	 */
	@JsonProperty("Code")
	private int code;

	/**
	 * 本次请求的状态，true 请求成功，false 预留
	 * https://docs.agora.io/cn/Video/rtc_channel_management_restfulapi?platform=RESTful#%E6%9F%A5%E8%AF%A2%E7%94%A8%E6%88%B7%E7%8A%B6%E6%80%81
	 */
	@JsonProperty("success")
	private boolean success;

	public boolean isSuccess() {
		return code == 200 || success == true;
	}

}
