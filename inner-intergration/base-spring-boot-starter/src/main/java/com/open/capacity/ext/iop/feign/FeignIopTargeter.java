/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import feign.Feign;
import feign.Target;
import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-13 15:10
 */
public interface FeignIopTargeter {
    <T> T target(IopFeignClientBuilder builder, Feign.Builder feign,
                 NamedContextFactory context, Target<T> target);

    class DefaultTargeter implements FeignIopTargeter {
        @Override
        public <T> T target(IopFeignClientBuilder builder, Feign.Builder feign, NamedContextFactory context, Target<T> target) {
            return feign.target(target);
        }
    }
}
