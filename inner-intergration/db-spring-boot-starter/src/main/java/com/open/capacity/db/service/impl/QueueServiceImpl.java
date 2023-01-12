package com.open.capacity.db.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.utils.EntityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于MybatisPlus请求合并服务类
 */
@Slf4j
public class QueueServiceImpl<M extends BaseMapper<T>, T extends Model<T>> extends ServiceImpl<M, T> {
	/**
	 * 并发安全队列，多个线程同时添加数据时保证线程安全
	 */
	private final ConcurrentLinkedQueue<FutureModel<T>> taskQueue = new ConcurrentLinkedQueue<>();

	public QueueServiceImpl() {
	}

	/**
	 * 从队列中取出指定数量的元素，返回到集合中
	 *
	 * @param queue 队列实例
	 * @param size  指定数量
	 * @param <T>   泛型
	 * @return 集合实例
	 */
	private static <T> List<T> extractElement(Queue<T> queue, int size) {
		Objects.requireNonNull(queue);
		List<T> lists = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			lists.add(queue.poll());
		}
		return lists;
	}

	/**
	 * 不推荐重载此方法 推荐重载createRequstConfig方法
	 */
	@PostConstruct
	public void init() {
		RequstConfig config = createRequstConfig();
		Runnable runnable = getRunnable(config.getMaxRequestSize());
		BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("scheduled-thread-pool-%d")
				.daemon(true).build();
		ScheduledExecutorService service = new ScheduledThreadPoolExecutor(config.getCorePoolSize(), threadFactory);
		service.scheduleAtFixedRate(runnable, 0L, config.getRequestInterval(), TimeUnit.MILLISECONDS);
	}

	/**
	 * 父类调用 子类重载修改参数行为
	 *
	 * @return RequstConfig
	 */
	protected RequstConfig createRequstConfig() {
		RequstConfig config = new RequstConfig();
		/* 单次最大合并请求数量 */
		config.setMaxRequestSize(100);
		/* 核心线程池大小 */
		config.setCorePoolSize(1);
		/* 请求间隔（毫秒） */
		config.setRequestInterval(200);
		return config;
	}

	/**
	 * 构建任务实例
	 *
	 * @param maxRequestSize 单次最大合并请求数量
	 * @return 任务实例
	 */
	private Runnable getRunnable(int maxRequestSize) {
		return () -> {
			int size = Math.min(taskQueue.size(), maxRequestSize);
			if (size != 0) {
				List<FutureModel<T>> requests = extractElement(taskQueue, size);
				Set<Serializable> ids = EntityUtils.toSet(requests, FutureModel::getId);
				List<T> list = super.listByIds(ids);
				Map<Serializable, T> map = EntityUtils.toMap(list, Model::pkVal, e -> e);
				requests.forEach((e) -> e.getFuture().complete(map.get(e.getId())));
			}
		};
	}

	/**
	 * 通过主键查询实体
	 *
	 * @param id 主键ID
	 * @return 实体
	 */
	@Override
	public T getById(Serializable id) {
		CompletableFuture<T> future = new CompletableFuture<>();
		taskQueue.add(new FutureModel<>(id, future));
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException exception) {
			log.error("查询异常：{}", exception.getMessage());
			return null;
		}
	}

	/**
	 * 包装实体Future类
	 *
	 * @param <T> 实体
	 */
	private static class FutureModel<T> {
		private Serializable id;
		private CompletableFuture<T> future;

		public FutureModel(Serializable id, CompletableFuture<T> future) {
			this.id = id;
			this.future = future;
		}

		public Serializable getId() {
			return this.id;
		}

		public void setId(Serializable id) {
			this.id = id;
		}

		public CompletableFuture<T> getFuture() {
			return this.future;
		}

		public void setFuture(CompletableFuture<T> future) {
			this.future = future;
		}
	}

	/**
	 * 动态配置类
	 */
	public static class RequstConfig {
		/* 单次合并最大请求大小 */
		private Integer maxRequestSize;
		/* 线程工厂核心线程数 */
		private Integer corePoolSize;
		/* 合并请求间隔（单位毫秒） */
		private Integer requestInterval;

		public Integer getMaxRequestSize() {
			return maxRequestSize;
		}

		public void setMaxRequestSize(Integer maxRequestSize) {
			this.maxRequestSize = maxRequestSize;
		}

		public Integer getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(Integer corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public Integer getRequestInterval() {
			return requestInterval;
		}

		public void setRequestInterval(Integer requestInterval) {
			this.requestInterval = requestInterval;
		}
	}
}