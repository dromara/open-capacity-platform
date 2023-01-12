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
 * 图片质量控制
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
public enum FaceQuality {
	 
	/**
	 * 不进行控制
	 */
	NONE,
	/**
	 * 较低的质量要求
	 */
	LOW,
	/**
	 * 一般的质量要求
	 */
	NORMAL,
	/**
	 * 较高的质量要求
	 */
	HIGH;

}
