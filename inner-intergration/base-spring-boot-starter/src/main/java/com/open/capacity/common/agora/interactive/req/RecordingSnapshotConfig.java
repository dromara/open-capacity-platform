package com.open.capacity.common.agora.interactive.req;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 截图设置 
 * 1、使用云端录制进行截图，需要注意以下参数的设置。设置错误会收到报错，或无法生成截图文件。 
 * a、请求 URL 中的 mode 参数必须设为individual。 
 * b、如果在一个录制进程中同时进行录制和截图，则必须设置 recordingFileConfig参数；
 * c、如果在一个录制进程中仅截图，则不可设置该参数。 streamTypes 必须设置为 1 或 2。 
 * d、如果设置了 subscribeAudioUid，则必须同时设置 subscribeVideoUids。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordingSnapshotConfig {

	/**
	 * 1、 截图周期（s），云端录制会按此周期定期截图。取值范围是 [1, 3600]，默认值 10。
	 */
	@JsonProperty("captureInterval")
	private Integer captureInterval = 10;

	/**
	 * 2、 由多个字符串组成的数组，指定截图的文件格式。目前只支持 ["jpg"]，即生成 JPG 截图文件
	 */
	@JsonProperty("fileType")
    private List<String> fileTypes = Arrays.asList("jpg");

}
