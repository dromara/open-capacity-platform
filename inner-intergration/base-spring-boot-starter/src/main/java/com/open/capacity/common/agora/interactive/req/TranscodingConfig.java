package com.open.capacity.common.agora.interactive.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 * 视频布局配置
 */
@Data
@JsonInclude( JsonInclude.Include.NON_NULL)
public class TranscodingConfig {

    /**
     * 1、视频的宽度，单位为像素，默认值 360。width 不能超过 1920，且 width 和 height 的乘积不能超过 1920 * 1080，超过最大值会报错。
     */
	@JsonProperty("width")
    private Integer width = 360;
    /**
     * 2、视频的高度，单位为像素，默认值 640。height 不能超过 1920，且 width 和 height 的乘积不能超过 1920 * 1080，超过最大值会报错。
     */
	@JsonProperty("height")
    private Integer height = 640;
    /**
     * 3、视频的帧率，单位 fps，默认值 15
     */
	@JsonProperty("fps")
    private Integer fps = 30;
    /**
     * 4、视频的码率，单位 Kbps，默认值 500
     */
	@JsonProperty("bitrate")
    private Integer bitrate = 1200;
    /**
     * 5、如果视频合流布局设为垂直布局，用该参数指定显示大视窗画面的用户 ID（选填）
     */
	@JsonProperty("maxResolutionUid")
    private String maxResolutionUid;
    /**
     * 6、设置视频合流布局，0、1、2 为预设的合流布局，3 为自定义合流布局。该参数设为 3 时必须设置 layoutConfig 参数（选填）
     * 0：（默认）悬浮布局。第一个加入频道的用户在屏幕上会显示为大视窗，铺满整个画布，其他用户的视频画面会显示为小视窗，从下到上水平排列，最多 4 行，每行 4 个画面，最多支持共 17 个画面。
     * 1：自适应布局。根据用户的数量自动调整每个画面的大小，每个用户的画面大小一致，最多支持 17 个画面。
     * 2：垂直布局。指定一个用户在屏幕左侧显示大视窗画面，其他用户的小视窗画面在右侧垂直排列，最多两列，一列 8 个画面，最多支持共 17 个画面。设置为垂直布局时，用 maxResolutionUid 参数指定显示大视窗画面的用户 ID。
     * 3：自定义布局。设置 layoutConfig 参数自定义合流布局。
     */
	@JsonProperty("mixedVideoLayout")
    private Integer mixedVideoLayout = 0;
    /**
     * 7、视频画布的背景颜色。支持 RGB 颜色表，字符串格式为 # 号后 6 个十六进制数。默认值 "#000000" 黑色（选填）
     */
	@JsonProperty("backgroundColor")
    private String backgroundColor = "#000000";
    /**
     * 8、视频画布的背景图的 URL 地址。背景图的显示模式为裁剪模式。裁剪模式下，优先保证画面被填满。背景图尺寸等比缩放，直至整个画面被背景图填满。如果背景图长宽与显示窗口不同，则背景图会按照画面设置的比例进行周边裁剪后填满画面。（选填）
     */
	@JsonProperty("backgroundImage")
    private String backgroundImage;
    /**
     * 9、默认的用户画面背景图的 URL 地址。配置该参数后，当任一⽤户停止发送视频流超过 3.5 秒，画⾯将切换为该背景图；如果针对某 UID 单独设置了背景图，则该设置会被覆盖（选填）
     */
	@JsonProperty("defaultUserBackgroundImage")
    private String defaultUserBackgroundImage;
    /**
     * 10、由每个用户对应的布局画面设置组成的数组，支持最多 17 个用户画面。当 mixedVideoLayout 设为 3 时，可以通过该参数自定义合流布局。（选填）
     */
	@JsonProperty("layoutConfig")
    private List<TranscodingLayoutConfig> layoutConfig;
    /**
     * 11、数组内容为各 UID 单独的背景图设置。（选填）
     * notice: 
     * a、上背景图设置中，URL 支持 HTTP 和 HTTPS 协议，背景图片支持 JPG 和 BMP 格式。
     * b、图片大小不得超过 6MB。录制服务成功下载图片后，设置才会生效；如果下载失败，则设置不⽣效。不同参数可能会互相覆盖，具体规则详见设置背景色或背景图。
     */
	@JsonProperty("backgroundConfig")
    private List<TranscodingBackgroundConfig> backgroundConfig;
    
}
