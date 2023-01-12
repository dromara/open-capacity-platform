package com.open.capacity;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description: TransmittableThreadLocal正确使用
 * author: JohnsonLiu
 * create at:  2021/12/24  23:19
 */
public class ServiceContext {

    private static final ThreadLocal<Map<Integer, Integer>> transmittableThreadLocal = new TransmittableThreadLocal() 
    {
        /**
         * 如果使用的是TtlExecutors装饰的线程池或者TtlRunnable、TtlCallable装饰的任务
         * 重写copy方法且重新赋值给新的LinkedHashMap，不然会导致父子线程都是持有同一个引用，只要有修改取值都会变化。引用值线程不安全
         * parentValue是父线程执行子任务那个时刻的快照值，后续父线程再次set值也不会影响子线程get，因为已经不是同一个引用
         * @param parentValue
         * @return
         */
        @Override
        public Object copy(Object parentValue) {
            if (parentValue instanceof Map) {
                System.out.println("copy");
                return new LinkedHashMap<Integer, Integer>((Map) parentValue);
            }
            return null;
        }

        /**
         * 如果使用普通线程池执行异步任务，重写childValue即可实现子线程获取的是父线程执行任务那个时刻的快照值，重新赋值给新的LinkedHashMap，父线程修改不会影响子线程（非共享）
         * 但是如果使用的是TtlExecutors装饰的线程池或者TtlRunnable、TtlCallable装饰的任务，此时就会变成引用共享，必须得重写copy方法才能实现非共享
         * @param parentValue
         * @return
         */
        @Override
        protected Object childValue(Object parentValue) {
            if (parentValue instanceof Map) {
                System.out.println("childValue");
                return new LinkedHashMap<Integer, Integer>((Map) parentValue);
            }
            return null;
        }

        /**
         * 初始化,每次get时都会进行初始化
         * @return
         */
        @Override
        protected Object initialValue() {
            System.out.println("initialValue");
            return new LinkedHashMap<Integer, Integer>();
        }
    }
    ;

    public static void set(Integer key, Integer value) {
        transmittableThreadLocal.get().put(key, value);
    }

    public static Map<Integer, Integer> get() {
        return transmittableThreadLocal.get();
    }

    public static void remove() {
        transmittableThreadLocal.remove();
    }
}