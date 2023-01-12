/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop;

import com.open.capacity.common.core.exception.HltRuntimeException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

public class AutoFieldClientDefiner implements IopClientsDefiner{
    private Map<Class,Set<String>> hasSetFields = new HashMap<>();

    @Override
    public void definePropertyValue(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment, BeanDefinitionBuilder definition, Map<String, Object> configAttrs , Class clientBuilder) {
        Set<String> fields = hasSetterFields(clientBuilder);

        configAttrs.forEach((key,val) -> {
            if (fields.contains(key)){
                definition.addPropertyValue(key, val);
            }
        });


    }

    private Set<String> hasSetterFields(Class clazz) {
        synchronized (clazz){
            if (hasSetFields.containsKey(clazz)){
                return hasSetFields.get(clazz);
            }
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                Set<String> fields = new HashSet<>();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors){
                    String fieldName = propertyDescriptor.getName();
                    if (!Objects.equals("class",fieldName) && Objects.nonNull(propertyDescriptor.getWriteMethod())){
                        fields.add(fieldName);
                    }
                }
                hasSetFields.put(clazz,fields);
                return fields;
            }catch (Exception e){
                throw new HltRuntimeException(e);
            }
        }
    }
}
