package com.open.capacity.db.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法
 * 
 * @author someday
 * @date 2019/5/6
 */
@Configuration
@Slf4j
public class SnowFlakeAutoConfig {

	/**
	 * 动态指定sharding jdbc 的雪花算法中的属性work.id属性 通过调用System.setProperty()的方式实现,可用容器的 id
	 * 或者机器标识位 workId最大值 1L << 100，就是1024，即 0<= workId < 1024
	 * {@link SnowflakeShardingKeyGenerator#getWorkerId()}
	 *
	 */
	static {
		try {
			InetAddress inetAddress = Inet4Address.getLocalHost();
			String hostAddressIp = inetAddress.getHostAddress();
			String workerId = Math.abs(hostAddressIp.hashCode()) % 1024 + "";
			System.setProperty("workerId", workerId);
			log.info("workerId:{}", workerId);
		} catch (UnknownHostException e) {
			System.setProperty("workerId", "1");
		}
	}

//    public static void main(String [] args){
//
//        InetAddress inetAddress = null;
//        try {
//            inetAddress = Inet4Address.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        System.out.println(inetAddress.getHostAddress());
//        System.out.println(inetAddress.getHostName());
//
//    }

}
