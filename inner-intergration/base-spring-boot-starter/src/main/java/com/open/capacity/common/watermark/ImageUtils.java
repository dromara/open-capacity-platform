package com.open.capacity.common.watermark;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;

/**
 * 生成水印图片工具类
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class ImageUtils {

	/**
	 * 根据指定的文本创建图片
	 *
	 * @param watermarkParam 水印信息
	 * @return {@link watermarkParam}
	 * @throws WatermarkException 水印异常
	 */
	public static void createImage(WatermarkParam watermarkParam) throws WatermarkException {

		Font font = new Font("宋体", Font.PLAIN, watermarkParam.getFontSize());
		Integer width = watermarkParam.getWidth();
		Integer height = watermarkParam.getHeight();
		// 创建图片
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 创建图片画布
		final Graphics2D g = image.createGraphics();
		// 增加下面代码使得背景透明
		image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g.dispose();
		final Graphics2D g2d = image.createGraphics();
		// 设置字体类型 加粗 大小
		g2d.setFont(font);
		g2d.translate(10, 10);
		// 设置字体颜色
		g2d.setColor(watermarkParam.getColor());
		// 设置透明度
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, watermarkParam.getAlpha()));
		// 旋转
		g2d.rotate(Math.toRadians(watermarkParam.getDegree()));
		final FontMetrics metrics = g2d.getFontMetrics(font);
		// 画出字符串
		List<String> list = Arrays.asList(watermarkParam.getText().split(","));
		int fontWith = 0;
		for (Iterator<String> it = list.iterator(); it.hasNext();) {
			String text = it.next() ;
			
			final int textLength = metrics.stringWidth(text);
			final int textHeight = metrics.getAscent() - metrics.getLeading() - metrics.getDescent();
			g2d.drawString(text,  Math.abs(image.getWidth() - textLength) / 2 + (fontWith)* watermarkParam.getXMove() , Math.abs(image.getWidth() + textHeight) / 2 +  (fontWith)*watermarkParam.getYMove());
			fontWith = fontWith + 1 ;
		}
		// 释放对象
		g2d.dispose();
		watermarkParam.setImageFile((BufferedImage) ImgUtil.rotate(image, Convert.toInt(watermarkParam.getDegree())));

	}

//	public static void main(String[] args) throws FileNotFoundException {
//
//		{
//			File file = new File("D:\\file\\sql优化.xlsx");
//			FileInputStream is = new FileInputStream(file);
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).width(300)
//					.height(600).text("阿斯顿发,点发射点发撒").fontSize(20).degree(30F).alpha(0.8f).bespread(Boolean.TRUE)
//					.color(Color.RED).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\sql优化1.xlsx"));
//			IoUtil.close(is);
//		}
//
//	}

}
