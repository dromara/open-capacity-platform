package com.xxl.job.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.xxl.job.core.discovery.DiscoveryProcessor;

/**
 * JOB ADMIN CONSOLE SERVICE LIST
 *
 * @author someday
 */
public class DiscoveryUtil {

    private static Logger logger = LoggerFactory.getLogger(DiscoveryUtil.class);

    public static Map<String, List<String>> adminServicesList = new ConcurrentHashMap<>();

    private static DiscoveryProcessor discoveryProcessor;

    public DiscoveryUtil(DiscoveryProcessor discoveryProcessor) {
        this.discoveryProcessor = discoveryProcessor;
    }

    /**
     * add JOB ADMIN service list
     * @param name 名称
     */
    public static void addList(String name) {
        List<String> servicesByDiscovery = getServicesByDiscovery(name);
        if (!CollectionUtils.isEmpty(servicesByDiscovery)) {
            adminServicesList.put(name, servicesByDiscovery);
            logger.info("registry scheduled success : {}", name);
        }
    }

    /**
     * 获取注册中心中指定注册名称的所有服务实例
     * 支持eureka nacos
     *
     * @param appName 应用实例名
     * @return List<InstanceInfo>
     */
    public static List<String> getServicesByDiscovery(String appName) {
        List<String> serverAddressList = new ArrayList<>(3);
        try {
            serverAddressList = discoveryProcessor.getServerAddressList(appName);
        } catch (NullPointerException e){
            logger.error("service does not exist -> {}", e.getMessage());
        }
        return serverAddressList;
    }

    /**
     * Determines if the remote host already exists
     * @param host admin console address
     * @return true | false
     */
    public static boolean hostExist(String host){
        for (String admin : adminServicesList.keySet()) {
            List<String> urls = adminServicesList.get(admin);

            List<String> ipList = new ArrayList<>(3);
            for (String url : urls) {
                String[] split = url.split(":");
                ipList.add(split[0]);
            }

            if (ipList.contains(host)) {
                return true;
            }
        }
        return false;
    }

}
