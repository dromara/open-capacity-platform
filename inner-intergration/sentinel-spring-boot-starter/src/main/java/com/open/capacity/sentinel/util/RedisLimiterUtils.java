package com.open.capacity.sentinel.util;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.utils.SpringUtil;
import com.open.capacity.common.utils.TimeUtil;
import com.open.capacity.redis.repository.RedisRepository;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SuppressWarnings("all")
public class RedisLimiterUtils {
	public static final String API_WEB_TIME_KEY = "time_key:";
	public static final String API_WEB_COUNTER_KEY = "counter_key:";
	private static final String EXCEEDS_LIMIT = "规定的时间内超出了访问的限制！";

	public ResponseEntity IpRateLimiter(String ip, int limit, int timeout) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);

		String identifier = UUID.randomUUID().toString();
		String time_key = "time_key:ip:" + ip;
		String counter_key = "counter_key:ip:" + ip;

		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			redisUtil.set(time_key, identifier, timeout);
			redisUtil.set(counter_key, 0);
		}
		if (redisUtil.hasKey(time_key) && redisUtil.incr(counter_key, 1) > limit) {
			log.info(EXCEEDS_LIMIT);
			return ResponseEntity.failedWith(null, -1, EXCEEDS_LIMIT);
		}
		return ResponseEntity.succeedWith(null, 0, "调用次数:" + redisUtil.get(counter_key));
	}

	public ResponseEntity clientRateLimiter(String clientid, int limit, int timeout) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);

		String identifier = UUID.randomUUID().toString();
		String time_key = "time_key:clientid:" + clientid;
		String counter_key = "counter_key:clientid:" + clientid;
		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			redisUtil.set(time_key, identifier, timeout);
			redisUtil.set(counter_key, 0);
		}
		if (redisUtil.hasKey(time_key) && redisUtil.incr(counter_key, 1) > limit) {
			log.info(EXCEEDS_LIMIT);
			return ResponseEntity.failedWith(null, -1, EXCEEDS_LIMIT);
		}
		return ResponseEntity.succeedWith(null, 0, "调用次数:" + redisUtil.get(counter_key));
	}

	public ResponseEntity urlRateLimiter(String path, int limit, int timeout) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		String identifier = UUID.randomUUID().toString();
		String time_key = "time_key:path:" + path;
		String counter_key = "counter_key:path:" + path;
		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			redisUtil.set(time_key, identifier, timeout);
			redisUtil.set(counter_key, 0);
		}
		if (redisUtil.hasKey(time_key) && redisUtil.incr(counter_key, 1) > limit) {
			log.info(EXCEEDS_LIMIT);
			return ResponseEntity.failedWith(null, -1, EXCEEDS_LIMIT);
		}
		return ResponseEntity.succeedWith(null, 0, "调用次数:" + redisUtil.get(counter_key));
	}

	public ResponseEntity clientPathRateLimiter(String clientid, String access_path, int limit, int timeout) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		String identifier = UUID.randomUUID().toString();
		LocalDate today = LocalDate.now();
		String time_key = "time_key:clientid:" + clientid + ":path:" + access_path;
		String counter_key = "counter_key:clientid:" + clientid + ":path:" + access_path;

		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			redisUtil.set(time_key, identifier, timeout);
			redisUtil.set(counter_key, 0);
		}
		if (redisUtil.hasKey(time_key) && redisUtil.incr(counter_key, 1) > limit) {
			log.info(EXCEEDS_LIMIT);
			return ResponseEntity.failedWith(null, -1, EXCEEDS_LIMIT);
		}
		return ResponseEntity.succeedWith(null, 0, "调用次数:" + redisUtil.get(counter_key));
	}

	public ResponseEntity rateLimitOfDay(String clientid, String access_path, long limit) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		String identifier = UUID.randomUUID().toString();
		LocalDate today = LocalDate.now();
		String time_key = "time_key:date:" + today + ":clientid:" + clientid + ":path:" + access_path;
		String counter_key = "counter_key:date:" + today + ":clientid:" + clientid + ":path:" + access_path;

		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			// 当天首次访问，初始化访问计数=0，有效期24h
			redisUtil.set(time_key, identifier,  TimeUtil.getRemainSecondsOneDay(DateUtil.date()));
			redisUtil.set(counter_key, 0);
		}

		// 累加访问次数， 超出配置的limit则返回错误
		if (redisUtil.incr(counter_key, 1) > limit) {
			log.info("日内超出了访问的限制！");
			return ResponseEntity.failedWith(null, -1, "日内超出了访问的限制!");
		}
		return ResponseEntity.succeedWith(null, 0, "调用总次数:" + redisUtil.get(counter_key));
	}

	public ResponseEntity acquireRateLimiter(String clientid, String access_path, int limit, int timeout) {

		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		String identifier = UUID.randomUUID().toString();
		LocalDate today = LocalDate.now();
		String time_key = "time_key:date:" + today + ":clientid:" + clientid + ":path:" + access_path;
		String counter_key = "counter_key:date:" + today + ":clientid:" + clientid + ":path:" + access_path;

		if (!redisUtil.hasKey(time_key) || redisUtil.getExpire(time_key) <= 0) {
			redisUtil.set(time_key, identifier, timeout);
			redisUtil.set(counter_key, 0);
		}
		if (redisUtil.hasKey(time_key) && redisUtil.incr(counter_key, 1) > limit) {
			log.info(EXCEEDS_LIMIT);
			return ResponseEntity.failedWith(null, -1, EXCEEDS_LIMIT);
		}
		return ResponseEntity.succeedWith(null, 0, "调用次数:" + redisUtil.get(counter_key));
	}

	public void save(String tokenType, String Token, int timeout) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		redisUtil.set(tokenType, Token, timeout);
	}

	public String getToken(String tokenType) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		return redisUtil.get(tokenType).toString();
	}

	public void saveObject(String key, Object obj, long timeout) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		redisUtil.set(key, obj, timeout);
	}

	public void saveObject(String key, Object obj) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		redisUtil.set(key, obj);
	}

	public Object getObject(String key) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		return redisUtil.get(key);
	}

	public void removeObject(String key) {
		RedisRepository redisUtil = SpringUtil.getBean(RedisRepository.class);
		redisUtil.del(key);
	}
}