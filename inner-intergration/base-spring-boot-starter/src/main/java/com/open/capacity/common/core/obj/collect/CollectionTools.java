/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.obj.collect;


import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectionTools {
    public static <S> String toJoinString(Collection<S> source,Function<S,String> toStrFun,String joinStr){
        if (isEmpty(source)){
            return "";
        }
        return source.stream().map(toStrFun).collect(Collectors.joining(joinStr));
    }
    /**
     * 集合类型转换
     * @param source 源集合
     * @param excluder 对象屏蔽器，excluder执行结构返回true则转存到目标集合中
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,T> List<T> exclude(Collection<S> source, Predicate<S> excluder){
        if (isEmpty(source)){
            return Collections.emptyList();
        }
        return collect(source,excluder, CollectionConstant.PROTOTYPE,Collectors.toList());
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
    public static <S,T> List<T> toList(Collection<S> source, Predicate<S> excluder, Function<S,T> converter){
        if (isEmpty(source)){
            return Collections.emptyList();
        }

        return collect(source,excluder,converter,Collectors.toList());
    }
    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,T> List<T> toList(Collection<S> source, Function<S,T> converter){
        return toList(source, CollectionConstant.NO_EXCLUDER,converter);
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
    public static <S,T> Set<T> toSet(Collection<S> source, Predicate<S> excluder, Function<S,T> converter){
        if (isEmpty(source)){
            return Collections.emptySet();
        }

        return collect(source,excluder,converter,Collectors.toSet());
    }
    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,T> Set<T> toSet(Collection<S> source, Function<S,T> converter){
        return toSet(source, CollectionConstant.NO_EXCLUDER,converter);
    }

    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,T> toMap(Collection<S> source, Function<S,K> keyFun, Function<S, T> converter){
        ToMapContext<S,K,T> context = new ToMapContext<S, K, T>(keyFun,source,converter);
        return toMap(context);
    }



    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,T> toMapByTarget(Collection<S> source, Function<T,K> keyFun, Function<S, T> converter){
        ToMapContext<S,K,T> context = new ToMapContext<S, K, T>(source,converter,keyFun);
        return toMap(context);
    }



    /**
     * 集合类型转换
     * @param context 源集合
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,T> toMap(ToMapContext<S,K,T> context){
        return toMap(context.source,context.converter,context.keyGetter,context.valExcluder,context.keyExcluder);
    }
    /**
     * 集合类型转换
     * @param source 源集合
     * @param converter 对象转换器
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <S,K,T> Map<K,T> toMap(Collection<S> source, Function<S, T> converter, BiFunction<S,T,K> keyFun, BiPredicate<S,T> valExcluder, Predicate<K> keyExcluder){
        if (CollectionTools.isEmpty(source)){
            return Collections.emptyMap();
        }
        Map<K,T> result = new HashMap<>();
        for (S s: source){
            T t = converter.apply(s);
            if (!valExcluder.test(s,t)){
                K key = keyFun.apply(s,t);
                if (!keyExcluder.test(key)){
                    result.put(key,t);
                }
            }
        }
        return result;
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
    private static <S,T,C> C collect(Collection<S> source, Predicate<S> excluder, Function<S,T> converter, Collector<T,?, C> collector){
       return source.stream()
               .filter(e -> !excluder.test(e))
               .map(converter)
               .collect(collector);
    }

    public static <K,V> V getOrPut(Map<K,V> map,K key, Function<K,V> valCreator){
        if (map.containsKey(key)){
            return map.get(key);
        }
        V v = valCreator.apply(key);
        map.put(key,v);
        return v;
    }

    public static <K,V> V getOrPut(Map<K,V> map,K key, Supplier<V> supplier){
        if (map.containsKey(key)){
            return map.get(key);
        }
        V v = supplier.get();
        map.put(key,v);
        return v;
    }


    public static <S> boolean isEmpty(Collection<S> source){
        return Objects.isNull(source) || source.isEmpty();
    }
    public static <K,V> boolean isEmpty(Map<K,V> source){
        return Objects.isNull(source) || source.isEmpty();
    }

    public static <S> boolean notEmpty(Collection<S> source){
        return !isEmpty(source);
    }
    public static <K,V> boolean notEmpty(Map<K,V> source){
        return !isEmpty(source);
    }

    public static class ToMapContext<S, K,T> {
        private Collection<S> source;
        private Function<S,T> converter;
        private BiFunction<S,T,K> keyGetter;
        private BiPredicate<S,T> valExcluder = CollectionConstant.NO_BI_EXCLUDER;
        private Predicate<K> keyExcluder = CollectionConstant.NO_EXCLUDER;

        public ToMapContext( Function<S, K> keyGetter,Collection<S> sources, Function<S, T> converter) {
            this(sources,converter,(e,t) -> keyGetter.apply(e));
        }
        public ToMapContext(Collection<S> sources, Function<S, T> converter, Function<T, K> keyGetter) {
            this(sources,converter,(e,t) -> keyGetter.apply(t));
        }
        public ToMapContext(Collection<S> sources, Function<S, T> converter, BiFunction<S,T, K> keyGetter) {
            this.source = sources;
            this.converter = converter;
            this.keyGetter = keyGetter;
        }

        public ToMapContext setValExcluder(BiPredicate<S,T> valExcluder){
            this.valExcluder = valExcluder;
            return this;
        }

        public ToMapContext setTargetExcluder(Predicate<K> keyExcluder) {
            this.keyExcluder = keyExcluder;
            return this;
        }

    }

}
