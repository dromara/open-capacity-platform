package com.open.capacity.common.disruptor.thread;

import com.lmax.disruptor.dsl.Disruptor;
/**
 * 
 * @author somday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class DisruptorShutdownHook extends Thread{
	
	private Disruptor<?> disruptor;
	
	public DisruptorShutdownHook(Disruptor<?> disruptor) {
		this.disruptor = disruptor;
	}
	
	@Override
	public void run() {
		disruptor.shutdown();
		System.out.println("shut down");
	}
	
}
