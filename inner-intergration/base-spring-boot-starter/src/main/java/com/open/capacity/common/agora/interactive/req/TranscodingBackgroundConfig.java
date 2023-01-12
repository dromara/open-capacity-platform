package com.open.capacity.common.agora.interactive.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 视频转码用户背景图设置
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class TranscodingBackgroundConfig {
	
	/**
	 * 1、用户 UID
	 */
	@JsonProperty("uid")
	private String uid;
	/**
	 * 2、该 UID 的背景图的 URL 地址。配置背景图后，当该⽤户停止发送视频流超过 3.5 秒，画⾯将切换为该背景图
	 */
	@JsonProperty("image_url")
	private String imageUrl;
	/**
	 * 3、画面显示模式（选填）
	 * 0：（默认）裁剪模式。优先保证画面被填满。背景图尺寸等比缩放，直至整个画面被背景图填满。如果背景图长宽与显示窗口不同，则背景图会按照画面设置的比例进行周边裁剪后填满画面。
	 * 1：缩放模式。优先保证背景图内容全部显示。背景图尺寸等比缩放，直至背景图的一边与画面边框对齐。如果背景图尺寸与画面尺寸不一致，在保持长宽比的前提下，将背景图进行缩放后填满画面，缩放后的背景图四周会有一圈黑边。
	 */
	@JsonProperty("render_mode")
	private Integer renderMode = 0;

}
