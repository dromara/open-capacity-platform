package com.open.capacity.uaa.service;

import java.util.Map;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.uaa.model.TokenVo;

/**
 * @author someday
 */
public interface ITokensService {
    /**
     * 查询token列表
     * @param params 请求参数
     * @param clientId 应用id
     */
    PageResult<TokenVo> listTokens(Map<String, Object> params, String clientId);
}
