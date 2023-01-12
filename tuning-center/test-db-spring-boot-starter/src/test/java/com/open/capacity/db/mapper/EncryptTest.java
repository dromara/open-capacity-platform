package com.open.capacity.db.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.UUIDUtils;
import com.open.capacity.db.DbCenterApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * 测试数据库加密 模糊查询
 */
@Slf4j
@SpringBootTest(classes = {DbCenterApp.class} , webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class EncryptTest {

    @Autowired
    private EncryptTestMapper encryptMapper;

    @Test
    public void insert() {
        SysUser sysUser = new SysUser();
        sysUser.setId(Double.valueOf(Math.random()).longValue());
        sysUser.setUsername("张五");
        sysUser.setPassword("123456");
        sysUser.setMobile("13522223333");
        sysUser.setType("0");
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        encryptMapper.insertEnc(sysUser);
    }

    @Test
    public void selectList(){
        SysUser sysUser = new SysUser();
        sysUser.setUsername("五");
        sysUser.setMobile("1352");
        Page<SysUser> page = new Page<>(1,10);
        List<SysUser> sysUsers = encryptMapper.selectListEnc(page, sysUser);

        System.out.println(page.getTotal());
        System.out.println(sysUsers);
    }

}
