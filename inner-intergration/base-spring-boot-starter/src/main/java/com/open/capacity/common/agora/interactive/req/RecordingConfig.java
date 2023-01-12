package com.open.capacity.common.agora.interactive.req;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 录制设置：用于设置媒体流订阅的 JSON Object。云端录制会根据此设置订阅频道内的媒体流，并生成录制文件或截图
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class RecordingConfig {

    /**
     * 1、频道场景。频道场景必须与 Agora RTC SDK 的设置一致，否则可能导致问题
     * 0：通信场景（默认）
     * 1：直播场景
     */
    @JsonProperty("channelType")
    private Integer channelType = 1;

    /**
     * 2、订阅的媒体流类型
     * 0：仅订阅音频
     * 1：仅订阅视频
     * 2：（默认）订阅音频和视频
     */
    @JsonProperty("streamTypes")
    private Integer streamTypes = 2;

    /**
     * 3、解密方案。如果频道设置了加密，该参数必须设置。解密方式必须与频道设置的加密方式一致。（选填）
     * 0：不加密（默认）
     * 5：AES_128_GCM 加密模式。128 位 AES 加密，GCM 模式
     * 6：AES_256_GCM 加密模式。256 位 AES 加密，GCM 模式
     * 7: AES_128_GCM2 加密模式。128 位 AES 加密，GCM 模式。 相比于 AES_128_GCM 加密模式，AES_128_GCM2 加密模式安全性更高且需要设置密钥和盐
     * 8: AES_256_GCM2 加密模式。256 位 AES 加密，GCM 模式。相比于 AES_256_GCM 加密模式，AES_256_GCM2 加密模式安全性更高且需要设置密钥和盐
     */
    @JsonProperty("decryptionMode")
    private Integer decryptionMode = 0;

    /**
     * 4、启用解密模式后，设置解密的密钥。如果 decryptionMode 不为 0，则需要设置该值。（选填）
     */
    @JsonProperty("secret")
    private String secret;

    /**
     * 5、Base64 编码、32 位字节。启用解密模式后，设置解密的盐。如果 decryptionMode 为 7 或 8，则需要设置该值。（选填）
     */
    @JsonProperty("salt")
    private String salt;

    /**
     * 6、设置输出音频的采样率、码率、编码模式和声道数。目前单流模式下不能设置该参数（选填）
     * 0：48 kHz 采样率，音乐编码，单声道，编码码率约 48 Kbps（默认）
     * 1：48 kHz 采样率，音乐编码，单声道，编码码率约 128 Kbps
     * 2：48 kHz 采样率，音乐编码，双声道，编码码率约 192 Kbps
     */
    @JsonProperty("audioProfile")
    private Integer audioProfile = 1;

    /**
     * 7、设置订阅的视频流类型。如果频道中有用户开启了双流模式，你可以选择订阅视频大流或者小流（选填）
     * 0：视频大流（默认），即高分辨率高码率的视频流
     * 1：视频小流，即低分辨率低码率的视频流
     */
    @JsonProperty("videoStreamType")
    private Integer videoStreamType = 0;

    /**
     * 8、最长空闲频道时间，单位为秒。默认值为 30。该值需大于等于 5，且小于等于 2,592,000，即 30 天。
     * a: 如果频道内无用户的状态持续超过该时间，录制程序会自动退出。退出后，再次调用 start 请求，会产生新的录制文件。
     * notice:
     * a、通信场景下，如果频道内有用户，但用户没有发流，不算作无用户状态。
     * b、直播场景下，如果频道内有观众但无主播，一旦无主播的状态超过 maxIdleTime，录制程序会自动退出。
     * c、因为主播离开频道会延时两分钟才会停止录制,所以录制用户在频道内多留5分钟再退出
     */
    @JsonProperty("maxIdleTime")
    private Integer maxIdleTime = 300;

    /**
     * 9、视频转码的详细设置。仅适用于合流模式，单流模式下不能设置该参数。（选填）
     * a、如果不设置将使用默认值。如果设置该参数，必须填入 width、height、fps 和 bitrate 字段
     */
    @JsonProperty("transcodingConfig")
    private TranscodingConfig transcodingConfig;
    /**
     * 12、由 UID 组成的数组，指定订阅哪几个 UID 的音频流。如需订阅全部 UID 的音频流，则无需设置该参数。数组长度不得超过 32，不推荐使用空数组。（选填）
     */
	@JsonProperty("subscribeAudioUids")
    private List<String> subscribeAudioUids;
	/**
     * 13、由 UID 组成的数组，指定不订阅哪几个 UID 的音频流。云端录制会订阅频道内除指定 UID 外所有 UID 的音频流。数组长度不得超过 32，不推荐使用空数组。（选填）
     */
	@JsonProperty("unSubscribeAudioUids")
    private List<String> unSubscribeAudioUids;
	/**
     * 14、由 UID 组成的数组，指定订阅哪几个 UID 的视频流。如需订阅全部 UID 的视频流，则无需设置该参数。数组长度不得超过 32，不推荐使用空数组。（选填）
     */
	@JsonProperty("subscribeVideoUids")
    private List<String> subscribeVideoUids;
	/**
     * 15、由 UID 组成的数组，指定不订阅哪几个 UID 的视频流。云端录制会订阅频道内除指定 UID 外所有 UID 的视频流。数组长度不得超过 32，不推荐使用空数组。（选填）
     */
	@JsonProperty("unSubscribeVideoUids")
    private List<String> unSubscribeVideoUids;
	/**
     * 15、预估的订阅人数峰值。在单流模式下，为必填参数。举例来说，如果 subscribeVideoUids 为 ["100","101","102"]，subscribeAudioUids 为 ["101","102","103"]，则订阅人数为 4 人。（选填）
     * 0：1 到 2 个 UID
     * 1：3 到 7 个 UID
     * 2：8 到 12 个 UID
     * 3：13 到 17 个 UID
     * 4：17 到 32 个 UID
     * 5：32 到 49 个 UID
     */
	@JsonProperty("subscribeUidGroup")
    private Integer subscribeUidGroup;

	public RecordingConfig() {
		super();
	}

	public RecordingConfig(TranscodingConfig transcodingConfig) {
		super();
		this.transcodingConfig = transcodingConfig;
	}



}
