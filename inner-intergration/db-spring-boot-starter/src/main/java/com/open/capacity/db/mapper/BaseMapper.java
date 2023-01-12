package com.open.capacity.db.mapper;

/**
 * mapper 父类，注意这个类不要让 mp 扫描到！！
 * @author zlt
 * @version 1.0
 * @date 2019/8/5
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {
    // 这里可以放一些公共的方法
}
