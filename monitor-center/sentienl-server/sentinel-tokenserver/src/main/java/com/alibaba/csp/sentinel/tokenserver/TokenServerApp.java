package com.alibaba.csp.sentinel.tokenserver;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;

public class TokenServerApp {

	static {
		System.setProperty("csp.sentinel.dashboard.server", "127.0.0.1:8080");
		System.setProperty("csp.sentienl.api.port", "8719");
		System.setProperty("project.name", "toekn-server");
		System.setProperty("csp.sentinel.log.use.pid", "true");
	}
	
	public static void main(String[] args) throws Exception {
		ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
		ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig().setIdleSeconds(600).setPort(10217));
		tokenServer.start();
		
		
		
	}
	
}
