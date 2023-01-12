package com.xxl.job.core.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * The nacos discovery client
 * @author someday
 */
public class NacosDiscoveryProcessor implements DiscoveryProcessor {

    private static Logger logger = LoggerFactory.getLogger(NacosDiscoveryProcessor.class);
 
    
    @Autowired
    private DiscoveryClient nacosDiscoveryClient;

    @Override
    public List<String> getServerAddressList(String appName) {
        try {
            final List<ServiceInstance> instances =   nacosDiscoveryClient.getInstances(appName) ;   ;
            if (instances != null) {
                return instances.stream().map(instance ->
                        instance.getHost() + ":" + instance.getPort()).collect(Collectors.toList());
            }
            logger.error("nacos service does not exist -> {}", appName);
        } catch (Exception e) {
            logger.error("nacos service discovery fail | appName : {} | error : {}", appName, e.getMessage());
        }
        return new ArrayList<>();
    }
}
