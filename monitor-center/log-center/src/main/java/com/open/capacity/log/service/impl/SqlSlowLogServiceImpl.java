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
import com.open.capacity.log.dmo.SqlSlowLogDmo;
import com.open.capacity.log.dto.SqlSlowLogDto;
import com.open.capacity.log.service.ISqlSlowLogService;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

@Service
public class SqlSlowLogServiceImpl implements ISqlSlowLogService {

	private static final String INDEX_NAME = "mysql-slowlog-*";

	private static final String MAPPER_NAME = "mapper/sqlSlowLog.xml";

	private static final String LIST_METHOD_NAME = "selectList";

	@Override
	public PageResult queryByPage(Map<String, Object> params) {
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil(MAPPER_NAME);
		List<SqlSlowLogDto> sysLogs = Lists.newArrayList();

		Integer from = (MapUtils.getInteger(params, "page",1) - 1) * MapUtils.getInteger(params, "limit",10);

		Integer to = MapUtils.getInteger(params, "limit",10);

		ESDatas<SqlSlowLogDto> esLogList = clientUtil
				.searchList(String.format("%s/_search", INDEX_NAME), LIST_METHOD_NAME,
						SqlSlowLogDmo.builder().query_str(MapUtils.getString(params, "query_str")).from(from).size(to)
								.startTime(DateUtil.offset(new Date(), DateField.MONTH, -1)).endTime(new Date()).build(),
						SqlSlowLogDto.class);
		return PageResult.<SqlSlowLogDto>builder().data(esLogList.getDatas()).statusCodeValue(0)
				.count(esLogList.getTotalSize()).build();
	}

}
