package com.open.capacity.common.watermark.processor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.capacity.common.watermark.FileTypeUtils;
import com.open.capacity.common.watermark.WatermarkUtils;
import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

/**
 * ppt处理器
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class PptxWatermarkProcessor extends AbstractWatermarkProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PptxWatermarkProcessor.class);

	private static final String SUFFIX = "pptx";

	@Override
	public Boolean supportType(WatermarkParam watermarkParam) {
		Boolean flag = SUFFIX.equals(StringUtils.substringAfterLast(watermarkParam.getFile(), ".").toLowerCase());
		if (flag) {
			watermarkParam.fontSize(20);
		}
		return flag;
	}

	@Override
	public void addWatermark(WatermarkParam watermarkParam, File target) throws WatermarkException {
		FileUtil.writeBytes(this.addWatermark(watermarkParam), target);
	}

	@Override
	@SneakyThrows
	public byte[] addWatermark(WatermarkParam watermarkParam) throws WatermarkException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		byte[] bytes = null;
		try (XMLSlideShow slideShow = new XMLSlideShow(watermarkParam.getInputStream())) {
			// 导出水印到字节流B
			BufferedImage image = watermarkParam.getImageFile();
			ImageIO.write(image, FileTypeUtils.IMAGE_FORMAT, imageOut);
			PictureData pictureData = slideShow.addPicture(imageOut.toByteArray(), PictureType.PNG);
			// 平铺或单个水印处理
			dealWaterMark(watermarkParam, slideShow, pictureData);
			slideShow.write(out);
			bytes = out.toByteArray();
			slideShow.close();
		} catch (Exception e) {
			logger.error("Description Failed to add watermark to ppt :  {}", e.getMessage());
			throw new WatermarkException(e.getMessage());
		} finally {
			IoUtil.close(out);
			IoUtil.close(imageOut);
		}
		return bytes;
	}

	/**
	 * 单个或平铺水印
	 * @param watermarkParam
	 * @param slideShow
	 * @param pictureData
	 */
	private void dealWaterMark(WatermarkParam watermarkParam, XMLSlideShow slideShow, PictureData pictureData) {
		for (XSLFSlide slide : slideShow.getSlides()) {
			// 平铺
			if (watermarkParam.getBespread()) {
				// 水印间隔
				Integer widthSpace = watermarkParam.getXMove();
				Integer heightSpace = watermarkParam.getYMove();
				// 计算 横竖水印个数
				Integer widthCount = watermarkParam.getWidth() / widthSpace;
				Integer heightCount = watermarkParam.getHeight() / heightSpace;
				for (int i = 0; i <= widthCount; i=i+3) {
					for (int j = 0; j <= heightCount; j=j+2) {
						// 添加水印
						List<String> list = Arrays.asList(watermarkParam.getText().split(","));
						for (Iterator<String> it = list.iterator(); it.hasNext();) {
							XSLFPictureShape pictureShape = slide.createPicture(pictureData);
							String text = it.next();
							//水印层
							java.awt.Rectangle rectangle = new java.awt.Rectangle((i * widthSpace) ,
									  (j * heightSpace ), 300, 300) ;
							pictureShape.setAnchor(rectangle);
						}

					}
				}

			} else {
				XSLFPictureShape pictureShape = slide.createPicture(pictureData);
				pictureShape.setAnchor(new java.awt.Rectangle(0, 0, 800, 600));
			}

		}
	}

//	public static void main(String[] args) throws FileNotFoundException {
//
//		{
//			String file = "D:\\file\\Java.pptx";
//			FileInputStream is = new FileInputStream(file);
//			String waterMark = "管理员,2020-10-10";
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).text(waterMark)
//					.fontSize(20).degree(345F).alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\Java1.pptx"));
//			IoUtil.close(is);
//		}
//
//	}
}
