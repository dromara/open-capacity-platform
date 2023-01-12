package com.open.capacity.flyway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.shardingsphere.datasource.ds0")
public class MetaDataSourceConfig {

    public String jdbcUrl;
    public String username;
    public String password;
    public String driverClassName;

}