package com.open.capacity.common.agora;

import java.io.IOException;

import com.open.capacity.common.agora.interactive.resp.ChannelUserListResponse;
import com.open.capacity.common.agora.interactive.resp.ChannelUserStateResponse;

/**
 * 1、频道管理
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * https://docs.agora.io/cn/Video/channel_management_overview?platform=RESTful
 */
public class AgoraChannelManagerOperations extends AgoraOperations {

	public AgoraChannelManagerOperations(AgoraTemplate agoraTemplate) {
		super(agoraTemplate);
	}

	/**
	 * 1、查询在线频道信息 > 查询用户状态
	 * 该方法可查询指定频道中某个用户的状态。请求成功后，返回的参数包括用户是否在频道中、加入频道的时间和用户角色等。
	 * API：https://docs.agora.io/cn/Video/rtc_channel_management_restfulapi?platform=RESTful#%E6%9F%A5%E8%AF%A2%E5%9C%A8%E7%BA%BF%E9%A2%91%E9%81%93%E4%BF%A1%E6%81%AF
	 * @param uid 字符串内容为云端录制服务在频道内使用的 UID，用于标识该录制服务，例如"527841"。需满足以下条件：
	 * a、取值范围 1 到 (232-1)，不可设置为 0。
	 * b、不能与当前频道内的任何 UID 重复。
	 * c、云端录制不支持 String 用户 ID（User Account），请确保该字段引号内为整型 UID，且频道内所有用户均使用整型 UID。
	 * @param channelName 频道名称
	 * @return 操作结果
	 */
	public ChannelUserStateResponse getChannelUserState(String uid, String channelName) throws IOException {
		String reqUrl = AgoraApiAddress.CHANNEL_USER_STATE.getUrl(getAgoraProperties().getAppId(), uid, channelName);
		ChannelUserStateResponse resp = super.get(AgoraApiAddress.CHANNEL_USER_STATE, reqUrl, ChannelUserStateResponse.class);
		return resp;
	}


	/**
	 * 2、查询在线频道信息 > 查询指定频道内的用户列表
	 * 在不同频道场景下，该方法返回的列表具体如下：
	 *     通信场景下，频道内的用户列表。
	 *     直播场景下，频道内的主播列表和观众列表。
	 * API：https://docs.agora.io/cn/Video/rtc_channel_management_restfulapi?platform=RESTful#%E6%9F%A5%E8%AF%A2%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8
	 * @param channelName 频道名称
	 * @return 操作结果
	 */
	public ChannelUserListResponse getChannelUserList(String channelName) throws IOException {
		String reqUrl = AgoraApiAddress.CHANNEL_USER_LIST.getUrl(getAgoraProperties().getAppId(), channelName);
		ChannelUserListResponse resp = super.get(AgoraApiAddress.CHANNEL_USER_LIST, reqUrl, ChannelUserListResponse.class);
		return resp;
	}

	/**
	 * 3、查询在线频道信息 > 查询项目的频道列表
	 * 该方法按页查询指定项目下的频道列表。你可以在请求路径中指定要查询的页面和每页显示的频道数量。请求成功后，会根据你指定的 page_size 返回指定页面的频道列表。
	 * API：https://docs.agora.io/cn/Video/rtc_channel_management_restfulapi?platform=RESTful#%E6%9F%A5%E8%AF%A2%E9%A1%B9%E7%9B%AE%E7%9A%84%E9%A2%91%E9%81%93%E5%88%97%E8%A1%A8
	 * @param pageNo 你想要查询的页面，默认值为 0，即第一页。
	 * 注意事项：page_no 的取值不能大于（频道总数/每页显示的频道数 - 1）。否则，指定的页面将不包含任何频道列表。
	 * @param pageSize 每个页面显示的频道数量，取值范围为 [1,500]，默认值为 100。
	 * @return 操作结果
	 */
	public ChannelUserListResponse getChannelList(Integer pageNo, Integer pageSize) throws IOException {
		String reqUrl = AgoraApiAddress.CHANNEL_LIST.getUrl(getAgoraProperties().getAppId());
		ChannelUserListResponse resp = super.get(AgoraApiAddress.CHANNEL_LIST, reqUrl, ChannelUserListResponse.class);
		return resp;
	}

}
