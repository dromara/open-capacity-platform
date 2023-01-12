package com.open.capacity;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.context.TenantContextHolder;

@RestController
public class TestAysnc3 {

	@Autowired
	private TaskExecutor taskExecutor;
	
	private Integer i = 0;

	@GetMapping("/test7")
	public String test7() {


		String tenat = TenantContextHolder.getTenant() ;

		if (tenat == null) {
			init();
		}

 
		System.out.println("主线程设置用户Id:" +  TenantContextHolder.getTenant());

		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child1-----------" + TenantContextHolder.getTenant());

		}, taskExecutor);

		TenantContextHolder.setTenant("2");

		System.out.println("主线程设置用户Id:" +  TenantContextHolder.getTenant());

		SysUserContextHolder.clear();

		System.out.println("主线程结束");

		return "test6";

	}

	private void init() {
		String tenat = TenantContextHolder.getTenant() ;
		if (tenat == null) {
			tenat = "1";
			TenantContextHolder.setTenant(tenat);
		}

	}
}
