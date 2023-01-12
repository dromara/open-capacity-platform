package com.open.capacity.gateway.loadbalancer;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import com.google.common.base.Joiner;

import io.vavr.collection.Stream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 偏向性路由 
 */
@Slf4j
public class DeflectionInstanceBalancer implements ReactorServiceInstanceLoadBalancer {

	public String serviceId;
	public ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

	public DeflectionInstanceBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
			String serviceId) {
		this.serviceId = serviceId;
		this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
	}

	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		if (serviceInstanceListSupplierProvider != null) {
			ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
					.getIfAvailable(NoopServiceInstanceListSupplier::new);
			return supplier.get().next().map((instances) -> getInstanceResponse(request, instances));
		}
		return null;
	}

	private Response<ServiceInstance> getInstanceResponse(Request request, List<ServiceInstance> instances) {
		if (instances.isEmpty()) {
			log.warn("No servers available for service: " + this.serviceId);
			return new EmptyResponse();
		}
		String instance = (String) request.getContext();

		ServiceInstance serviceInstance = instances.stream()
				.filter(item -> instance
						.equals( Joiner.on(":").join(Stream.of( item.getHost() ,item.getPort())))
						)
				.findFirst().orElse(null);

		return new DefaultResponse(serviceInstance);
	}

	 
}
