package com.open.capacity.agora;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.agora.AgoraConstant;
import com.open.capacity.common.agora.AgoraOkHttp3Template;
import com.open.capacity.common.agora.AgoraProperties;
import com.open.capacity.common.agora.AgoraTemplate;
import com.open.capacity.common.agora.AgoraUserIdProvider;
import com.open.capacity.common.agora.interactive.req.RecordingConfig;
import com.open.capacity.common.agora.interactive.req.RecordingFileConfig;
import com.open.capacity.common.agora.interactive.req.RecordingStorageConfig;
import com.open.capacity.common.agora.interactive.resp.AcquireResourceResponse;
import com.open.capacity.common.agora.interactive.resp.ChannelUserStateResponse;

import okhttp3.OkHttpClient;

public class AgoraTemplate_Test {

	ObjectMapper objectMapper = new ObjectMapper();
	OkHttpClient okhttp3Client = new OkHttpClient.Builder().build();
	AgoraProperties properties = new AgoraProperties();
	AgoraTemplate template;

	@Before
	public void setup() {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		properties.setAppId("");
		properties.setAppCertificate("");
		properties.setLoginKey("");
		properties.setLoginSecret("");
		AgoraOkHttp3Template agoraOkHttp3Template = new AgoraOkHttp3Template(okhttp3Client, objectMapper, properties);
		template = new AgoraTemplate(new AgoraUserIdProvider() {}, agoraOkHttp3Template, properties );

	}

	//@Test
	public void testAcquireId() throws Exception {

		AcquireResourceResponse response =  template.opsForCloudRecording().acquireId("10000", "121212");
		System.out.println(objectMapper.writeValueAsString(response));

	}

	//@Test
    public void testStartRecording() throws Exception {

		String token = template.generateToken("10000", "121212");

		AcquireResourceResponse response =  template.opsForCloudRecording().acquireId("10000", "121212");
		System.out.println(objectMapper.writeValueAsString(response));

		String channelName = response.getCname();
		String resourceId = response.getResourceId();
		String uid = "121212";

		RecordingConfig recordingConfig = new RecordingConfig();
		RecordingFileConfig recordingFileConfig = new RecordingFileConfig();

		 // 阿里云存储桶
        RecordingStorageConfig storageConfig = new RecordingStorageConfig();
        storageConfig.setVendor(2); // 2代表产商为阿里云
        storageConfig.setRegion(7);
        storageConfig.setAccessKey("");
        storageConfig.setSecretKey("");
        storageConfig.setBucket("");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        storageConfig.setFileNamePrefix(Arrays.asList(AgoraConstant.VEIDO_PAHT, formatter.format(new Date())));

    	template.opsForCloudRecording().startRecording(channelName, uid, token, resourceId, recordingConfig, recordingFileConfig, storageConfig);

    }

	@Test
	public void testGetChannelUserState() throws Exception {

		ChannelUserStateResponse response =  template.opsForChannel().getChannelUserState("10", "912740950978068480");
		System.out.println(objectMapper.writeValueAsString(response));

	}

}
