package com.open.capacity.uaa.face;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import com.open.capacity.common.face.FaceRecognitionV3Template;
import com.open.capacity.uaa.common.token.FaceIdAuthenticationToken;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.utils.PreAuthenticationChecks;

import lombok.Data;

/**
 * 人脸识别登录
 * @author someday
 * @date 2018/7/24
 */
@Data
public class FaceIdAuthenticationProvider implements AuthenticationProvider {
	private UserDetailServiceFactory userDetailsServiceFactory;
	private FaceRecognitionV3Template faceRecognitionV3Template;
	private UserDetailsChecker detailsChecker = new PreAuthenticationChecks();

	@Override
	public Authentication authenticate(Authentication authentication) {
		FaceIdAuthenticationToken authenticationToken = (FaceIdAuthenticationToken) authentication;
		String userId = (String) authenticationToken.getPrincipal();
		UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByUserId(userId);
		detailsChecker.check(user);
		FaceIdAuthenticationToken authenticationResult = new FaceIdAuthenticationToken(user, user.getAuthorities());
		authenticationResult.setDetails(authenticationToken.getDetails());
		return authenticationResult;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return FaceIdAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
