package com.open.capacity.common.watermark;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;
import com.open.capacity.common.watermark.processor.DocxWatermarkProcessor;
import com.open.capacity.common.watermark.processor.PdfWatermarkProcessor;
import com.open.capacity.common.watermark.processor.ImageWatermarkProcessor;
import com.open.capacity.common.watermark.processor.PptxWatermarkProcessor;
import com.open.capacity.common.watermark.processor.WatermarkProcessor;
import com.open.capacity.common.watermark.processor.XlsxWatermarkProcessor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 水印工具类
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class WatermarkUtils {

	private static final List<WatermarkProcessor> processors = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(WatermarkUtils.class);

	static {
		processors.add(new PdfWatermarkProcessor());
		processors.add(new DocxWatermarkProcessor());
		processors.add(new XlsxWatermarkProcessor());
		processors.add(new PptxWatermarkProcessor());
		processors.add(new ImageWatermarkProcessor());
	}

	/**
	 * 添加处理器
	 *
	 * @param processor 处理器
	 */
	public static void addProcessor(WatermarkProcessor processor) {
		if (ObjectUtil.isNotNull(processor)) {
			processors.add(processor);
		}
	}

	/**
	 * 添加水印
	 *
	 * @param watermarkParam 水印参数
	 * @param outputFile     输出文件
	 * @throws WatermarkException 水印异常
	 */
	public static void addWatermark(WatermarkParam watermarkParam, File outputFile) throws WatermarkException {
		try {
			FileUtil.writeBytes(addWatermark(watermarkParam), outputFile);
		} finally {
			IoUtil.close(watermarkParam.getInputStream());
		}

	}

	/**
	 * 添加水印
	 *
	 * @param watermarkParam 水印参数
	 * @throws WatermarkException 水印异常
	 */
	public static byte[] addWatermark(WatermarkParam watermarkParam) throws WatermarkException {
		// 根据后缀名处理
		WatermarkProcessor processor = processors.stream().filter(a -> a.supportType(watermarkParam)).findAny()
				.orElse(null);
		if (ObjectUtil.isNull(processor)) {
			//返回无水印文件
			return IoUtil.readBytes(watermarkParam.getInputStream());
		}
		// 如果使用图片水印画图片
		if (watermarkParam.getUseImage()) {
			handlerWatermarkFile(watermarkParam);
		}

		return processor.addWatermark(watermarkParam);
	}

	/**
	 * 处理程序水印图片
	 *
	 * @param watermarkParam 水印参数
	 * @throws WatermarkException 水印的例外
	 */
	private static void handlerWatermarkFile(WatermarkParam watermarkParam) throws WatermarkException {
		ImageUtils.createImage(watermarkParam);
	}
	
	
}
