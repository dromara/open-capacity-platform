package com.open.capacity.file.event.lisener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.ExecutorService;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.file.constant.CommandType;
import com.open.capacity.file.context.UploadContext;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.event.UploadEvent;
import com.open.capacity.file.service.FileServiceFactory;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

@Service
@Channel(CommandType.BATCH_UPLOAD)
public class BatchUploadEventLisener extends EventListener<UploadEvent, UploadContext> {

	@Resource
	private FileServiceFactory fileServiceFactory;

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean accept(BaseEvent event) {

		UploadEvent uploadEvent = (UploadEvent) event;
		// s3 批量个文件上传
		if (CommandType.BATCH_UPLOAD.equals(uploadEvent.getCommandType())) {
			return true;
		}
		return false;
	}

	@Override
	@SneakyThrows
	public void onEvent(UploadEvent event, UploadContext eventContext) {
		String ko = objectMapper.writeValueAsString(ResponseEntity.succeed("批量上传失败"));
		executorService.execute(CommandType.BATCH_UPLOAD, () -> {
			ServletResponse response = eventContext.getAsyncContext().getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				TenantContextHolder.setTenant(event.getTenant());
				List<FileInfo> fileInfos = Lists.newArrayList();
				for (MultipartFile file : event.getFiles()) {
					FileInfo fileInfo = fileServiceFactory.getService(event.getFileType()).upload(file);
					fileInfos.add(fileInfo);
				}
				out.write(objectMapper.writeValueAsString(fileInfos).getBytes(StandardCharsets.UTF_8));
				out.flush();
			} catch (IOException e) {
				IoUtil.write(out, false, ko.getBytes(StandardCharsets.UTF_8));
			} finally {
				IoUtil.close(out);
				eventContext.getAsyncContext().complete();
			}
		});

	}

}
