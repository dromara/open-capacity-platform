package com.open.capacity.db.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.db.DbCenterApp;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
		DbCenterApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 配置启动类
public class SysRoleMapperTest {

	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	@Test
	void test() {
 
		sysRoleMapper.selectList(new QueryWrapper<SysRole>().eq("id", 1));
	}

}
