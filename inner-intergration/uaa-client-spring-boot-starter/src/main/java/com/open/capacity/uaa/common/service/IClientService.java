package com.open.capacity.uaa.common.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.model.Client;

/**
 * @author someday
 * @version 1.0
 * @date 2018/6/4
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public interface IClientService extends IService<Client> {
	
    ResponseEntity saveClient(Client clientDto) throws Exception;

    /**
     * 查询应用列表
     * @param params
     * @param isPage 是否分页
     */
    PageResult<Client> listClient(Map<String, Object> params, boolean isPage);

    boolean delClient(long id);

    Client loadClientByClientId(String clientId);
}
