package com.open.capacity.file.event.lisener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.file.constant.CommandType;
import com.open.capacity.file.constant.FileType;
import com.open.capacity.file.context.UploadContext;
import com.open.capacity.file.entity.DownloadDto;
import com.open.capacity.file.event.DownloadEvent;
import com.open.capacity.file.service.FileServiceFactory;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import lombok.SneakyThrows;

@Service
@Channel(CommandType.DOWNLOAD_ZIP)
public class DownloadZipEventLisener extends EventListener<DownloadEvent, UploadContext> {

	@Resource
	private FileServiceFactory fileServiceFactory;

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean accept(BaseEvent event) {

		DownloadEvent uploadEvent = (DownloadEvent) event;

		// s3 单个文件上传
		if (CommandType.DOWNLOAD_ZIP.equals(uploadEvent.getCommandType())) {
			return true;
		}
		return false;
	}

	@Override
	@SneakyThrows
	public void onEvent(DownloadEvent event, UploadContext eventContext) {
		String ko = objectMapper.writeValueAsString(ResponseEntity.succeed("下载压缩文件失败"));
		executorService.execute(CommandType.DOWNLOAD_ZIP, () -> {
			HttpServletResponse response = (HttpServletResponse) eventContext.getAsyncContext().getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				TenantContextHolder.setTenant(event.getTenant());
				DownloadDto downloadInfo = fileServiceFactory.getService(FileType.S3).download(event.getFileId());
				response.reset();
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
						String.format("attachment; filename=%s.zip", URLUtil.encode(downloadInfo.getFileName())));
				response.setContentType("application/octet-stream; charset=utf-8");
				response.setHeader("Access-Control-Allow-Headers", "*");
				response.setHeader("Access-Control-Expose-Headers", "content-disposition");
				response.setCharacterEncoding("UTF-8");
				try (ZipOutputStream zip = new ZipOutputStream(out)) {
					zip.putNextEntry(new ZipEntry(downloadInfo.getFileName()));
					IoUtil.write(zip, false, downloadInfo.getBytes());

				}
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
