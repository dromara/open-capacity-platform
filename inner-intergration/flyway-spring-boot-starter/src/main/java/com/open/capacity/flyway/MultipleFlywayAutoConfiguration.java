package com.open.capacity.flyway;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "ocp-flyway", name = "enabled", matchIfMissing = true)
@ConditionalOnBean(DataSource.class)
@ConditionalOnMissingBean(Flyway.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties(MultipleFlywayProperties.class)
@Slf4j
public class MultipleFlywayAutoConfiguration {

    private Map<String, DataSource> dataSources;
    private MultipleFlywayProperties multipleFlywayProperties;

    @Autowired
    public MultipleFlywayAutoConfiguration(Map<String, DataSource> dataSources, MultipleFlywayProperties multipleFlywayProperties) {
        this.dataSources = dataSources;
        this.multipleFlywayProperties = multipleFlywayProperties;
    }

    @PostConstruct
    public void migrateFlyway() {
        multipleFlywayProperties.getProperties().forEach(properties -> {
            DataSource dataSource = dataSources.get(properties.getDatasource());
            if(null != dataSource){
                FluentConfiguration configuration = Flyway.configure().dataSource(dataSource);
                configureProperties(properties, configuration);
                Flyway flyway = configuration.load();
                flyway.migrate();
            }
        });
    }

    private void configureProperties(MultipleFlywayProperties.FlywayCustomProperties properties, FluentConfiguration configuration) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getLocations()).as(StringUtils::toStringArray).to(configuration::locations);
        map.from(properties.getEncoding()).to(configuration::encoding);
        map.from(properties.getConnectRetries()).to(configuration::connectRetries);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getDefaultSchema()).to((schema) -> configuration.defaultSchema(schema));
        map.from(properties.getSchemas()).as(StringUtils::toStringArray).to(configuration::schemas);
        map.from(properties.getTable()).to(configuration::table);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getTablespace()).whenNonNull().to((tablespace) -> configuration.tablespace(tablespace));
        map.from(properties.getBaselineDescription()).to(configuration::baselineDescription);
        map.from(properties.getBaselineVersion()).to(configuration::baselineVersion);
        map.from(properties.getInstalledBy()).to(configuration::installedBy);
        map.from(properties.getPlaceholders()).to(configuration::placeholders);
        map.from(properties.getPlaceholderPrefix()).to(configuration::placeholderPrefix);
        map.from(properties.getPlaceholderSuffix()).to(configuration::placeholderSuffix);
        map.from(properties.isPlaceholderReplacement()).to(configuration::placeholderReplacement);
        map.from(properties.getSqlMigrationPrefix()).to(configuration::sqlMigrationPrefix);
        map.from(properties.getSqlMigrationSuffixes()).as(StringUtils::toStringArray)
                .to(configuration::sqlMigrationSuffixes);
        map.from(properties.getSqlMigrationSeparator()).to(configuration::sqlMigrationSeparator);
        map.from(properties.getRepeatableSqlMigrationPrefix()).to(configuration::repeatableSqlMigrationPrefix);
        map.from(properties.getTarget()).to(configuration::target);
        map.from(properties.isBaselineOnMigrate()).to(configuration::baselineOnMigrate);
        map.from(properties.isCleanDisabled()).to(configuration::cleanDisabled);
        map.from(properties.isCleanOnValidationError()).to(configuration::cleanOnValidationError);
        map.from(properties.isGroup()).to(configuration::group);
        map.from(properties.isMixed()).to(configuration::mixed);
        map.from(properties.isOutOfOrder()).to(configuration::outOfOrder);
        map.from(properties.isSkipDefaultCallbacks()).to(configuration::skipDefaultCallbacks);
        map.from(properties.isSkipDefaultResolvers()).to(configuration::skipDefaultResolvers);
        configureValidateMigrationNaming(configuration, properties.isValidateMigrationNaming());
        map.from(properties.isValidateOnMigrate()).to(configuration::validateOnMigrate);
        map.from(properties.getInitSqls()).whenNot(CollectionUtils::isEmpty)
                .as((initSqls) -> StringUtils.collectionToDelimitedString(initSqls, "\n"))
                .to(configuration::initSql);
        // Pro properties
        map.from(properties.getBatch()).whenNonNull().to(configuration::batch);
        map.from(properties.getDryRunOutput()).whenNonNull().to(configuration::dryRunOutput);
        map.from(properties.getErrorOverrides()).whenNonNull().to(configuration::errorOverrides);
        map.from(properties.getLicenseKey()).whenNonNull().to(configuration::licenseKey);
        map.from(properties.getOracleSqlplus()).whenNonNull().to(configuration::oracleSqlplus);
        // No method reference for compatibility with Flyway 5.x
        map.from(properties.getOracleSqlplusWarn()).whenNonNull()
                .to((oracleSqlplusWarn) -> configuration.oracleSqlplusWarn(oracleSqlplusWarn));
        map.from(properties.getStream()).whenNonNull().to(configuration::stream);
        map.from(properties.getUndoSqlMigrationPrefix()).whenNonNull().to(configuration::undoSqlMigrationPrefix);
    }

    private void configureValidateMigrationNaming(FluentConfiguration configuration,
                                                  boolean validateMigrationNaming) {
        try {
            configuration.validateMigrationNaming(validateMigrationNaming);
        } catch (NoSuchMethodError ex) {
            log.error("NoSuchMethodError error :{}",ex.getMessage());
        }
    }

}