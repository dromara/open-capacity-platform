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
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.STConnectType;
import com.microsoft.schemas.vml.CTFormulas;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTH;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.STExt;
import com.open.capacity.common.watermark.FontUtils;
import com.open.capacity.common.watermark.WatermarkUtils;
import com.open.capacity.common.watermark.domain.WatermarkParam;
import com.open.capacity.common.watermark.exception.WatermarkException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

/**
 * word处理器
 * 
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class DocxWatermarkProcessor extends AbstractWatermarkProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DocxWatermarkProcessor.class);
	private static final String SUFFIX = "docx";
	// 字体颜色
	private static final String FONT_COLOR = "#d0d0d0";
	// 一个字平均长度，单位pt，用于：计算文本占用的长度（文本总个数*单字长度）
	private static final int WIDTH_PER_WORD = 5;

	@Override
	public Boolean supportType(WatermarkParam param) {

		Boolean flag = SUFFIX.equals(StringUtils.substringAfterLast(param.getFile(), ".").toLowerCase());
		if (flag) {
			param.degree(30f);
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

		// 输入的docx文档
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 文档对象
		try (XWPFDocument docx = new XWPFDocument(watermarkParam.getInputStream());) {
			// 遍历文档，添加水印
			Integer styleTop = 0;
			// 页面页脚水印
			waterMarkTop(docx,  watermarkParam);
			// 处理内容水印
			waterMarkDocxDocument(docx, watermarkParam, styleTop);
			docx.write(out);
		} catch (Exception e) {
			logger.error("Description Failed to add watermark to docx :  {}", e.getMessage());
			throw new WatermarkException(e.getMessage());
		} finally {
			IoUtil.close(out);
		}
		return out.toByteArray();
	}

	/**
	 * 设置页眉水印
	 * 
	 * @param doc
	 * @param watermarkParam
	 */
	public static void waterMarkTop(XWPFDocument doc,  WatermarkParam watermarkParam) {

		for (HeaderFooterType type : HeaderFooterType.values()) {
			XWPFHeader header = doc.createHeader(type);
			int size = header.getParagraphs().size();
			if (size == 0) {
				header.createParagraph();
			}
			CTP p = header.getParagraphArray(0).getCTP();
			// 设置页眉参数
			p.addNewR().addNewT().setStringValue(watermarkParam.getText());
			p.addNewR().addNewT().setSpace(SpaceAttribute.Space.PRESERVE);
			CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr() ? doc.getDocument().getBody().getSectPr()
					: doc.getDocument().getBody().addNewSectPr();
		}
	}
 
	/**
	 *  给文档添加水印 此方法可以单独使用
	 * @param doc
	 * @param watermarkParam
	 * @param styleTop
	 */
	public static void waterMarkDocxDocument(XWPFDocument doc, WatermarkParam watermarkParam, Integer styleTop) {
		Integer top = styleTop;
		if (watermarkParam.getBespread()) {
			// 水印文字之间使用8个空格分隔
			String customText = watermarkParam.getText();
			// 遍历文档，添加水印
			for (int i = -10; i <= 10; i = i + 2) {
				int j = 0;
				// 添加水印
				List<String> list = Arrays.asList(watermarkParam.getText().split(","));
				for (Iterator<String> it = list.iterator(); it.hasNext();) {
					// 一行水印重复水印文字次数
					String text = (FontUtils.repeatString(it.next() + FontUtils.repeatString(" ", 20), 5));
					top = 200 * (i + j) + 100;
					dealWaterMark(doc, text, watermarkParam, top);
					j++;
				}
			}
		} else {

			// 添加水印
			List<String> list = Arrays.asList(watermarkParam.getText().split(","));
			int fontWith = 1;
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				top = 100 * fontWith;
				String text = it.next();
				dealWaterMark(doc, text, watermarkParam, top);
				fontWith = fontWith + 1;
			}

		}

	}

	/**
	 * 为文档添加水印
	 * @param doc		需要被处理的docx文档对象
	 * @param text		需要添加的水印文字
	 * @param watermarkParam
	 * @param styleTop
	 */
	public static void dealWaterMark(XWPFDocument doc, String text, WatermarkParam watermarkParam, Integer styleTop) {

		for (HeaderFooterType type : HeaderFooterType.values()) {
			XWPFHeader header = doc.createHeader(type);
			int size = header.getParagraphs().size();
			if (size == 0) {
				header.createParagraph();
			}
			CTP p = header.getParagraphArray(0).getCTP();
			byte[] rsidr = doc.getDocument().getBody().getPArray(0).getRsidR();
			byte[] rsidrdefault = doc.getDocument().getBody().getPArray(0).getRsidRDefault();
			p.setRsidP(rsidr);
			p.setRsidRDefault(rsidrdefault);
			CTPPr pPr = p.addNewPPr();
			pPr.addNewPStyle().setVal("Header");
			// start watermark paragraph
			CTR r = p.addNewR();
			CTRPr rPr = r.addNewRPr();
			rPr.addNewNoProof();
			CTPicture pict = r.addNewPict();
			CTGroup group = CTGroup.Factory.newInstance();
			CTShapetype shapetype = group.addNewShapetype();
			shapetype.setId("_x0000_t136");
			shapetype.setCoordsize("1600,21600");
			shapetype.setSpt(136);
			shapetype.setAdj("10800");
			shapetype.setPath2("m@7,0l@8,0m@5,21600l@6,21600e");
			CTFormulas formulas = shapetype.addNewFormulas();
			formulas.addNewF().setEqn("sum #0 0 10800");
			formulas.addNewF().setEqn("prod #0 2 1");
			formulas.addNewF().setEqn("sum 21600 0 @1");
			formulas.addNewF().setEqn("sum 0 0 @2");
			formulas.addNewF().setEqn("sum 21600 0 @3");
			formulas.addNewF().setEqn("if @0 @3 0");
			formulas.addNewF().setEqn("if @0 21600 @1");
			formulas.addNewF().setEqn("if @0 0 @2");
			formulas.addNewF().setEqn("if @0 @4 21600");
			formulas.addNewF().setEqn("mid @5 @6");
			formulas.addNewF().setEqn("mid @8 @5");
			formulas.addNewF().setEqn("mid @7 @8");
			formulas.addNewF().setEqn("mid @6 @7");
			formulas.addNewF().setEqn("sum @6 0 @5");
			CTPath path = shapetype.addNewPath();
			path.setTextpathok(STTrueFalse.T);
			path.setConnecttype(STConnectType.CUSTOM);
			path.setConnectlocs("@9,0;@10,10800;@11,21600;@12,10800");
			path.setConnectangles("270,180,90,0");
			CTTextPath shapeTypeTextPath = shapetype.addNewTextpath();
			shapeTypeTextPath.setOn(STTrueFalse.T);
			shapeTypeTextPath.setFitshape(STTrueFalse.T);
			CTHandles handles = shapetype.addNewHandles();
			CTH h = handles.addNewH();
			h.setPosition("#0,bottomRight");
			h.setXrange("6629,14971");
			CTLock lock = shapetype.addNewLock();
			lock.setExt(STExt.EDIT);
			CTShape shape = group.addNewShape();
			shape.setId("PowerPlusWaterMarkObject" + "_x0000_s102");
			shape.setSpid("_x0000_s102" + (4 + "_x0000_s102"));
			shape.setType("#_x0000_t136");
			shape.setStyle(getShapeStyle(text, watermarkParam, styleTop));
			shape.setWrapcoords("616 5068 390 16297 39 16921 -39 17155 7265 17545 7186 17467 -39 17467 18904 17467 10507 17467 8710 17545 18904 17077 18787 16843 18358 16297 18279 12554 19178 12476 20701 11774 20779 11228 21131 10059 21248 8811 21248 7563 20975 6316 20935 5380 19490 5146 14022 5068 2616 5068");
			shape.setFillcolor(FONT_COLOR);
			shape.setStroked(STTrueFalse.FALSE);
			CTTextPath shapeTextPath = shape.addNewTextpath();
			shapeTextPath.setStyle("font-family:&quot;宋体&quot;;font-size:" + watermarkParam.getFontSize() + ".pt");
			shapeTextPath.setString(text);
			pict.set(group);

		}

	}

	/**
	 * 构建Shape的样式参数
	 * @param text
	 * @param watermarkParam
	 * @param styleTop
	 * @return
	 */
	private static String getShapeStyle(String text, WatermarkParam watermarkParam, Integer styleTop) {
		StringBuilder sb = new StringBuilder();
		// 文本path绘制的定位方式
		sb.append("position: ").append("absolute");
		// 计算文本占用的长度（文本总个数*单字长度）
		sb.append(";width: ").append(text.length() * WIDTH_PER_WORD).append("pt");
		// 字体高度
		sb.append(";height: ").append("20pt");
		sb.append(";z-index: ").append("-251654144");
		sb.append(";mso-wrap-edited: ").append("f");
		sb.append(";margin-top: ").append(styleTop);
		sb.append(";mso-position-horizontal-relative: ").append("page");
		sb.append(";mso-position-vertical-relative: ").append("page");
		sb.append(";mso-position-vertical: ").append("left");
		sb.append(";mso-position-horizontal: ").append("center");
		sb.append(";rotation: ").append(watermarkParam.getDegree() + 300);
		return sb.toString();
	}

	 

//	public static void main(String[] args) throws FileNotFoundException {
//
//		{
//			String file = "D:\\file\\自主升级部分功能.docx" ;
//			FileInputStream is = new FileInputStream(file);
//			String waterMark = "管理员,2020-10-10";
//			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true).text(waterMark)
//					.fontSize(20).degree(345F).alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
//			WatermarkUtils.addWatermark(param, new File("D:\\file\\自主升级部分功能1.docx"));
//			IoUtil.close(is);
//		}
//
//	}
}
