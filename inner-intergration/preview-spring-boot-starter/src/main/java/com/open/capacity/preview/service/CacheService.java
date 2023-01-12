package com.open.capacity.preview.service;

import java.util.Map;

/**
 * <p>
 * 缓存接口
 * <p/>
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 * @since 2022/7/7 16:26
 */
public interface CacheService {

    String FILE_PREVIEW_KEY = "converted-preview-file";
    String TASK_QUEUE_NAME = "convert-task";

    Integer DEFAULT_CAPACITY = 500000;

    /**
     * 初始化缓存
     *
     * @param capacity 初始容量
     */
    void initCachePool(Integer capacity);

    /**
     * 添加缓冲
     *
     * @param key   键
     * @param value 值
     */
    void putCache(String key, String value);

    /**
     * 获取所有的缓存
     */
    Map<String, String> getCache();

    /**
     * 根据key获取缓存中的文件地址
     *
     * @param key 缓存key
     * @return java.lang.String
     */
    String getCache(String key);

    /**
     * 刪除指定key，返回刪除的內容
     *
     * @param key key
     * @return java.lang.String
     */
    String removeCache(String key);

    /**
     * 清理缓存
     */
    void cleanCache();

    void addQueueTask(String url);

    String takeQueueTask() throws InterruptedException;

}
