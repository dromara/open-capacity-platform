package com.open.capacity;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.model.SysUser;

@RestController
public class TestAysnc2 {

	@Autowired
	private TaskExecutor taskExecutor;
	
	private Integer i = 0;

	@GetMapping("/test6")
	public String test6() {


		SysUser user = SysUserContextHolder.getUser();

		if (user == null) {
			init();
		}

		SysUserContextHolder.getUser().setId( Long.valueOf(++i) );
		SysUserContextHolder.getUser().setCreateTime(new Date());
		SysUserContextHolder.getUser().setEnabled(true);
		SysUserContextHolder.getUser().setNickname("test");
		SysUserContextHolder.getUser().setUsername("测试");
		SysUserContextHolder.getUser().setMobile("13000000000");
		SysUserContextHolder.getUser().setOpenId("123");
		SysUserContextHolder.getUser().setType("admin");
		SysUserContextHolder.getUser().setOldPassword("123456");
		SysUserContextHolder.getUser().setNewPassword("654321");

		System.out.println("主线程设置用户Id:" + SysUserContextHolder.getUser().getId() + "=" + SysUserContextHolder.getUser());

		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("child1-----------" + SysUserContextHolder.getUser());

		}, taskExecutor);

		SysUserContextHolder.getUser().setUsername("aaa");

		System.out.println("主线程设置用户Id:" + SysUserContextHolder.getUser().getId() + "=" + SysUserContextHolder.getUser());

		SysUserContextHolder.clear();

		System.out.println("主线程结束");

		return "test6";

	}

	private void init() {
		SysUser user = SysUserContextHolder.getUser();
		if (user == null) {
			user = new SysUser();
			SysUserContextHolder.setUser(user);
		}

	}
}
