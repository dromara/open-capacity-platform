package com.open.capacity.common.watermark.processor;

import java.io.File;

import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.io.FileUtil;

/**
 * 水印处理器
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public interface WatermarkProcessor {

	/**
	 * 支持类型
	 * @param param
	 * @return
	 */
	default Boolean supportType(WatermarkParam param) {
		return Boolean.FALSE;
	}

	/**
	 * 添加水印
	 * 
	 * @param watermarkParam 水印参数
	 * @param target         目标文件
	 * @throws WatermarkException 水印异常
	 */
	default void addWatermark(WatermarkParam watermarkParam, File target) throws WatermarkException {
		FileUtil.writeBytes(this.addWatermark(watermarkParam), target);
	}

	/**
	 * 添加水印
	 * 
	 * @param watermarkParam 水印参数
	 * @return {@link byte[]}
	 * @throws WatermarkException 水印异常
	 */
	byte[] addWatermark(WatermarkParam watermarkParam) throws WatermarkException;

}
