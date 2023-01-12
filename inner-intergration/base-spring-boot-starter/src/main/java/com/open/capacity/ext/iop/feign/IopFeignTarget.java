/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import feign.Target;
import org.springframework.util.StringUtils;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-16 9:01
 */
public class IopFeignTarget {
    private String name;
    private String url;
    private String path;
    private boolean loadBalancer = false;

    public IopFeignTarget(String name, String url,  String path) {
        this.name = name;
        this.url = url;
        this.path = path;
        if (!StringUtils.hasText(this.url)){
            loadBalancer = true;
        }
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public <T> Target<T> buildTarget(Class<T> objectType){
        if (loadBalancer){
            return cloudTarget(objectType);
        }else {
            return simpleTarget(objectType);
        }
    }

    private <T> Target<T> simpleTarget(Class<T> objectType) {
        if (StringUtils.hasText(this.url) && !this.url.startsWith("http")) {
            this.url = "http://" + this.url;
        }
        this.url += cleanPath();

        return new Target.HardCodedTarget(objectType, this.name, this.url);
    }

    private <T> Target<T> cloudTarget(Class<T> objectType) {
        if (!this.name.startsWith("http")) {
            this.url = "http://" + this.name;
        }
        else {
            this.url = this.name;
        }
        this.url += cleanPath();
        return new Target.HardCodedTarget(objectType, this.name, this.url);
    }


    private String cleanPath() {
        String path = this.path.trim();
        if (StringUtils.hasLength(path)) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    public boolean isLoadBalancer() {
        return loadBalancer;
    }
}
