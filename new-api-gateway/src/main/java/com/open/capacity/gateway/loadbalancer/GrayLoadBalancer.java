package com.open.capacity.gateway.loadbalancer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.gateway.utils.WeightRandomUtils;
import com.open.capacity.gateway.weight.WeightMeta;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 灰度权重路由
 */
@Slf4j
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {
	private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
	private String serviceId;

	public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId) {
		this.serviceId = serviceId;
		this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
	}

	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		HttpHeaders headers = (HttpHeaders) request.getContext();
		if (this.serviceInstanceListSupplierProvider != null) {
			ServiceInstanceListSupplier supplier = (ServiceInstanceListSupplier) this.serviceInstanceListSupplierProvider
					.getIfAvailable(NoopServiceInstanceListSupplier::new);
			return ((Flux) supplier.get()).next()
					.map(list -> getInstanceResponse((List<ServiceInstance>) list, headers));
		}
		return null;
	}

	private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
		if (instances.isEmpty()) {
			return getServiceInstanceEmptyResponse();
		} else {
			return getServiceInstanceResponseWithWeight(instances);
		}
	}

	/**
	 * 根据版本进行分发
	 * 
	 * @param instances
	 * @param headers
	 * @return
	 */
	private Response<ServiceInstance> getServiceInstanceResponseByVersion(List<ServiceInstance> instances,
			HttpHeaders headers) {
		String version = headers.getFirst(CommonConstant.METADATA_VERSION);
		log.info("已经选择{}版本分发", version);
		Map<String, String> versionMap = new HashMap<>();
		versionMap.put(CommonConstant.METADATA_VERSION, version);
		final Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(versionMap.entrySet());
		ServiceInstance serviceInstance = null;
		for (ServiceInstance instance : instances) {
			Map<String, String> metadata = instance.getMetadata();
			if (metadata.entrySet().containsAll(attributes)) {
				serviceInstance = instance;
				break;
			}
		}
		if (ObjectUtils.isEmpty(serviceInstance)) {
			return getServiceInstanceEmptyResponse();
		}
		return new DefaultResponse(serviceInstance);
	}

	/**
	 *
	 * 根据在nacos中配置的权重值，进行分发
	 * @param instances
	 * @return
	 */
	private Response<ServiceInstance> getServiceInstanceResponseWithWeight(List<ServiceInstance> instances) {
		Map<ServiceInstance, Integer> weightMap = new HashMap<>();
		for (ServiceInstance instance : instances) {
			Map<String, String> metadata = instance.getMetadata();
			if (metadata.containsKey(CommonConstant.WEIGHT_KEY)) {
				weightMap.put(instance, Integer.valueOf(metadata.get(CommonConstant.WEIGHT_KEY)));
			}
		}
		WeightMeta<ServiceInstance> weightMeta = WeightRandomUtils.buildWeightMeta(weightMap);
		if (ObjectUtils.isEmpty(weightMeta)) {
			return getServiceInstanceEmptyResponse();
		}
		ServiceInstance serviceInstance = weightMeta.random();
		if (ObjectUtils.isEmpty(serviceInstance)) {
			return getServiceInstanceEmptyResponse();
		}
		String version = serviceInstance.getMetadata().get(CommonConstant.METADATA_VERSION) ;
		log.debug("已经选择{}版本分发", version);
		return new DefaultResponse(serviceInstance);
	}

	private Response<ServiceInstance> getServiceInstanceEmptyResponse() {
		log.warn("No servers available for service: " + this.serviceId);
		return new EmptyResponse();
	}
}