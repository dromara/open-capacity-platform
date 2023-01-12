package com.open.capacity.generator.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.generator.mapper.SysGeneratorMapper;
import com.open.capacity.generator.service.SysGeneratorService;
import com.open.capacity.generator.utils.GenUtils;

/**
 * @Author: [dawei QQ:64738479]
 * @Date: [2019-04-25 21:48]
 * @Description: [ ]
 * @Version: [1.0.1]
 * @Copy: [com.zzg]
 */
@Service
public class SysGeneratorServiceImpl implements SysGeneratorService {
	
    @Autowired
    private SysGeneratorMapper sysGeneratorMapper;


    @Override
    public PageResult<Map<String, Object>> queryList(Map<String, Object> map) {
    	//设置分页信息，分别是当前页数和每页显示的总记录数【记住：必须在mapper接口中的方法执行之前设置该分页信息】
        PageHelper.startPage(MapUtils.getInteger(map, "page"),MapUtils.getInteger(map, "limit"),true);

        List list = sysGeneratorMapper.queryList(map);
        PageInfo pageInfo = new PageInfo<>(list);
        return PageResult.builder().data(pageInfo.getList()).statusCodeValue(0).count(pageInfo.getTotal()).build();
    }


    @Override
    public int queryTotal(Map<String, Object> map) {
        return 0;
    }

    @Override
    public Map<String, String> queryTable(String tableName) {
        return sysGeneratorMapper.queryTable(tableName);
    }

    @Override
    public List<Map<String, String>> queryColumns(String tableName) {
        return sysGeneratorMapper.queryColumns(tableName);
    }

    @Override
    public byte[] generatorCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for(String tableName : tableNames){
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GenUtils.generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
