package com.open.capacity;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

/**
 * description:
 * author: JohnsonLiu
 * create at:  2021/12/24  23:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestContext {

    private static final ThreadLocal<RequestContext> transmittableThreadLocal = new TransmittableThreadLocal();


    private static final RequestContext INSTANCE = new RequestContext();
    private RequestHeader requestHeader;


    public static void create(RequestHeader requestHeader) {
        transmittableThreadLocal.set(new RequestContext(requestHeader));
    }

    public static RequestContext current() {
        return ObjectUtils.defaultIfNull(transmittableThreadLocal.get(), INSTANCE);
    }

    public static RequestHeader get() {
        return current().getRequestHeader();
    }

    public static void remove() {
        transmittableThreadLocal.set(null);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    static class RequestHeader {
        private String requestUrl;
        private String requestType;
    }
}