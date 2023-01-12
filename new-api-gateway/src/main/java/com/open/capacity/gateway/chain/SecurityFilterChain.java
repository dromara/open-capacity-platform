package com.open.capacity.gateway.chain;

import javax.annotation.Resource;

import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class SecurityFilterChain extends ChainBase implements InitializingBean {

	@Resource
	private ActuatorCommand actuatorCommand;
	@Resource
	private AntiReplayCommand antiReplayCommand;
	@Resource
	private BlackListCommand blackListCommand;
	@Resource
	private HighFrequencyCommand highFrequencyCommand ;
	@Resource
	private RateLimitCommand rateLimitCommand;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 将请求处理者角色加入链中
		addCommand(actuatorCommand);
		addCommand(antiReplayCommand);
		addCommand(blackListCommand);
		addCommand(highFrequencyCommand);
		addCommand(rateLimitCommand);

	}

}
