package com.open.capacity.preview.service.impl;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import com.open.capacity.preview.service.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * jdk缓存实现
 * <p/>
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 * @since 2022/7/7 16:26
 */
@Service
@ConditionalOnExpression("'${office.cache.type:default}'.equals('jdk')")
public class CacheServiceJDKImpl implements CacheService {

    private Map<String, String> pdfCache;
    private static final int QUEUE_SIZE = 500000;
    private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    @PostConstruct
    public void initCache() {
        initCachePool(CacheService.DEFAULT_CAPACITY);
    }

    @Override
    public void putCache(String key, String value) {
        pdfCache.put(key, value);
    }

    @Override
    public Map<String, String> getCache() {
        return pdfCache;
    }

    @Override
    public String getCache(String key) {
        return pdfCache.get(key);
    }

    @Override
    public String removeCache(String key) {
        return pdfCache.remove(key);
    }

    @Override
    public void cleanCache() {
        initCachePool(CacheService.DEFAULT_CAPACITY);
    }

    @Override
    public void addQueueTask(String url) {
        blockingQueue.add(url);
    }

    @Override
    public String takeQueueTask() throws InterruptedException {
        return blockingQueue.take();
    }

    @Override
    public void initCachePool(Integer capacity) {
        pdfCache = new ConcurrentLinkedHashMap.Builder<String, String>()
                .maximumWeightedCapacity(capacity).weigher(Weighers.singleton())
                .build();
    }

}
