package com.open.capacity.common.agora.interactive.resp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude( JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class ChannelUserStateResponse extends AgoraResponse {

	/**
	 * 用户状态数据
	 */
	@JsonProperty("data")
	private ChannelUserStateData data;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ChannelUserStateData {

		/**
		 * 1、用户当前是否在频道内，true：是。 false：否
		 */
		@JsonProperty("in_channel")
		private Boolean inChannel;

		/**
		 * 2、用户加入频道的 Unix 时间戳，单位为秒
		 */
		@JsonProperty("join")
		private Long join;

		/**
		 * 3、用户 UID，表示录制的是哪个用户的音频流或视频流。合流录制模式下，uid 为 "0"
		 */
		@JsonProperty("uid")
		private String uid;

		/**
		 * 4、用户在频道内的角色：
		 *     0: 未知
		 *     1：通信场景下的用户
		 *     2：直播场景下的主播
		 *     3：直播场景下的观众
		 */
		@JsonProperty("role")
		private Integer role;

		/**
		 * 5、用户设备所属平台，常见的返回值如下：
		 *     1：Android
		 *     2：iOS
		 *     5：Windows
		 *     6：Linux
		 *     7：Web
		 *     8：macOS
		 *     0：其他平台
		 */
		@JsonProperty("platform")
		private Integer platform;

	}
}
