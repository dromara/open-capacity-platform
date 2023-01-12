package com.open.capacity.common.agora.interactive.req;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 录制文件设置
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class RecordingFileConfig {

	/**
	 * 1、由多个字符串组成的数组，指定录制的视频文件类型（选填）。云端录制会生成 avFileType 中包含的所有文件类型。目前支持以下值：
	 * "hls"：默认值，即录制生成 M3U8 和 TS 文件。
	 * "mp4"：录制生成 MP4 文件。只有在合流录制模式（mix）和页面录制模式（web）下，才可设置 "mp4"，且设置 "mp4" 时必须同时设置 "hls"，否则会收到错误码 2。录制服务会在当前 MP4 文件时长超过约 2 小时或大小超过 2 GB 左右时创建一个新的 MP4 文件。
	 */
	@JsonProperty("avFileType")
    private List<String> fileTypes = Arrays.asList("hls", "mp4");

}
