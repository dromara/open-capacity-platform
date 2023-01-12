package com.open.capacity.file.event.lisener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.marshalling.ByteInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.ExecutorService;
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
@Channel(CommandType.PART_DOWNLOAD)
public class PartDownloadEventLisener extends EventListener<DownloadEvent, UploadContext> {

	@Resource
	private FileServiceFactory fileServiceFactory;
	
	@Resource
	private ObjectMapper objectMapper;

	@Override
	public boolean accept(BaseEvent event) {

		DownloadEvent uploadEvent = (DownloadEvent) event;

		// s3 单个文件上传
		if (CommandType.PART_DOWNLOAD.equals(uploadEvent.getCommandType())) {
			return true;
		}
		return false;
	}

	@Override
	@SneakyThrows
	public void onEvent(DownloadEvent event, UploadContext eventContext) {
		String ko = objectMapper.writeValueAsString(ResponseEntity.succeed("分段下载失败"));
		executorService.execute(CommandType.PART_DOWNLOAD ,()-> {
			HttpServletResponse response = (HttpServletResponse) eventContext.getAsyncContext().getResponse();
			response.setCharacterEncoding("UTF-8");
			ServletOutputStream out = null ;
			try{
				out = response.getOutputStream();
				TenantContextHolder.setTenant(event.getTenant());
				DownloadDto downloadInfo = fileServiceFactory.getService(FileType.S3).downloadChunkFile(event.getFileId() ,event.getRange() );
				// 断点开始 响应头设置
		        //https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept-Ranges
		        response.setHeader("Accept-Ranges", "bytes");
		        if(downloadInfo.getFlag()==2) {
		        	response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		        }
				response.setHeader("Content-Range", downloadInfo.getContentRange());
				response.setContentType(downloadInfo.getContentType());
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;fileName=" + URLUtil.encode(downloadInfo.getFileName()));
				response.setContentLengthLong(downloadInfo.getContentLength());
				response.addHeader(HttpHeaders.CACHE_CONTROL, "private");
				response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition,Content-Length");
				IoUtil.write(out, false, downloadInfo.getBytes());
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
