package com.open.capacity.common.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

 
/**
 * @author someday
 * @date 2018/1/6
 * spring security 实体类转互相转换工具
 */
public class OrikaUtils {

	/**
	 * 获取默认字段工厂
	 */
	private static final MapperFactory MAPPER_FACTORY;

	/**
	 * 默认字段实例
	 */
	private static final MapperFacade MAPPER_FACADE;

	/**
	 * 默认字段实例集合
	 */
	private static final Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP;

	static {
		// mapNulls 表示 原对象中的null不会拷贝到目标对象
		MAPPER_FACTORY = new DefaultMapperFactory.Builder().mapNulls(false).build();
		MAPPER_FACTORY.getConverterFactory().registerConverter(new DateToString());
		MAPPER_FACTORY.getConverterFactory().registerConverter(new TimestampToString());
		MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();
		CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();
	}

	/**
	 * 映射实体（默认字段） 这种映射就是DTO字段名称和实体对象PO之间字段名称一致
	 * 
	 * @param source  数据（对象）DO对象
	 * @param toClass 映射类对象 DTO对象
	 * @return 映射类对象
	 */
	public static <S, D> D convert(S source, Class<D> toClass) {
		return MAPPER_FACADE.map(source, toClass);
	}

	/**
	 * 映射实体（自定义配置）
	 * 
	 * @param targetClass 映射类对象
	 * @param source      数据（对象）
	 * @param configMap   自定义配置不同字段的对应关系
	 * @param <S>         源对象类型
	 * @param <D>         目标对象类型
	 * @return 映射类对象
	 */
	public static <S, D> D convert(S source, Class<D> targetClass, Map<String, String> configMap) {
		MapperFacade mapperFacade = getMapperFacade(targetClass, source.getClass(), configMap);
		return mapperFacade.map(source, targetClass);
	}

	/**
	 * 将源对象的值拷贝到目标对象中，源对象中的null属性不拷贝到目标对象。
	 * 
	 * @param source 源对象
	 * @param target 目标对象
	 * @param <S>    源对象类型
	 * @param <D>    目标对象类型
	 */
	public static <S, D> void copy(S source, D target) {
		MAPPER_FACADE.map(source, target);
	}

	/**
	 * 映射集合（默认字段） 映射为集合的形式
	 * 
	 * @param targetClass 映射类对象 DTO对象
	 * @param sources     数据（集合） DO对象
	 * @return 映射类对象
	 */
	public static <S, D> List<D> converts(Iterable<S> sources, Class<D> targetClass) {
		return MAPPER_FACADE.mapAsList(sources, targetClass);
	}

	/**
	 * 映射 mybatis 分页字段集合，需要字段一样 映射为集合的形式
	 * 
	 * @param targetClass 映射类对象 DTO对象
	 * @param source      数据（集合） DO对象
	 * @return 映射类对象
	 */
	public static <S, D> Page<D> page(Page<S> source, Class<D> targetClass) {
		Page<D> dPage = convert(source, Page.class);
		dPage.setRecords(converts(source.getRecords(), targetClass));
		return dPage;
	}

	/**
	 * 映射集合（自定义配置）
	 * 
	 * @param targetClass 映射类
	 * @param source      数据（集合）
	 * @param configMap   自定义配置不同字段的对应关系
	 * @return 映射类对象
	 */
	public static <S, D> List<D> converts(Collection<S> source, Class<D> targetClass, Map<String, String> configMap) {
		if (source.iterator().next() == null) {
			return null;
		}
		/*
		 * S t = source.stream().findFirst().orElseThrow(() -> new
		 * WqException(CodeEnum.ERROR)); MapperFacade mapperFacade =
		 * getMapperFacade(targetClass, t.getClass(), configMap);
		 */
		MapperFacade mapperFacade = getMapperFacade(targetClass, source.iterator().next().getClass(), configMap);
		return mapperFacade.mapAsList(source, targetClass);
	}

