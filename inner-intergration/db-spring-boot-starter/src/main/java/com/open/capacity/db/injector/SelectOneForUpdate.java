package com.open.capacity.db.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author liuchunqing
 * @date 2022/1/5
 * @project IntelliJ IDEA
 * @package com.hushan.iotp.db.mybatisplus.injector
 */
public class SelectOneForUpdate extends AbstractMethod {

    private static final String MAPPER_METHOD = "selectOneForUpdate";
    private static final String SQL_TEMPLATE = "<script>%s SELECT %s FROM %s %s %s for update\n</script>";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, String.format(SQL_TEMPLATE,
                sqlFirst(), sqlSelectColumns(tableInfo, true), tableInfo.getTableName(),
                sqlWhereEntityWrapper(true, tableInfo), sqlComment()), modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, MAPPER_METHOD, sqlSource, tableInfo);
    }
}
