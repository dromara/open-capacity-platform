/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package  com.open.capacity.common.geoip.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.maxmind.db.CacheKey;
import com.maxmind.db.DecodedValue;
import com.maxmind.db.NodeCache;

import lombok.extern.slf4j.Slf4j;
/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Slf4j
public class GuavaNodeCache implements NodeCache {

    private static final int DEFAULT_CAPACITY = 4096;
    private static final int DEFAULT_MAXIMUM_SIZE = DEFAULT_CAPACITY * 8;
    private static final int DEFAULT_EXPIRE_DURATION = 1;
    private static final TimeUnit DEFAULT_EXPIRE_UNIT = TimeUnit.HOURS;
    
    private final int capacity;
    private final Cache<Object, Object> cache;
    private boolean cacheFull = false;

    public GuavaNodeCache() {
        this(DEFAULT_CAPACITY, DEFAULT_MAXIMUM_SIZE, DEFAULT_EXPIRE_DURATION, DEFAULT_EXPIRE_UNIT);
    }
    
    public GuavaNodeCache(int capacity, int maximumSize, long duration, TimeUnit unit) {
        this.capacity = capacity;
        this.cache = CacheBuilder.newBuilder()
        		.initialCapacity(capacity)
        		.maximumSize(maximumSize)
        		.removalListener(new RemovalListener<Object, Object>() {

					@Override
					public void onRemoval(RemovalNotification<Object, Object> notification) {
						log.debug("Remove Cache : {}", notification.getKey());
					}
        			
				})
        		.expireAfterAccess(duration, unit)
        		.build();
    }

	@Override
    @SuppressWarnings("rawtypes")
    public DecodedValue get(CacheKey key, Loader loader) throws IOException {
        Object value = cache.getIfPresent(key);
        if (value == null) {
            value = loader.load(key);
            if (!cacheFull) {
                if (cache.size() < capacity) {
                    cache.put(key, value);
                } else {
                    cacheFull = true;
                }
            }
        }
        return (DecodedValue) value;
    }

}
