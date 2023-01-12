package com.open.capacity.file.event.lisener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import lombok.Cleanup;
import lombok.SneakyThrows;

@Service
@Channel(CommandType.PART_UPLOAD)
public class PartUploadEventLisener extends EventListener<UploadEvent, UploadContext> {

	@Resource
	private FileServiceFactory fileServiceFactory;

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean accept(BaseEvent event) {

		UploadEvent uploadEvent = (UploadEvent) event;

		// s3 单个文件分片上传
		if (CommandType.PART_UPLOAD.equals(uploadEvent.getCommandType())) {
			return true;
		}
		return false;
	}

	@Override
	@SneakyThrows
	public void onEvent(UploadEvent event, UploadContext eventContext) {
		String ok = objectMapper.writeValueAsString(ResponseEntity.succeed("分段上传成功"));
		String ko = objectMapper.writeValueAsString(ResponseEntity.succeed("分段上传失败"));
		executorService.execute(CommandType.PART_UPLOAD, () -> {
			ServletResponse response = eventContext.getAsyncContext().getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				TenantContextHolder.setTenant(event.getTenant());
				MultipartFile file = event.getFiles().get(0);
				String filepath = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
				fileServiceFactory.getService(event.getFileType()).chunk(event.getGuid(), event.getChunk(), file,
						event.getChunks(), filepath);
				IoUtil.write(out, false, ok.getBytes(StandardCharsets.UTF_8));
				out.flush();
			} catch (Exception e1) {
				IoUtil.write(out, false, ko.getBytes(StandardCharsets.UTF_8));
			} finally {
				IoUtil.close(out);
				eventContext.getAsyncContext().complete();
			}
		});
	}

}
