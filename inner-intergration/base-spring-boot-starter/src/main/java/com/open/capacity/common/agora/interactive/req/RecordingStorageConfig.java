package com.open.capacity.common.agora.interactive.req;

import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 存储参数(阿里云配置)
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class RecordingStorageConfig {

    /**
     * 1、第三方云存储平台
     * 0：七牛云
     * 1：Amazon S3
     * 2：阿里云
     * 3：腾讯云
     * 4：金山云
     * 5：Microsoft Azure
     * 6：谷歌云
     * 7：华为云
     * 8：百度智能云
     */
	@JsonProperty("vendor")
    private Integer vendor;

    /**
     * 2、第三方云存储指定的地区信息。录制服务仅支持以下列表中的地区
     * a: 为了确保录制文件上传的成功率和实时性，如果你在 acquire 方法中设置了云端录制的 region，则需要保证第三方云存储的 region 与云端录制的 region 在同一个地域中。
     * b、例如：云端录制的 region 设置为 CN（中国区），第三方云存储需要设置为 CN 内的区域
     * https://docs.agora.io/cn/cloud-recording/cloud_recording_api_rest?platform=RESTful#start：开始云端录制的-api
     */
	@JsonProperty("region")
    private Integer region;

    /**
     * 3、第三方云存储的 bucket，bucket 名称需要符合对应第三方云存储服务的命名规则
     */
	@JsonProperty("bucket")
    private String bucket;

	/**
	 * 4、第三方云存储的 access key。在一般情况下，建议提供只写权限的访问密钥。如需延时转码，则访问密钥必须同时具备读写权限
	 */
	@JsonProperty("accessKey")
    private String accessKey;
	
	/**
	 * 5、第三方云存储的 secret key
	 */
	@JsonProperty("secretKey")
    private String secretKey;

	/**
	 * 6、由多个字符串组成的数组，指定录制文件在第三方云存储中的存储位置。
	 * a、举个例子，fileNamePrefix = ["directory1","directory2"]，将在录制文件名前加上前缀 "directory1/directory2/"，即 directory1/directory2/xxx.m3u8。
	 * b、前缀长度（包括斜杠）不得超过 128 个字符。字符串中不得出现斜杠、下划线、括号等符号字符。以下为支持的字符集范围：
	 * 26 个小写英文字母 a-z
	 * 26 个大写英文字母 A-Z
	 * 10 个数字 0-9
	 */
	@JsonProperty("fileNamePrefix")
    private List<String> fileNamePrefix;

}
