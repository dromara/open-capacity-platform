package com.open.capacity.sms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.db.mapper.BaseMapper;
import com.open.capacity.sms.model.Sms;

/**
 * * 程序名 : SmsMapper
 * 建立日期: 2018-07-09
 * 作者 : someday
 * 模块 : 短信中心
 * 描述 : 短信crud
 * 备注 : version20180709001
 * <p>
 * 修改历史
 * 序号 	       日期 		        修改人 		         修改原因
 */
@Mapper
public interface SmsMapper  extends BaseMapper<Sms> {
	
	List<Sms> findList(Page<Sms> page, @Param("params") Map<String, Object> params);
 
}
