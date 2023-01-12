package com.open.capacity.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实体类List批量字段填充工具
 *
 * @author liuchunqing
 */
public class BeanDetailFillHelper {

    /**
     * @param mainList                  被填充数据列表
     * @param conditionFieldGetFunction 关联查询填充信息的字段get方法引用
     * @param targetFieldSetFunction    填充字段的set方法引用
     * @param detailList                明细列表
     * @param serviceRelevanceMethod    明细实体中与主类关联字段的get方法引用
     * @param <MAIN>                    主列表对象类型
     * @param <R>                       关联查询填充信息的字段类型
     */
    public static <MAIN, R, DETAIL> void fillListFieldByMainId(List<MAIN> mainList,
                                                               SFunction<MAIN, R> conditionFieldGetFunction,
                                                               BiConsumer<MAIN, List<DETAIL>> targetFieldSetFunction,
                                                               List<DETAIL> detailList,
                                                               SFunction<DETAIL, R> serviceRelevanceMethod) {
        mainList.forEach(mainEntity -> {
            R mainId = conditionFieldGetFunction.apply(mainEntity);
            Map<R, List<DETAIL>> detailsGroupByMainId = detailList.stream().collect(Collectors.groupingBy(serviceRelevanceMethod));
            targetFieldSetFunction.accept(mainEntity, detailsGroupByMainId.get(mainId));
        });
    }

    /**
     * @param mainList                  被填充数据列表
     * @param conditionFieldGetFunction 关联查询填充信息的字段get方法引用
     * @param targetFieldSetFunction    填充字段的set方法引用
     * @param detailList                明细列表
     * @param serviceRelevanceMethod    明细实体中与主类关联字段的get方法引用
     * @param <MAIN>                    主列表对象类型
     * @param <R>                       关联查询填充信息的字段类型
     */
    public static <MAIN, R, DETAIL, V> void fillFieldByMainId(List<MAIN> mainList,
                                                              SFunction<MAIN, R> conditionFieldGetFunction,
                                                              BiConsumer<MAIN, V> targetFieldSetFunction,
                                                              List<DETAIL> detailList,
                                                              SFunction<DETAIL, R> serviceRelevanceMethod,
                                                              SFunction<DETAIL, V> getFieldMethod) {
        mainList.forEach(mainEntity -> {
            R mainId = conditionFieldGetFunction.apply(mainEntity);
            Map<R, DETAIL> detailsGroupByMainId = detailList.stream().collect(Collectors.toMap(serviceRelevanceMethod, Function.identity()));
            DETAIL detail = detailsGroupByMainId.get(mainId);
            if (ObjectUtil.isNotNull(detail)) {
                targetFieldSetFunction.accept(mainEntity, getFieldMethod.apply(detail));
            }
        });
    }


    /**
     * @param mainList                  被填充数据列表
     * @param conditionFieldGetFunction 关联查询填充信息的字段get方法引用
     * @param targetFieldSetFunction    填充字段的set方法引用
     * @param service                   根据conditionField获取填充信息的service
     * @param serviceRelevanceMethod    service获取填充信息的方法引用
     * @param <MAIN>                    主列表对象类型
     * @param <R>                       关联查询填充信息的字段类型
     * @param <SERVICE>                 用来查填充信息的service
     * @param <T>                       填充信息对象类型
     */
    public static <MAIN, R, SERVICE, T> void fillDetail(List<MAIN> mainList, SFunction<MAIN, R> conditionFieldGetFunction, BiConsumer<MAIN, T> targetFieldSetFunction,
                                                        SERVICE service, BiFunction<SERVICE, List<R>, Map<R, T>> serviceRelevanceMethod) {
        if (CollUtil.isEmpty(mainList)) {
            return;
        }
        List<R> detailIdSet = mainList.stream().map(conditionFieldGetFunction).collect(Collectors.toList());
        Map<R, T> rtMap = serviceRelevanceMethod.apply(service, detailIdSet);
        mainList.forEach(e -> {
            R relevanceKey = conditionFieldGetFunction.apply(e);
            if (!StringUtils.isEmpty(relevanceKey)) {
                T t = rtMap.get(relevanceKey);
                if (StringUtils.isEmpty(t)) {
                    return;
                }
                targetFieldSetFunction.accept(e, t);
            }
        });
    }

    /**
     * @param mainList                   被填充数据列表
     * @param conditionFieldGetFunctions 关联查询填充信息的字段get方法引用
     * @param targetFieldSetFunctions    关联查询填充信息的字段set方法引用
     * @param service                    根据conditionField获取填充信息的service
     * @param serviceRelevanceMethod     service获取填充信息的方法引用
     * @param <MAIN>                     主列表对象类型
     * @param <R>                        关联查询填充信息的字段类型
     * @param <SERVICE>                  用来查填充信息的service
     * @param <T>                        填充信息对象类型
     */
    public static <MAIN, R, SERVICE, T> void batchFillDetail(
            List<MAIN> mainList, List<SFunction<MAIN, R>> conditionFieldGetFunctions, List<BiConsumer<MAIN, T>> targetFieldSetFunctions,
            SERVICE service, BiFunction<SERVICE, List<R>, Map<R, T>> serviceRelevanceMethod) {
        List<R> detailIdSet = new LinkedList<>();
        for (SFunction<MAIN, R> conditionFieldGetFunction : conditionFieldGetFunctions) {
            detailIdSet.addAll(mainList.stream().map(conditionFieldGetFunction).collect(Collectors.toList()));
        }
        Map<R, T> rtMap = serviceRelevanceMethod.apply(service, detailIdSet);
        mainList.forEach(e -> {
            for (int i = 0; i < conditionFieldGetFunctions.size(); i++) {
                SFunction<MAIN, R> conditionFieldGetFunction = conditionFieldGetFunctions.get(i);
                R relevanceKey = conditionFieldGetFunction.apply(e);
                if (!StringUtils.isEmpty(relevanceKey)) {
                    T t = rtMap.get(relevanceKey);
                    if (StringUtils.isEmpty(t)) {
                        return;
                    }
                    BiConsumer<MAIN, T> targetFieldSetFunction = targetFieldSetFunctions.get(i);
                    targetFieldSetFunction.accept(e, t);
                }
            }
        });
    }
}
