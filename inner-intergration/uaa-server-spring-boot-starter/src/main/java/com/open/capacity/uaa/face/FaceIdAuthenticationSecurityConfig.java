package com.open.capacity.uaa.face;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

import com.open.capacity.common.face.FaceRecognitionV3Template;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;

/**
 * 人脸识别的相关处理配置
 * @author someday
 */
@Component
public class FaceIdAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	@Autowired
	private UserDetailServiceFactory userDetailsServiceFactory;
	@Autowired(required = false )
	private FaceRecognitionV3Template faceRecognitionV3Template;

	@Override
	public void configure(HttpSecurity http) {
		// faceId provider
		FaceIdAuthenticationProvider provider = new FaceIdAuthenticationProvider();
		provider.setUserDetailsServiceFactory(userDetailsServiceFactory);
		provider.setFaceRecognitionV3Template(faceRecognitionV3Template);
		http.authenticationProvider(provider);
	}
}
