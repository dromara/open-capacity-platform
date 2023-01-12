package com.open.capacity.db.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.common.model.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * dev环境 user-center1库新建了数据库表 sys_user_enc 用于测试
 */
@Mapper
public interface EncryptTestMapper extends BaseMapper<SysUser> {

    void insertEnc(SysUser sysUser);

    List<SysUser> selectListEnc(Page<SysUser> page, SysUser sysUser);

}
