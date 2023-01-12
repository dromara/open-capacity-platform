package com.open.capacity.generator.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Author: [dawei QQ:64738479]
 * @Date: [2019-04-25 21:48]
 * @Description: [ ]
 * @Version: [1.0.1]
 * @Copy: [com.zzg]
 */
@Mapper
public interface SysGeneratorMapper {

	@InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> queryList(@Param("p") Map<String, Object> map);

	@InterceptorIgnore(tenantLine = "true")
    int queryTotal(Map<String, Object> map);

	@InterceptorIgnore(tenantLine = "true")
    Map<String, String> queryTable(String tableName);

	@InterceptorIgnore(tenantLine = "true")
    List<Map<String, String>> queryColumns(String tableName);

}
