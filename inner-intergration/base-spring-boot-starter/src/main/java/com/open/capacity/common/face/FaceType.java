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

/**
 * 人脸的类型
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
public enum FaceType {

	/**
	 * 表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等
	 */
	LIVE,
	/**
	 * 表示身份证芯片照：二代身份证内置芯片中的人像照片
	 */
	IDCARD,
	/**
	 * 表示带水印证件照：一般为带水印的小图，如公安网小图
	 */
	WATERMARK,
	/**
	 * 表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片
	 */
	CERT;

}
