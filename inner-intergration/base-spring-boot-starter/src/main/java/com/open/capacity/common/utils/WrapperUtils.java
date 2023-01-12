package com.open.capacity.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * MybatisPlus数据报表 复杂SQL Lambda表达式构建工具类
 * </p>
 * <pre>
 *     (((industry = ?) OR (industry = ?)) AND ((round = ?) OR (round = ?) OR (round = ?)))
 * </pre>
 * <p>
 * 特点是and左右两端连接 0个、1个、多个以or组成语句复合体，并且支持动态变化
 * </p>
 **/
public class WrapperUtils {
	/**
	 * <p>
	 * and 内连 or，处理形如下面复杂SQL的情形
	 * </p>
	 * 
	 * <pre>
	 * (((industry = ?) OR (industry = ?)) AND ((round = ?) OR (round = ?) OR (round = ?)))
	 * </pre>
	 *
	 * @param wrapper Lambda形式的包装器
	 * @param data    查询条件列数据集合
	 * @param column  数据库匹配列（方法引用指定）
	 * @param <T>     DO实体类泛型
	 * @param <R>     查询列数据泛型
	 */
	public static <T, R> void andInlineOr(final LambdaQueryWrapper<T> wrapper, final List<R> data,
			final SFunction<T, R> column) {
		if (data != null && data.size() > 0) {
			wrapper.and(e -> {
				for (R r : data) {
					e.or(f -> f.eq(column, r));
				}
			});
		}
	}

	/**
	 * <p>
	 * and 内连 or，处理形如下面复杂SQL的情形
	 * </p>
	 * 
	 * <pre>
	 * (((industry = ?) OR (industry = ?)) AND ((round = ?) OR (round = ?) OR (round = ?)))
	 * </pre>
	 *
	 * @param wrapper Lambda形式的包装器
	 * @param data    查询条件列数据集合
	 * @param column  数据库匹配列（方法引用指定）
	 * @param <T>     DO实体类泛型
	 * @param <R>     查询列数据泛型
	 */
	public static <T, R> void andInlineOr(final LambdaQueryWrapper<T> wrapper, final R[] data,
			final SFunction<T, R> column) {
		andInlineOr(wrapper, Arrays.asList(data), column);
	}
}
