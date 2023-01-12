package com.open.capacity.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.io.IoUtil;
import lombok.Cleanup;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.watermark.WatermarkUtils;
import com.open.capacity.common.watermark.domain.WatermarkParam;

import javax.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 *
 * @author someday
 * @date 2018/1/6
 */
public class ExcelUtil {
	private ExcelUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 导出
	 *
	 * @param list           数据列表
	 * @param title          标题
	 * @param sheetName      sheet名称
	 * @param pojoClass      元素类型
	 * @param fileName       文件名
	 * @param isCreateHeader 是否创建列头
	 * @param response
	 * @throws IOException
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
			boolean isCreateHeader, HttpServletResponse response) throws IOException {
		ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
		exportParams.setCreateHeadRows(isCreateHeader);
		defaultExport(list, pojoClass, fileName, response, exportParams);

	}

	/**
	 * 导出
	 *
	 * @param list      数据列表
	 * @param title     标题
	 * @param sheetName sheet名称
	 * @param pojoClass 元素类型
	 * @param fileName  文件名
	 * @param response
	 * @throws IOException
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
			HttpServletResponse response) throws IOException {
		defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
	}

	/**
	 * 导出
	 *
	 * @param list     数据列表(元素是Map)
	 * @param fileName 文件名
	 * @param response
	 * @throws IOException
	 */
	public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response)
			throws IOException {
		defaultExport(list, fileName, response);
	}

	private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response,
			ExportParams exportParams) throws IOException {
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
		if (workbook != null) {
			downLoadExcel(fileName, response, workbook);
		}
	}

	/**
	 * 水印导出
	 *
	 * @param list           数据列表
	 * @param title          标题
	 * @param sheetName      sheet名称
	 * @param pojoClass      元素类型
	 * @param fileName       文件名
	 * @param isCreateHeader 是否创建列头
	 * @param response
	 * @throws IOException
	 */
	public static void exportExcelWithWaterMark(Workbook workbook, String file, HttpServletResponse response)
			throws IOException {
		if (workbook != null) {
			@Cleanup
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			byte[] content = out.toByteArray();
			@Cleanup
			InputStream is = new ByteArrayInputStream(content);
			WatermarkParam param = WatermarkParam.builder().file(file).inputStream(is).useImage(true)
					.text(SysUserContextHolder.getUser().getUsername()).width(150).height(100).fontSize(20).degree(345F)
					.alpha(0.4f).bespread(Boolean.TRUE).color(Color.GRAY).build();
			byte[] imageContent = WatermarkUtils.addWatermark(param);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("users", "UTF-8"));
			IoUtil.write(response.getOutputStream(), true, imageContent);
		}

	}

	private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response)
			throws IOException {
		Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.XSSF);
		if (workbook != null) {
			downLoadExcel(fileName, response, workbook);
		}
	}

	private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		workbook.write(response.getOutputStream());
	}

	public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
		if (StringUtils.isBlank(filePath)) {
			return Collections.emptyList();
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
	}

	public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass)
			throws Exception {
		if (file == null) {
			return Collections.emptyList();
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		return ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
	}
}
