package com.open.capacity.db.interceptor;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.logging.jdbc.PreparedStatementLogger;
import org.apache.ibatis.logging.jdbc.StatementLogger;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.statement.ShardingPreparedStatement;

import com.open.capacity.log.enums.LogEnums;
import com.open.capacity.log.model.Log;
import com.open.capacity.log.trace.MDCTraceUtils;
import com.open.capacity.log.util.BizLogUtil;

import cn.hutool.core.util.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author someday 自定义监控查询数据数量拦截器
 * @date 2019/5/6
 */
@Intercepts({ @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBigResultInterceptor implements Interceptor {
	private Boolean flag;
	private Integer threshold;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		Object result = invocation.proceed();
		String sql = "";
		try {

			if (flag) {
				Statement statement = (Statement) invocation.getArgs()[0];
				// mybatis设置了logging时启动的代理机制
				if (Proxy.isProxyClass(statement.getClass())) {
					MetaObject metaObject = SystemMetaObject.forObject(statement);
					Object h = metaObject.getValue("h");
					if (h instanceof StatementLogger) {
						RoutingStatementHandler rsh = (RoutingStatementHandler) invocation.getTarget();
						sql = rsh.getBoundSql().getSql();
					} else if (h instanceof PreparedStatementLogger) {
						PreparedStatementLogger psl = (PreparedStatementLogger) h;
						sql = (String) ReflectUtil.getFieldValue(psl.getPreparedStatement(), "sql");
					}
				} else {
					if (statement instanceof ShardingPreparedStatement) {
						sql = (String) ReflectUtil.getFieldValue(statement, "sql");
					} else {
						sql = statement.toString();
					}
				}
				if (result instanceof List && ((List) result).size() >= threshold) {
					if (log.isWarnEnabled()) {
						BizLogUtil.info(LogEnums.BIGRESULT_LOG.getTag(), LogEnums.BIGRESULT_LOG.getName(), Log.builder().objectId(LogEnums.BIGRESULT_LOG.getId())
								.traceId(MDCTraceUtils.getTraceId()).spanId(MDCTraceUtils.getSpanId()).sql(sql)
								.msg(String.format("数据库查询结果集[%s]条，存在内存泄露风险，请使用分页查询。", ((List) result).size()))
								.build());
					}
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * @param target
	 * @return
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof ResultSetHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

}
