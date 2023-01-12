package com.open.capacity.oss.client;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Oss 基础操作
 * 想要更复杂订单操作可以直接获取AmazonS3，通过AmazonS3 来进行复杂的操作
 * https://docs.aws.amazon.com/zh_cn/sdk-for-java/v1/developer-guide/examples-s3-buckets.html
 */
public interface OssClient{
    /**
     * 创建bucket
     * @param bucketName
     */
    void createBucket(String bucketName);
    
    /**
	 * 获取全部bucket
	 * <p>
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
	 * API Documentation</a>
	 */
    List<Bucket> getAllBuckets() ;
    
    /**
	 * @param bucketName bucket名称
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListBuckets">AWS
	 * API Documentation</a>
	 */
	Optional<Bucket> getBucket(String bucketName) ;
	
	/**
	 * @param bucketName bucket名称
	 * @see <a href=
	 * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteBucket">AWS API
	 * Documentation</a>
	 */
	void removeBucket(String bucketName) ;
	
	  
    /**
	 * 根据文件前置查询文件
	 * @param bucketName bucket名称
	 * @param prefix 前缀
	 * @param recursive 是否递归查询
	 * @return S3ObjectSummary 列表
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects">AWS
	 * API Documentation</a>
	 */
	List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) ;
	
    /**
     * 获取url
     * @param bucketName
     * @param objectName
     * @return
     */
    String getObjectURL(String bucketName, String objectName);


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
    String getObjectURL(String bucketName, String objectName, Duration expires, HttpMethod method);

	/**
	 * 获取文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 二进制流
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject">AWS
	 * API Documentation</a>
	 */
	public S3Object getObject(String bucketName, String objectName)  ;


    /**
     * 获取存储对象信息
     * @param bucketName
     * @param objectName
     * @return
     */
    S3Object getObjectInfo(String bucketName, String objectName);


    /**
     * 上传文件
     * @param bucketName
     * @param objectName
     * @param stream
     * @param size
     * @param contextType
	 * @param isPub				上传文件是否公有
     * @return
     * @throws IOException
     */
    PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size, String contextType,boolean isPub) throws IOException;

    /**
  	 * 删除文件
  	 * @param bucketName bucket名称
  	 * @param objectName 文件名称
  	 * @throws Exception
  	 * @see <a href=
  	 * "http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteObject">AWS API
  	 * Documentation</a>
  	 */
  	void removeObject(String bucketName, String objectName) ;

    default PutObjectResult putObject(String bucketName, String objectName, InputStream stream) throws IOException{
        return putObject(bucketName,objectName,stream, stream.available(), "application/octet-stream",true);
    }

    AmazonS3 getS3Client();
    
  
  
}
