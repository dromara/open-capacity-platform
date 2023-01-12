package com.open.capacity.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * EntityUtils工具类用于基于Lambda表达式实现类型转换，具有如下优点：
 * 1. 实现对象转对象；集合转集合；分页对象转分页对象
 * 2. 实体类转Vo、实体类转DTO等都能应用此工具类
 * 3. 转换参数均为不可变类型，业务更加安全
 *
 **/
public class EntityUtils {
	
	
	
	/**
     * 将单个对象转化为集合
     *
     * @param e   对象实例
     * @param <E> 对象类型
     * @return 包含对象的集合实例
     */
    public static <E> List<E> toCol(E e) {
        return toCol(e, ArrayList::new);
    }

    /**
     * 将单个对象转化为集合
     *
     * @param t        对象实例
     * @param supplier 集合工厂
     * @param <E>      对象类型
     * @param <C>      集合类型
     * @return 包含对象的集合实例
     */
    public static <E, C extends List<E>> List<E> toCol(E t, Supplier<C> supplier) {
        return Stream.of(t).collect(Collectors.toCollection(supplier));
    }

    /**
     * 取出集合中第一个元素
     *
     * @param collection 集合实例
     * @param <E>        集合中元素类型
     * @return 泛型类型
     */
    public static <E> E toObj(Collection<E> collection) {
        // 处理集合空指针异常
        Collection<E> coll = Optional.ofNullable(collection).orElseGet(ArrayList::new);
        // 此处可以对流进行排序，然后取出第一个元素
        return coll.stream().findFirst().orElse(null);
    }
	
    /**
     * 将对象集合按照一定规则映射后收集为另一种形式的集合
     *
     * @param <R>       最终结果的泛型
     * @param <S>       原始集合元素的类泛型
     * @param <T>       转换后元素的中间状态泛型
     * @param <A>       最终结果收集器泛型
     * @param source    最原始的集合实例
     * @param action    转换规则
     * @param collector 收集器的类型
     * @return 变换后存储新元素的集合实例
     */
    public static <R, S, T, A> R collectList(final Collection<S> source, Function<? super S, ? extends T> action, Collector<? super T, A, R> collector) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(collector);
        return source.stream().map(action).collect(collector);
    }

    /**
     * 将对象集合按照一定规则映射后收集为另一种形式的集合
     *
     * @param <S>    原始集合元素的类泛型
     * @param <T>    转换后元素的中间状态泛型
     * @param source 最原始的集合实例
     * @param action 转换规则
     * @return 变换后存储新元素的集合实例
     */
    public static <S, T> Set<T> collectSet(final Collection<S> source, Function<? super S, ? extends T> action) {
        Objects.requireNonNull(source);
        return source.stream().map(action).collect(Collectors.toSet());
    }

    /**
     * 将对象集合按照一定规则映射后收集为List集合
     *
     * @param <S>    原始集合元素的类泛型
     * @param source 最原始的集合实例
     * @param action 转换规则
     * @return 变换后存储新元素的集合实例
     */
    public static <S> List<? extends S> collectList(final Collection<S> source, Function<? super S, ? extends S> action) {
        return collectList(source, action, Collectors.toList());
    }

    /**
     * 将对象以一种类型转换成另一种类型
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型，如果source为null,则返回null
     */
    public static <T, R> R toObj(final T source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        return Optional.ofNullable(source).map(action).orElse(null);
    }

    /**
     * 将List集合以一种类型转换成另一种类型
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> List<R> toList(final Collection<T> source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return source.stream().map(action).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 将Array数组以一种类型转换成另一种类型
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源Array数组
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> Object[] toArray(final T[] source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return Arrays.stream(source).map(action).toArray();
        }
        return new ArrayList<>().toArray();
    }

    /**
     * 将IPaged对象以一种类型转换成另一种类型
     *
     * @param source 源Page
     * @param action 转换规则
     * @param <E>    源Page类型泛型
     * @param <T>    源实体类
     * @param <R>    目标Page类型泛型
     * @return 变换后的分页类型
     */
    public static <E extends IPage<T>, T, R> IPage<R> toPage(E source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(action);
        return source.convert(action);
    }

    /**
     * 将集合转化成Map
     *
     * @param data      集合实例
     * @param keyAction key转换规则
     * @param <T>       集合实体类泛型
     * @param <K>       Key实体类型泛型
     * @return Map实例
     */
    public static <T, K> Map<K, T> toMap(final Collection<T> data, Function<? super T, ? extends K> keyAction) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(keyAction);
        return data.stream().collect(Collectors.toMap(keyAction, Function.identity()));
    }

    /**
     * 将集合转化成Map
     *
     * @param data        集合实例
     * @param keyAction   key转换规则
     * @param valueAction value转换规则
     * @param <T>         集合实体类泛型
     * @param <K>         Key实体类型泛型
     * @param <V>         Value实体类型泛型
     * @return Map实例
     */
    public static <T, K, V> Map<K, V> toMap(final Collection<T> data, Function<? super T, ? extends K> keyAction, Function<? super T, ? extends V> valueAction) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(keyAction);
        Objects.requireNonNull(valueAction);
        return data.stream().collect(Collectors.toMap(keyAction, valueAction));
    }

    /**
     * 将List集合以一种类型转换成Set集合
     *
     * @param <T>    源数据类型
     * @param <R>    变换后数据类型
     * @param source 源List集合
     * @param action 映射Lmabda表达式
     * @return 变换后的类型集合，如果source为null,则返回空集合
     */
    public static <T, R> Set<R> toSet(final Collection<T> source, final Function<? super T, ? extends R> action) {
        Objects.requireNonNull(action);
        if (Objects.nonNull(source)) {
            return source.stream().map(action).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
    
    
    
    /**
     * <p>对集合中元素按照指定列进行分组</p>
     * <p>返回值是{@code Map}，其中Key为分组列，Value为当前元素</p>
     *
     * @param data    集合实例
     * @param gColumn 分组列（方法引用表示）
     * @param <E>     集合元素泛型
     * @param <R>     分组列数据类型泛型
     * @return {@code Map}实例
     */
    public static <E, R> Map<R, List<E>> groupBy(final Collection<E> data, final Function<E, R> gColumn) {
        Objects.requireNonNull(gColumn);
        if (Objects.nonNull(data)) {
            return data.stream().collect(Collectors.groupingBy(gColumn));
        }
        return new HashMap<>();
    }

    /**
     * <p>对集合中元素按照指定列进行分组</p>
     * <p>返回值是{@code Map}，其中Key为分组列</p>
     *
     * @param data    集合实例
     * @param gColumn 分组列（方法引用表示）
     * @param action  转换行为
     * @param <U>     Value集合元素类型泛型
     * @param <E>     集合元素泛型
     * @param <G>     分组列数据类型泛型
     * @return {@code Map}实例
     */
    public static <E, G, U> Map<G, List<U>> groupBy(final Collection<E> data, final Function<E, G> gColumn, final Function<E, U> action) {
        Objects.requireNonNull(gColumn);
        if (Objects.nonNull(data)) {
            return data.stream().collect(Collectors.groupingBy(gColumn, Collectors.mapping(action, Collectors.toList())));
        }
        return new HashMap<>(16);
    }
    
}