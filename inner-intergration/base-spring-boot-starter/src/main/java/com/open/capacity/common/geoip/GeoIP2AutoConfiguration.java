package  com.open.capacity.common.geoip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.maxmind.db.NodeCache;
import com.maxmind.geoip2.DatabaseReader;
import com.open.capacity.common.geoip.cache.GuavaNodeCache;

/**
 * geoip配置
 */
@Configuration
@ConditionalOnClass(DatabaseReader.class)
@EnableConfigurationProperties({ GeoIP2Properties.class })
@ConditionalOnProperty(prefix = GeoIP2Properties.PREFIX , name = "enabled", havingValue = "true" )
public class GeoIP2AutoConfiguration {

	protected ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();

	@Bean
	@ConditionalOnMissingBean
	public NodeCache nodeCache() {
		return new GuavaNodeCache();
	}

	@Bean
	public DatabaseReader geoip2Reader(NodeCache nodeCache, GeoIP2Properties properties) throws FileNotFoundException, IOException {
		// A File object pointing to your GeoIP2 or GeoLite2 database
		File database = new File(properties.getLocation());
		if(database.exists()) {
			// the object across lookups. The object is thread-safe.
			DatabaseReader reader = new DatabaseReader.Builder(database).withCache(nodeCache).build();
			return reader;
		} else {
			// 查找resource
			Resource resource = resourceLoader.getResource(properties.getLocation());
			if(resource.exists()){
				return new DatabaseReader.Builder(resource.getInputStream()).withCache(nodeCache).build();
			}
			throw new IOException("not found db form : " + properties.getLocation());
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public GeoIP2Template geoip2Template(DatabaseReader dbReader) {
		return new GeoIP2Template(dbReader);
	}

}
