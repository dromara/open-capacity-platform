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

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
@ConfigurationProperties(FaceRecognitionV3Properties.PREFIX)
public class FaceRecognitionV3Properties {

	public static final String PREFIX = "baidu.face.v3";

	/**
	 * 	Enable Baidu Face Recognition.
	 */
	private boolean enabled = false;
	/**
	 * 	官网获取的 API Key（百度云应用的AK）
	 */
	private String clientId;
	/**
	 * 	官网获取的 Secret Key（百度云应用的SK）
	 */
	private String clientSecret;
	/**
	 * 最多处理人脸的数目，默认值为1，仅检测图片中面积最大的那个人脸；最大值10，检测图片中面积最大的几张人脸。
	 */
	private int maxFaceNum = 1;
	/**
	 * 包括age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type信息;逗号分隔. 默认只返回face_token、人脸框、概率和旋转角度
	 */
	private String faceFields = "age,beauty,expression,face_shape,gender,glasses,landmark,landmark150,race,quality,eye_status,emotion,face_type";
	/**
	 * 查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回50个。
	 */
	private int maxUserNum = 1;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public int getMaxFaceNum() {
		return maxFaceNum;
	}

	public void setMaxFaceNum(int maxFaceNum) {
		this.maxFaceNum = maxFaceNum;
	}

	public String getFaceFields() {
		return faceFields;
	}

	public void setFaceFields(String faceFields) {
		this.faceFields = faceFields;
	}

	public int getMaxUserNum() {
		return maxUserNum;
	}

	public void setMaxUserNum(int maxUserNum) {
		this.maxUserNum = maxUserNum;
	}

}
