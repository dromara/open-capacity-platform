package com.open.capacity.common.agora;

import java.text.MessageFormat;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful
 * https://docs.agora.io/cn/Video/channel_management_overview?platform=RESTful
 */
public enum AgoraApiAddress {

	// ---------------- 云端录制 ------------------

	/**
	 * 获取云端录制资源ID
	 * URL: https://api.agora.io/v1/apps/<yourappid>/cloud_recording/acquire
	 */
	ACQUIRE_RESOURCE_ID("获取云端录制资源ID", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/acquire"),
	/**
	 * 开始云端录制
	 * URL: https://api.agora.io/v1/apps/<yourappid>/cloud_recording/resourceid/<resourceid>/mode/<mode>/start
	 */
	START_CLOUD_RECORDING("开始云端录制", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/resourceid/{1}/mode/{2}/start"),
	/**
	 * 更新云端录制
	 * URL: https://api.agora.io/v1/apps/<appid>/cloud_recording/resourceid/<resourceid>/sid/<sid>/mode/<mode>/updateLayout
	 */
	UPDATE_CLOUD_RECORDING("更新云端录制", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/resourceid/{1}/sid/{2}/mode/{3}/updateLayout"),
	/**
	 * 更新合流布局
	 * URL: https://api.agora.io/v1/apps/<appid>/cloud_recording/resourceid/<resourceid>/sid/<sid>/mode/<mode>/update
	 */
	UPDATE_CLOUD_RECORDING_LAYOUT("更新合流布局", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/resourceid/{1}/sid/{2}/mode/{3}/update"),
	/**
	 * 查询云端录制状态
	 * URL: https://api.agora.io/v1/apps/<yourappid>/cloud_recording/resourceid/<resourceid>/sid/<sid>/mode/<mode>/query
	 */
	QUERY_CLOUD_RECORDING("查询云端录制状态", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/resourceid/{1}/sid/{2}/mode/{3}/query"),
	/**
	 * 停止云端录制
	 * URL:  https://api.agora.io/v1/apps/<yourappid>/cloud_recording/resourceid/<resourceid>/sid/<sid>/mode/<mode>/stop
	 */
	STOP_CLOUD_RECORDING("停止云端录制", RequestMethod.POST,"https://api.agora.io/v1/apps/{0}/cloud_recording/resourceid/{1}/sid/{2}/mode/{3}/stop"),

	// ---------------- 项目管理 ------------------

	/**
	 * 创建项目
	 */
	PROJECT_POST("创建项目", RequestMethod.POST,"https://api.agora.io/v1/project"),
	/**
	 * 获取指定项目
	 */
	PROJECT_GET("获取指定项目", RequestMethod.POST,"https://api.agora.io/v1/project"),
	/**
	 * 获取所有项目
	 */
	PROJECTS_GET("获取所有项目", RequestMethod.POST,"https://api.agora.io/v1/projects"),
	/**
	 * 禁用或启用项目
	 */
	PROJECT_STATUS_POST("禁用或启用项目", RequestMethod.POST,"https://api.agora.io/v1/projects_status"),
	/**
	 *获取指定项目的用量数据
	 */
	PROJECT_USAGE_GET("获取指定项目的用量数据", RequestMethod.POST,"https://api.agora.io/v3/usage"),
	/**
	 * 设置录制服务器 IP
	 */
	RECORDING_CONFIG_POST("设置录制服务器 IP", RequestMethod.POST,"https://api.agora.io/v1/recording_config"),
	/**
	 * 启用或禁用主要 App 证书
	 */
	SIGNKEY_POST("启用或禁用主要 App 证书", RequestMethod.POST,"https://api.agora.io/v1/signkey"),
	/**
	 *重置主要 App 证书
	 */
	SIGNKEY_RESET_POST("重置主要 App 证书", RequestMethod.POST,"https://api.agora.io/v1/reset_signkey"),

	// ---------------- 封禁用户权限 ------------------

	/**
	 * 创建封禁用户权限规则
	 */
	KICKING_RULE_POST("创建封禁用户权限规则", RequestMethod.POST,"https://api.agora.io/v1/kicking-rule"),
	/**
	 * 获取封禁用户权限规则列表
	 */
	KICKING_RULE_GET("获取封禁用户权限规则列表", RequestMethod.POST,"https://api.agora.io/v1/kicking-rule"),
	/**
	 * 更新封禁用户权限规则的生效时间
	 */
	KICKING_RULE_PUT("更新封禁用户权限规则的生效时间", RequestMethod.POST,"https://api.agora.io/v1/kicking-rule"),
	/**
	 * 删除封禁用户权限规则
	 */
	KICKING_RULE_DELETE("删除封禁用户权限规则", RequestMethod.POST,"https://api.agora.io/v1/kicking-rule"),

	// ---------------- 查询在线频道信息 ------------------

	/**
	 * 查询用户状态
	 * URL:  https://api.agora.io/v1/channel/user/property/{appid}/{uid}/{channelName}
	 */
	CHANNEL_USER_STATE("查询用户状态", RequestMethod.GET,"https://api.agora.io/dev/v1/channel/user/property/{0}/{1}/{2}"),
	/**
	 * 获取用户列表
	 * URL:  https://api.agora.io/v1/channel/user/{appid}/{channelName}
	 */
	CHANNEL_USER_LIST("获取用户列表", RequestMethod.GET,"https://api.agora.io/dev/v1/channel/user/{0}/{1}"),
	/**
	 * 分页查询项目的频道列表
	 * URL:  https://api.agora.io/v1/channel/{appid}
	 */
	CHANNEL_LIST("分页查询项目的频道列表", RequestMethod.GET,"https://api.agora.io/dev/v1/channel/{0}"),

	 ;

	private String opt;

	private RequestMethod method;
	private String url;

    AgoraApiAddress(String opt, RequestMethod method,String url) {
		this.opt = opt;
		this.method = method;
		this.url = url;
	}

	public String getOpt() {
		return opt;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getUrl(Object ...args) {
		return MessageFormat.format(url, args);
	}

}
