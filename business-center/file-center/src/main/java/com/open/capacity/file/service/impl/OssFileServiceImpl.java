package com.open.capacity.file.service.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.plugin.core.Plugin;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.util.IOUtils;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.lock.DistributedLock;
import com.open.capacity.common.lock.LockAdapter;
import com.open.capacity.common.utils.UUIDUtils;
import com.open.capacity.file.constant.FileType;
import com.open.capacity.file.entity.DownloadDto;
import com.open.capacity.file.entity.FileInfo;
import com.open.capacity.file.entity.FileInfoPart;
import com.open.capacity.file.mapper.FileInfoPartMapper;
import com.open.capacity.file.mapper.FileMapper;
import com.open.capacity.file.utils.FileUtil;
import com.open.capacity.oss.client.OssClient;
import com.open.capacity.oss.config.OssProperties;
import com.open.capacity.redis.repository.RedisRepository;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OssFileServiceImpl extends AbstractFileService implements Plugin<FileType> {

	@Resource
	private OssClient ossClient;

	@Resource
	private OssProperties ossProperties;

	@Autowired
	private FileMapper fileMapper;

	@Resource
	private FileInfoPartMapper fileInfoPartMapper;

	@Resource
	private RedisRepository redisRepository;

	@Autowired
	private DistributedLock locker;

	@Override
	protected FileMapper getFileMapper() {
		return fileMapper;
	}

	@Override
	protected FileInfoPartMapper getFileInfoPartMapper() {
		return fileInfoPartMapper;
	}

	@Override
	public boolean supports(FileType FileType) {
		return FileType.S3.compareTo(FileType) == 0;
	}

	@Override
	@SneakyThrows
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) {

		Assert.isTrue(file != null, "No files");
		String bucketName = ossProperties.getBucketName();
		String directory = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
		// 验证文件类型
		String fileName = fileInfo.getName();
		String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		String path = directory + UUIDUtils.getGUID32() + suffix;
		ossClient.putObject(bucketName, path, file.getInputStream());
		String objectURL = "";
		if (StrUtil.isNotBlank(ossProperties.getDomain())) {
			S3Object object = ossClient.getObject(bucketName, path);
			objectURL = ossProperties.getDomain() + object.getKey();
		} else {
			objectURL = ossClient.getObjectURL(bucketName, path);
		}
		fileInfo.setPath(path);
		fileInfo.setUrl(objectURL);
	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		ossClient.removeObject(ossProperties.getBucketName(), fileInfo.getPath());
		return true;
	}

	@Override
	@SneakyThrows
	protected FileInfoPart chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks, String filePath) {

		FileInfo info = FileUtil.getFileInfo(file);
		StringBuffer path = new StringBuffer();
		path.append(filePath).append(guid).append("/").append(file.getOriginalFilename());
		String lockKey = CommonConstant.PREFIX_FILE_KEY + StrUtil.COLON + "UPLOAD" + StrUtil.COLON + guid;
		String pathKey = CommonConstant.PREFIX_FILE_KEY + StrUtil.COLON + "PATH" + StrUtil.COLON + guid;
		String uploadId = getUploadId(pathKey, path, lockKey);
		FileInfoPart fileInfoPart = null;
		try {
			BufferedInputStream stream = new BufferedInputStream(file.getInputStream());
			long partSize = file.getSize();
			UploadPartRequest partRequest = new UploadPartRequest().withBucketName(ossProperties.getBucketName())
					.withKey(path.toString()).withUploadId(uploadId).withPartNumber(chunk + 1).withPartSize(partSize)
					.withInputStream(stream);
			partRequest.getRequestClientOptions().setReadLimit(1024 * 1024 * 100);
			UploadPartResult uploadResult = ossClient.getS3Client().uploadPart(partRequest);
			info.setPath(path.toString());
			fileInfoPart = Optional.ofNullable(info).map(item -> {
				FileInfoPart part = new FileInfoPart();
				part.setGuid(guid);
				part.setName(info.getName());
				part.setSize(info.getSize());
				part.setIsImg(info.getIsImg());
				part.setContentType(info.getContentType());
				part.setMd5(info.getMd5());
				part.setUploadId(uploadId);
				part.setETag(uploadResult.getPartETag().getETag());
				part.setPart(uploadResult.getPartETag().getPartNumber());
				part.setPath(path.toString());
				return part;
			}).orElseThrow(() -> new BusinessException("文件转换异常" + info.getName()));

		} catch (IOException e) {
			removeUpload(lockKey, pathKey);
			throw e;
		} catch (SdkClientException e) {
			ossClient.getS3Client().abortMultipartUpload(
					new AbortMultipartUploadRequest(ossProperties.getBucketName(), path.toString(), uploadId));
			removeUpload(lockKey, pathKey);
			throw e;
		}
		return fileInfoPart;
	}

	private void removeUpload(String lockey, String pathKey) {
		redisRepository.del(lockey);
		redisRepository.del(pathKey);
	}

	@SneakyThrows
	private String getUploadId(String pathKey, StringBuffer path, String lockKey) {
		String uploadId = redisRepository.getString(lockKey);
		if (uploadId == null) {
			try (LockAdapter lock = locker.lock(lockKey, 10, TimeUnit.SECONDS)) {
				uploadId = redisRepository.getString(lockKey);
				if (uploadId == null) {
					InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
							ossProperties.getBucketName(), path.toString())
									.withCannedACL(CannedAccessControlList.PublicRead);
					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setExpirationTime(DateTime.now().offset(DateField.DAY_OF_YEAR, 365));
					initRequest.setObjectMetadata(metadata);
					InitiateMultipartUploadResult initResult = ossClient.getS3Client()
							.initiateMultipartUpload(initRequest);
					uploadId = initResult.getUploadId();
					redisRepository.setString(lockKey, uploadId);
					redisRepository.setString(pathKey, path.toString());
				}
			}
		}

		return uploadId;
	}

	@Override
	protected FileInfo mergeFile(String guid, String fileName, String filePath, List<FileInfoPart> list) {
		String lockKey = CommonConstant.PREFIX_FILE_KEY + StrUtil.COLON + "UPLOAD" + StrUtil.COLON + guid;
		String pathKey = CommonConstant.PREFIX_FILE_KEY + StrUtil.COLON + "PATH" + StrUtil.COLON + guid;

		FileInfo fileInfo = null;
		try {
			Assert.isTrue(CollectionUtils.isNotEmpty(list), "No files");
			FileInfoPart fileInfoPart = list.stream().filter(Objects::nonNull).findFirst().get();
			String uploadId = redisRepository.getString(lockKey);
			String path = redisRepository.getString(pathKey);
			long size = list.stream().mapToLong(FileInfoPart::getSize).sum();
			fileInfo = new FileInfo();
			fileInfo.setMd5(fileInfoPart.getMd5());
			fileInfo.setIsImg(fileInfoPart.getIsImg());
			fileInfo.setName(fileInfoPart.getName());
			fileInfo.setPath(path);
			fileInfo.setUpdateTime(new Date());
			fileInfo.setContentType(fileInfoPart.getContentType());
			fileInfo.setSize(size);
			List<PartETag> partETags = list.stream().map(item -> {
				return new PartETag(item.getPart(), item.getETag());
			}).collect(Collectors.toList());
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					ossProperties.getBucketName(), path.toString(), uploadId, partETags.stream()
							.sorted(Comparator.comparing(PartETag::getPartNumber)).collect(Collectors.toList()));
			ossClient.getS3Client().completeMultipartUpload(compRequest);
			String objectURL = "";
			if (StrUtil.isNotBlank(ossProperties.getDomain())) {
				S3Object object = ossClient.getObject(ossProperties.getBucketName(), path);
				objectURL = ossProperties.getDomain() + object.getKey();
			} else {
				objectURL = ossClient.getObjectURL(ossProperties.getBucketName(), path);
			}
			fileInfo.setUrl(objectURL);
		} finally {
			removeUpload(lockKey, pathKey);
		}

		return fileInfo;

	}

	@Override
	@SneakyThrows
	protected DownloadDto downloadFile(FileInfo file) {
		S3Object s3Object = ossClient.getObject(ossProperties.getBucketName(), file.getPath());
		try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
			return DownloadDto.builder().fileName(file.getName()).bytes(IOUtils.toByteArray(inputStream)).build();
		}
	}

	@Override
	@SneakyThrows
	protected DownloadDto downloadChunkFile(FileInfo file, String range) {
		ObjectMetadata objectMetadata = ossClient.getS3Client().getObjectMetadata(ossProperties.getBucketName(),
				file.getPath());
		// 总文件大小
		long fileLength = objectMetadata.getContentLength() - 1;
		// 开始下载位置
		long startByte = 0;
		// 结束下载位置
		long endByte = objectMetadata.getContentLength() - 1;
		int flag = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=1024-）；2,从某字节开始到某字节结束的下载（bytes=1024-2048）
		// client requests a file block download start byte
		if (Objects.nonNull(range)) {
			range = range.substring(range.lastIndexOf("=") + 1).trim();
			String ranges[] = range.split("-");
			try {
				// 根据range解析下载分片的位置区间
				if (ranges.length == 1) {
					if (range.endsWith("-")) {
						// 情况1，如：bytes=1024- 第1024个字节到最后字节的数据
						flag = 1;
						startByte = Long.parseLong(ranges[0]);
					}
				} else if (ranges.length == 2) {
					// 情况2，如：bytes=1024-2048 第1024个字节到2048个字节的数据
					flag = 2;
					startByte = Long.parseLong(ranges[0]);
					endByte = Long.parseLong(ranges[1]);
				}
			} catch (NumberFormatException e) {
				// 从头开始的全文下载
				flag = 0;
				startByte = 0;
				endByte = objectMetadata.getContentLength();
			}
		}
		// 要下载的长度
		long contentLength = endByte - startByte + 1;
		GetObjectRequest getObjectRequest = new GetObjectRequest(ossProperties.getBucketName(), file.getPath());
		// 响应的格式是:
		String contentRange = "";
		contentRange = new StringBuffer("bytes ").append(startByte).append("-").append(endByte).append("/")
				.append(fileLength).toString();

		Assert.isTrue(endByte <= fileLength, "分段下载异常");
		if (flag == 1) {
			getObjectRequest = getObjectRequest.withRange(startByte);
		} else if (flag == 2) {
			getObjectRequest = getObjectRequest.withRange(startByte, endByte);
		}
		if (endByte == fileLength) {
			flag = 1;
		}
		S3Object s3Object = ossClient.getS3Client().getObject(getObjectRequest);
		try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
			return DownloadDto.builder().fileName(file.getName()).bytes(IOUtils.toByteArray(inputStream))
					.contentType(objectMetadata.getContentType()).contentRange(contentRange).fileSize(fileLength)
					.contentLength(contentLength).flag(flag).build();
		}

	}

}
