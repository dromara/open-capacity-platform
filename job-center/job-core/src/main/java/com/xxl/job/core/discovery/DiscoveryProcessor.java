package com.xxl.job.core.discovery;

import java.util.List;

/**
 * @author someday
 */
public interface DiscoveryProcessor {

    List<String> getServerAddressList(String appName);

}
