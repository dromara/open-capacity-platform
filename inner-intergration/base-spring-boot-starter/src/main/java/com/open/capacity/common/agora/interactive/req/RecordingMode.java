package com.open.capacity.common.agora.interactive.req;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 录制模式
 * https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful#acquire-%E8%AF%B7%E6%B1%82%E7%A4%BA%E4%BE%8B
 */
public enum RecordingMode {

	/**
	 * 单流模式：分开录制频道内每个 UID 的音频流和视频流，每个 UID 均有其对应的音频文件和视频文件。
	 */
	INDIVIDUAL("individual", "分开录制频道内每个 UID 的音频流和视频流，每个 UID 均有其对应的音频文件和视频文件 "),
	/**
	 * 合流模式  ：（默认模式）频道内所有 UID 的音视频混合录制为一个音视频文件。
	 */
	MIX("mix", "（默认模式）频道内所有 UID 的音视频混合录制为一个音视频文件"),
	/**
	 *页面录制模式 web：将指定网页的页面内容和音频混合录制为一个音视频文件。
	 */
	WEB("web", "将指定网页的页面内容和音频混合录制为一个音视频文件"),

	;

	private String name;

	private String desc;

	private static Logger log = LoggerFactory.getLogger(RecordingMode.class);

	private RecordingMode(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
 
	public static RecordingMode getByName(String name) {
		for (RecordingMode region : RecordingMode.values()) {
			if (region.getName().equalsIgnoreCase(name)) {
				return region;
			}
		}
		log.error("Cannot found RecordingMode with name '" + name + "'.");
		return RecordingMode.MIX;
	}

	public boolean equals(RecordingMode region) {
		return this.compareTo(region) == 0;
	}

}
