package com.open.capacity.common.watermark;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.helpers.DefaultHandler;

import com.open.capacity.common.watermark.enums.FileType;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 流获取扩展名工具类
 * @author owen
 * @date 2022/09/29 14:59:25
 */
public class FileTypeUtils {

	public static final String IMAGE_FORMAT = "png";

	/**
	 * 获取类型
	 * @param is 输入流
	 * @return {@link String} mimeType
	 */
	private static String getMimeType(InputStream is) {
		AutoDetectParser parser = new AutoDetectParser();
		parser.setParsers(MapUtil.newHashMap());
		Metadata metadata = new Metadata();
		try (InputStream stream = new BufferedInputStream(is)) {
			parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return metadata.get(HttpHeaders.CONTENT_TYPE);
	}

	/**
	 * 检查类型
	 * @param is     输入流
	 * @param fileType 文件类型
	 * @return {@link Boolean}
	 */
	private static Boolean checkType(InputStream is, FileType fileType) {
		String type = getMimeType(is);
		Pattern p = Pattern.compile(fileType.getValue());
		Matcher m = p.matcher(type);
		return StrUtil.equals(fileType.getValue(), type) || m.matches();
	}

	/**
	 * 是pdf
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isPdf(InputStream is) {
		return checkType(is, FileType.PDF);
	}

	/**
	 * 是否是word(.doc)
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isDoc(InputStream is) {
		return checkType(is, FileType.DOC);
	}
	
	/**
	 * 是Zip
	 *
	 * @param  is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isZip(InputStream is){
		return checkType(is, FileType.ZIP);
	}

	/**
	 * 是否是powerpoint(.ppt)
	 * 
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isPpt(InputStream is) {
		return checkType(is, FileType.PPT);
	}
	/**
	 * 是xls
	 * 
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isXls(InputStream is) {
		return checkType(is, FileType.XLS);
	}
	/**
	 * 是word(.docx)
	 * 
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isDocx(InputStream is) {
		return checkType(is, FileType.DOCX);
	}
	/**
	 * 是pptx
	 *
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isPptx(InputStream is) {
		return checkType(is, FileType.PPTX);
	}

	/**
	 * 是xlsx
	 * 
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isXlsx(InputStream is) {
		return checkType(is, FileType.XLSX);
	}

	/**
	 * 是否是Excel文件
	 *
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isExcel(InputStream is) {
		return isXlsx(is) || isXls(is);
	}

	/**
	 * 是否是Word文件
	 *
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isWord(InputStream is) {
		return isDocx(is) || isDoc(is)|| isZip(is);
	}

	/**
	 * 是否是PPT,PPTX文件
	 *
	 * @param is     输入流
	 * @return {@link Boolean}
	 */
	public static Boolean isPpts(InputStream is) {
		return isPpt(is) || isPptx(is);
	}

	/**
	 * 得到文件类型
	 *
	 * @param is     输入流
	 * @return {@link String}
	 */
	public static String getFileType(InputStream is) {
		return getMimeType(is);
	}
	

//
//	public static void main(String[] args) throws Exception {
//
//		InputStream is = new FileInputStream("D:\\file\\11.txt");
//
//		// 处理文件类型判断
//		System.out.println(FileTypeUtils.getFileType(is));
//	 
//	}

}
