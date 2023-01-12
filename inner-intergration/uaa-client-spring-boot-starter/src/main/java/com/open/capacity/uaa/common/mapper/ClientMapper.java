package com.open.capacity.uaa.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.common.model.Client;
import com.open.capacity.db.mapper.BaseMapper;

/**
 * @author someday
 */
@Mapper
public interface ClientMapper extends BaseMapper<Client> {
    List<Client> findList(Page<Client> page, @Param("params") Map<String, Object> params );
}
