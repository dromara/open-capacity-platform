package com.open.capacity.gateway.route;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.zalando.problem.Status;

import com.open.capacity.common.dto.ResponseEntity;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * nacos路由数据源
 * @author someday
 * @date 2018/10/7
 */
@Slf4j
public class DynamicRouteDefinitionRepository  implements ApplicationEventPublisherAware {
	
	private RouteDefinitionWriter routeDefinitionWriter ; //路由数据的写入
	
	private ApplicationEventPublisher applicationEventPublisher ;
	
	
	
	public DynamicRouteDefinitionRepository(RouteDefinitionWriter routeDefinitionWriter,
			ApplicationEventPublisher applicationEventPublisher) {
		super();
		this.routeDefinitionWriter = routeDefinitionWriter;
		this.applicationEventPublisher = applicationEventPublisher;
	}
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher ;
	}
	/**
	 * 追加新的路由
	 * @param routeDefinition
	 * @return
	 */
	public Mono<ResponseEntity<Object>> add(RouteDefinition routeDefinition) {
		//日志输出
		log.info("增加路由配置项，新的路由Id为：{}" , routeDefinition.getId());
		try {
			//配置写入
			this.routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe() ;
			//发布路由事件
			this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
		} catch (Exception e) {
			log.error("路由新增失败，新的路由Id为：{}" , routeDefinition.getId());
			return Mono.just(ResponseEntity.of(Status.NOT_FOUND.getStatusCode(), "路由新增失败", null));
		}
		return Mono.just(ResponseEntity.succeed("增加路由成功")) ;
	}
	/**
	 * 删除路由
	 * @param routeDefinition
	 * @return
	 */
	public Mono<ResponseEntity<Object>> delete(RouteDefinition routeDefinition) {
		log.info("删除路由配置项，新的路由Id为：{}" , routeDefinition.getId());
		return this.routeDefinitionWriter.delete(Mono.just(routeDefinition.getId()))
				.then(Mono.defer(() -> Mono.just(ResponseEntity.succeed("删除路由成功"))))
				.onErrorResume(t -> t instanceof NotFoundException, t -> Mono.just(ResponseEntity.of(Status.NOT_FOUND.getStatusCode(), "删除路由失败", null))); 
	}
	/**
	 * 更新路由
	 * @param routeDefinition
	 * @return
	 */
    public Mono<ResponseEntity<Object>> update(RouteDefinition routeDefinition) {
    	
    	log.info("修改路由配置项，新的路由Id为：{}" , routeDefinition.getId());
    	try {
    		
    		this.delete(routeDefinition);
			//配置写入
			this.routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe() ;
			//发布路由事件
			this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
		} catch (Exception e) {
			log.error("修改路由配置项，新的路由Id为：{}" , routeDefinition.getId());
			return Mono.just(ResponseEntity.of(Status.NOT_FOUND.getStatusCode(), "路由修改失败", null));
		}
    	return Mono.just(ResponseEntity.succeed("修改路由成功")) ;
    	
    }
}
