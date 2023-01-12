package com.open.capacity.preview.service.impl;

import com.open.capacity.preview.service.CacheService;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * redis缓存实现
 * <p/>
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 * @since 2022/7/7 16:26
 */
@ConditionalOnExpression("'${office.cache.type:default}'.equals('redis')")
@Service
public class CacheServiceRedisImpl implements CacheService {

    private final RedissonClient redissonClient;

    public CacheServiceRedisImpl(Config config) {
        this.redissonClient = Redisson.create(config);
    }

    @Override
    public void initCachePool(Integer capacity) {

    }

    @Override
    public void putCache(String key, String value) {
        RMapCache<String, String> convertedList = redissonClient.getMapCache(FILE_PREVIEW_KEY);
        convertedList.fastPut(key, value);
    }


    @Override
    public Map<String, String> getCache() {
        return redissonClient.getMapCache(FILE_PREVIEW_KEY);
    }

    @Override
    public String getCache(String key) {
        RMapCache<String, String> convertedList = redissonClient.getMapCache(FILE_PREVIEW_KEY);
        return convertedList.get(key);
    }

    @Override
    public String removeCache(String key) {
        RMapCache<String, String> convertedList = redissonClient.getMapCache(FILE_PREVIEW_KEY);
        return convertedList.remove(key);
    }

    @Override
    public void cleanCache() {
        cleanPdfCache();
    }

    @Override
    public void addQueueTask(String url) {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(TASK_QUEUE_NAME);
        queue.addAsync(url);
    }

    @Override
    public String takeQueueTask() throws InterruptedException {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(TASK_QUEUE_NAME);
        return queue.take();
    }

    private void cleanPdfCache() {
        RMapCache<String, String> pdfCache = redissonClient.getMapCache(FILE_PREVIEW_KEY);
        pdfCache.clear();
    }
}
