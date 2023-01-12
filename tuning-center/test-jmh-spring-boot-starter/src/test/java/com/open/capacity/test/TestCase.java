package com.open.capacity.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.open.capacity.JmhTestApp;
import com.open.capacity.dao.TestDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { JmhTestApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 配置启动类
public class TestCase {

	@Autowired
	private TestDao testDao;
	@Test
	public void doLogin() {
		System.out.println(11);
		testDao.add(true);
	}
}