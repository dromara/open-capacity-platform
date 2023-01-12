package com.open.capacity.common.geoip;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(GeoIP2Properties.PREFIX)
@Data
public class GeoIP2Properties {

	public static final String PREFIX = "ocp.geoip2";
	
	 /**
     * 是否开启
     */
    private Boolean enabled = false;

	/** GeoIP2 or GeoLite2 Database Location */
	private String location = "classpath*:GeoLite2-Country.mmdb";

}
