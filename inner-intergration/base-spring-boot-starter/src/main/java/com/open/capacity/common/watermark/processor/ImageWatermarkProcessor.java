package com.open.capacity.common.watermark.processor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.capacity.common.watermark.WatermarkUtils;
import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;

/**
 * 图像处理器
 *
 * @author owen
 * @date 2018年9月17日 下午5:22:56
 */
public class ImageWatermarkProcessor extends AbstractWatermarkProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ImageWatermarkProcessor.class);

	private static final String SUFFIX = "jpg,jpeg,png";

	@Override
	public Boolean supportType(WatermarkParam param) {
		return Stream.of(SUFFIX.split(","))
				.anyMatch(item -> item.equals(StringUtils.substringAfterLast(param.getFile(), ".").toLowerCase()));
	}

	@Override
	public void addWatermark(WatermarkParam watermarkParam, File targetFile) throws WatermarkException {
		FileUtil.writeBytes(this.addWatermark(watermarkParam), targetFile);
	}

	@Override
	public byte[] addWatermark(WatermarkParam watermarkParam) throws WatermarkException {
		ByteArrayOutputStream outputStream = null;
		try {
			Image srcImage = ImageIO.read(watermarkParam.getInputStream());
			BufferedImage bufferImg = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			// 1、得到画笔对象
			Graphics2D g = bufferImg.createGraphics();
			// 2、设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImage.getScaledInstance(srcImage.getWidth(null), srcImage.getHeight(null),
					BufferedImage.SCALE_SMOOTH), 0, 0, null);
			// 水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
			ImageIcon imgIcon = new ImageIcon(watermarkParam.getImageFile());
			Image img = imgIcon.getImage();
			// 水印图片的位置
			if (!watermarkParam.getBespread()) {
				g.drawImage(img, srcImage.getWidth(null) / 2 - watermarkParam.getXMove(),
						srcImage.getHeight(null) / 2 - watermarkParam.getYMove(), null);
			} else {
				// 水印间隔
				Integer widthSpace = watermarkParam.getXMove();
				Integer heightSpace = watermarkParam.getYMove();
				// 计算 横竖水印个数
				Integer widthCount = watermarkParam.getWidth();
				Integer heightCount = watermarkParam.getHeight() / heightSpace;
				for (int i = 0; i <= widthCount; i = i + 3) {
					for (int j = 0; j <= heightCount; j = j + 2) {
						// 添加水印
						g.drawImage(img, (i * widthSpace), (j * heightSpace), null);
					}
				}
			}
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();
			outputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferImg, FileUtil.extName(watermarkParam.getFile()), outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			logger.error("Failed to add watermark to the image {}", e.getMessage());
			throw new WatermarkException(e.getMessage());
		} finally {
			IoUtil.close(outputStream);
		}
	}

//	public static void main(String[] args) throws FileNotFoundException {
//		
//		{
//			String file = "D:\\file\\飞机.png";
//			FileInputStream is = new FileInputStream(file);
//			String waterMark = "管理员,2020-10-10";
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).text(waterMark)
//					.fontSize(20).degree(345F).alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\飞机1.png"));
//			IoUtil.close(is);
//		}
//		
//	}
}
