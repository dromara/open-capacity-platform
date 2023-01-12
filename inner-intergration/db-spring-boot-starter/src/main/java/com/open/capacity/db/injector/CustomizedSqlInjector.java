package com.open.capacity.db.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.yulichang.injector.MPJSqlInjector;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 自定义方法SQL注入器
 * @author liuchunqing
 */
@Primary
public class CustomizedSqlInjector extends MPJSqlInjector {
    /**
     * 如果只需增加方法，保留mybatis plus自带方法，
     * 可以先获取super.getMethodList()，再添加add
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass,tableInfo);
        methodList.add(new SaveBatchMethod());
        methodList.add(new SaveOrUpdateBatch());
        methodList.add(new SelectListForUpdate());
        methodList.add(new SelectOneForUpdate());
        return methodList;
    }
}