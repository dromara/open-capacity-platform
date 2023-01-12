package com.alibaba.csp.sentinel.dashboard.controller.v2;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * @Description: 问题：Sentinel Dashboard官方版本中支持创建DynamicRuleProvider和DynamicRulePublisher来和外部数据源通信，但是仅仅增加这两个类的实现并不够，使用下来发现逻辑上有一些问题
 *      *              1. 新建RuleEntity的时候，ID是从代码中的AtomicLong变量获取的，每次这个变量都是从0开始计数，也就意味着每次重启之后ID都重新计数，
 *      *              这在使用内存存储rule的时候没有问题，但是一旦有外部数据源，这地方逻辑就不对了
 *      *              2. 新建RuleEntity之后，会将当前所有的RuleEntity发布到外部数据源
 *      *              如果是从资源列表页（请求链路或簇点链路）直接创建规则，那么这时候还没从外部数据源加载已存在的rule（只有访问对应的规则页面的list接口才会从远程加载），
 *      *              当前rule创建完成之后发布到外部数据源的时候，只会把刚创建的这个发布出去，导致之前存在的rule被覆盖掉。
 *      *              3. 在原有的各个Controller的list方法中，在从外部加载rule之后，会调用repository的saveAll方法（就是InMemoryRuleRepositoryAdapter的saveAll方法），在该方法中会清除所有的rule
 *      *              这相当于内存中同时只能有一个app的rule集合存在。
 *      *            改动：
 *      *              1. 不再使用InMemoryRuleRepositoryAdapter的各个实现类作为repository，仅使用外部数据源。
 *      *              2. 增加NacosConfig和NacosConfigUtil，作为和Nacos通信的基础类
 *      *              3. 增加rule>nacos以下类(以Provider或Publisher结尾)，用于各类Rule和外部数据源交互
 *      *              4. 增加InDataSourceRuleStore类，该类提供了findById、list、save、update、delete方法用于和外部数据源交互
 *      *              提供了format方法，用于格式化从外部数据源获取到的数据
 *      *              提供了merge方法用于在update时做数据整合
 *      *              5. 修改controller类，继承InDataSourceRuleStore类，不再使用原有的repository，同时修改注入的DynamicRuleProvider和DynamicRulePublisher实现
 * @Author sisyphus
 * @Date 2021/8/25 17:59
 * @Version V-1.0
 */
public abstract class InDataSourceRuleStore<T extends RuleEntity> {
    private final Logger logger = LoggerFactory.getLogger(InDataSourceRuleStore.class);

    /**
    *@Author sisyphus
    *@Description 格式化从外部数据源获取到的数据（RuleEntity下的部分数据字段填充等）
    *@Date 2021/8/25 18:11
    *@Param [entity - 远程获取规则且匹配当前需查询规则的实体对象, app - 当前规则标识]
    *@return void
    **/
    protected abstract void format(T entity, String app);
    /**
    *@Author sisyphus
    *@Description 更新规则时部分数据的整合，字段维护
    *@Date 2021/8/26 14:20
    *@Param [entity, oldEntity]
    *@return void
    **/
    protected abstract void merge(T entity, T oldEntity);

