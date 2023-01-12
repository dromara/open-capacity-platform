package com.open.capacity.file.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import com.open.capacity.file.constant.FileType;
import com.open.capacity.file.event.UploadEvent;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnablePluginRegistries(IFileService.class)
public class FileServiceFactory {

	private static final String ERROR_MSG = "找不到文件类型类型为 {} 的实现类";

	@Resource
	private List<IFileService> fileService;

	@Autowired
	@Qualifier("iFileServiceRegistry")
	private PluginRegistry<IFileService, FileType> registry;

	public IFileService getService(UploadEvent uploadEvent) {
		FileType fileType = uploadEvent.getFileType();
		if (fileType == null) {
			fileType = FileType.S3;
		}
		return this.getService(fileType);
	}

	public IFileService getService(final FileType fileType) {

		return Optional.ofNullable(registry.getPluginFor(fileType))
				.orElseThrow(() -> new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, fileType)));

	}

}
