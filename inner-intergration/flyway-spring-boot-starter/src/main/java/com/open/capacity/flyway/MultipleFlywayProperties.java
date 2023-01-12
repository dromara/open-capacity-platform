package com.open.capacity.flyway;

import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("ocp-flyway")
public class MultipleFlywayProperties {
    private List<FlywayCustomProperties> properties;
    private boolean enabled = true;

    public List<FlywayCustomProperties> getProperties() {
        return this.properties;
    }

    public void setProperties(List<FlywayCustomProperties> properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class FlywayCustomProperties extends FlywayProperties {
        private String datasource;

        public String getDatasource() {
            return this.datasource;
        }

        public void setDatasource(String datasource) {
            this.datasource = datasource;
        }
    }
}