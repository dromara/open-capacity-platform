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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
@Configuration
@ConditionalOnProperty(prefix = FaceRecognitionV3Properties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ FaceRecognitionV3Properties.class })
public class FaceRecognitionV3AutoConfiguration {
	
	@Bean
	public FaceRecognitionV3Template faceRecognitionV3Template(FaceRecognitionV3Properties properties) {
		return new FaceRecognitionV3Template(properties);
	}
	
}
