/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.obj.collect;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TreeTools {
    private TreeTools(){};

    public static <E, K,T extends TreeNode<K,T>> List<T> toTree(Collection<E> sources,  Function<E,T> converter){
        ToTreeContext<E,K,T> context = new ToTreeNodeContext<>(sources,converter);
        return toTree(context);
    }

    public static <E, K,T> List<T> toTree(Collection<E> sources,  Function<E,T> converter,
                                          BiConsumer<T,List<T>> childrenSetter,
                                          Function<T, K> keyFun, Function<T, K> parentKeyFun){
        ToTreeContext<E,K,T> context = new ToTreeContext<>(sources,converter,childrenSetter,keyFun,parentKeyFun);
        return toTree(context);
    }

    public static <E, K,T> List<T> toTree(Collection<E> sources,
                                          Function<E, K> keyFun, Function<E, K> parentKeyFun,  Function<E,T> converter,
                                          BiConsumer<T,List<T>> childrenSetter){
        ToTreeContext<E,K,T> context = new ToTreeContext<>(sources,keyFun,parentKeyFun,converter,childrenSetter);
        return toTree(context);
    }

    public static <E, K,T> List<T> toTree(Collection<E> sources,  Function<E,T> converter,
                                          BiConsumer<T,List<T>> childrenSetter,
                                          BiFunction<E,T, K> keyFun, BiFunction<E,T, K> parentKeyFun){
        ToTreeContext<E,K,T> context = new ToTreeContext<>(sources,converter,childrenSetter,keyFun,parentKeyFun);
        return toTree(context);
    }

    public  static <E, K,T> List<T> toTree(ToTreeContext<E,K,T> context){
        return toTree(context.sources,context.excluder,context.converter,context.childrenSetter,context.targetExcluder,context.keyFun,context.parentKeyFun,context.sort);
    }

    public static <E, K,T> List<T> toTree(Collection<E> sources, Predicate<E> excluder, Function<E,T> converter,
                                          BiConsumer<T,List<T>> childrenSetter,Predicate<T> tExcluder,
                                          BiFunction<E,T, K> keyFun, BiFunction<E,T, K> parentKeyFun, Comparator<T> sort){
        Map<K,List<T>> childrenGroup = new HashMap<>();
        Map<K, Set<K>> childrenKeyGroup = new HashMap<>();
        Map<K,T> nodeMap = new LinkedHashMap<>();
        Set<K> rootSet = new HashSet<>();

        for (E e: sources){
            if (excluder.test(e)){
                continue;
            }
            T t = converter.apply(e);
            if (tExcluder.test(t)){
                continue;
            }

            K key = keyFun.apply(e,t);
            if (nodeMap.containsKey(key)){
                throw new IllegalStateException("duplicate key fund");
            }
            nodeMap.put(key,t);

            K parentKey = parentKeyFun.apply(e,t);
            List<T> brothers = CollectionTools.getOrPut(childrenGroup,parentKey,() -> new ArrayList<>());
            brothers.add(t);

            if (!nodeMap.containsKey(parentKey)){
                rootSet.add(key);
                Set<K> brotherKeys = CollectionTools.getOrPut(childrenKeyGroup,parentKey,() -> new HashSet<>());
                brotherKeys.add(key);
            }

            if (childrenKeyGroup.containsKey(key)){
                rootSet.removeAll(childrenKeyGroup.get(key));
            }
        }
        List<T> result = new ArrayList<>(rootSet.size());
        for (Map.Entry<K,T> entry : nodeMap.entrySet()){
            K key = entry.getKey();
            T val = entry.getValue();
            if (childrenGroup.containsKey(key)){
                List<T> children = childrenGroup.get(key);
                if (Objects.nonNull(sort)){
                    children.sort(sort);
                }
                childrenSetter.accept(val,children);
            }
            if (rootSet.contains(key)){
                result.add(val);
            }
        }
        if (Objects.nonNull(sort)){
            result.sort(sort);
        }
        return result;
    }

    public static class ToTreeContext<E, K,T> {
        private  Collection<E> sources;
        private Predicate<E> excluder = CollectionConstant.NO_EXCLUDER;
        private Predicate<T> targetExcluder = CollectionConstant.NO_EXCLUDER;
        private Function<E,T> converter;
        private BiConsumer<T,List<T>> childrenSetter;
        private BiFunction<E,T, K> keyFun;
        private BiFunction<E,T, K> parentKeyFun;
        private Comparator<T> sort;

        public ToTreeContext(Collection<E> sources, Function<E, T> converter, BiConsumer<T, List<T>> childrenSetter,
                             BiFunction<E, T, K> keyFun, BiFunction<E, T, K> parentKeyFun) {
            this.sources = sources;
            this.converter = converter;
            this.childrenSetter = childrenSetter;
            this.keyFun = keyFun;
            this.parentKeyFun = parentKeyFun;
        }

        public ToTreeContext(Collection<E> sources,Function<E, K> keyFun, Function<E, K> parentKeyFun, Function<E, T> converter, BiConsumer<T, List<T>> childrenSetter) {
            this(sources,converter,childrenSetter,(e,t) -> keyFun.apply(e),(e,t) -> parentKeyFun.apply(e));
        }
        public ToTreeContext(Collection<E> sources, Function<E, T> converter, BiConsumer<T, List<T>> childrenSetter,
                             Function<T, K> keyFun, Function<T, K> parentKeyFun) {
            this(sources,converter,childrenSetter,(e,t) -> keyFun.apply(t),(e,t) -> parentKeyFun.apply(t));
        }

        public ToTreeContext<E, K,T> setExcluder(Predicate<E> excluder){
            this.excluder = excluder;
            return this;
        }

        public ToTreeContext<E, K,T> setTargetExcluder(Predicate<T> targetExcluder) {
            this.targetExcluder = targetExcluder;
            return this;
        }


        public ToTreeContext<E, K,T>  setSort(Comparator<T> sort) {
            this.sort = sort;
            return this;
        }
    }

    public static class ToTreeNodeContext<E, K,T extends TreeNode<K,T>> extends ToTreeContext<E, K,T>{
        public ToTreeNodeContext(Collection<E> sources, Function<E, T > converter) {
            super(sources,converter,(t,ts) -> t.setChildren(ts),(e,t) -> t.getKey(),(e,t) -> t.getParentKey());
        }
    }
}
