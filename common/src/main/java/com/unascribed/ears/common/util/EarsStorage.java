package com.unascribed.ears.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.unascribed.ears.api.features.AlfalfaData;

/**
 * General purpose thread-local type-safe storage for providing "inside-out" fields where needed.
 */
public final class EarsStorage {

	public static final class Key<T> {
		public static final Key<AlfalfaData> ALFALFA = new Key<AlfalfaData>(AlfalfaData.NONE);
		
		public final T def;

		public Key() { this(null); }
		public Key(T def) { this.def = def; }
	}
	
	private static final ThreadLocal<Map<Object, Map<Key<?>, Object>>> storage = new ThreadLocal<Map<Object, Map<Key<?>, Object>>>() {
		@Override
		protected Map<Object, Map<Key<?>, Object>> initialValue() {
			return new WeakHashMap<Object, Map<Key<?>, Object>>();
		}
	};
	
	public static <T> void put(Object peer, Key<T> key, T value) {
		Map<Object, Map<Key<?>, Object>> stor = storage.get();
		Map<Key<?>, Object> map = stor.get(peer);
		if (map == null) {
			map = new HashMap<Key<?>, Object>();
			stor.put(peer, map);
		}
		map.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Object peer, Key<T> key) {
		Map<Object, Map<Key<?>, Object>> stor = storage.get();
		Map<Key<?>, Object> map = stor.get(peer);
		if (map == null || !map.containsKey(key)) return key.def;
		return (T)map.get(key);
	}

	private EarsStorage() {}
	
}
