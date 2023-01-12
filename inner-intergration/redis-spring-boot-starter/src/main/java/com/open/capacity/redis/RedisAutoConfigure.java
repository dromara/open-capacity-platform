package com.open.capacity.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.open.capacity.redis.properties.CacheManagerProperties;

import cn.hutool.core.text.StrPool;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;

/**
 * redis 配置类
 * 
 * @author someday
 * @date 2018/11/6 11:02
 */
@EnableCaching
@AutoConfigureBefore(RedisTemplate.class)
@EnableConfigurationProperties({ RedisProperties.class, CacheManagerProperties.class })
public class RedisAutoConfigure {
	@Autowired
	private CacheManagerProperties cacheManagerProperties;

	
//	@Bean
//	@ConditionalOnClass(RedisClient.class)
//	@Conditional(RedisClusterCondition.class)
//	public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
//		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
//				redisProperties.getCluster().getNodes());
//
//		/**
//		 * ClusterTopologyRefreshOptions配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常！
//		 * 稳定简易使用jedis
//		 * https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster#user-content-refreshing-the-cluster-topology-view
//		 */
//		ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//				.enablePeriodicRefresh().enableAllAdaptiveRefreshTriggers().refreshPeriod(Duration.ofSeconds(20))
//				.build();
//
//		ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
//				.topologyRefreshOptions(clusterTopologyRefreshOptions).build();
//
//		// https://github.com/lettuce-io/lettuce-core/wiki/ReadFrom-Settings
//		LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
//				.readFrom(ReadFrom.REPLICA_PREFERRED).clientOptions(clusterClientOptions).build();
//
//		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration)  ;
//		
//		return lettuceConnectionFactory;
//	}
//
//	/**
//	 * 集群配置
//	 */
//	public static class RedisClusterCondition implements Condition {
//
//		@Override
//		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
//			return context.getEnvironment().containsProperty("spring.redis.cluster.nodes");
//		}
//
//	}

	@Bean
	public RedisSerializer<String> redisKeySerializer() {
		return RedisSerializer.string();
	}

	@Bean
	public RedisSerializer<Object> redisValueSerializer() {
		return RedisSerializer.json();
	}
	 
	/**
	 * RedisTemplate配置
	 * 
	 * @param factory
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
			RedisSerializer<String> redisKeySerializer, RedisSerializer<Object> redisValueSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);

		redisTemplate.setDefaultSerializer(redisValueSerializer);
		redisTemplate.setKeySerializer(redisKeySerializer);
		redisTemplate.setHashKeySerializer(redisKeySerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(CacheManagerCustomizers.class)
	public CacheManagerCustomizers cacheManagerCustomizers(
			ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
		return new CacheManagerCustomizers(customizers.getIfAvailable());
	}

	@Primary
	@Bean(name = "cacheManager")
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
			RedisSerializer<String> redisKeySerializer, RedisSerializer<Object> redisValueSerializer,
			CacheManagerCustomizers cacheManagerCustomizers) {
		RedisCacheConfiguration difConf = getDefConf(redisKeySerializer, redisValueSerializer)
				.entryTtl(Duration.ofHours(1));

		// 自定义的缓存过期时间配置
		int configSize = cacheManagerProperties.getConfigs() == null ? 0 : cacheManagerProperties.getConfigs().size();
		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(configSize);
		if (configSize > 0) {
			cacheManagerProperties.getConfigs().forEach(e -> {
				RedisCacheConfiguration conf = getDefConf(redisKeySerializer, redisValueSerializer)
						.entryTtl(Duration.ofSeconds(e.getSecond()));
				redisCacheConfigurationMap.put(e.getKey(), conf);
			});
		}

		RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(difConf)
				.withInitialCacheConfigurations(redisCacheConfigurationMap).build();

		cacheManager.setTransactionAware(false);

		return cacheManagerCustomizers.customize(cacheManager);
	}

	@Bean
	public KeyGenerator keyGenerator() {
		return (target, method, objects) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(target.getClass().getName());
			sb.append(StrPool.COLON + method.getName() + StrPool.COLON);
			for (Object obj : objects) {
				sb.append(obj.toString());
			}
			return sb.toString();
		};
	}

	private RedisCacheConfiguration getDefConf(RedisSerializer<String> redisKeySerializer,
			RedisSerializer<Object> redisValueSerializer) {
		return RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
				.computePrefixWith(cacheName -> "cache".concat(StrPool.COLON).concat(cacheName).concat(StrPool.COLON))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisKeySerializer))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer));
	}
}
