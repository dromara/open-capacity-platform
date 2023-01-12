package com.open.capacity.file.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;


/**
 * @Author:      owen
 * @Date:        2020/4/8 16:17
 * @Description: 分段上传信息本地缓存
 */

public class PartETagsCacheUtils {
	  //创建一个列表保存某个文件所有分段的 PartETag
    private static Map<String, List<PartETag>> partETagList = new ConcurrentHashMap<>();
    //分段上传<文件id，uploadId>
    private static Map<String,String> uploadIds = new ConcurrentHashMap<>();

    public static void addPartETag(String id, PartETag partETag) {
        List<PartETag> partETags = partETagList.get(id);
        if (null == partETags){
            partETags = new ArrayList<>();
        }
        partETags.add(partETag);
        partETagList.put(id,partETags);
    }

    public static List<PartETag> getPartETags(String id){
        return partETagList.get(id);
    }

    public static void cleanCache(String id){
        partETagList.remove(id);
        uploadIds.remove(id);
    }

    public static synchronized String getUploadId(String id, String bucketName, String key, AmazonS3 amazonS3){
        String uploadId = uploadIds.get(id);
        if (null == uploadId){
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key)
                    .withCannedACL(CannedAccessControlList.PublicRead);
            //启动分段上传，并返回包含上传ID的InitiateMultipartUploadResult
            InitiateMultipartUploadResult initResult = amazonS3.initiateMultipartUpload(initRequest);
            uploadId = initResult.getUploadId();
            uploadIds.put(id,uploadId);
        }
        return uploadId;
    }
}
