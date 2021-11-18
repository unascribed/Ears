package com.unascribed.ears.api.iface;

import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.OverrideResult;
import com.unascribed.ears.api.registry.EarsStateOverriderRegistry;

public interface EarsStateOverrider {

	/**
	 * @see EarsStateOverriderRegistry#register(String, EarsStateOverrider)
	 */
	OverrideResult isActive(EarsStateType state, Object peer);
	
}
