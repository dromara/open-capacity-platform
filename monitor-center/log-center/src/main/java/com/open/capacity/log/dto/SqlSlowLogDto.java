package com.open.capacity.log.dto;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ELK收集mysql慢查询日志数据，映射es中的mysql-slowlog-*
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SqlSlowLogDto{
    private String id;
    private Date timestamp;
    private String query_str;
    private String user;
    private String clientip;
    private Float query_time;
    private Float lock_time;
    private Long rows_sent;
    private Long rows_examined;
}