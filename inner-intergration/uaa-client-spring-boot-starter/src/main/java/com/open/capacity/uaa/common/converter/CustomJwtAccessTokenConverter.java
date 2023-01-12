package com.open.capacity.uaa.common.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.open.capacity.common.constant.SecurityConstants;

/**
 *  JWT RSA 公钥配置
 */
@SuppressWarnings("all")
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

	public CustomJwtAccessTokenConverter() {

		Resource res = new ClassPathResource(SecurityConstants.RSA_PUBLIC_KEY);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
			this.setVerifierKey(br.lines().collect(Collectors.joining("\n")));
		} catch (IOException ioe) {
		}
	}

}
