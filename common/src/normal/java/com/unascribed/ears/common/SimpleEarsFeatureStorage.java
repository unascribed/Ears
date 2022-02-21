package com.unascribed.ears.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.unascribed.ears.EarsFeaturesLookup;
import com.unascribed.ears.api.features.EarsFeatures;

public class SimpleEarsFeatureStorage implements EarsFeaturesLookup {

	private final Map<UUID, EarsFeatures> byId = new ConcurrentHashMap<UUID, EarsFeatures>();
	private final Map<String, EarsFeatures> byName = new ConcurrentHashMap<String, EarsFeatures>();
	
	public void put(String username, UUID id, EarsFeatures features) {
		if (features == null) {
			return;
		}
		// not atomic, but it is faster; avoids synchronizing (this is called every frame!)
		// this should be OK for our purposes as only one thread will be calling put
		if (id != null && byId.get(id) != features) {
			byId.put(id, features);
		}
		if (username != null && byName.get(username) != features) {
			byName.put(username, features);
		}
	}
	
	@Override
	public EarsFeatures getById(UUID id) {
		EarsFeatures feat = byId.get(id);
		if (feat == null) return EarsFeatures.DISABLED;
		return feat;
	}

	@Override
	public EarsFeatures getByUsername(String username) {
		EarsFeatures feat = byName.get(username);
		if (feat == null) return EarsFeatures.DISABLED;
		return feat;
	}

}
