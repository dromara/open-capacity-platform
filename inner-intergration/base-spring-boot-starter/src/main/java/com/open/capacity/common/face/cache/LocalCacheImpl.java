/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package com.open.capacity.common.face.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @author ： <a href="https://github.com/vindell">wandl</a>
 */
public class LocalCacheImpl<K, V> implements LocalCache<K, V> {
	private static final long MAX_SIZE = 65535;
	private static final long EXPIRE_TIME = 10;
	private Cache<K, Optional<V>> caches = CacheBuilder.newBuilder().maximumSize(MAX_SIZE)
			.expireAfterAccess(EXPIRE_TIME, TimeUnit.SECONDS).removalListener(new RemovalListener<K, Optional<V>>() {
				@Override
				public void onRemoval(RemovalNotification<K, Optional<V>> notification) {
					// TODO 
				}
			}).build();

	@Override
	public V get(K key) throws Exception {
		Optional<V> opt = caches.get(key, new Callable<Optional<V>>() {
			@Override
			public Optional<V> call() throws Exception {
				// TODO获取数据，加入缓存
				return Optional.fromNullable(null);
			}
		});
		return opt.isPresent() ? opt.get() : null;
	}

	@Override
	public void put(K key, V value) {
		caches.put(key, Optional.of(value));
	}

	@Override
	public void remove(Object key) {
		caches.invalidate(key);
	}
}
