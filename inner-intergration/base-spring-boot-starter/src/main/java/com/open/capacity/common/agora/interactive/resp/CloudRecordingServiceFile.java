package com.open.capacity.common.agora.interactive.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudRecordingServiceFile {
	
	/**
	 * 1、录制产生的 M3U8 文件和 MP4 文件的文件名
	 */
	@JsonProperty("fileName")
	private String fileName;

	/**
	 * 2、录制文件的类型
	 *  "audio"：纯音频文件。
	 *  "video"：纯视频文件。
	 *  "audio_and_video"：音视频文件。
	 */
	@JsonProperty("trackType")
	private String trackType;

	/**
	 * 3、用户 UID，表示录制的是哪个用户的音频流或视频流。合流录制模式下，uid 为 "0"
	 */
	@JsonProperty("uid")
	private String uid;

	/**
	 * 4、是否可以在线播放
	 * true：可以在线播放。
	 * false：无法在线播放
	 */
	@JsonProperty("mixedAllUser")
	private Boolean mixedAllUser;

	/**
	 * 5、该文件的录制开始时间，Unix 时间戳，单位为毫秒
	 */
	@JsonProperty("sliceStartTime")
	private Long sliceStartTime;

}