package com.open.capacity.common.watermark.processor;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.open.capacity.common.watermark.WatermarkUtils;
import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

/**
 * pdf处理器
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class PdfWatermarkProcessor extends AbstractWatermarkProcessor {

	private static final String SUFFIX = "pdf";

	private static final Logger logger = LoggerFactory.getLogger(PdfWatermarkProcessor.class);

	@Override
	public Boolean supportType(WatermarkParam watermarkParam) {

		Boolean flag = SUFFIX.equals(StringUtils.substringAfterLast(watermarkParam.getFile(), ".").toLowerCase()) ;
		if (flag) {
			watermarkParam.fontSize(10) ;
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

		PdfReader reader = null;
		PdfStamper stamper = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			reader = new PdfReader(watermarkParam.getInputStream());
			stamper = new PdfStamper(reader, out);
			//pdf中文支持
			BaseFont baseFontChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			PdfGState gs = new PdfGState();
			PdfContentByte content;
			int total = reader.getNumberOfPages();
			for (int i = 0; i < total; i++) {
				// 页宽度
				float width = reader.getPageSize(i + 1).getWidth();
				// 页高度
				float height = reader.getPageSize(i + 1).getHeight();
				// 内容
				content = stamper.getOverContent(i + 1);
				// 开始写入文本
				content.beginText();
				// 水印透明度
				gs.setFillOpacity(watermarkParam.getAlpha());
				content.setGState(gs);
				content.setColorFill(GrayColor.LIGHT_GRAY);
				// 设置字体的输出位置
				content.setTextMatrix(75, 50);
				content.setFontAndSize(baseFontChinese, watermarkParam.getFontSize());
				// showTextAligned 方法的参数分别是（文字对齐方式，位置内容，输出水印X轴位置，Y轴位置，旋转角度）
				dealWaterMark(content, watermarkParam, width, height);
				content.endText();
			}
			stamper.close();
			bytes = out.toByteArray();
		} catch (Exception e) {
			logger.error("Description Failed to add watermark to PDF :  {}", e.getMessage());
			throw new WatermarkException(e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (stamper != null) {
				stamper.close();
			}
			IoUtil.close(out);
		}
		return bytes;
	}

	/**
	 * 处理水印
	 * 
	 * @param content
	 * @param watermarkParam
	 * @param width
	 * @param height
	 */
	private static void dealWaterMark(PdfContentByte content, WatermarkParam watermarkParam, Float width,
			Float height) {
		// 添加水印
		if (watermarkParam.getBespread()) {
			// 水印间隔
			Integer widthSpace = watermarkParam.getXMove();
			Integer heightSpace = watermarkParam.getYMove();
			// 计算 横竖水印个数
			Integer widthCount = width.intValue() / widthSpace;
			Integer heightCount = height.intValue() / heightSpace;
			for (int i = 0; i <= widthCount; i++) {
				for (int j = 0; j <= heightCount; j++) {
					// 添加水印
					List<String> list = Arrays.asList(watermarkParam.getText().split(","));
					int top = 0 ;
					for (Iterator<String> it = list.iterator(); it.hasNext();) {
						top= top +1 ;
						String text =  it.next() ;
						content.showTextAligned(Element.ALIGN_CENTER, text ,  (i * widthSpace) +top *100  ,  ((j) * heightSpace)  +top *200  , 25);
					}
				}
			}
			
		} else {
			content.showTextAligned(Element.ALIGN_CENTER, watermarkParam.getText(),width / 2 - watermarkParam.getXMove(), height / 2 - watermarkParam.getYMove(), 25);
		}

	}
//	public static void main(String[] args) throws FileNotFoundException {
//		
//		{
//			String file = "D:\\file\\中台主机部署.pdf";
//			FileInputStream is = new FileInputStream(file);
//			String waterMark = "管理员,2020-10-10";
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).text(waterMark)
//					.fontSize(20).degree(345F).alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\中台主机部署1.pdf"));
//			IoUtil.close(is);
//		}
//		
//	}

}
