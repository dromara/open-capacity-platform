package com.open.capacity.flyway;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
public class DataSourceConfig {

	@Primary
	@Bean(name = "getShardingDataSource")
	@Qualifier("getShardingDataSource")
	public DataSource getShardingDataSource(@Autowired MetaDataSourceConfig metaDataSourceConfig) {
		DataSource dataSource = DataSourceBuilder.create().url(metaDataSourceConfig.jdbcUrl)
				.username(metaDataSourceConfig.username).password(metaDataSourceConfig.password)
				.driverClassName(metaDataSourceConfig.driverClassName).build();
		return new TransactionAwareDataSourceProxy(dataSource);
	}

//    @Bean(name = "noShardingTransactionManager")
//    @Primary
//    public PlatformTransactionManager noShardingTransactionManager(@Qualifier("getNoShardingDataSource")
//                                                                           DataSource getNoShardingDataSource) {
//        return new DataSourceTransactionManager(getNoShardingDataSource);
//    }
//
//
//    @Bean(name = "getShardingDataSource")
//    @Qualifier("getShardingDataSource")
//    public DataSource shardingDataSource(@Qualifier("shardingDataSource") DataSource shardingDataSource) {
//        return new TransactionAwareDataSourceProxy(shardingDataSource);
//    }
//
//    @Bean(name = "shardingTransactionManager")
//    public PlatformTransactionManager shardingTransactionManager(@Qualifier("getShardingDataSource")
//                                                                         DataSource getShardingDataSource) {
//        return new DataSourceTransactionManager(getShardingDataSource);
//    }

}