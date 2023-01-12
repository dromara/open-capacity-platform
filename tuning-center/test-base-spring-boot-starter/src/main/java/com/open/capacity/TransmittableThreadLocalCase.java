package com.open.capacity;

import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: TransmittableThreadLocal正确使用
 * author: JohnsonLiu
 * create at:  2021/12/24  22:24
 */
public class TransmittableThreadLocalCase {


//    private static final Executor executor = TtlExecutors.getTtlExecutor(new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000)));
    private static final Executor executor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000));

    static int i = 0;

    public static void main(String[] args) {

        ServiceContext.set(++i, i);

        System.out.println("第一次设置" + i);
        
        executor.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 子线程(rm之前)：" + ServiceContext.get());
        });

        ServiceContext.set(++i, i);
        System.out.println("第二次设置" + i);
        ServiceContext.remove();
        System.out.println("第一次删除" + i);

        executor.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 子线程(rm之后)：" + ServiceContext.get());
        });
        
        System.out.println(Thread.currentThread().getName() + " 子线程(rm之后)：" + ServiceContext.get());
    }
}
