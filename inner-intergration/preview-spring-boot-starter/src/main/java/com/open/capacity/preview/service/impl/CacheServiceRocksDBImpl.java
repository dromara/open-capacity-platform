package com.open.capacity.preview.service.impl;

import com.open.capacity.preview.ConfigUtils;
import com.open.capacity.preview.KkFileUtils;
import com.open.capacity.preview.exception.KKFileException;
import com.open.capacity.preview.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * <p>
 * rocksDB缓存实现
 * <p/>
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 * @since 2022/7/7 16:26
 */
@Slf4j
@ConditionalOnExpression("'${office.cache.type:default}'.equals('default')")
@Service
public class CacheServiceRocksDBImpl implements CacheService {

    static {
        RocksDB.loadLibrary();
    }

    private static final String DB_PATH = ConfigUtils.getHomePath(null) + File.separator + "cache";
    private static final int QUEUE_SIZE = 500000;
    private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private RocksDB db;

    @PostConstruct
    public void init() {
        try {
            boolean exist = KkFileUtils.exist(DB_PATH);
            if (!exist) {
                KkFileUtils.mkdir(DB_PATH);
            }
            db = RocksDB.open(DB_PATH);
            if (db.get(FILE_PREVIEW_KEY.getBytes()) == null) {
                Map<String, String> initPDFCache = new HashMap<>();
                db.put(FILE_PREVIEW_KEY.getBytes(), toByteArray(initPDFCache));
            }
        } catch (RocksDBException | IOException e) {
            log.error(e.getMessage(), e);
            throw new KKFileException("Uable to init RocksDB");
        }
    }

    @Override
    public void initCachePool(Integer capacity) {

    }

    @Override
    public void putCache(String key, String value) {
        try {
            Map<String, String> pdfCacheItem = getCache();
            pdfCacheItem.put(key, value);
            db.put(FILE_PREVIEW_KEY.getBytes(), toByteArray(pdfCacheItem));
        } catch (RocksDBException | IOException e) {
            log.error("Put into RocksDB Exception" + e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getCache() {
        Map<String, String> result = new HashMap<>();
        try {
            result = (Map<String, String>) toObject(db.get(FILE_PREVIEW_KEY.getBytes()));
        } catch (RocksDBException | IOException | ClassNotFoundException e) {
            log.error("Get from RocksDB Exception" + e);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getCache(String key) {
        String result = "";
        try {
            Map<String, String> map = (Map<String, String>) toObject(db.get(FILE_PREVIEW_KEY.getBytes()));
            result = map.get(key);
        } catch (RocksDBException | IOException | ClassNotFoundException e) {
            log.error("Get from RocksDB Exception" + e);
        }
        return result;
    }

    @Override
    public String removeCache(String key) {
        try {
            Map<String, String> pdfCacheItem = getCache();
            String remove = pdfCacheItem.remove(key);
            db.put(FILE_PREVIEW_KEY.getBytes(), toByteArray(pdfCacheItem));
            return remove;
        } catch (RocksDBException | IOException e) {
            log.error("remove from RocksDB Exception" + e);
        }
        return null;
    }

    @Override
    public void cleanCache() {
        try {
            cleanPdfCache();
        } catch (IOException | RocksDBException e) {
            log.error("Clean Cache Exception" + e);
        }
    }

    @Override
    public void addQueueTask(String url) {
        blockingQueue.add(url);
    }

    @Override
    public String takeQueueTask() throws InterruptedException {
        return blockingQueue.take();
    }

    private byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        bytes = bos.toByteArray();
        oos.close();
        bos.close();
        return bytes;
    }

    private Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

    private void cleanPdfCache() throws IOException, RocksDBException {
        Map<String, String> initPDFCache = new HashMap<>();
        db.put(FILE_PREVIEW_KEY.getBytes(), toByteArray(initPDFCache));
    }
}
