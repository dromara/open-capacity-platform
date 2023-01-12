package com.open.capacity.redis.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen
 * @version 创建时间：2017年04月23日 下午20:01:06 类说明
 * redis工具类
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class RedisRepository {
	/**
	 * Spring Redis Template
	 */
	private RedisTemplate<String, Object> redisTemplate;
	/**
     * json序列化方式
     */
    private static GenericJackson2JsonRedisSerializer redisObjectSerializer = new GenericJackson2JsonRedisSerializer();

	public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 获取链接工厂
	 */
	public RedisConnectionFactory getConnectionFactory() {
		return this.redisTemplate.getConnectionFactory();
	}

	/**
	 * 获取 RedisTemplate对象
	 */
	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	/**
	 * 清空DB
	 *
	 * @param node redis 节点
	 */
	public void flushDB(RedisClusterNode node) {
		this.redisTemplate.opsForCluster().flushDb(node);
	}

	/**
	 * 添加到带有 过期时间的 缓存
	 *
	 * @param key   redis主键
	 * @param value 值
	 * @param time  过期时间(单位秒)
	 */
	public void setExpire(final byte[] key, final byte[] value, final long time) {
		execute((RedisCallback<Long>) connection -> {
			connection.setEx(key, time, value);
			return 1L;
		});
	}

	/**
	 * 添加到带有 过期时间的 缓存
	 *
	 * @param key      redis主键
	 * @param value    值
	 * @param time     过期时间
	 * @param timeUnit 过期时间单位
	 */
	public void setExpire(final String key, final Object value, final long time, final TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, time, timeUnit);
	}

	public void setExpire(final String key, final Object value, final long time) {
		this.setExpire(key, value, time, TimeUnit.SECONDS);
	}

	public void setExpire(final String key, final Object value, final long time, final TimeUnit timeUnit,
			RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		byte[] rawValue = rawValue(value, valueSerializer);

		execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				potentiallyUsePsetEx(connection);
				return null;
			}

			public void potentiallyUsePsetEx(RedisConnection connection) {
				if (!TimeUnit.MILLISECONDS.equals(timeUnit) || !failsafeInvokePsetEx(connection)) {
					connection.set(rawKey, rawValue, Expiration.from( time , timeUnit), SetOption.upsert());
					
				}
			}

			private boolean failsafeInvokePsetEx(RedisConnection connection) {
				boolean failed = false;
				try {
					connection.pSetEx(rawKey, time, rawValue);
				} catch (UnsupportedOperationException e) {
					failed = true;
				}
				return !failed;
			}
		}, true);
	}

	/**
	 * 一次性添加数组到 过期时间的 缓存，不用多次连接，节省开销
	 *
	 * @param keys   redis主键数组
	 * @param values 值数组
	 * @param time   过期时间(单位秒)
	 */
	public void setExpire(final String[] keys, final Object[] values, final long time) {
		for (int i = 0; i < keys.length; i++) {
			redisTemplate.opsForValue().set(keys[i], values[i], time, TimeUnit.SECONDS);
		}
	}

	/**
	 * 一次性添加数组到 过期时间的 缓存，不用多次连接，节省开销
	 *
	 * @param keys   the keys
	 * @param values the values
	 */
	public void set(final String[] keys, final Object[] values) {
		for (int i = 0; i < keys.length; i++) {
			redisTemplate.opsForValue().set(keys[i], values[i]);
		}
	}

	/**
	 * 添加到缓存
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public void set(final String key, final Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
	public boolean setString(String key, String value) {

		redisTemplate.execute((RedisCallback<Long>) connection -> {
			// redis info
			byte[] values = redisObjectSerializer.serialize(value);
			connection.set(key.getBytes(), values);
			connection.close();

			return 1L;
		});
		return true;

	}
	
	
	/**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public String getString(String key) {
        String value = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {

                byte[] temp = null;
                temp = connection.get(key.getBytes());
                connection.close();
                return (String) redisObjectSerializer.deserialize(temp);
            }
        });

        return value ;
    }
	
	/**
	 * 查询在以keyPatten的所有 key
	 *
	 * @param keyPatten the key patten
	 * @return the set
	 */
	public Set<String> keys(final String keyPatten) {
		return redisTemplate.keys(keyPatten + "*");
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public boolean hasKey(String key) {
		return execute((RedisCallback<Boolean>) connection -> connection.exists(key.getBytes()));
	}

	/**
	 * 根据key获取对象
	 *
	 * @param key the key
	 * @return the byte [ ]
	 */
	public byte[] get(final byte[] key) {
		return execute((RedisCallback<byte[]>) connection -> connection.get(key));
	}

	/**
	 * 根据key获取对象
	 *
	 * @param key the key
	 * @return the string
	 */
	public Object get(final String key) {
		return redisTemplate.opsForValue().get(key);
	}

	
	
	
	
	/**
	 * 根据key 获取过期时间
	 *
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public long getExpire(String key) {

		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					return connection.pTtl(key.getBytes(), TimeUnit.SECONDS);
				} catch (Exception e) {
					return connection.ttl(key.getBytes(), TimeUnit.SECONDS);
				}
			}
		});
	}

	/**
	 * 获取原来key键对应的值并重新赋新值。
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String getAndSet(final String key, String value) {
		String result = null;
		if (StringUtils.isEmpty(key)) {
			log.error("非法入参");
			return null;
		}
		try {
			Object object = redisTemplate.opsForValue().getAndSet(key, value);
			if (object != null) {
				result = object.toString();
			}
		} catch (Exception e) {
			log.error("redisTemplate操作异常", e);
		}
		return result;
	}

	/**
	 * 根据key获取对象
	 *
	 * @param key             the key
	 * @param valueSerializer 序列化
	 * @return the string
	 */
	public Object get(final String key, RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		return execute(connection -> deserializeValue(connection.get(rawKey), valueSerializer), true);
	}

	/**
	 * Ops for hash hash operations.
	 *
	 * @return the hash operations
	 */
	public HashOperations<String, String, Object> opsForHash() {
		return redisTemplate.opsForHash();
	}

	/**
	 * 对HashMap操作
	 *
	 * @param key       the key
	 * @param hashKey   the hash key
	 * @param hashValue the hash value
	 */
	public void putHashValue(String key, String hashKey, Object hashValue) {
		opsForHash().put(key, hashKey, hashValue);
	}

	/**
	 * 获取单个field对应的值
	 *
	 * @param key     the key
	 * @param hashKey the hash key
	 * @return the hash values
	 */
	public Object getHashValues(String key, String hashKey) {
		return opsForHash().get(key, hashKey);
	}

	/**
	 * 根据key值删除
	 *
	 * @param key      the key
	 * @param hashKeys the hash keys
	 */
	public void delHashValues(String key, Object... hashKeys) {
		opsForHash().delete(key, hashKeys);
	}

	/**
	 * key只匹配map
	 *
	 * @param key the key
	 * @return the hash value
	 */
	public Map<String, Object> getHashValue(String key) {
		return opsForHash().entries(key);
	}

	/**
	 * 批量添加
	 *
	 * @param key the key
	 * @param map the map
	 */
	public void putHashValues(String key, Map<String, Object> map) {
		opsForHash().putAll(key, map);
	}

	/**
	 * 集合数量
	 *
	 * @return the long
	 */
	public long dbSize() {
		return execute(RedisServerCommands::dbSize);
	}

	/**
	 * 清空redis存储的数据
	 *
	 * @return the string
	 */
	public String flushDB() {
		return execute((RedisCallback<String>) connection -> {
			connection.flushDb();
			return "ok";
		});
	}

	/**
	 * 判断某个主键是否存在
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 删除key
	 *
	 * @param keys the keys
	 * @return the long
	 */
	public boolean del(final String... keys) {
		boolean result = false;
		for (String key : keys) {
			result = redisTemplate.delete(key);
		}
		return result;
	}

	/**
	 * 对某个主键对应的值加一,value值必须是全数字的字符串
	 *
	 * @param key the key
	 * @return the long
	 */
	public long incr(final String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	/***
	 * 递增**
	 * 
	 * @param key   键*
	 * @param delta 要增加几(大于0)
	 * @return
	 */
	public long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		return execute((RedisCallback<Long>) connection -> {
			return connection.incrBy(key.getBytes(), delta);
		});
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(小于0)
	 * @return
	 */
	public long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		return execute((RedisCallback<Long>) connection -> {
			return connection.incrBy(key.getBytes(), -delta);
		});

	}

	/**
	 * redis List 引擎
	 *
	 * @return the list operations
	 */
	public ListOperations<String, Object> opsForList() {
		return redisTemplate.opsForList();
	}

	/**
	 * redis List数据结构 : 将一个或多个值 value 插入到列表 key 的表头
	 *
	 * @param key   the key
	 * @param value the value
	 * @return the long
	 */
	public Long leftPush(String key, Object value) {
		return opsForList().leftPush(key, value);
	}

	/**
	 * redis List数据结构 : 移除并返回列表 key 的头元素
	 *
	 * @param key the key
	 * @return the string
	 */
	public Object leftPop(String key) {
		return opsForList().leftPop(key);
	}

	/**
	 * redis List数据结构 :将一个或多个值 value 插入到列表 key 的表尾(最右边)。
	 *
	 * @param key   the key
	 * @param value the value
	 * @return the long
	 */
	public Long in(String key, Object value) {
		return opsForList().rightPush(key, value);
	}

	/**
	 * redis List数据结构 : 移除并返回列表 key 的末尾元素
	 *
	 * @param key the key
	 * @return the string
	 */
	public Object rightPop(String key) {
		return opsForList().rightPop(key);
	}

	/**
	 * redis List数据结构 : 返回列表 key 的长度 ; 如果 key 不存在，则 key 被解释为一个空列表，返回 0 ; 如果 key
	 * 不是列表类型，返回一个错误。
	 *
	 * @param key the key
	 * @return the long
	 */
	public Long length(String key) {
		return opsForList().size(key);
	}

	/**
	 * redis List数据结构 : 根据参数 i 的值，移除列表中与参数 value 相等的元素
	 *
	 * @param key   the key
	 * @param i     the
	 * @param value the value
	 */
	public void remove(String key, long i, Object value) {
		opsForList().remove(key, i, value);
	}

	/**
	 * redis List数据结构 : 将列表 key 下标为 index 的元素的值设置为 value
	 *
	 * @param key   the key
	 * @param index the index
	 * @param value the value
	 */
	public void set(String key, long index, Object value) {
		opsForList().set(key, index, value);
	}

	/**
	 * redis List数据结构 : 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定。
	 *
	 * @param key   the key
	 * @param start the start
	 * @param end   the end
	 * @return the list
	 */
	public List<Object> getList(String key, int start, int end) {
		return opsForList().range(key, start, end);
	}

	/**
	 * redis List数据结构 : 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定。
	 *
	 * @param key             the key
	 * @param start           the start
	 * @param end             the end
	 * @param valueSerializer 序列化
	 * @return the list
	 */
	public List<Object> getList(String key, int start, int end, RedisSerializer<Object> valueSerializer) {
		byte[] rawKey = rawKey(key);
		return redisTemplate
				.execute(connection -> deserializeValues(connection.lRange(rawKey, start, end), valueSerializer), true);
	}

	/**
	 * redis List数据结构 : 批量存储
	 *
	 * @param key  the key
	 * @param list the list
	 * @return the long
	 */
	public Long leftPushAll(String key, List<String> list) {
		return opsForList().leftPushAll(key, list);
	}

	/**
	 * redis List数据结构 : 将值 value 插入到列表 key 当中，位于值 index 之前或之后,默认之后。
	 *
	 * @param key   the key
	 * @param index the index
	 * @param value the value
	 */
	public void insert(String key, long index, Object value) {
		opsForList().set(key, index, value);
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				execute((RedisCallback<Long>) connection -> {
					// redis info
					byte[] values = rawKey(key);
					connection.set(key.getBytes(), values);
					connection.expire(key.getBytes(), 60 * time);
					connection.close();
					return 1L;
				});
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                long rawTimeout = TimeoutUtils.toMillis(time, TimeUnit.SECONDS);
                try {
                    return connection.pExpire(key.getBytes(), rawTimeout);
                } catch (Exception e) {
                    // Driver may not support pExpire or we may be running on
                    // Redis 2.4
                    return connection.expire(key.getBytes(), TimeoutUtils.toSeconds(rawTimeout, TimeUnit.SECONDS));
                }
            }
        });
    }
	/**
	 * 添加经纬度信息 map.put("北京" ,new Point(116.405285 ,39.904989)) //redis 命令：geoadd
	 * cityGeo 116.405285 39.904989 "北京"
	 */
	public Long addGeoPoint(String key, Map<Object, Point> map) {
		return redisTemplate.opsForGeo().add(key, map);
	}

	/**
	 * 查找指定key的经纬度信息 redis命令：geopos cityGeo 北京
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public Point geoGetPoint(String key, String member) {
		List<Point> lists = redisTemplate.opsForGeo().position(key, member);
		return lists.get(0);
	}

	/**
	 * 返回两个地方的距离，可以指定单位 redis命令：geodist cityGeo 北京 上海
	 * 
	 * @param key
	 * @param srcMember
	 * @param targetMember
	 * @return
	 */
	public Distance geoDistance(String key, String srcMember, String targetMember) {
		Distance distance = redisTemplate.opsForGeo().distance(key, srcMember, targetMember, Metrics.KILOMETERS);
		return distance;
	}

	/**
	 * 根据指定的地点查询半径在指定范围内的位置 redis命令：georadiusbymember cityGeo 北京 100 km WITHDIST
	 * WITHCOORD ASC COUNT 5
	 * 
	 * @param key
	 * @param member
	 * @param distance
	 * @return
	 */
	public GeoResults geoRadiusByMember(String key, String member, double distance) {
		return redisTemplate.opsForGeo().radius(key, member, new Distance(distance, Metrics.KILOMETERS));
	}

	/**
	 * 根据给定的经纬度，返回半径不超过指定距离的元素 redis命令：georadius cityGeo 116.405285 39.904989 100 km
	 * WITHDIST WITHCOORD ASC COUNT 5
	 * 
	 * @param key
	 * @param circle
	 * @param distance
	 * @return
	 */
	public GeoResults geoRadiusByCircle(String key, Circle circle, double distance) {
		return redisTemplate.opsForGeo().radius(key, circle, new Distance(distance, Metrics.KILOMETERS));
	}

	private <T> T execute(RedisCallback<T> action) {
		return redisTemplate.execute(action);
	}

	private <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
		return redisTemplate.execute(action, exposeConnection);
	}

	private byte[] rawKey(Object key) {
		Assert.notNull(key, "non null key required");

		if (key instanceof byte[]) {
			return (byte[]) key;
		}
		RedisSerializer<Object> redisSerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
		return redisSerializer.serialize(key);
	}

	private byte[] rawValue(Object value, RedisSerializer valueSerializer) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		}

		return valueSerializer.serialize(value);
	}

	private List deserializeValues(List<byte[]> rawValues, RedisSerializer<Object> valueSerializer) {
		if (valueSerializer == null) {
			return rawValues;
		}
		return SerializationUtils.deserialize(rawValues, valueSerializer);
	}

	private Object deserializeValue(byte[] value, RedisSerializer<Object> valueSerializer) {
		if (valueSerializer == null) {
			return value;
		}
		return valueSerializer.deserialize(value);
	}

}
