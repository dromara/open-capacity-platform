package com.open.capacity.log.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.dmo.NinxLogDmo;
import com.open.capacity.log.dto.NinxLogDto;
import com.open.capacity.log.service.INginxLogService;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

@Service
public class NginxLogServiceImpl implements INginxLogService {

	private static final String INDEX_NAME = "kafka_nginxlogs-*";

	private static final String MAPPER_NAME = "mapper/nginxLog.xml";

	private static final String LIST_METHOD_NAME = "selectList";


	@Override
	public PageResult queryByPage(Map<String, Object> params) {
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil(MAPPER_NAME);
		List<NinxLogDto> sysLogs = Lists.newArrayList();

		Integer from = (MapUtils.getInteger(params, "page",1) - 1) * MapUtils.getInteger(params, "limit",10);

		Integer to = MapUtils.getInteger(params, "limit",10);

		ESDatas<NinxLogDto> esLogList = clientUtil.searchList(String.format("%s/_search", INDEX_NAME),
				LIST_METHOD_NAME,
				NinxLogDmo.builder().lat(MapUtils.getString(params, "lat")).lon(MapUtils.getString(params, "lon"))
						.from(from).size(to).startTime(DateUtil.offset(new Date(), DateField.MONTH, -3)).endTime(new Date()).build(),
						NinxLogDto.class);
		return PageResult.<NinxLogDto>builder().data(esLogList.getDatas()).statusCodeValue(0)
				.count(esLogList.getTotalSize()).build();
	}

}
