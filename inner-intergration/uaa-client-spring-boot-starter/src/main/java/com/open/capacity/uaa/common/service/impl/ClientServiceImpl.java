package com.open.capacity.uaa.common.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.lock.DistributedLock;
import com.open.capacity.common.model.Client;
import com.open.capacity.redis.repository.RedisRepository;
import com.open.capacity.uaa.common.mapper.ClientMapper;
import com.open.capacity.uaa.common.service.IClientService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @version 1.0
 * @date 2018/6/4
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {

	@Autowired
	private RedisRepository redisRepository;

	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;

	@Autowired
	private DistributedLock lock;

	@Override
	public ResponseEntity saveClient(Client client) throws Exception {
		client.setClientSecret(passwordEncoder.encode(client.getClientSecretStr()));
		String clientId = client.getClientId();
		this.saveOrUpdate(client);
		return ResponseEntity.succeed("操作成功");
	}

	@Override
	public PageResult<Client> listClient(Map<String, Object> params, boolean isPage) {
		Page<Client> page;
		if (isPage) {
			page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
		} else {
			page = new Page<>(1, -1);
		}
		List<Client> list = baseMapper.findList(page, params);
		page.setRecords(list);
		return PageResult.<Client>builder().data(list).statusCodeValue(0).count(page.getTotal()).build();
	}

	@Override
	public boolean delClient(long id) {
		
		boolean flag = false ;
		String clientId = baseMapper.selectById(id).getClientId();
		redisRepository.del(clientRedisKey(clientId));
		return baseMapper.deleteById(id) > 0 ? true : false ;
	}

	@Override
	@Cacheable(value = "auth", key = "#clientId", unless = "#result == null")
	public Client loadClientByClientId(String clientId) {
		return this.getOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientId, clientId));

	}

	private String clientRedisKey(String clientId) {
		return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
	}
}
