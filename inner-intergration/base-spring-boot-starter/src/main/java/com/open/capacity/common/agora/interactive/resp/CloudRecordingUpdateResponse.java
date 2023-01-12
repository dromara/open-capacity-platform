package com.open.capacity.common.agora.interactive.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude( JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class CloudRecordingUpdateResponse extends AgoraResponse {

	/**
	 * 1、录制 ID。成功开始云端录制后，你会得到一个 sid （录制 ID)。该 ID 是一次录制周期的唯一标识
	 */
	@JsonProperty("sid")
	private String sid;

	/**
	 * 2、云端录制使用的 resource ID
	 */
	@JsonProperty("resourceId")
	private String resourceId;

}
