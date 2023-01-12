package com.xxl.job.core.config;


import com.xxl.job.core.discovery.DiscoveryProcessor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.util.DiscoveryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.util.StringUtils;

/**
 * @author someday
 */
@ComponentScan("com.xxl.job.core.endpoint")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JobConfiguration implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(JobConfiguration.class);

    private static String logPath;

    private static int logRetentionDays;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        new DiscoveryUtil(applicationContext.getBean(DiscoveryProcessor.class));
        Environment environment = applicationContext.getEnvironment();
        String logPath = environment.getProperty("xxl.job.executor.logpath");
        String logRetentionDays = environment.getProperty("xxl.job.executor.logretentiondays");

        this.logPath = StringUtils.isEmpty(logPath) ? "/data/applogs/xxl-job/jobhandler" : logPath;
        this.logRetentionDays = StringUtils.isEmpty(logRetentionDays) ? -1 : Integer.parseInt(logRetentionDays);
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }
}
