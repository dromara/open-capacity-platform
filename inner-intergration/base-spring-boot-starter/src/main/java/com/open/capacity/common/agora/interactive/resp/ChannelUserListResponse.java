package com.open.capacity.common.agora.interactive.resp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude( JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = false)
public class ChannelUserListResponse extends AgoraResponse {

	/**
	 * 响应体
	 */
	@JsonProperty("data")
	private ChannelUserListData data;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ChannelUserListData {

		/**
		 * 1、指定的频道是否存在
		 *     true：存在。
		 *     false：不存在。
		 *
		 * 注意事项：当 channel_exist 的值为 false 时，不会返回其他字段
		 */
		@JsonProperty("channel_exist")
		private Boolean exist;

		/**
		 * 2、频道场景
		 *     1：通信场景
		 *     2：直播场景
		 */
		@JsonProperty("mode")
		private Integer mode;

		/**
		 * 3、频道内的用户总人数。该字段仅在通信场景 （mode 的值为 1）下返回。
		 */
		@JsonProperty("total")
		private Long total;

		/**
		 * 4、频道内所有用户的用户 ID。该字段仅在通信场景 （mode 的值为 1）下返回
		 */
		@JsonProperty("users")
		private List<String> users;

		/**
		 * 5、频道内所有主播的用户 ID。该字段仅在直播场景 （mode 的值为 2）下返回
		 */
		@JsonProperty("broadcasters")
		private List<String> broadcasters;

		/**
		 * 6、频道内观众的用户 ID。最多包含当前频道内前 10,000 名观众的用户 ID。该字段仅在直播场景 （mode 的值为 2）下返回
		 */
		@JsonProperty("audience")
		private List<String> audience;

		/**
		 * 7、频道内的观众总人数。该字段仅在直播场景 （mode 的值为 2）下返回
		 */
		@JsonProperty("audience_total")
		private Long audienceTotal;

	}

}
