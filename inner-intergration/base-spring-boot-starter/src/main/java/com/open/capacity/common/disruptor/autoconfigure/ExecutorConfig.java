package com.open.capacity.common.disruptor.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author someday
 * 线程池配置
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ConfigurationProperties(prefix = "disruptor")
public class ExecutorConfig {

    private List<Config> executors = new ArrayList<>();

    public List<Config> getExecutors() {
        return executors;
    }

    public void setExecutors(List<Config> executors) {
        this.executors = executors;
    }

    public static class Config {
        private String threadPool;
        private Integer threadCount;

        public String getThreadPool() {
            return threadPool;
        }

        public void setThreadPool(String threadPool) {
            this.threadPool = threadPool;
        }

        public Integer getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(Integer threadCount) {
            this.threadCount = threadCount;
        }
    }
}
