package com.open.capacity.file.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.file.entity.DownloadDto;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.entity.FileInfoPart;
import com.open.capacity.file.mapper.FileInfoPartMapper;
import com.open.capacity.file.mapper.FileMapper;
import com.open.capacity.file.service.IFileService;
import com.open.capacity.file.utils.FileUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 AbstractFileService 抽取类 根据filetype
 *          实例化具体oss对象
 */
@Slf4j
public abstract class AbstractFileService extends ServiceImpl<FileMapper, FileInfo> implements IFileService {

	protected abstract FileMapper getFileMapper();

	protected abstract FileInfoPartMapper getFileInfoPartMapper();

	/**
	 * 上传文件
	 *
	 * @param file
	 * @param fileInfo
	 */
	protected abstract void uploadFile(MultipartFile file, FileInfo fileInfo);

	/**
	 * 删除文件资源
	 *
	 * @param fileInfo
	 * @return
	 */
	protected abstract boolean deleteFile(FileInfo fileInfo);

	/**
	 * 上传大文件 分片上传 每片一个临时文件
	 * 
	 * @param file
	 * @return
	 */
	protected abstract FileInfoPart chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks,
			String filePath);

	/**
	 * 合并分片文件 每一个小片合并一个完整文件
	 * 
	 * @param fileName
	 * @return
	 */
	protected abstract FileInfo mergeFile(String guid, String fileName, String filePath, List<FileInfoPart> list);

	/**
	 * 失败回调
	 * 
	 * @param guid
	 * @param fileName
	 * @param filePath
	 * @throws Exception
	 */
//	protected abstract void uploadError( String guid,String fileName,String filePath ) throws Exception;
 
	/**
	 * 下载文件
	 * @param fileName
	 * @return
	 */
	protected abstract DownloadDto  downloadFile(FileInfo file);
	
	/**
	 * 下载大文件
	 * @param file
	 * @return
	 */
	protected abstract DownloadDto downloadChunkFile(FileInfo file,String range);


	@Override
	@SneakyThrows
	public FileInfo upload(MultipartFile file) {
		FileInfo fileInfo = FileUtil.getFileInfo(file);
		FileInfo oldFileInfo = getFileMapper().selectById(fileInfo.getId());
		if (oldFileInfo != null) {
			return oldFileInfo;
		}

		if (!fileInfo.getName().contains(".")) {
			throw new IllegalArgumentException("缺少后缀名");
		}
		// 文件存储
		uploadFile(file, fileInfo);
		// 将文件信息保存到数据库
		getFileMapper().insert(fileInfo);
		return fileInfo;
	}

	@Override
	public boolean delete(Long id) {
		FileInfo fileInfo = this.baseMapper.selectById(id);
		deleteFile(fileInfo);
		this.remove(Wrappers.<FileInfo>lambdaUpdate().eq(FileInfo::getId, id));
		return true;
	}

	@Override
	public FileInfo getById(Long id) {
		return getFileMapper().selectById(id);
	}

	@Override
	public PageResult<FileInfo> findList(Map<String, Object> params) {
		Page<FileInfo> page = new Page<>(MapUtils.getInteger(params, "page", 1),
				MapUtils.getInteger(params, "limit", 10));
		List<FileInfo> list = this.getFileMapper().findList(page, params);
		return PageResult.<FileInfo>builder().data(list).statusCodeValue(0).count(page.getTotal()).build();
	}

	@Override
	public void unZip(String filePath, String descDir) {

	}

	@Override
	public FileInfoPart chunk(String guid, Integer chunk, MultipartFile file, Integer chunks, String filePath) {
		//分片存储
		FileInfoPart fileInfoPart = chunkFile(guid, chunk, file, chunks, filePath);
		//分片表记录
		getFileInfoPartMapper().insert(fileInfoPart);
		return fileInfoPart;
	}

	@Override
	public FileInfo merge(String guid, String fileName, String filePath) {

		FileInfo fileInfo = null ;
		try {
			//查询分片表
			List<FileInfoPart> list = getFileInfoPartMapper()
					.selectList(Wrappers.<FileInfoPart>lambdaQuery().eq(FileInfoPart::getGuid, guid));
			//合并文件
			fileInfo = mergeFile(guid, fileName, filePath, list);
			//文件转存
			getFileMapper().insert(fileInfo);
		} finally {
			getFileInfoPartMapper().delete(Wrappers.<FileInfoPart>lambdaUpdate().eq(FileInfoPart::getGuid, guid));
		}

		return fileInfo;
	}
	
	
	
	@Override
	public DownloadDto download(String fileId) {
		FileInfo file = getFileMapper().selectById(fileId);
		Assert.isTrue(file != null, "No files");
		return this.downloadFile(file) ;
	}
	
	@Override
	public DownloadDto downloadChunkFile(String fileId,String range) {
		FileInfo file = getFileMapper().selectById(fileId);
		Assert.isTrue(file != null, "No files");
		return this.downloadChunkFile(file,range) ;
	}
 
	
	
	@Override
	public void uploadError(String guid, String fileName, String filePath) {
		File parentFileDir = new File(filePath + File.separator + guid);
		try {
		} finally {
			// 删除临时目录中的分片文件
			try {
				FileUtils.deleteDirectory(parentFileDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
