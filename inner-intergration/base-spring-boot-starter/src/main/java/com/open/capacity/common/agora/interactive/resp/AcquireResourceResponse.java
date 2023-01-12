package com.open.capacity.common.agora.interactive.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AcquireResourceResponse extends AgoraResponse {

	/**
	 * 1、云端录制资源 resource ID，使用这个 resource ID 可以开始一段云端录制。这个 resource ID 的有效期为 5 分钟，超时需要重新请求。
	 */
	@JsonProperty("resourceId")
	private String resourceId;

	/**
	 * 2、云端录制使用的频道名
	 */
	@JsonIgnore
	private String cname;
}
