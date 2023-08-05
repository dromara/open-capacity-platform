package com.open.capacity.uaa.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.uaa.common.model.DefaultClientDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 类说明
 * 将oauth_client_details表数据缓存到redis，这里做个缓存优化
 * layui模块中有对oauth_client_details的crud， 注意同步redis的数据
 * 注意对oauth_client_details清楚redis db部分数据的清空
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class RedisClientDetailsService extends JdbcClientDetailsService {

	private static final String SELECT_CLIENT_DETAILS_SQL = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, "
			+ "web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove ,if_limit, limit_count ,id "
			+ "from oauth_client_details where client_id = ?   ";
	// 扩展 默认的 ClientDetailsService, 增加逻辑删除判断( status = 1)
	private static final String SELECT_FIND_STATEMENT = "select client_id, client_secret,resource_ids, scope, "
			+ "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
			+ "refresh_token_validity, additional_information, autoapprove ,if_limit, limit_count ,id  from oauth_client_details where 1 = 1 order by client_id ";

	private RedisTemplate<String, Object> redisTemplate;

	private final JdbcTemplate jdbcTemplate;

	public RedisClientDetailsService(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
		super(dataSource);
		this.redisTemplate = redisTemplate;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setSelectClientDetailsSql(SELECT_CLIENT_DETAILS_SQL);
		setFindClientDetailsSql(SELECT_FIND_STATEMENT);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		// 先从redis获取
		ClientDetails clientDetails = (ClientDetails) redisTemplate.opsForValue().get(clientRedisKey(clientId));
		if (clientDetails == null) {
			clientDetails = cacheAndGetClient(clientId);
		}
		return clientDetails;
	}

	/**
	 * 缓存client并返回client
	 * @param clientId
	 * @return
	 */
	private ClientDetails cacheAndGetClient(String clientId) {
		// 从数据库读取
		ClientDetails clientDetails = null;
		try {
			clientDetails =   jdbcTemplate.queryForObject(SELECT_CLIENT_DETAILS_SQL, new ClientDetailsRowMapper(), clientId);
			if (clientDetails != null) {
				// 写入redis缓存
				redisTemplate.opsForValue().set(clientRedisKey(clientId), clientDetails);
				log.info("缓存clientId:{},{}", clientId, clientDetails);
			}
		} catch (EmptyResultDataAccessException e) {
			log.error("clientId:{},{}", clientId, clientId);
			throw new AuthenticationException("应用不存在") {
			};
		} catch (NoSuchClientException e) {
			log.error("clientId:{},{}", clientId, clientId);
			throw new AuthenticationException("应用不存在") {
			};
		} catch (InvalidClientException e) {
			throw new AuthenticationException("应用状态不合法") {
			};
		}
		return clientDetails;
	}

	/**
	 * 追加if_limit limit_count ClientDetails
	 * 
	 */
	public List<ClientDetails> listClientDetails() {

		return jdbcTemplate.query(SELECT_FIND_STATEMENT, new ClientDetailsRowMapper());
	}

	private static class ClientDetailsRowMapper implements RowMapper<ClientDetails> {
		private com.open.capacity.uaa.json.JsonMapper mapper = createJsonMapper();

		public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
			DefaultClientDetails details = new DefaultClientDetails(rs.getString(1), rs.getString(3), rs.getString(4),
					rs.getString(5), rs.getString(7), rs.getString(6));
			details.setClientSecret(rs.getString(2));
			if (rs.getObject(8) != null) {
				details.setAccessTokenValiditySeconds(rs.getInt(8));
			}
			if (rs.getObject(9) != null) {
				details.setRefreshTokenValiditySeconds(rs.getInt(9));
			}
			String json = rs.getString(10);
			if (json != null) {
				try {
					Map<String, Object> additionalInformation = mapper.read(json, Map.class);
					details.setAdditionalInformation(additionalInformation);
				} catch (Exception e) {
					log.warn("Could not decode JSON for additional information: " + details, e);
				}
			}
			String scopes = rs.getString(11);
			int ifLimit = rs.getInt(12);
			details.setIfLimit(ifLimit);
			long limitCount = rs.getLong(13);
			details.setLimitCount(limitCount);
			details.setId(rs.getLong(14));
			if (scopes != null) {
				details.setAutoApproveScopes(org.springframework.util.StringUtils.commaDelimitedListToSet(scopes));
			}
			return details;
		}
	}

	/**
	 * json process
	 * 
	 * @return
	 */
	private static com.open.capacity.uaa.json.JsonMapper createJsonMapper() {

		if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
			return new com.open.capacity.uaa.json.JacksonMapper();
		}
		return new com.open.capacity.uaa.json.NotSupportedJsonMapper();
	}

	@Override
	public void updateClientDetails(ClientDetails clientDetails) {
		super.updateClientDetails(clientDetails);
		cacheAndGetClient(clientDetails.getClientId());
	}

	@Override
	public void updateClientSecret(String clientId, String secret) {
		super.updateClientSecret(clientId, secret);
		cacheAndGetClient(clientId);
	}

	@Override
	public void removeClientDetails(String clientId) {
		super.removeClientDetails(clientId);
		removeRedisCache(clientId);
	}

	/**
	 * 删除redis缓存
	 *
	 * @param clientId
	 */
	private void removeRedisCache(String clientId) {
		redisTemplate.delete(clientRedisKey(clientId));
	}

	/**
	 * 将oauth_client_details全表刷入redis
	 */
	public void loadAllClientToCache() {
		
		
		
		List<ClientDetails> list = this.listClientDetails();
		if (CollectionUtils.isEmpty(list)) {
			log.error("oauth_client_details表数据为空，请检查");
			return;
		}

		list.forEach(client -> redisTemplate.opsForValue().set(clientRedisKey(client.getClientId()), client));
	}

	private String clientRedisKey(String clientId) {
		return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
	}
}
