package com.open.capacity.common.watermark.processor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
 * excel处理器
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class XlsxWatermarkProcessor extends AbstractWatermarkProcessor {

	private static final Logger logger = LoggerFactory.getLogger(XlsxWatermarkProcessor.class);

	private static final String SUFFIX = "xlsx";

	@Override
	public Boolean supportType(WatermarkParam watermarkParam) {
		Boolean flag =  SUFFIX.equals(StringUtils.substringAfterLast(watermarkParam.getFile(), ".").toLowerCase()) ;
		 
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
		try(XSSFWorkbook workbook = new XSSFWorkbook(watermarkParam.getInputStream())) {
			// 导出水印到字节流B
			BufferedImage image = watermarkParam.getImageFile();
			ImageIO.write(image, FileTypeUtils.IMAGE_FORMAT, imageOut);
			int pictureIdx = workbook.addPicture(imageOut.toByteArray(), Workbook.PICTURE_TYPE_PNG);
			POIXMLDocumentPart poixmlDocumentPart = workbook.getAllPictures().get(pictureIdx);
			// 获取每个Sheet表
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				PackagePartName ppn = poixmlDocumentPart.getPackagePart().getPartName();
				String relType = XSSFRelation.IMAGES.getRelation();
				// add relation from sheet to the picture data
				PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType,null);
				// set background picture to sheet
				sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
			}
			workbook.write(out);
			bytes = out.toByteArray();
		} catch (Exception e) {
			logger.error("Description Failed to add watermark to EXCEL :  {}", e.getMessage());
			throw new WatermarkException(e.getMessage());
		} finally {
			IoUtil.close(out);
			IoUtil.close(imageOut);
		}
		return bytes;
	}

//	public static void main(String[] args) throws FileNotFoundException {
//
//		{
//			String file =  "D:\\file\\sql优化.xls" ;
//			FileInputStream is = new FileInputStream(file);
//			String waterMark = "管理员,2020-10-10";
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).text(waterMark)
//					.fontSize(20).degree(345f).alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\sql优化1.xls"));
//			IoUtil.close(is);
//		}
//
//	}
}
