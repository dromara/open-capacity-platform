package com.open.capacity.common.utils;

import java.util.function.BiConsumer;

/**
 * <p>批量处理
 * @author wh
 */
public class BatchUtil {
    
    public static void batchHandle(long total, int size, BiConsumer<Integer, Integer> handler) {
        int offset = 0;
        do {
            handler.accept(offset, size);
            offset = offset + size;
        } while (offset < total);
    }
//    public static void main(String[] args) {
//		
//    	batchHandle(10000, 100, (offset, size) -> {
//    	    System.out.println("查询数据库，偏移量：" + offset + " size: " + size);
//    	    System.out.println("处理");
//    	});
//    	
//	}

}
