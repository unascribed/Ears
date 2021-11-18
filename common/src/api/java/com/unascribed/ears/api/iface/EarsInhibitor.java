package com.unascribed.ears.api.iface;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;

public interface EarsInhibitor {

	/**
	 * @see EarsInhibitorRegistry#register(String, EarsInhibitor)
	 */
	boolean shouldInhibit(EarsFeatureType type, Object peer);
	
}
