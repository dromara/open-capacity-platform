package com.open.capacity.common.agora;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Configuration
@EnableConfigurationProperties({ AgoraRecordingProperties.class})
public class AgoraLocalRecordingConfiguration {

	/*
	@Bean(destroyMethod = "shutdown")
    public RecordingSDK recordingSdk(AgoraRecordingProperties recordingProperties) {
		return new RecordingSDK(recordingProperties.getLibPath());
	}*/

}
