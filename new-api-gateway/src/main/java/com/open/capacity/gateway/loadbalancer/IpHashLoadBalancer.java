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

import com.google.common.hash.Hashing;

 
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/**
 * 一致性 IP Hash负载均衡器
 * @author owen
 */
@Slf4j
public class IpHashLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    public String serviceId;
    public ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    
    public IpHashLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
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

    private Response<ServiceInstance> getInstanceResponse(
            Request request,
            List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        String ip = (String) request.getContext();
        long hash =  Hashing.murmur3_32().hashUnencodedChars(ip).padToLong()  ;
        int pos = (int) (hash % instances.size());
        ServiceInstance instance = instances.get(pos);
        return new DefaultResponse(instance);
    }
}
