/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.open.capacity.common.face;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.open.capacity.common.utils.HttpUtil;

/**
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
public class FaceRecognitionV3Template {

	public static final String CONTENT_TYPE = "application/json";

	public static final String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
	public static final String FACE_MATCH_URL = "https://aip.baidubce.com/rest/2.0/face/v3/match";
	public static final String FACE_SEARCH_URL = "https://aip.baidubce.com/rest/2.0/face/v3/search";
	public static final String FACE_PERSON_VERIFY_URL = "https://aip.baidubce.com/rest/2.0/face/v3/person/verify";
	public static final String FACE_LIVENESS_VERIFY_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceverify";
	public static final String FACE_MERGE_URL = "https://aip.baidubce.com/rest/2.0/face/v1/merge";

	public static final String FACESET_USER_ADD_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
	public static final String FACESET_USER_UPDATE_URL = "https://aip.baidubce.com/rest/2.0/face/v2/faceset/user/update";
	public static final String FACESET_FACE_DELETE_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/delete";
	
	public static final String FACESET_USER_GET_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/get";
	public static final String FACESET_USER_COPY_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/copy";
	public static final String FACESET_USER_DELETE_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";
	public static final String FACESET_USER_LIST_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/getlist";
	
	public static final String FACESET_GROUP_ADD_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/add";
	public static final String FACESET_GROUP_DELETE_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/delete";
	public static final String FACESET_GROUP_LIST_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getlist";
	public static final String FACESET_GROUP_USERS_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers";
	
	private MessageSourceAccessor messages = FaceRecognitionMessageSource.getAccessor();
	private FaceRecognitionV3Properties properties;
	
	
	public FaceRecognitionV3Template(FaceRecognitionV3Properties properties) {
		this.properties = properties;
	}
	
	protected JSONObject wrap(JSONObject result) {
		String error_code = result.getString("error_code");
		// 添加中文提示
		if(!StringUtils.equalsIgnoreCase(error_code, "0")) {
			// 获取异常信息
			String error_msg = messages.getMessage(error_code);
			result.put("error_msg", error_msg);
		} else {
			result.put("error_code", 0);
		}
		if(StringUtils.equalsIgnoreCase(error_code, "223120")) {
			result.put("liveness", 0);
		} 
		return result;
	}
	
	/**
	 * 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
	 */
	private final LoadingCache<String, Optional<String>> ACCESS_TOKEN_CACHES = CacheBuilder.newBuilder()
			// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
			.concurrencyLevel(8)
			// 设置写缓存后600秒钟过期
			.expireAfterWrite(29, TimeUnit.DAYS)
			// 设置缓存容器的初始容量为10
			.initialCapacity(2)
			// 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
			.maximumSize(10)
			// 设置要统计缓存的命中率
			.recordStats()
			// 设置缓存的移除通知
			.removalListener(new RemovalListener<String, Optional<String>>() {
				@Override
				public void onRemoval(RemovalNotification<String, Optional<String>> notification) {
					System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
				}
			})
			// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<String, Optional<String>>() {

				@Override
				public Optional<String> load(String keySecret) throws Exception {
					JSONObject key = JSONObject.parseObject(keySecret);
					String token = AuthClient.getAuth(key.getString("clientId"), key.getString("clientSecret"));
					return Optional.fromNullable(token);
				}
			});

	/**
	 * 
	 * 企业内部开发获取access_token 先从缓存查，再到百度查
	 * https://ai.baidu.com/docs#/Face-Detect-V3/top
	 * 
	 * @param clientId     官网获取的 API Key（百度云应用的AK）
	 * @param clientSecret 官网获取的 Secret Key（百度云应用的SK）
	 * @return
	 * @throws ExecutionException
	 */
	public String getAccessToken(String clientId, String clientSecret) throws ExecutionException {

		JSONObject key = new JSONObject();
		key.put("clientId", clientId);
		key.put("clientSecret", clientSecret);

		Optional<String> opt = ACCESS_TOKEN_CACHES.get(key.toJSONString());
		return opt.isPresent() ? opt.get() : null;

	}

	public JSONObject detect(byte[] imageBytes) {
		return detect(imageBytes, FaceType.LIVE);
	}
	
	public JSONObject detect(byte[] imageBytes, FaceType face_type) {
		return detect(imageBytes, face_type, FaceLiveness.NONE);
	}
	
	/**
	 * 人脸检测与属性分析： https://ai.baidu.com/docs#/Face-Detect-V3/top
	 * 
	 * @param imageBytes       图片字节码：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param face_type        人脸的类型：LIVE表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等
	 *                         IDCARD表示身份证芯片照：二代身份证内置芯片中的人像照片
	 *                         WATERMARK表示带水印证件照：一般为带水印的小图，如公安网小图
	 *                         CERT表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认LIVE
	 * @param liveness		        活体控制 检测结果中不符合要求的人脸会被过滤 NONE: 不进行控制 LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率) NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @return
	 */
	public JSONObject detect(byte[] imageBytes, FaceType face_type, FaceLiveness liveness) {
		try {
			String imgStr = Base64Util.encode(imageBytes);
			return detect(imgStr, face_type, liveness);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject detect(String imageBase64) {
		return detect(imageBase64, FaceType.LIVE);
	}
	
	public JSONObject detect(String imageBase64, FaceType face_type) {
		return detect(imageBase64, face_type, FaceLiveness.NONE);
	}
	
	/**
	 * 人脸检测与属性分析： https://ai.baidu.com/docs#/Face-Detect-V3/top
	 * 
	 * @param imageBase64      Base64编码：请求的图片需经过Base64编码，图片的base64编码指将图片数据编码成一串字符串，使用该字符串代替图像地址。
	 *                         您可以首先得到图片的二进制，然后用Base64格式编码即可。需要注意的是，图片的base64编码是不包含图片头的，如data:image/jpg;base64,
	 *                         图片格式：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param face_type        人脸的类型：LIVE表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等
	 *                         IDCARD表示身份证芯片照：二代身份证内置芯片中的人像照片
	 *                         WATERMARK表示带水印证件照：一般为带水印的小图，如公安网小图
	 *                         CERT表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认LIVE
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤 NONE: 不进行控制 LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率) NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @return
	 */
	public JSONObject detect(String imageBase64, FaceType face_type, FaceLiveness liveness) {
		

		try {
			
			Map<String, Object> map = new HashMap<>();
			
			// 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
			map.put("image", imageBase64);
			/* 图片类型
			 BASE64:图片的base64值，base64编码后的图片数据，编码后的图片大小不超过2M；
			 URL:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)；
			 FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个。
			 */
			map.put("image_type", "BASE64");
			/*
			 * 包括age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type信息逗号分隔. 
			 * 默认只返回face_token、人脸框、概率和旋转角度
			 */
			map.put("face_field", "age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type");
			map.put("face_type", face_type.name());
			
			// 最多处理人脸的数目，默认值为1，仅检测图片中面积最大的那个人脸；最大值10，检测图片中面积最大的几张人脸。
			map.put("max_face_num", properties.getMaxFaceNum());
			// 活体控制 检测结果中不符合要求的人脸会被过滤
			map.put("liveness_control", liveness.name());
			
			String param = JSONObject.toJSONString(map);

			// 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
			String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

			String result = HttpUtil.post(FACE_DETECT_URL, accessToken, CONTENT_TYPE, param);

			JSONObject detect = wrap(JSONObject.parseObject(result));
			
			return detect;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject match(byte[] imageBytes_1, byte[] imageBytes_2) {
		return match(imageBytes_1, imageBytes_2, FaceType.LIVE);
	}
	
	public JSONObject match(byte[] imageBytes_1, byte[] imageBytes_2, FaceType face_type) {
		return match(imageBytes_1, imageBytes_2, face_type, FaceQuality.LOW);
	}
	
	public JSONObject match(byte[] imageBytes_1, byte[] imageBytes_2, FaceType face_type, FaceQuality quality) {
		return match(imageBytes_1, imageBytes_2, face_type, quality, FaceLiveness.NORMAL);
	}
	
	/**
	 * 人脸对比: https://ai.baidu.com/docs#/Face-Match-V3/top
	 * 
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param imageBytes_1     图片字节码：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param imageBytes_2     图片字节码：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param face_type        人脸的类型：LIVE表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等
	 *                         IDCARD表示身份证芯片照：二代身份证内置芯片中的人像照片
	 *                         WATERMARK表示带水印证件照：一般为带水印的小图，如公安网小图
	 *                         CERT表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认LIVE
	 * @param quality  图片质量控制 NONE: 不进行控制 LOW:较低的质量要求 NORMAL: 一般的质量要求 HIGH:
	 *                         较高的质量要求 默认 NONE 若图片质量不满足要求，则返回结果中会提示质量检测失败
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤 NONE: 不进行控制 LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率) NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
	 * @return
	 */
	public JSONObject match(byte[] imageBytes_1, byte[] imageBytes_2, FaceType face_type, FaceQuality quality, FaceLiveness liveness) {
		String imageBase64_1 = Base64Util.encode(imageBytes_1);
		String imageBase64_2 = Base64Util.encode(imageBytes_2);
		return match(imageBase64_1, imageBase64_2, face_type, quality, liveness);
	}

	public JSONObject match(String imageBase64_1, String imageBase64_2) {
		return match(imageBase64_1, imageBase64_2, FaceType.LIVE);
	}
	
	public JSONObject match(String imageBase64_1, String imageBase64_2, FaceType face_type) {
		return match(imageBase64_1, imageBase64_2, face_type, FaceQuality.LOW);
	}
	
	public JSONObject match(String imageBase64_1, String imageBase64_2, FaceType face_type, FaceQuality quality) {
		return match(imageBase64_1, imageBase64_2, face_type, quality,  FaceLiveness.NORMAL);
	}
	
	/**
	 * 人脸对比: https://ai.baidu.com/docs#/Face-Match-V3/top
	 * 
	 * @param imageBase64_1    Base64编码：请求的图片需经过Base64编码，图片的base64编码指将图片数据编码成一串字符串，使用该字符串代替图像地址。
	 *                         您可以首先得到图片的二进制，然后用Base64格式编码即可。需要注意的是，图片的base64编码是不包含图片头的，如data:image/jpg;base64,
	 *                         图片格式：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param imageBase64_2    Base64编码：请求的图片需经过Base64编码，图片的base64编码指将图片数据编码成一串字符串，使用该字符串代替图像地址。
	 *                         您可以首先得到图片的二进制，然后用Base64格式编码即可。需要注意的是，图片的base64编码是不包含图片头的，如data:image/jpg;base64,
	 *                         图片格式：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param face_type        人脸的类型：LIVE表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等
	 *                         IDCARD表示身份证芯片照：二代身份证内置芯片中的人像照片
	 *                         WATERMARK表示带水印证件照：一般为带水印的小图，如公安网小图
	 *                         CERT表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认LIVE
	 * @param quality  图片质量控制； NONE: 不进行控制； LOW:较低的质量要求； NORMAL: 一般的质量要求；
	 *                         HIGH: 较高的质量要求； 默认 NONE； 若图片质量不满足要求，则返回结果中会提示质量检测失败
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤； NONE: 不进行控制 ；LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率)； NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)； HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率)； 默认NONE
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @return
	 */
	public JSONObject match(String imageBase64_1, String imageBase64_2, FaceType face_type, FaceQuality quality, FaceLiveness liveness) {
		try {

			List<Map<String, Object>> images = new ArrayList<>();

			Map<String, Object> map1 = new HashMap<>();
			map1.put("image", imageBase64_1);
			map1.put("image_type", "BASE64");
			map1.put("face_type", face_type.name());
			map1.put("quality_control", quality.name());
			map1.put("liveness_control", liveness.name());

			Map<String, Object> map2 = new HashMap<>();
			map2.put("image", imageBase64_2);
			map2.put("image_type", "BASE64");
			map2.put("face_type", face_type.name());
			map2.put("quality_control", quality.name());
			map2.put("liveness_control", liveness.name());

			images.add(map1);
			images.add(map2);

			String param = JSONObject.toJSONString(images);

			// 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
			String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

			String result = HttpUtil.post(FACE_MATCH_URL, accessToken, CONTENT_TYPE, param);
			
			return wrap(JSONObject.parseObject(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject search(byte[] imageBytes, String group_id_list) {
		String imageBase64 = Base64Util.encode(imageBytes);
		return search(imageBase64, group_id_list, FaceQuality.LOW);
	}
	
	public JSONObject search(byte[] imageBytes, String group_id_list, FaceQuality quality) {
		String imageBase64 = Base64Util.encode(imageBytes);
		return search(imageBase64, group_id_list, quality, FaceLiveness.NORMAL);
	}
	
	public JSONObject search(String imageBase64, String group_id_list) {
		return search(imageBase64, group_id_list, FaceQuality.LOW);
	}
	
	public JSONObject search(String imageBase64, String group_id_list, FaceQuality quality) {
		return search(imageBase64, group_id_list, quality, FaceLiveness.NORMAL);
	}
	
	/**
	 * 人脸搜索 : https://ai.baidu.com/docs#/Face-Search-V3/top
	 */
	public JSONObject search(String imageBase64, String group_id_list, FaceQuality quality, FaceLiveness liveness) {
		try {
			Map<String, Object> map = new HashMap<>();
			// 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
			map.put("image", imageBase64);
			map.put("image_type", "BASE64");
			map.put("group_id_list", group_id_list);
			map.put("liveness_control", quality.name());
			map.put("quality_control", liveness.name());
			map.put("max_user_num", properties.getMaxUserNum());
			
			String param = JSONObject.toJSONString(map);

			// 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
			String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

			String result = HttpUtil.post(FACE_SEARCH_URL, accessToken, CONTENT_TYPE, param);
			
			return wrap(JSONObject.parseObject(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**##########################人脸库管理########################### */
	
	/**
	 * 人脸注册： https://ai.baidu.com/docs#/Face-Set-V3/8bea9967；
	 * 用于向人脸库中新增用户，及组内用户的人脸图片，典型应用场景：构建您的人脸库，如会员人脸注册，已有用户补全人脸信息等。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param imageBase64      Base64编码：请求的图片需经过Base64编码，图片的base64编码指将图片数据编码成一串字符串，使用该字符串代替图像地址。
	 *                         您可以首先得到图片的二进制，然后用Base64格式编码即可。需要注意的是，图片的base64编码是不包含图片头的，如data:image/jpg;base64,
	 *                         图片格式：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param group_id         用户组id，标识一组用户（由数字、字母、下划线组成），长度限制48B。产品建议：根据您的业务需求，可以将需要注册的用户，按照业务划分，分配到不同的group下，例如按照会员手机尾号作为groupid，用于刷脸支付、会员计费消费等，这样可以尽可能控制每个group下的用户数与人脸数，提升检索的准确率
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @param user_info        用户资料，长度限制256B 默认空
	 * @param quality  图片质量控制； NONE: 不进行控制； LOW:较低的质量要求； NORMAL: 一般的质量要求；
	 *                         HIGH: 较高的质量要求； 默认 NONE； 若图片质量不满足要求，则返回结果中会提示质量检测失败
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤； NONE: 不进行控制 ；LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率)； NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)； HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率)； 默认NONE
	 * @return
	 */
	public JSONObject faceNew(String imageBase64, String group_id, String user_id, String user_info, FaceQuality quality, FaceLiveness liveness) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageBase64);
            map.put("image_type", "BASE64");
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            map.put("user_info", user_info);
            map.put("quality_control", quality.name());
            map.put("liveness_control", liveness.name());
            /*
             	* 操作方式
				APPEND: 当user_id在库中已经存在时，对此user_id重复注册时，新注册的图片默认会追加到该user_id下
				REPLACE : 当对此user_id重复注册时,则会用新图替换库中该user_id下所有图片
				默认使用APPEND
             */
            map.put("action_type", "REPLACE");
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_USER_ADD_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
			
	}
	
	/**
	 * 人脸更新： https://ai.baidu.com/docs#/Face-Set-V3/bc7f58d1；
	 * 用于对人脸库中指定用户，更新其下的人脸图像。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param imageBase64      Base64编码：请求的图片需经过Base64编码，图片的base64编码指将图片数据编码成一串字符串，使用该字符串代替图像地址。
	 *                         您可以首先得到图片的二进制，然后用Base64格式编码即可。需要注意的是，图片的base64编码是不包含图片头的，如data:image/jpg;base64,
	 *                         图片格式：现支持PNG、JPG、JPEG、BMP，不支持GIF图片
	 * @param group_id         用户组id，标识一组用户（由数字、字母、下划线组成），长度限制48B。产品建议：根据您的业务需求，可以将需要注册的用户，按照业务划分，分配到不同的group下，例如按照会员手机尾号作为groupid，用于刷脸支付、会员计费消费等，这样可以尽可能控制每个group下的用户数与人脸数，提升检索的准确率
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @param user_info        用户资料，长度限制256B 默认空
	 * @param quality  图片质量控制； NONE: 不进行控制； LOW:较低的质量要求； NORMAL: 一般的质量要求；
	 *                         HIGH: 较高的质量要求； 默认 NONE； 若图片质量不满足要求，则返回结果中会提示质量检测失败
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤； NONE: 不进行控制 ；LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率)； NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)； HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率)； 默认NONE
	 * @return
	 */
	public JSONObject faceRenew(String imageBase64, String group_id, String user_id, String user_info, FaceQuality quality, FaceLiveness liveness) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("image", imageBase64);
            map.put("image_type", "BASE64");
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            map.put("user_info", user_info);
            map.put("quality_control", quality.name());
            map.put("liveness_control", liveness.name());
            /*
             	* 操作方式
				APPEND: 当user_id在库中已经存在时，对此user_id重复注册时，新注册的图片默认会追加到该user_id下
				REPLACE : 当对此user_id重复注册时,则会用新图替换库中该user_id下所有图片
				默认使用APPEND
             */
            map.put("action_type", "REPLACE");
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_USER_UPDATE_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
			
	}
	
	/**
	 * 人脸删除： https://ai.baidu.com/docs#/Face-Set-V3/912191e1；
	 * 删除用户的某一张人脸，如果该用户只有一张人脸图片，则同时删除用户
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id，标识一组用户（由数字、字母、下划线组成），长度限制48B。产品建议：根据您的业务需求，可以将需要注册的用户，按照业务划分，分配到不同的group下，例如按照会员手机尾号作为groupid，用于刷脸支付、会员计费消费等，这样可以尽可能控制每个group下的用户数与人脸数，提升检索的准确率
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @param face_token       需要删除的人脸图片token，（由数字、字母、下划线组成）长度限制64B
	 * @return
	 */
	public JSONObject faceDelete( String group_id, String user_id, String face_token) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            map.put("face_token", face_token);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_FACE_DELETE_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 用户信息查询： https://ai.baidu.com/docs#/Face-Set-V3/a8205a0b;
	 * 获取人脸库中某个用户的信息(user_info信息和用户所属的组)。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id(由数字、字母、下划线组成，长度限制48B)，如传入“@ALL”则从所有组中查询用户信息。注：处于不同组，但uid相同的用户，我们认为是同一个用户。
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @return
	 */
	public JSONObject faceInfo( String group_id, String user_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = com.open.capacity.common.utils.HttpUtil.post(FACESET_USER_GET_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 获取用户人脸列表： https://ai.baidu.com/docs#/Face-Set-V3/871dcfcb;
	 * 获取人脸库中某个用户的信息(user_info信息和用户所属的组)。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id(由数字、字母、下划线组成，长度限制48B)，如传入“@ALL”则从所有组中查询用户信息。注：处于不同组，但uid相同的用户，我们认为是同一个用户。
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @return
	 */
	public JSONObject faceList( String group_id, String user_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_USER_LIST_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 获取用户列表： https://ai.baidu.com/docs#/Face-Set-V3/67d10e05;
	 * 用于查询指定用户组中的用户列表。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id(由数字、字母、下划线组成，长度限制48B)，如传入“@ALL”则从所有组中查询用户信息。注：处于不同组，但uid相同的用户，我们认为是同一个用户。
	 * @param start          	默认值0，起始序号
	 * @param length          	返回数量，默认值100，最大值1000
	 * @return
	 */
	public JSONObject faceUsers( String group_id, int start, int length) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            map.put("start", Math.max(start, 0));
            map.put("length", length);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_GROUP_USERS_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 复制用户： https://ai.baidu.com/docs#/Face-Set-V3/4c8cc30a;
	 * 用于将已经存在于人脸库中的用户复制到一个新的组。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id(由数字、字母、下划线组成，长度限制48B)，如传入“@ALL”则从所有组中查询用户信息。注：处于不同组，但uid相同的用户，我们认为是同一个用户。
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @param target_group_id  需要添加信息的组id，多个的话用逗号分隔
	 * @return
	 */
	public JSONObject userCopy( String group_id, String user_id, String target_group_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("src_group_id", group_id);
            map.put("user_id", user_id);
            map.put("dst_group_id", target_group_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_USER_COPY_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 删除用户： https://ai.baidu.com/docs#/Face-Set-V3/95b207bf;
	 * 用于将用户从某个组中删除。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id(由数字、字母、下划线组成，长度限制48B) ，如传入“@ALL”则从所有组中删除用户
	 * @param user_id          用户id（由数字、字母、下划线组成），长度限制128B
	 * @return
	 */
	public JSONObject userDelete( String group_id, String user_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            map.put("user_id", user_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_USER_DELETE_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 创建用户组： https://ai.baidu.com/docs#/Face-Set-V3/5867daad;
	 * 用于创建一个空的用户组，如果用户组已存在 则返回错误。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id，标识一组用户（由数字、字母、下划线组成），长度限制48B。
	 * @return
	 */
	public JSONObject groupNew( String group_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_GROUP_ADD_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 删除用户组： https://ai.baidu.com/docs#/Face-Set-V3/5867daad;
	 * 删除用户组下所有的用户及人脸，如果组不存在 则返回错误。 注：组内的人脸数量如果大于500条，会在后台异步进行删除。在删除期间，无法向该组中添加人脸。1秒钟可以删除20条记录，相当于一小时可以将7万人的人脸组删除干净。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param group_id         用户组id，标识一组用户（由数字、字母、下划线组成），长度限制48B。
	 * @return
	 */
	public JSONObject groupDelete( String group_id) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", group_id);
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_GROUP_DELETE_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 删除用户组： https://ai.baidu.com/docs#/Face-Set-V3/5867daad;
	 * 删除用户组下所有的用户及人脸，如果组不存在 则返回错误。 注：组内的人脸数量如果大于500条，会在后台异步进行删除。在删除期间，无法向该组中添加人脸。1秒钟可以删除20条记录，相当于一小时可以将7万人的人脸组删除干净。
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param start        默认值0，起始序号
	 * @param length          回数量，默认值100，最大值1000
	 * @return
	 */
	public JSONObject groupList(int start, int length) {
		 try {
			 
            Map<String, Object> map = new HashMap<>();
            map.put("start", Math.max(start, 0));
            map.put("length", Math.max(length, 1000));
            
            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACESET_GROUP_LIST_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	
	public JSONObject personverify(byte[] imageBytes, String id_card_number, String name) {
		String imageBase64 = Base64Util.encode(imageBytes);
		return personverify(imageBase64, id_card_number, name, FaceQuality.LOW);
	}
	
	public JSONObject personverify(byte[] imageBytes, String id_card_number, String name, FaceQuality quality) {
		String imageBase64 = Base64Util.encode(imageBytes);
		return personverify(imageBase64, id_card_number, name, quality, FaceLiveness.HIGH);
	}
	
	public JSONObject personverify(String imageBase64, String id_card_number, String name) {
		return personverify(imageBase64, id_card_number, name, FaceQuality.LOW);
	}
	
	public JSONObject personverify(String imageBase64, String id_card_number, String name, FaceQuality quality) {
		return personverify(imageBase64, id_card_number, name, quality, FaceLiveness.HIGH);
	}
	
	/**
	 * 身份验证：https://ai.baidu.com/docs#/Face-PersonVerify-V3/top
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param imageBase64      图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
	 * @param id_card_number   身份证号码
	 * @param name             姓名（真实姓名，和身份证号匹配）
	 * @param quality  图片质量控制 NONE: 不进行控制 LOW:较低的质量要求 NORMAL: 一般的质量要求 HIGH:
	 *                         较高的质量要求 默认 NONE 若图片质量不满足要求，则返回结果中会提示质量检测失败
	 * @param liveness 活体控制 检测结果中不符合要求的人脸会被过滤 NONE: 不进行控制 LOW:较低的活体要求(高通过率
	 *                         低攻击拒绝率) NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH:
	 *                         较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
	 * @return
	 */
	public JSONObject personverify(String imageBase64, String id_card_number, String name, FaceQuality quality, FaceLiveness liveness) {
        try {
        	
            Map<String, Object> map = new HashMap<>();
            
            map.put("image", imageBase64);
            map.put("image_type", "BASE64");
            map.put("id_card_number", id_card_number);
            map.put("name", URLEncoder.encode(name, "UTF-8"));
            map.put("quality_control", quality.name());
            map.put("liveness_control", liveness.name());

            String param = JSONObject.toJSONString(map);

            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
            String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACE_PERSON_VERIFY_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public JSONObject faceVerify(byte[] imageBytes, FaceOption option) {
		String imageBase64 = Base64Util.encode(imageBytes);
		return faceVerify(imageBase64, option);
	}
	
	/**
	 * 在线活体检测：https://ai.baidu.com/docs#/Face-Liveness-V3/top
	 * @author ： <a href="https://github.com/hiwepy">wandl</a>
	 * @param imageBase64 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
	 * @param option      场景信息，程序会视不同的场景选用相对应的模型。当前支持的场景有COMMON(通用场景)，GATE(闸机场景)，默认使用COMMON
	 * @return
	 */
	public JSONObject faceVerify(String imageBase64, FaceOption option) {
       
        try {

        	Map<String, Object> map = new HashMap<>();
            
            map.put("image", imageBase64);
            map.put("image_type", "BASE64");
            map.put("face_field", "age,beauty,expression,face_shape,gender,glasses,landmark,race,quality,face_type");
            map.put("option", option.name());

            String param = JSONObject.toJSONString(map);
            // 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
         	String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

            String result = HttpUtil.post(FACE_LIVENESS_VERIFY_URL, accessToken, CONTENT_TYPE, param);
            
            return wrap(JSONObject.parseObject(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public JSONObject merge(byte[] templateBytes, byte[] targetBytes) {
		String templateBase64 = Base64Util.encode(templateBytes);
		String targetBase64 = Base64Util.encode(targetBytes);
		return merge(templateBase64, targetBase64);
	}
	
	/**
	 * 人脸融合 
	 * https://ai.baidu.com/docs#/Face-Merge/top
	 */
	public JSONObject merge(String templateBase64, String targetBase64) {
		try {
			
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> image_templateMap = new HashMap<>();
			image_templateMap.put("image", templateBase64);
			image_templateMap.put("image_type", "BASE64");
			image_templateMap.put("quality_control", "NONE");
			map.put("image_template", image_templateMap);
			
			Map<String, Object> image_targetMap = new HashMap<>();
			image_targetMap.put("image", targetBase64);
			image_targetMap.put("image_type", "BASE64");
			image_targetMap.put("quality_control", "NONE");
			map.put("image_target", image_targetMap);

			String param = JSONObject.toJSONString(map);

			// 注意：access_token的有效期为30天，切记需要每30天进行定期更换，或者每次请求都拉取新token；
			String accessToken = getAccessToken(properties.getClientId(), properties.getClientSecret());

			String result = HttpUtil.post(FACE_MERGE_URL, accessToken, CONTENT_TYPE, param);
			
			return wrap(JSONObject.parseObject(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public FaceRecognitionV3Properties getProperties() {
		return properties;
	}

}
