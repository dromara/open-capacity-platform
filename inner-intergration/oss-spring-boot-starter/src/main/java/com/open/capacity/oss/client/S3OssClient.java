package com.open.capacity.oss.client;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.open.capacity.oss.config.OssProperties;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * s3 是一个协议
 * S3是Simple Storage Service的缩写，即简单存储服务
 */
@RequiredArgsConstructor
public class S3OssClient implements OssClient {

    private final AmazonS3 amazonS3;

    @Override
    public void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket((bucketName));
        }
    }
    
    @Override
	public List<Bucket> getAllBuckets() {
		return amazonS3.listBuckets();
	}

	@Override
	public Optional<Bucket> getBucket(String bucketName) {
		return amazonS3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
	}

	@Override
	public void removeBucket(String bucketName) {
		amazonS3.deleteBucket(bucketName);
		
	}
	
	@Override
	public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
		ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
		return new ArrayList<>(objectListing.getObjectSummaries());
	}

    @Override
    public String getObjectURL(String bucketName, String objectName) {
        URL url = amazonS3.getUrl(bucketName, objectName);
        String str = url.toString();
        return str;
    }

    @Override
    public S3Object getObjectInfo(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size, String contextType,boolean isPub) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contextType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, stream, objectMetadata);
        putObjectRequest.getRequestClientOptions().setReadLimit(Long.valueOf(size).intValue() + 1);
        if(isPub) putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        return amazonS3.putObject(putObjectRequest);
    }

    /**
     * 获取文件外链
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires 过期时间，请注意该值必须小于7天
     * @param method 文件操作方法：GET（下载）、PUT（上传）
     * @return url
     * @see AmazonS3#generatePresignedUrl(String bucketName, String key, Date expiration,
     * HttpMethod method)
     */
    public String getObjectURL(String bucketName, String objectName, Duration expires, HttpMethod method) {
        // Set the pre-signed URL to expire after `expires`.
        Date expiration = Date.from(Instant.now().plus(expires));

        // Generate the pre-signed URL.
        URL url = amazonS3.generatePresignedUrl(
                new GeneratePresignedUrlRequest(bucketName, objectName).withMethod(method).withExpiration(expiration));
        return url.toString();
    }

    @Override
    public AmazonS3 getS3Client() {
        return amazonS3;
    }

	@Override
	public S3Object getObject(String bucketName, String objectName) {
		return amazonS3.getObject(bucketName, objectName);
	}

	@Override
	public void removeObject(String bucketName, String objectName) {
		amazonS3.deleteObject(bucketName, objectName);
		
	}
}
