package com.open.capacity.gateway.service;

public interface IRateLimitService {

	public boolean checkRateLimit(String reqUrl, String accessToken) ;
	
}
