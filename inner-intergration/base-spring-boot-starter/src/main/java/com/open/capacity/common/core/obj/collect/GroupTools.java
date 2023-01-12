/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.obj.collect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class GroupTools {
    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,List<T>> groupByTarget(Collection<S> source,  Function<S,T> converter,Function<T,K> keyGetter){
        ToGroupContext<S, K,T> context = new ToGroupContext<>(source,converter,keyGetter);
        return group(context);
    }


    /**
     * 集合类型转换
     * @param source 源集合
     * @param <S> 源对象类型
     * @return
     */
    public static <S,K> Map<K,List<S>> group(Collection<S> source, Function<S,K> keyGetter){
        ToGroupContext<S, K,S> context = new ToGroupContext<>(keyGetter,source,Function.identity());
        return group(context);
    }


    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,List<T>> group(Collection<S> source,  Function<S,T> converter,Function<S,K> keyGetter){
        ToGroupContext<S, K,T> context = new ToGroupContext<>(keyGetter,source,converter);
        return group(context);
    }

    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,List<T>> group(Collection<S> source,  Function<S,T> converter,BiFunction<S,T,K> keyGetter){
        ToGroupContext<S, K,T> context = new ToGroupContext<>(source,converter,keyGetter);
        return group(context);
    }

    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,List<T>> group(ToGroupContext<S, K,T> context){
        return group(context.source,context.excluder,context.converter,context.keyGetter,context.targetExcluder,context.sort);
    }

    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    private static <S,K,T> Map<K,List<T>> group(Collection<S> source, Predicate<S> excluder, Function<S,T> converter,BiFunction<S,T,K> keyGetter, Predicate<T> tExcluder,Comparator<T> sort){
        if (CollectionTools.isEmpty(source)){
            return Collections.emptyMap();
        }
        Map<K,List<T>> result = new HashMap<>();
        for (S s: source){
            if (!excluder.test(s)){
                T t = converter.apply(s);
                if (!tExcluder.test(t)){
                    K key = keyGetter.apply(s,t);
                    List<T> ts = CollectionTools.getOrPut(result,key,() -> new ArrayList<>());
                    ts.add(t);
                }
            }
        }
        if (Objects.nonNull(sort)){
            for (List<T> val : result.values()){
                val.sort(sort);
            }
        }
        return result;
    }
    public static class ToGroupContext<S, K,T> {
        private Collection<S> source;
        private Function<S,T> converter;
        private BiFunction<S,T,K> keyGetter;
        private Comparator<T> sort;
        private Predicate<S> excluder = CollectionConstant.NO_EXCLUDER;
        private Predicate<T> targetExcluder = CollectionConstant.NO_EXCLUDER;

        public ToGroupContext( Function<S, K> keyGetter,Collection<S> sources, Function<S, T> converter) {
            this(sources,converter,(e,t) -> keyGetter.apply(e));
        }
        public ToGroupContext(Collection<S> sources, Function<S, T> converter, Function<T, K> keyGetter) {
            this(sources,converter,(e,t) -> keyGetter.apply(t));
        }
        public ToGroupContext(Collection<S> sources, Function<S, T> converter, BiFunction<S,T, K> keyGetter) {
            this.source = sources;
            this.converter = converter;
            this.keyGetter = keyGetter;
        }

        public ToGroupContext<S, K,T> setExcluder(Predicate<S> excluder){
            this.excluder = excluder;
            return this;
        }

        public ToGroupContext<S, K,T> setTargetExcluder(Predicate<T> targetExcluder) {
            this.targetExcluder = targetExcluder;
            return this;
        }


        public ToGroupContext<S, K,T> setSort(Comparator<T> sort) {
            this.sort = sort;
            return this;
        }
    }
}
