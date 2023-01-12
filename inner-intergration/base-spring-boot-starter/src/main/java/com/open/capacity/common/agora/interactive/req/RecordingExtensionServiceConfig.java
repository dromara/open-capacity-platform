package com.open.capacity.common.agora.interactive.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 扩展服务的设置，目前包括阿里云视频点播服务和页面录制的设置
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class RecordingExtensionServiceConfig {

    /**
     * 1、错误处理策略（选填）。目前仅可设置为默认值 "error_abort"，表示当某一扩展服务发生错误后，订阅及其他非扩展服务均停止。
     */
	@JsonProperty("errorHandlePolicy")
    private String errorHandlePolicy;
	
	/**
     * 2、云端录制 RESTful API 的版本号，默认为 "v1"（选填）
     */
	@JsonProperty("apiVersion")
    private String apiVersion =  "v1";
	
	/**
	 * 3、每个扩展服务的设置组成的数组
	 */
	@JsonProperty("extensionServices")
    private List<ExtensionService> extensionServices;
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class ExtensionService {
		
		/**
		 * 1、扩展服务的名称。要使用阿里云视频点播服务（VoD），你需要将其设置为 "aliyun_vod_service"。
		 */
		@JsonProperty("serviceName")
		private String serviceName;

		/**
		 * 2、错误处理策略。目前仅可设置为默认值 "error_abort"，表示如果当前扩展服务发生错误，其他扩展服务均停止。
		 */
		@JsonProperty("errorHandlePolicy")
		private String errorHandlePolicy = "error_abort";
		
		/**
		 * 3、扩展服务的具体参数设置
		 */
		@JsonProperty("serviceParam")
		private ExtensionServiceParam serviceParam;
		
	}
	
	public interface ExtensionServiceParam {
		
		
	}
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class AliyunVodServiceParam implements ExtensionServiceParam{
		
		/**
		 * 1、阿里云访问密钥 AccessKey 中的 AccessKeyId
		 */
		@JsonProperty("accessKey")
		private String accessKey;

		/**
		 * 2、阿里云访问密钥 AccessKey 中的 AccessKeySecret
		 */
		@JsonProperty("secretKey")
		private String secretKey;
		
		/**
		 * 3、接入区域标识
		 */
		@JsonProperty("regionId")
		private String regionId;

		/**
		 * 4、阿里云视频点播服务的详细设置
		 */
		@JsonProperty("apiData")
		private AliyunVodServiceParamApiData apiData;
		
	}
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class AliyunVodServiceParamApiData {
		
		/**
		 * 1、视频设置
		 */
		@JsonProperty("videoData")
		private AliyunVodServiceParamVideoData videoData;
		
		
	}
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class AliyunVodServiceParamVideoData {
		
		/**
		 * 1、视频标题
		 */
		@JsonProperty("title")
		private String title;

		/**
		 * 2、视频描述（选填）
		 */
		@JsonProperty("description")
		private String description;
		
		/**
		 * 3、自定义视频封面的 URL 地址（选填）
		 */
		@JsonProperty("coverUrl")
		private String coverUrl;

		/**
		 * 4、字符串内容为视频分类 ID，必须为整型（选填）
		 */
		@JsonProperty("cateId")
		private String cateId;

		/**
		 * 5、视频标签（选填）
		 */
		@JsonProperty("tags")
		private String tags;

		/**
		 * 6、转码模板组 ID（选填）
		 */
		@JsonProperty("templateGroupId")
		private String templateGroupId;

		/**
		 * 7、自定义设置（选填）
		 */
		@JsonProperty("userData")
		private String userData;

		/**
		 * 8、存储地址（选填）
		 */
		@JsonProperty("storageLocation")
		private String storageLocation;

		/**
		 * 9、工作流 ID（选填）
		 */
		@JsonProperty("workFlowId")
		private String workFlowId;
		
	}
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class WebRecorderServiceParam implements ExtensionServiceParam{
		
		/**
		 * 1、设置待录制页面的地址
		 */
		@JsonProperty("url")
		private String url;

		/**
		 * 2、输出视频的码率，单位为 kbps，范围为 [50, 8000]。针对不同的输出视频分辨率，videoBitrate 的默认值不同：
		 * a、输出视频分辨率大于或等于 1280 × 720：默认值为 2000
		 * b、输出视频分辨率小于 1280 × 720：默认值为 1500
		 */
		@JsonProperty("videoBitrate")
		private Float videoBitrate;
		
		/**
		 * 3、输出视频的帧率，单位为 fps，范围为 [5, 60]，默认值为 15
		 */
		@JsonProperty("videoFps")
		private Float videoFps;
		
		/**
		 * 4、设置输出音频的采样率、码率、编码模式和声道数。
		 * 0：48 kHz 采样率，音乐编码，单声道，编码码率约 48 Kbps
		 * 1：48 kHz 采样率，音乐编码，单声道，编码码率约 128 Kbps
		 * 2：48 kHz 采样率，音乐编码，双声道，编码码率约 192 Kbps
		 */
		@JsonProperty("audioProfile")
		private Integer audioProfile;

		/**
		 * 5、设置输出视频的宽度，单位为 pixel，范围为 [480, 1920]。videoWidth 和 videoHeight 的乘积需小于等于 1920 × 1080
		 */
		@JsonProperty("videoWidth")
		private Integer videoWidth;

		/**
		 * 6、设置输出视频的高度，单位为 pixel，范围为 [480, 1920]。videoWidth 和 videoHeight 的乘积需小于等于 1920 × 1080
		 */
		@JsonProperty("videoHeight")
		private Integer videoHeight;

		/**
		 * 7、设置录制的最大时长，单位为小时，范围为 [1,720]。当录制时长超过 maxRecordingHour，录制会自动停止。建议 maxRecordingHour 不超过你在 acquire 方法中设置的 resourceExpiredHour 的值
		 */
		@JsonProperty("maxRecordingHour")
		private Integer maxRecordingHour;

		/**
		 * 8、设置页面录制生成的 MP4 切片文件的最大时长，单位为分钟，取值范围为 [30,240]，默认值为 120 分钟。页面录制过程中，录制服务会在当前 MP4 文件时长超过约 maxVideoDuration 左右时创建一个新的 MP4 切片文件
		 */
		@JsonProperty("maxVideoDuration")
		private Integer maxVideoDuration;

		/**
		 * 9、设置是否在启动页面录制任务时暂停页面录制
		 * true：在启动页面录制任务时暂停页面录制。开启页面录制任务后立即暂停录制，录制服务会打开并渲染待录制页面，但不生成切片文件。
		 * false：启动页面录制任务并进行页面录制(默认) 
		 */
		@JsonProperty("onhold")
		private Boolean onhold;

		/**
		 * 10、设置页面加载超时时间，单位为秒，取值范围 [0,60]
		 * 0：设置为 0 或不设置，表示不检测页面加载状态
		 * 1：设置为大于等于 1 的整数，表示页面加载超时时间
		 * 设置为小于 0 或非整数，表示设置错误并收到错误码 2
		 */
		@JsonProperty("readyTimeout")
		private Integer readyTimeout;
		
		/**
		 * 11、CDN 推流地址
		 */
		@JsonProperty("outputs")
		private List<WebRecorderServiceParamCDN> outputs;
	}
	
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class WebRecorderServiceParamCDN {
		
		/**
		 * 1、CDN 推流地址
		 */
		@JsonProperty("rtmpUrl")
		private String rtmpUrl;
 
	}

}
