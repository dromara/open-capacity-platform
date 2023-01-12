/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Configuration
public class NacosConfig {

	@Autowired
	private NacosProperties nacosProperties ;

	/**
	 * 流控规则转换器
	 **/
	@Bean
	public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
		return s -> JSON.parseArray(s, FlowRuleEntity.class);
	}

	/**
	 * 降级规则转换器
	 **/
	@Bean
	public Converter<List<DegradeRuleEntity>, String> degradeRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<DegradeRuleEntity>> degradeRuleEntityDecoder() {
		return s -> JSON.parseArray(s, DegradeRuleEntity.class);
	}

	/**
	 * 系统规则转换器
	 **/
	@Bean
	public Converter<List<SystemRuleEntity>, String> systemRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<SystemRuleEntity>> systemRuleEntityDecoder() {
		return s -> JSON.parseArray(s, SystemRuleEntity.class);
	}

	/**
	 * 授权规则转换器
	 **/
	@Bean
	public Converter<List<AuthorityRuleEntity>, String> authorityRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<AuthorityRuleEntity>> authorityRuleEntityDecoder() {
		return s -> JSON.parseArray(s, AuthorityRuleEntity.class);
	}

	/**
	 * 热点参数规则转换器
	 **/
	@Bean
	public Converter<List<ParamFlowRuleEntity>, String> paramRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<ParamFlowRuleEntity>> paramRuleEntityDecoder() {
		return s -> JSON.parseArray(s, ParamFlowRuleEntity.class);
	}

	/**
	 * 网关API规则转换器
	 **/
	@Bean
	public Converter<List<ApiDefinitionEntity>, String> apiDefinitionEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<ApiDefinitionEntity>> apiDefinitionEntityDecoder() {
		return s -> JSON.parseArray(s, ApiDefinitionEntity.class);
	}

	/**
	 * 网关flow规则转换器
	 **/
	@Bean
	public Converter<List<GatewayFlowRuleEntity>, String> gatewayFlowRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	@Bean
	public Converter<String, List<GatewayFlowRuleEntity>> gatewayFlowRuleEntityDecoder() {
		return s -> JSON.parseArray(s, GatewayFlowRuleEntity.class);
	}

	@Bean
	public ConfigService nacosConfigService() throws Exception {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, nacosProperties.getAddress());

		if (!StringUtils.isEmpty(nacosProperties.getNamespace())) {
			properties.put(PropertyKeyConst.NAMESPACE, nacosProperties.getNamespace());
		}

		return ConfigFactory.createConfigService(properties);
	}
}
