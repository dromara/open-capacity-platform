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
 * 验证结论：
 * 1.线程池必须使用TtlExecutors修饰，或者Runnable\Callable必须使用TtlRunnable\TtlCallable修饰
 * ---->原因：子线程复用，子线程拥有的上下文内容会对下次使用造成“污染”，而修饰后的子线程在执行run方法后会进行“回放”，防止污染
 */
public class TransmittableThreadLocalCase2 {
  
  // 为达到线程100%复用便于测试，线程池核心数1
  
  private static final Executor TTL_EXECUTOR = TtlExecutors.getTtlExecutor(new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000)));
  
  // 如果使用一般的线程池或者Runnable\Callable时，会存在线程“污染”，比如线程池中线程会复用，复用的线程会“污染”该线程执行下一次任务
  private static final Executor EXECUTOR = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(1000));
  
  
  public static void main(String[] args) {
    
    RequestContext.create(new RequestContext.RequestHeader("url", "get"));
    System.out.println(Thread.currentThread().getName() + " 子线程(rm之前 同步)：" + RequestContext.get());
    // 模拟另一个线程修改上下文内容
    EXECUTOR.execute(() -> {
      RequestContext.create(new RequestContext.RequestHeader("url", "put"));
    });
    
    // 保证上面子线程修改成功
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // 异步获取上下文内容
    TTL_EXECUTOR.execute(() -> {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(Thread.currentThread().getName() + " 子线程(rm之前 异步)：" + RequestContext.get());
    });
    
    // 主线程修改上下文内容
    RequestContext.create(new RequestContext.RequestHeader("url", "post"));
    System.out.println(Thread.currentThread().getName() + " 子线程(rm之前 同步<reCreate>)：" + RequestContext.get());
    
    // 主线程remove
    RequestContext.remove();
    
    // 子线程获取remove后的上下文内容
    TTL_EXECUTOR.execute(() -> {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(Thread.currentThread().getName() + " 子线程(rm之后 异步)：" + RequestContext.get());
    });
  }
}