package com.open.capacity.gateway.chain;

import javax.annotation.Resource;

import com.esotericsoftware.minlog.Log;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.gateway.context.SecurityContext;
import com.open.capacity.gateway.event.RequestEvent;
import com.open.capacity.gateway.event.RequestEventListener;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class HighFrequencyCommand implements Command {

	@Resource
	private SecurityProperties securityProperties;

	private static final EPServiceProvider esper = EPServiceProviderManager.getDefaultProvider();

	public HighFrequencyCommand() {
		esper.getEPAdministrator().createEPL(
				"select * from com.open.capacity.gateway.event.RequestEvent.win:time_batch(1 sec) having count(*) > 1000")
				.addListener(new RequestEventListener());
	}

	@Override
	public boolean execute(Context context) throws Exception {
		if (securityProperties.getHighFrequency().getEnable()) {
			try {
				SecurityContext securityContext = (SecurityContext) context;
				ServerHttpRequest request = securityContext.getExchange().getRequest();
				String method = request.getMethodValue();
				String path = request.getPath().toString();
				long timestamp = System.currentTimeMillis();
				esper.getEPRuntime().sendEvent(new RequestEvent(method, path, timestamp));
			} catch (Exception e) {
				Log.error(e.getMessage(), e);
			}
		}
		return false;
	}

}
