package com.open.capacity.db.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.open.capacity.log.enums.LogEnums;
import com.open.capacity.log.model.Log;
import com.open.capacity.log.trace.MDCTraceUtils;
import com.open.capacity.log.util.BizLogUtil;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 慢sql日志
 * 
 * @author someday
 * @date 2019/5/6
 */

@Intercepts({
		@Signature(method = "query", type = Executor.class, args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CustomSlowQueryInterceptor implements Interceptor {
	private Boolean flag;
	private long slowSqlThresholdMs;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		long sqlStartTime = System.currentTimeMillis();
		Object re = invocation.proceed();
		long sqlEndTime = System.currentTimeMillis();
		long executeTime = sqlEndTime - sqlStartTime;
		if (flag) {
			if (executeTime >= slowSqlThresholdMs) {
				// 获取执行方法的MappedStatement参数,不管是Executor的query方法还是update方法，第一个参数都是MappedStatement
				MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
				Object parameter = null;
				if (invocation.getArgs().length > 1) {
					parameter = invocation.getArgs()[1];
				}
				String sqlId = mappedStatement.getId();
				BoundSql boundSql = mappedStatement.getBoundSql(parameter);
				Configuration configuration = mappedStatement.getConfiguration();
				// 打印mysql执行语句
				String sql = assembleSql(configuration, boundSql);
				if (log.isWarnEnabled()) {
					BizLogUtil.info(LogEnums.SLOWRESULT_LOG.getTag(), LogEnums.SLOWRESULT_LOG.getName(),
							Log.builder().objectId(LogEnums.SLOWRESULT_LOG.getId())
									.traceId(MDCTraceUtils.getTraceId()).spanId(MDCTraceUtils.getSpanId()).sql(sql)
									.msg(String.format("SQL执行耗时:[%s],存在慢SQL风险,执行方法:[%s]]", executeTime + " ms", sqlId))
									.build());

				}
			}
		}
		return re;
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	private static String assembleSql(Configuration configuration, BoundSql boundSql) {
		// 获取mapper里面方法上的参数
		Object sqlParameter = boundSql.getParameterObject();
		// sql语句里面需要的参数 -- 真实需要用到的参数 因为sqlParameter里面的每个参数不一定都会用到
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		// sql原始语句(?还没有替换成我们具体的参数)
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if (parameterMappings.size() > 0 && sqlParameter != null) {
			// sql语句里面的?替换成真实的参数
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(sqlParameter.getClass())) {
				sql = sql.replaceFirst("\\?", getParameterValue(sqlParameter));
			} else {
				MetaObject metaObject = configuration.newMetaObject(sqlParameter);
				for (ParameterMapping parameterMapping : parameterMappings) {
					// 一个一个把对应的值替换进去 按顺序把?替换成对应的值
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					}
				}
			}
		}
		return sql;
	}

	private static String getParameterValue(Object obj) {
		String value;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + formatter.format(new Date()) + "'";
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "";
			}

		}
		// 对特殊字符进行转义，方便之后处理替换
		return value != null ? makeQueryStringAllRegExp(value) : null;
	}

	private static String makeQueryStringAllRegExp(String str) {
		if (str != null && !str.equals("")) {
			return str.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("|", "\\|")
					.replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^")
					.replace("$", "\\$").replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,")
					.replace(".", "\\.").replace("&", "\\&");
		}
		return str;
	}
}
