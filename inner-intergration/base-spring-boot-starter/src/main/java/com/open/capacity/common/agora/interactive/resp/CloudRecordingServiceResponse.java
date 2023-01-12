package com.open.capacity.common.agora.interactive.resp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudRecordingServiceResponse {
	
	/**
	 * 1、fileList 字段的数据格式。
	 * "string"：fileList 为 String 类型。合流模式下，如果 avFileType 设置为 ["hls"]，fileListMode 为 "string"。
	 * "json"：fileList 为 JSONArray 类型。单流模式下，或合流模式下 avFileType 设置为 ["hls","mp4"]时，fileListMode 为 "json"。
	 */
	@JsonProperty("fileListMode")
	private String fileListMode;

	/**
	 * 2、当 fileListMode 为 "string" 时，fileList 为 String 类型，录制产生的 M3U8 文件的文件名。当
	 *  fileListMode 为 "json" 时, fileList 为 JSONArray 类型，由每个录制文件的具体信息组成的数组。如果你设置了 snapshotConfig，则不会返回该字段
	 */
	@JsonProperty("fileList")
	private List<CloudRecordingServiceFile> fileList;

	/**
	 * 3、当前云服务的状态。
	 * 0：没有开始云服务。
	 * 1：云服务初始化完成。
	 * 2：云服务组件开始启动。
	 * 3：云服务部分组件启动完成。
	 * 4：云服务所有组件启动完成。
	 * 5：云服务正在进行中。
	 * 6：云服务收到停止请求。
	 * 7：云服务所有组件均停止。
	 * 8：云服务已退出。
	 * 20：云服务异常退出。
	 */
	@JsonProperty("status")
	private Integer status;

	/**
	 * 4、录制开始的时间，Unix 时间戳，单位为毫秒
	 */
	@JsonProperty("sliceStartTime")
	private Long sliceStartTime;
	
	/**
	 * 5、云端录制子模块的状态。页面录制模式下不会返回该字段
	 */
	@JsonProperty("subServiceStatus")
	private CloudRecordingSubServiceStatus subServiceStatus;

	/**
	 * 6、由每个扩展服务的详细状态信息组成的数组
	 */
	@JsonProperty("extensionServiceState")
	private List<CloudRecordingExtensionServiceState> extensionServiceState;
	
}