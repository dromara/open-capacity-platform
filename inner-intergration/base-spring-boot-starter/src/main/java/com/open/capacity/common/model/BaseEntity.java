package com.open.capacity.common.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Getter;
import lombok.Setter;

/**
 * 实体父类
 * @author someday
 */
@Setter
@Getter
public class BaseEntity<T extends Model<?>> extends Model<T> {
    /**
     * 主键ID
     */
    private Long id;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
//    @TableLogic
//    @JsonIgnore
//    @TableField(value = "is_deleted", select = false)
//    private boolean deleted = false;

    
}
