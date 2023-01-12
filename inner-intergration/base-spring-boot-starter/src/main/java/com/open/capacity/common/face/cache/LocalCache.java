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

public interface LocalCache<K, V> {
	
	/**
	 * Returns the value associated with {@code key} in this cache, or {@code null}
	 * if there is no cached value for {@code key}.
	 **/
	V get(K key) throws Exception;

	/**
	 * Associates {@code value} with {@code key} in this cache. If the cache
	 * previously contained a value associated with {@code key}, the old value is
	 * replaced by {@code value}.
	 *
	 **/
	void put(K key, V value);

	/**
	 * Discards any cached value for key {@code key}.
	 */
	void remove(Object key);
	
}
