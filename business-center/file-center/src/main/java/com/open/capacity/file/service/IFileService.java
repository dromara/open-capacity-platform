package com.open.capacity.file.service;

import java.util.Map;

import org.springframework.plugin.core.Plugin;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.file.constant.FileType;
import com.open.capacity.file.entity.DownloadDto;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.entity.FileInfoPart;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 文件service 目前仅支持阿里云oss,七牛云
 */
public interface IFileService extends IService<FileInfo>, Plugin<FileType> {

	FileInfo upload(MultipartFile file);

	boolean delete(Long id);

	FileInfo getById(Long id);

	PageResult<FileInfo> findList(Map<String, Object> params);

	void unZip(String filePath, String descDir);

	FileInfoPart chunk(String guid, Integer chunk, MultipartFile file, Integer chunks, String filePath);

	FileInfo merge(String guid, String fileName, String filePath);

	void uploadError(String guid, String fileName, String filePath) throws Exception;

	DownloadDto download(String fileName);

	DownloadDto downloadChunkFile(String fileId,String range);

}
