
/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.iop.IopClientsDefiner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class FeignIopClientsDefiner implements IopClientsDefiner {
    @Override
    public void definePropertyValue(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment, BeanDefinitionBuilder definition, Map<String, Object> configAttrs, Class clientBuilder) {
        String name = getName(configAttrs,environment);
        validate(configAttrs);
        definition.addPropertyValue("url", getUrl(configAttrs,environment));
        definition.addPropertyValue("path", getPath(configAttrs,environment));
        definition.addPropertyValue("name", name);
        definition.addPropertyValue("decode404", configAttrs.get("decode404"));
        definition.addPropertyValue("fallback", configAttrs.get("fallback"));
        definition.addPropertyValue("fallbackFactory", configAttrs.get("fallbackFactory"));
    }

    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        validateFallback(annotation.getClass("fallback"));
        validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }
    private String getName(Map<String, Object> attributes, Environment environment) {
        String name = resolve((String)attributes.get("name"),environment);
        return getName(name);
    }

    private String getUrl(Map<String, Object> attributes, Environment environment) {
        String url = resolve((String) attributes.get("url"),environment);
        return getUrl(url);
    }

    private String getPath(Map<String, Object> attributes, Environment environment) {
        String path = resolve((String) attributes.get("path"),environment);
        return getPath(path);
    }
    private String resolve(String value,Environment environment) {
        if (StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }

    static void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(),
                "Fallback class must implement the interface annotated by @FeignClient");
    }

    static void validateFallbackFactory(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
                + "of fallback classes that implement the interface annotated by @FeignClient");
    }
    static String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        String host = null;
        try {
            String url;
            if (!name.startsWith("http://") && !name.startsWith("https://")) {
                url = "http://" + name;
            }
            else {
                url = name;
            }
            host = new URI(url).getHost();

        }
        catch (URISyntaxException e) {
        }
        Assert.state(host != null, "Service id not legal hostname (" + name + ")");
        return name;
    }

    static String getUrl(String url) {
        if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                new URL(url);
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

    static String getPath(String path) {
        if (StringUtils.hasText(path)) {
            path = path.trim();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }
}

