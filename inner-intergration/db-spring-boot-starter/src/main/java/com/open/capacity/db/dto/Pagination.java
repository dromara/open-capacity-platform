package com.open.capacity.db.dto;

import lombok.Data;

/**
 * @author woniu
 * @version 1.0.0
 * @description 分页
 * @since 1.0.0
 * from: https://gitee.com/fuqiangma/demo
 * https://www.bilibili.com/video/BV1C14y127Rs/?spm_id_from=333.1007.0.0
 */
@Data
public class Pagination {
    /**
     * 页码
     */
    private Integer page;
    /**
     * 每页大小
     */
    private Integer limit;
}
