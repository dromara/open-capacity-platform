package com.open.capacity.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.open.capacity.common.model.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author owen
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_log")
public class SysLog extends  BaseEntity<SysLog>{

    private static final long serialVersionUID = 1L;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 租户id
     */
    private String clientId;

    /**
     * 操作信息
     */
    private String operation;


}
