package com.open.capacity.log.service;

import com.open.capacity.log.model.Audit;

/**
 * 审计日志接口
 *
 * @author zlt
 * @date 2020/2/3
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public interface IAuditService {
    void save(Audit audit);
}
