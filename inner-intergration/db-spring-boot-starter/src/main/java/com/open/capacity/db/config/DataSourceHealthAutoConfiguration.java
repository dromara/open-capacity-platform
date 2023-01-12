package com.open.capacity.db.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 健康检查 
 * gitgeek
 *
 */
@Configuration
public class DataSourceHealthAutoConfiguration {
    /**
     * 解決新版Spring中,健康健康檢查用到 sharding jdbc 時,該元件沒有完全實現Mysql驅動導致的問題.
     */
    @Bean
    DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider() {
        return dataSource -> dataSource instanceof HikariDataSource
                // 這裡如果所使用的資料來源沒有對應的 DataSourcePoolMetadata 實現的話也可以全部使用 NotAvailableDataSourcePoolMetadata
                ? new HikariDataSourcePoolMetadata((HikariDataSource) dataSource)
                : new NotAvailableDataSourcePoolMetadata();
    }

    /**
     * 不可用的資料來源池元資料.
     */
    private static class NotAvailableDataSourcePoolMetadata implements DataSourcePoolMetadata {
        @Override
        public Float getUsage() {
            return null;
        }

        @Override
        public Integer getActive() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public String getValidationQuery() {
            // 該字串是適用於MySQL的簡單查詢語句,用於檢查檢查,其他資料庫可能需要更換
            return "select 1";
        }

        @Override
        public Boolean getDefaultAutoCommit() {
            return null;
        }
    }
}
