package com.open.capacity.common.watermark.domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import lombok.Data;

/**
 * 水印参数
 * @author owen
 * @date 2022/09/29 14:59:25
 */
@Data
public class WatermarkParam {

	/**
	 * 目标文件
	 */
	private String file;

	/**
	 * 目标文件流
	 */
	private InputStream inputStream;

	/**
	 * 是否图片水印
	 */
	private Boolean useImage = false;

	/**
	 * 水印图片, 与文本二选一
	 */
	private BufferedImage imageFile;

	/**
	 * 水印文本, 与图片二选一
	 */
	private String text;

	/**
	 * 水印透明度
	 */
	private Float alpha = 0.5f;

	/**
	 * 水印文字大小
	 */
	public Integer fontSize = 60;

	/**
	 * 水印文字颜色
	 */
	private Color color = Color.LIGHT_GRAY;

	/**
	 * 水印旋转角度
	 */
	private Float degree = 0.0F;

	/**
	 * 水印宽度
	 */

	private Integer width = 800;

	/**
	 * 水印高度
	 */

	private Integer height = 500;

	/**
	 * 水印之间的间隔
	 */
	private Integer xMove = 80;

	/**
	 * 水印之间的间隔
	 */
	private Integer yMove = 80;

	/**
	 * 是否铺满
	 */
	private Boolean bespread = Boolean.FALSE;

	/**
	 * 构建器
	 * 
	 * @return {@link WatermarkParam} 构建器对象
	 */
	public static WatermarkParam builder() {
		return new WatermarkParam();
	}

	public WatermarkParam file(String file) {
		this.file = file;
		return this;
	}
	public WatermarkParam inputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}
	
	public WatermarkParam imageFile(BufferedImage imageFile) {
		this.imageFile = imageFile;
		return this;
	}

	public WatermarkParam useImage(Boolean useImage) {
		this.useImage = useImage;
		return this;
	}

	public WatermarkParam width(Integer width) {
		this.width = width;
		return this;
	}

	public WatermarkParam height(Integer height) {
		this.height = height;
		return this;
	}

	public WatermarkParam text(String text) {
		this.text = text;
		return this;
	}

	public WatermarkParam alpha(Float alpha) {
		this.alpha = alpha;
		return this;
	}

	public WatermarkParam fontSize(Integer fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public WatermarkParam color(Color color) {
		this.color = color;
		return this;
	}

	public WatermarkParam degree(Float degree) {
		this.degree = degree;
		return this;
	}

	public WatermarkParam yMove(Integer yMove) {
		this.yMove = yMove;
		return this;
	}

	public WatermarkParam xMove(Integer xMove) {
		this.xMove = xMove;
		return this;
	}

	public WatermarkParam bespread(Boolean bespread) {
		this.bespread = bespread;
		return this;
	}

	/**
	 * 返回对象
	 * 
	 * @return WatermarkParam
	 */
	public WatermarkParam build() {
		return this;
	}
}
