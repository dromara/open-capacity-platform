package com.open.capacity;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.open.capacity.common.context.TenantContextHolder;

@RestController
public class TestAysnc {

	private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

	private ThreadLocal<Integer> inheritableThreadLocal = new InheritableThreadLocal<>();

	private ThreadLocal<Integer> transmittableThreadLocal = new TransmittableThreadLocal<>();

	private Integer i = 0;
	private Integer j = 0;
	private Integer k = 0;

	@Autowired
	private TaskExecutor taskExecutor;

	@GetMapping("/test0")
	public String test0() {

		System.out.println("111111111111111");
		TenantContextHolder.setTenant("hello");

		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child-----------" + TenantContextHolder.getTenant());

			CompletableFuture.runAsync(() -> {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				System.out.println("child-child-----------" + TenantContextHolder.getTenant());
			});

		});

		System.out.println("parent-----------" + TenantContextHolder.getTenant());

		TenantContextHolder.clear();

		System.out.println("parent-----------" + TenantContextHolder.getTenant());

		return "test0";
	}

	@GetMapping("/test1")
	public String test1() {

		System.out.println("111111111111111");
		TenantContextHolder.setTenant("hello");

		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child-----------" + TenantContextHolder.getTenant());

			CompletableFuture.runAsync(() -> {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				System.out.println("child-child" + TenantContextHolder.getTenant());
			}, taskExecutor);

		}, taskExecutor);

		System.out.println("parent-----------" + TenantContextHolder.getTenant());

		TenantContextHolder.clear();

		System.out.println("parent-----------" + TenantContextHolder.getTenant());

		return "test1";
	}

	@GetMapping("/test2")
	public String test2() {

		threadLocal.set(++i);
		inheritableThreadLocal.set(++j);
		transmittableThreadLocal.set(++k);
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child1-----------" + threadLocal.get());
			System.out.println("child2-----------" + inheritableThreadLocal.get());
			System.out.println("child3-----------" + transmittableThreadLocal.get());

		} );
		System.out.println("parent1-----------" + threadLocal.get());
		System.out.println("parent2-----------" + inheritableThreadLocal.get());
		System.out.println("parent2-----------" + transmittableThreadLocal.get());
		return "test2";
	}
	
	@GetMapping("/test3")
	public String test3() {

		threadLocal.set(++i);
		inheritableThreadLocal.set(++j);
		transmittableThreadLocal.set(++k);
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child1-----------" + threadLocal.get());
			System.out.println("child2-----------" + inheritableThreadLocal.get());
			System.out.println("child3-----------" + transmittableThreadLocal.get());

		}, taskExecutor);
		System.out.println("parent1-----------" + threadLocal.get());
		System.out.println("parent2-----------" + inheritableThreadLocal.get());
		System.out.println("parent2-----------" + transmittableThreadLocal.get());
		return "test3";
	}
}