	/**
	 * 映射集合（自定义配置）
	 * 
	 * @param source      数据（集合）
	 * @param sourceClass 数据源映射类
	 * @param targetClass 目标映射类
	 * @param configMap   自定义配置不同字段的对应关系
	 * @return 映射类对象
	 */
	public static <S, D> List<D> converts(Collection<S> source, Class<S> sourceClass, Class<D> targetClass,
			Map<String, String> configMap) {
		MapperFacade mapperFacade = getMapperFacade(targetClass, sourceClass, configMap);
		return mapperFacade.mapAsList(source, targetClass);
	}

	/**
	 * 获取自定义映射
	 * 
	 * @param sourceClass 映射类
	 * @param targetClass 数据映射类
	 * @param configMap   自定义配置
	 * @return 映射类对象
	 */
	private static <S, D> MapperFacade getMapperFacade(Class<S> sourceClass, Class<D> targetClass,
			Map<String, String> configMap) {
		String mapKey = targetClass.getCanonicalName() + "_" + sourceClass.getCanonicalName();
		MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
		if (Objects.isNull(mapperFacade)) {
			ClassMapBuilder<D, S> classMapBuilder = classMap(sourceClass, targetClass);
			configMap.forEach(classMapBuilder::field);
			classMapBuilder.byDefault().register();
			mapperFacade = MAPPER_FACTORY.getMapperFacade();
			CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
		}
		return mapperFacade;
	}

	/**
	 * 简单复制出新对象列表到数组 通过source.getComponentType() 获得源Class destinationType
	 * 
	 * @param <S>         源对象类型
	 * @param <D>         目标对象类型
	 * @param target      目标对象数组
	 * @param source      源对象数组
	 * @param targetClass 目标类型
	 * @return 目标对象对象数组
	 */
	public static <S, D> D[] convertArray(final S[] source, final D[] target, final Class<D> targetClass) {
		return MAPPER_FACADE.mapAsArray(target, source, targetClass);
	}

	/**
	 *
	 * @param source 源对象
	 * @param target 目标对象
	 * @param <S>    源对象类型
	 * @param <D>    目标对象类型
	 * @return 处理的目标对象
	 */
	public static <S, D> ClassMapBuilder<D, S> classMap(Class<S> source, Class<D> target) {
		return MAPPER_FACTORY.classMap(target, source);
	}

	/**
	 * 预先获取orika转换所需要的Type，避免每次转换.
	 * 
	 * @param <S>     对象类型
	 * @param rawType 要转换的类型
	 * @return 转换后的类型
	 */
	public static <S> Type<S> getType(final Class<S> rawType) {
		return TypeFactory.valueOf(rawType);
	}

	/**
	 * Date 转为 yyyy-MM-dd HH:mm:ss 时间格式
	 */
	static class DateToString extends BidirectionalConverter<Date, String> {

		@Override
		public String convertTo(Date date, Type<String> type, MappingContext mappingContext) {
			// 可以转成你想要的格式 yyyy/MM/dd HH:mm:ss 等等
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(date);
		}

		@SneakyThrows
		@Override
		public Date convertFrom(String s, Type<Date> type, MappingContext mappingContext) {
			/*
			 * SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); return
			 * format.parse(s);
			 */
			return null;
		}
	}

	/**
	 * Timestamp 转为 yyyy-MM-dd HH:mm:ss 时间格式
	 */
	static class TimestampToString extends BidirectionalConverter<Timestamp, String> {

		@Override
		public String convertTo(Timestamp date, Type<String> type, MappingContext mappingContext) {
			// 可以转成你想要的格式 yyyy/MM/dd HH:mm:ss 等等
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(date);
		}

		@SneakyThrows
		@Override
		public Timestamp convertFrom(String s, Type<Timestamp> type, MappingContext mappingContext) {
			return null;
		}
	}

}
