package com.open.capacity.file.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.beust.jcommander.internal.Lists;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.file.constant.CommandType;
import com.open.capacity.file.constant.FileType;
import com.open.capacity.file.context.UploadContext;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.entity.MergeFileDTO;
import com.open.capacity.file.event.DownloadEvent;
import com.open.capacity.file.event.UploadEvent;
import com.open.capacity.file.service.FileServiceFactory;
import com.open.capacity.file.utils.FileUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(tags = "文件模块api")
@Slf4j
@RequestMapping("/file")
public class FileController {

	@Resource
	private FileServiceFactory fileServiceFactory;

	@Autowired
	private DisruptorTemplate disruptorTemplate;

	@ApiOperation("文件上传")
	@PostMapping(value = "/files-anon")
	@SneakyThrows
	public void filesAnon(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

		Assert.isTrue(file != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		List<MultipartFile> files = Lists.newArrayList();
		files.add(FileUtil.createFileItem(file));
		UploadEvent event = UploadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.UPLOAD).files(files).build();
		disruptorTemplate.publish(CommandType.UPLOAD, event, context);
	}

	@ApiOperation("批量上传文件上传")
	@PostMapping(value = "/upload", consumes = "multipart/form-data", produces = { "application/json" })
	@SneakyThrows
	public void filesAnon(@RequestParam("files") List<MultipartFile> files, HttpServletRequest request) {

		Assert.isTrue(files != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		UploadEvent event = UploadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.BATCH_UPLOAD)
				.files(files.stream().map(file -> FileUtil.createFileItem(file)).collect(Collectors.toList())).build();
		disruptorTemplate.publish(CommandType.BATCH_UPLOAD, event, context);
	}

	/**
	 * 上传大文件
	 * 
	 * @param file
	 * @param chunks
	 */
	@PostMapping(value = "/files-anon/bigFile")
	public void bigFile(String guid, Integer chunk, MultipartFile file, Integer chunks, HttpServletRequest request) {

		Assert.isTrue(file != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		List<MultipartFile> files = Lists.newArrayList();
		files.add(FileUtil.createFileItem(file));
		UploadEvent event = UploadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.PART_UPLOAD).files(files).chunk(chunk).chunks(chunks).guid(guid).build();
		disruptorTemplate.publish(CommandType.PART_UPLOAD, event, context);
	}

	/**
	 * 合并文件
	 * 
	 * @param mergeFileDTO
	 */
	@RequestMapping(value = "/files-anon/merge", method = RequestMethod.POST)
	public ResponseEntity mergeFile(@RequestBody MergeFileDTO mergeFileDTO) {
		try {

			String filepath = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
			return ResponseEntity.succeed(fileServiceFactory.getService(FileType.S3).merge(mergeFileDTO.getGuid(),
					mergeFileDTO.getFileName(), filepath), "操作成功");
		} catch (Exception ex) {
			return ResponseEntity.failed("操作失败");
		}
	}

	@GetMapping("/download")
	public void downloadFile(@RequestParam String id, HttpServletRequest request) {

		Assert.isTrue(id != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		DownloadEvent event = DownloadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.DOWNLOAD).fileId(id).build();
		disruptorTemplate.publish(CommandType.DOWNLOAD, event, context);

	}
	
	
	@GetMapping("/downloadBigFile")
	public void downloadBigFile(@RequestParam String id, HttpServletRequest request) {
		Assert.isTrue(id != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		DownloadEvent event = DownloadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.PART_DOWNLOAD).fileId(id).range(request.getHeader("Range")).build();
		disruptorTemplate.publish(CommandType.PART_DOWNLOAD, event, context);

	}
	
	
	
	
	@GetMapping("/download-zip")
	public void downloadZipFile(@RequestParam String id, HttpServletRequest request) {

		Assert.isTrue(id != null, "No files");
		UploadContext context = new UploadContext();
		javax.servlet.AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(900000);
		context.setAsyncContext(asyncContext);
		DownloadEvent event = DownloadEvent.builder().tenant(TenantContextHolder.getTenant()).fileType(FileType.S3)
				.commandType(CommandType.DOWNLOAD_ZIP).fileId(id).build();
		disruptorTemplate.publish(CommandType.DOWNLOAD_ZIP, event, context);

	}

	/**
	 * 文件查询
	 *
	 * @param params
	 * @return
	 */
	@GetMapping("/files")
	public PageResult<FileInfo> findFiles(@RequestParam Map<String, Object> params) {
		return fileServiceFactory.getService(FileType.S3).findList(params);
	}

	/**
	 * 文件删除
	 *
	 * @param id
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable Long id) {

		Try.of(() -> fileServiceFactory.getService(FileType.S3).delete(id))
				.onFailure(ex -> log.error("file-delete-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));

		return ResponseEntity.succeed("操作成功");

	}

	/**
	 * 上传失败
	 * 
	 * @param mergeFileDTO
	 * @return
	 */
	@RequestMapping(value = "/files-anon/uploadError", method = RequestMethod.POST)
	public ResponseEntity uploadError(@RequestBody MergeFileDTO mergeFileDTO) {
		try {
			// 使用默认的 FileService
			fileServiceFactory.getService(FileType.S3).uploadError(mergeFileDTO.getGuid(), mergeFileDTO.getFileName(),
					"");
			return ResponseEntity.succeed("操作成功");
		} catch (Exception ex) {
			return ResponseEntity.failed("操作失败");
		}
	}

}
