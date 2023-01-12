package com.open.capacity.common.disruptor.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author someday
 * 消费者配置
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ConfigurationProperties(prefix = "disruptor.async")
public class WorkerConfig {

    private List<Config> workers = new ArrayList<>();

    public List<Config> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Config> workers) {
        this.workers = workers;
    }

    public static class Config {
        private String channel;
        private Integer ringbufferSize;
        private Integer eventHandlerNum;

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

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
}