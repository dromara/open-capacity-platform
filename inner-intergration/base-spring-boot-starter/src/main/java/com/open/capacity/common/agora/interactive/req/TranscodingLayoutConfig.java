package com.open.capacity.common.agora.interactive.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 视频转码用户布局设置
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class TranscodingLayoutConfig {
	
	/**
	 * 1、字符串内容为待显示在该区域的用户的 UID，32 位无符号整数。如果不指定 UID，会按照用户加入频道的顺序自动匹配 layoutConfig 中的画面设置（选填）
	 */
	@JsonProperty("uid")
	private String uid;
	/**
	 * 2、屏幕里该画面左上角的横坐标的相对值，范围是 [0.0,1.0]，精确到小数点后六位。从左到右布局，0.0 在最左端，1.0 在最右端
	 */
	@JsonProperty("x_axis")
	private Float xAxis;
	/**
	 * 3、屏幕里该画面左上角的纵坐标的相对值，范围是 [0.0,1.0]，精确到小数点后六位。从上到下布局，0.0 在最上端，1.0 在最下端
	 */
	@JsonProperty("y_axis")
	private Float yAxis;
	/**
	 * 4、屏幕里该画面左上角的纵坐标的相对值，范围是 [0.0,1.0]，精确到小数点后六位。从上到下布局，0.0 在最上端，1.0 在最下端
	 */
	@JsonProperty("width")
	private Float width;
	/**
	 * 5、屏幕里该画面左上角的纵坐标的相对值，范围是 [0.0,1.0]，精确到小数点后六位。从上到下布局，0.0 在最上端，1.0 在最下端
	 */
	@JsonProperty("height")
	private Float height;
	/**
	 * 6、图像的透明度。取值范围是 [0.0,1.0] ，精确到小数点后六位。默认值 1.0。0.0 表示图像为透明的，1.0 表示图像为完全不透明的
	 */
	@JsonProperty("alpha")
	private Float alpha = 1.0F;
	/**
	 * 7、画面显示模式（选填）
	 * 0：（默认）裁剪模式。优先保证画面被填满。视频尺寸等比缩放，直至整个画面被视频填满。如果视频长宽与显示窗口不同，则视频流会按照画面设置的比例进行周边裁剪后填满画面。
	 * 1：缩放模式。优先保证视频内容全部显示。视频尺寸等比缩放，直至视频窗口的一边与画面边框对齐。如果视频尺寸与画面尺寸不一致，在保持长宽比的前提下，将视频进行缩放后填满画面，缩放后的视频四周会有一圈黑边。
	 */
	@JsonProperty("render_mode")
	private Integer renderMode = 0;
	

}
