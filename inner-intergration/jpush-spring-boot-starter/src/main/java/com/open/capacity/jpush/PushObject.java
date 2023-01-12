package com.open.capacity.jpush;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PushObject {

	/**
	 * The ID of one application on Local System.
	 */
	private String appId;

	/**
	 * 通知信息
	 */
	private String alert;

	/**
	 * 消息内容
	 */
	private String msgContent;

	// ios声音
	private String sound = "happy";

	// ios右上角条数
	private int badge = 1;

	Map<String, Object> extras = new HashMap<String, Object>();

}