    /**
    *@Author sisyphus
    *@Description 根据当前id获取远程匹配的规则实体
     *              此处只对普通流控做了转换，会经过format进行，其余规则直接返回远程规则对象，后面根据具体情况自行转换改造
    *@Date 2021/8/26 10:33
    *@Param [ruleProvider, app, id]
    *@return T
    **/
    protected T findById(DynamicRuleProvider<List<T>> ruleProvider, String app, Long id) {
        try {
            // 远程获取规则(当前种类下（如网关流控，普通流控，系统等）的所有规则数据)
            List<T> rules = ruleProvider.getRules(app);
            // 匹配符合当前查询的规则，格式化远端规则数据为sentinel服务端可使用格式（Entity形式）
            if (rules != null && !rules.isEmpty()) {
                Optional<T> entity = rules.stream().filter(rule -> (id.equals(rule.getId()))).findFirst();
                if (entity.isPresent()){
                    T t = entity.get();
                    this.format(t, app);
                    return t;
                }
            }
        } catch (Exception e) {
            logger.error("服务[{}]规则[{}]匹配远端规则异常：{}", app, id, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
    *@Author sisyphus
    *@Description 获取对应模块下的所有规则，存在format的进行规则转换
    *@Date 2021/8/26 11:27
    *@Param [ruleProvider, app]
    *@return java.util.List<T>
    **/
    protected List<T> list(DynamicRuleProvider<List<T>> ruleProvider, String app) throws Exception {
        List<T> rules = ruleProvider.getRules(app);
        if (rules != null && !rules.isEmpty()) {
            for (T entity : rules) {
                this.format(entity, app);
            }
            // 此处排序是为了确保保存（save）时方便获取id以生成nextId
            rules.sort((p1,p2) -> (int) (p1.getId() - p2.getId()));
        } else {
            rules = new ArrayList<>();
        }
        return rules;
    }

    /**
    *@Author sisyphus
    *@Description 添加规则至远程数据源
     *              添加前先获取远程数据源，再加入本次新增，一起推送到远程数据源（否则存在覆盖的可能）
     *            修改nextId生成规则，原nextId生成由InMemoryRuleRepositoryAdapter类下nextId()方法实现，内部维护了一个AtomicLong实现自增，每次重启则重新从0开始
    *@Date 2021/8/26 11:43
    *@Param [rulePublisher, ruleProvider, entity]
    *@return void
    **/
    protected void save(DynamicRulePublisher<List<T>> rulePublisher, DynamicRuleProvider<List<T>> ruleProvider, T entity) throws Exception {
        if (null == entity || StringUtils.isEmpty(entity.getApp())) {
            throw new InvalidParameterException("app is required");
        }
        if (null != entity.getId()) {
            throw new InvalidParameterException("id must be null");
        }
        // 获取远程规则数据
        List<T> rules = this.list(ruleProvider, entity.getApp());
        // 增规则添加至集合
        long nextId = 1;
        if (rules.size() > 0) {
            // 获取集合的最后一个元素，得到id，进行增1操作（集合在）list方法内进行过排序，以保证此处获取到的最后一个元素为当前集合内id最大的元素
            nextId = rules.get(rules.size() - 1).getId() + 1;
        }
        entity.setId(nextId);
        rules.add(entity);
        // 推送远程存储源
        rulePublisher.publish(entity.getApp(), rules);
    }

    /**
     * @title update
     * @description 获取远程所有模式下的规则，匹配id，进行替换
     * @author sisyphus
     * @param: rulePublisher
     * @param: ruleProvider
     * @param: entity
     * @updateTime 2021/8/30 9:35
     * @return: com.alibaba.csp.sentinel.dashboard.domain.Result<T>
     * @throws
     **/
    protected Result<T> update(DynamicRulePublisher<List<T>> rulePublisher, DynamicRuleProvider<List<T>> ruleProvider, T entity) throws Exception {
        if (null == entity || null == entity.getId() || StringUtils.isEmpty(entity.getApp())) {
            return Result.ofFail(-1, "id is required");
        }
        // 获取远程规则数据
        List<T> rules = this.list(ruleProvider, entity.getApp());
        if (null == rules || rules.isEmpty()) {
            return Result.ofFail(-1, "Failed to save authority rule, no matching authority rule");
        }
        // 远程规则集合与当前规则匹配项，当前规则填充旧的集合中对应规则数据
        for (int i = 0; i < rules.size(); i++) {
            T oldEntity = rules.get(i);
            if (oldEntity.getId().equals(entity.getId())) {
                // 新旧值替换填充，字段检查
                this.merge(entity, oldEntity);
                // 写回规则集合
                rules.set(i, entity);
                break;
            }
        }
        // 推送远程存储源
        rulePublisher.publish(entity.getApp(), rules);
        return Result.ofSuccess(entity);
    }

    /**
     * @title delete
     * @description 获取远程所有模式下的规则,从集合中删除对应规则项
     * @author sisyphus 
     * @param: rulePublisher
     * @param: ruleProvider
     * @param: id
     * @param: app
     * @updateTime 2021/8/30 9:36 
     * @return: com.alibaba.csp.sentinel.dashboard.domain.Result<java.lang.Long>
     * @throws
     **/
    protected Result<Long> delete(DynamicRulePublisher<List<T>> rulePublisher, DynamicRuleProvider<List<T>> ruleProvider, long id, String app) throws Exception {
        List<T> rules = this.list(ruleProvider, app);
        if (null == rules || rules.isEmpty()) {
            return Result.ofSuccess(null);
        }
        // 匹配删除项，移除集合
        boolean removeIf = rules.removeIf(flowRuleEntity -> flowRuleEntity.getId().equals(id));
        if (!removeIf){
            return Result.ofSuccess(null);
        }
        // 推送远程存储源
        rulePublisher.publish(app, rules);
        return Result.ofSuccess(id);
    }
}
