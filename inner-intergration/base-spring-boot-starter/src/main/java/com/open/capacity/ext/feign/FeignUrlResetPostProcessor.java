/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.feign;

import com.open.capacity.common.core.obj.collect.CollectionTools;
import com.open.capacity.common.core.obj.collect.GroupTools;
import com.open.capacity.common.core.obj.reflect.ClassTools;
import com.open.capacity.ext.commom.utli.ReflectUtils;
import com.open.capacity.ext.core.BeanDefinitionPostProcessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeignUrlResetPostProcessor implements BeanDefinitionPostProcessor , EnvironmentAware {
    private String feignClientFactoryBeanClassName = "org.springframework.cloud.openfeign.FeignClientFactoryBean";
    private Map<String,Map<String,String>> urlResetConfigs = new HashMap<>();
    @Override
    public boolean supportResetDefinition(BeanDefinition beanDefinition) {
        return !CollectionUtils.isEmpty(urlResetConfigs) && Objects.equals(beanDefinition.getBeanClassName(),feignClientFactoryBeanClassName);
    }

    @Override
    public void resetDefinition(String beanName, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {
        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
        String resetUrl = getResetUrl(mutablePropertyValues);
        if (StringUtils.hasText(resetUrl)){
            String factoryBeanName = "&" + beanName;
            if (beanFactory.containsBean(factoryBeanName)){
                Object factoryBean = beanFactory.getBean(factoryBeanName);
                ReflectUtils.setBeanFieldVal(factoryBean,"url",resetUrl);
            }else {
                mutablePropertyValues.add("url",resetUrl);
            }

        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        StandardEnvironment standardEnvironment = (StandardEnvironment) environment;

        FeignUrlResetProperties feignUrlResetProperties = new FeignUrlResetProperties();
        Bindable<FeignUrlResetProperties> bindable = Bindable.ofInstance(feignUrlResetProperties);

        new Binder(ConfigurationPropertySources.from(standardEnvironment.getPropertySources()))
                .bind(FeignUrlResetProperties.FEIGN_URL_RESET_PREFIX,bindable);
        if (Objects.nonNull(feignUrlResetProperties) && !CollectionUtils.isEmpty(feignUrlResetProperties.getConfigs())){
            Map<String, List<FeignUrlResetConfig>> configMap = GroupTools.group(feignUrlResetProperties.getConfigs(),FeignUrlResetConfig::getType);
            configMap.forEach((key,val) -> urlResetConfigs.put(key,CollectionTools.toMap(val,FeignUrlResetConfig::getValue,FeignUrlResetConfig::getNewurl)));
        }
    }

    private String getResetUrl(MutablePropertyValues mutablePropertyValues){
        String url = getResetUrlByContextId(mutablePropertyValues);
        if (StringUtils.isEmpty(url)){
            url = getResetUrlByName(mutablePropertyValues);
        }
        if (StringUtils.isEmpty(url)){
            url = getResetUrlByPackage(mutablePropertyValues);
        }
        return url;
    }

    private String getResetUrlByName(MutablePropertyValues mutablePropertyValues){
        if (!mutablePropertyValues.contains("name") || !urlResetConfigs.containsKey(FeignUrlResetProperties.ResetType.NAME.type)){
            return null;
        }
        Map<String,String> resetByNameConfig = urlResetConfigs.get(FeignUrlResetProperties.ResetType.NAME.type);
        String name = Objects.toString(mutablePropertyValues.get("name"));
        if (resetByNameConfig.containsKey(name)){
            return resetByNameConfig.get(name);
        }
        return null;
    }

    private String getResetUrlByContextId(MutablePropertyValues mutablePropertyValues){
        if (!mutablePropertyValues.contains("contextId") || !urlResetConfigs.containsKey(FeignUrlResetProperties.ResetType.CONTEXT_ID.type)){
            return null;
        }
        Map<String,String> resetByContextIdConfig = urlResetConfigs.get(FeignUrlResetProperties.ResetType.CONTEXT_ID.type);
        String contextId = Objects.toString(mutablePropertyValues.get("contextId"));
        if (resetByContextIdConfig.containsKey(contextId)){
            return resetByContextIdConfig.get(contextId);
        }
        return null;
    }


    private String getResetUrlByPackage(MutablePropertyValues mutablePropertyValues){
        if (!urlResetConfigs.containsKey(FeignUrlResetProperties.ResetType.PACKAGE.type)){
            return null;
        }
        Map<String,String> resetByPackageConfig = urlResetConfigs.get(FeignUrlResetProperties.ResetType.PACKAGE.type);
        String type = Objects.toString(mutablePropertyValues.get("type"));
        List<String> packageNames = ClassTools.getAllPackagesByClassName(type);
        for (String packageName : packageNames){
            if (resetByPackageConfig.containsKey(packageName)){
                return resetByPackageConfig.get(packageName);
            }
        }

        return null;
    }
}
