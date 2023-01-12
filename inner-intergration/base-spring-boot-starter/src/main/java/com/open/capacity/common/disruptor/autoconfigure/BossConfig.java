package com.open.capacity.common.disruptor.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author someday
 * boss核心分发配置
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ConfigurationProperties(prefix = "disruptor.async.boss")
public class BossConfig {

    private Integer ringbufferSize;

    private Integer eventHandlerNum;

    public Integer getRingbufferSize() {
        return ringbufferSize;
    }

    public void setRingbufferSize(Integer ringbufferSize) {
        this.ringbufferSize = ringbufferSize;
    }

    public Integer getEventHandlerNum() {
        return eventHandlerNum;
    }

    public void setEventHandlerNum(Integer eventHandlerNum) {
        this.eventHandlerNum = eventHandlerNum;
    }
}