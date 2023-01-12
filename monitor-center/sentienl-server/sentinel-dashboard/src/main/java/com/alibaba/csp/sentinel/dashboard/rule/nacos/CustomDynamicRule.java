package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.util.JSONUtils;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 定义 Nacos数据源 推送，拉取操作
 * @Author sisyphus
 * @Date 2021/8/25 15:11
 * @Version V-1.0
 */
public interface CustomDynamicRule<T> {

    /**
    *@Author sisyphus
    *@Description 远程获取规则-nacos数据源
    *@Date 2021/8/25 17:24
    *@Param [configService, appName, postfix, clazz-反序列化类]
    *@return java.util.List<T>
    **/
    default List<T> fromNacosRuleEntity(ConfigService configService, String appName, String postfix, Class<T> clazz) throws NacosException {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        String rules = configService.getConfig(
                genDataId(appName, postfix),
                NacosConfigUtil.GROUP_ID,
                3000
        );
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSONUtils.parseObject(clazz, rules);
    }
    
    /**
     * @title setNacosRuleEntityStr
     * @description  将规则序列化成JSON文本，存储到Nacos server中
     * @author sisyphus
     * @param: configService nacos config service
     * @param: appName       应用名称
     * @param: postfix       规则后缀 eg.NacosConfigUtil.FLOW_DATA_ID_POSTFIX
     * @param: rules         规则对象
     * @updateTime 2021/8/26 15:47 
     * @throws  NacosException 异常
     **/
    default void setNacosRuleEntityStr(ConfigService configService, String appName, String postfix, List<T> rules) throws NacosException{
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        String dataId = genDataId(appName, postfix);

        //存储，推送远程nacos服务配置中心
        boolean publishConfig = configService.publishConfig(
                dataId,
                NacosConfigUtil.GROUP_ID,
                printPrettyJSON(rules)
        );
        if(!publishConfig){
            throw new RuntimeException("publish to nacos fail");
        }
    }

    /**
    *@Author sisyphus
    *@Description 组装nacos dateId
    *@Date 2021/8/25 16:34
    *@Param [appName, postfix]
    *@return java.lang.String
    **/
    default String genDataId(String appName, String postfix) {
        return appName + postfix;
    }

    /**
    *@Author sisyphus
    *@Description 规则对象转换为json字符串
    *@Date 2021/8/25 17:19
    *@Param [obj]
    *@return java.lang.String
    **/
    default String printPrettyJSON(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return JSON.toJSONString(obj);
        }
    }
}
