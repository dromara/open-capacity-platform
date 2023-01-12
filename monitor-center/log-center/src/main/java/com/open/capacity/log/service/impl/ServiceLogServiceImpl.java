package com.open.capacity.log.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.dmo.ServiceLogDmo;
import com.open.capacity.log.dto.ServiceLogDto;
import com.open.capacity.log.service.IServiceLogService;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

@Service
public class ServiceLogServiceImpl implements IServiceLogService {

	private static final String INDEX_NAME = "ocp-log-*";

	private static final String MAPPER_NAME = "mapper/serviceLog.xml";

	private static final String LIST_METHOD_NAME = "selectList";


	@Override
	public PageResult queryByPage(Map<String, Object> params) {
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil(MAPPER_NAME);
		List<ServiceLogDto> sysLogs = Lists.newArrayList();

		Integer from = (MapUtils.getInteger(params, "page",1) - 1) * MapUtils.getInteger(params, "limit",10);

		Integer to = MapUtils.getInteger(params, "limit",10);
		
		
		 
		String traceId= StringUtils.substringAfter( MapUtils.getString(params, "queryStr", null), "traceId:")   ;
		String appName= StringUtils.substringAfter( MapUtils.getString(params, "queryStr", null), "appName:")   ;
		String serverIp= StringUtils.substringAfter( MapUtils.getString(params, "queryStr", null), "serverIp:")  ;
		String message= StringUtils.substringAfter( MapUtils.getString(params, "queryStr", null), "message:")  ;
		

		ESDatas<ServiceLogDto> esLogList = clientUtil.searchList(String.format("%s/_search", INDEX_NAME),
				LIST_METHOD_NAME,
				ServiceLogDmo.builder().traceId( StringUtils.isNotBlank(traceId)? traceId:null )
						.appName( StringUtils.isNotBlank(appName)? appName:null )
						.serverIp(StringUtils.isNotBlank(serverIp)? serverIp:null )
						.message( StringUtils.isNotBlank(message)? message:null )
						.from(from).size(to).startTime(DateUtil.offset(new Date(), DateField.MONTH, -6)).endTime(new Date()).build(),
				ServiceLogDto.class);
		return PageResult.<ServiceLogDto>builder().data(esLogList.getDatas()).statusCodeValue(0)
				.count(esLogList.getTotalSize()).build();
	}

}
