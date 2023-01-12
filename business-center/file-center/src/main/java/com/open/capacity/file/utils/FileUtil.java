package com.open.capacity.file.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.open.capacity.file.entity.FileInfo;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 文件工具类
 */
@Slf4j
@UtilityClass
public class FileUtil {

	private static final String TEXT_FIELD_NAME = "uploadfile";

	public FileInfo getFileInfo(MultipartFile file) throws Exception {
		String md5 = fileMd5(file.getInputStream());
		FileInfo fileInfo = new FileInfo();
		// 将文件的md5设置为文件表的md5
		fileInfo.setMd5(md5);
		fileInfo.setName(file.getOriginalFilename());
		fileInfo.setContentType(file.getContentType());
		fileInfo.setIsImg(fileInfo.getContentType().startsWith("image/"));
		fileInfo.setSize(file.getSize());
		fileInfo.setCreateTime(new Date());
		return fileInfo;
	}

	
	/**
	 * file转换 MultipartFile对象
	 * @param is 文件流
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SneakyThrows
	public static MultipartFile createFileItem(MultipartFile file){
		FileItemFactory factory = new DiskFileItemFactory(16, null);
		String textFieldName = "uploadfile";
		FileItem item = factory.createItem(textFieldName, file.getContentType() , false, file.getOriginalFilename());
		InputStream inputStream = file.getInputStream();
		try (OutputStream os = item.getOutputStream()) {
			IoUtil.copy(inputStream, os);
		} finally {
			IoUtil.close(inputStream);
		}
		
		return new CommonsMultipartFile(item);
	}
	
 
	/**
     * 获取文件内容长度
     *
     * @param name
     * @return
     */
    public static long getFileContentLength(String name) {
        File file = new File(name);
        return file.exists() && file.isFile() ? file.length() : 0;
    }

	/**
	 * 文件的md5
	 * 
	 * @param inputStream
	 * @return
	 */
	public String fileMd5(InputStream inputStream) {
		try {
			return DigestUtils.md5Hex(inputStream);
		} catch (IOException e) {
			log.error("FileUtil->fileMd5:{}", e.getMessage());
		}

		return null;
	}

	public String saveFile(MultipartFile file, String path) {
		try {
			File targetFile = new File(path);
			if (targetFile.exists()) {
				return path;
			}

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			file.transferTo(targetFile);

			return path;
		} catch (Exception e) {
			log.error("FileUtil->saveFile:{}", e.getMessage());
		}

		return null;
	}

	public String saveBigFile(String guid, File parentFileDir, File destTempFile) {
		try {
			if (parentFileDir.isDirectory()) {
				if (!destTempFile.exists()) {
					// 先得到文件的上级目录，并创建上级目录，在创建文件,
					destTempFile.getParentFile().mkdir();
					try {
						// 创建文件
						destTempFile.createNewFile(); // 上级目录没有创建，这里会报错
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < parentFileDir.listFiles().length; i++) {
					File partFile = new File(parentFileDir, guid + "_" + i + ".part");
					FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
					// 遍历"所有分片文件"到"最终文件"中
					FileUtils.copyFile(partFile, destTempfos);
					destTempfos.close();
				}
			}
		} catch (Exception e) {
			log.error("FileUtil->saveBigFile:{}", e.getMessage());
		}

		return null;
	}

	public boolean deleteFile(String pathname) {
		File file = new File(pathname);
		if (file.exists()) {
			boolean flag = file.delete();

			if (flag) {
				File[] files = file.getParentFile().listFiles();
				if (files == null || files.length == 0) {
					file.getParentFile().delete();
				}
			}

			return flag;
		}

		return false;
	}

	// byte数组写到到硬盘上
	public void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);

			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IoUtil.close(bos);
			IoUtil.close(fos);
		}
	}

	/**
	 * 从网络Url中下载文件
	 * 
	 * @param urlStr
	 * @param fileName
	 * @param savePath
	 * @throws IOException
	 */
	public void downLoadByUrl(String urlStr, String savePath, String fileName) {
		InputStream inputStream = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 设置超时间为3秒
			conn.setConnectTimeout(3 * 1000);

			// 得到输入流
			inputStream = conn.getInputStream();
			// 获取自己数组
			byte[] getData = readInputStream(inputStream);

			// 文件保存位置
			File saveDir = new File(savePath);
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			File file = new File(saveDir + File.separator + fileName);
			fos = new FileOutputStream(file);
			fos.write(getData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IoUtil.close(fos);
			IoUtil.close(inputStream);

		}
	}

	/**
	 * 从输入流中获取字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.close();
			return bos.toByteArray();
		}
	}

}
