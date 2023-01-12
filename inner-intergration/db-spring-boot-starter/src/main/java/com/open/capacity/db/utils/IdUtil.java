package com.open.capacity.db.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;

import lombok.experimental.UtilityClass;

/**
 * 
 * @author owen
 * sharding雪花算法生成器
 *
 */
@UtilityClass
public class IdUtil {

	private static SnowflakeShardingKeyGenerator shardingKeyGenerator = new SnowflakeShardingKeyGenerator();

	static {
		Properties props = new Properties();
		String workerId = null ;
		try {
			InetAddress inetAddress = Inet4Address.getLocalHost();
			String hostAddressIp = inetAddress.getHostAddress();
			workerId = Math.abs(hostAddressIp.hashCode()) % 1024 + "";
		} catch (UnknownHostException e) {
			workerId="1";
		}
		props.setProperty("worker.id",workerId);
		shardingKeyGenerator.setProperties(props);
	}
	
	/**
	 * 雪花算法生成器
	 * 
	 * @return
	 */
	public Comparable<?> geneSnowFlakeId() {

		return shardingKeyGenerator.generateKey();
	}
	
	
}
