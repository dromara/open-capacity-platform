package com.open.capacity.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import lombok.Data;

/**
 * @author someday
 * @date 2018/1/4
 */
@Data
@ConfigurationProperties(prefix = "ocp.security")
@RefreshScope
public class SecurityProperties {
	
	private ActuatorProperties actuator = new ActuatorProperties();
	
    private AuthProperties auth = new AuthProperties();
    
    private XssProperties xss  = new XssProperties();

    private PermitProperties ignore = new PermitProperties();
    
    private RateLimitProperties ratelimit = new RateLimitProperties() ;
    
    private HighFrequencyProperties highFrequency = new HighFrequencyProperties();
    
    private AntiReplayProperties replay = new AntiReplayProperties();

    private ValidateCodeProperties code = new ValidateCodeProperties();
}
