package com.open.capacity.user.mapper;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.UserCenterApp;
import com.open.capacity.common.model.SysUser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { UserCenterApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 配置启动类
class SysUserMapperTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void insert() {
    	
	        SysUser sysUser = new SysUser();
	        sysUser.setId(Double.valueOf(Math.random()).longValue());
	        sysUser.setUsername("张%");
	        sysUser.setPassword("123456");
	        sysUser.setMobile("13511112222");
	        sysUser.setType("0");
	        sysUser.setCreateTime(new Date());
	        sysUser.setUpdateTime(new Date());
	        sysUserMapper.insertEnc(sysUser);
    }

    @Test
    @SneakyThrows
    public void selectList(){
        SysUser sysUser = new SysUser();
        sysUser.setUsername("%");
        Page<SysUser> page = new Page<>(1,1000);
        List<SysUser> sysUsers = sysUserMapper.selectListEnc(page, sysUser);

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(sysUsers));
        
        List<SysUser> orderList = sysUsers.stream().sorted(Comparator.comparing(SysUser::getUsername)).collect(Collectors.toList()) ;
        
        System.out.println(page.getTotal());
        System.out.println(orderList);
    }

	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
