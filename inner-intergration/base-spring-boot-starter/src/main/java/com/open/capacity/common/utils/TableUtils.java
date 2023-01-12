package com.open.capacity.common.utils;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * 谷歌Table工具类
 * @since 2019/12/01 19:32
 **/
public class TableUtils {

    /**
     * 将指定的实体类转化成谷歌Table
     *
     * @param <T>    源集合元素泛型
     * @param <R>    Table行元素泛型
     * @param <C>    Table列元素泛型
     * @param <V>    Table值元素泛型
     * @param source 源集合实例
     * @param r      行转换规则
     * @param c      列转换规则
     * @param v      值转换规则
     * @return Table实例
     */
    public static <T, R, C, V> Table<R, C, V> createHashTable(Iterable<T> source, Function<? super T, ? extends R> r, Function<? super T, ? extends C> c, Function<? super T, ? extends V> v) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(r);
        Objects.requireNonNull(c);
        Objects.requireNonNull(v);
        Table<R, C, V> table = HashBasedTable.create();
        source.forEach(e -> table.put(EntityUtils.toObj(e, r), EntityUtils.toObj(e, c), EntityUtils.toObj(e, v)));
        return table;
    }

    /**
     * 将指定的实体类转化成谷歌Table
     *
     * @param source 源集合实例
     * @param r      行转换规则
     * @param c      列转换规则
     * @param <T>    源集合元素泛型
     * @param <R>    Table行元素泛型
     * @param <C>    Table列元素泛型
     * @return Table实例
     */
    public static <R, C, T> Table<R, C, T> createHashTable(Iterable<T> source, Function<? super T, ? extends R> r, Function<? super T, ? extends C> c) {
        return createHashTable(source, r, c, e -> e);
    }
}