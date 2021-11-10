package com.unascribed.ears.api;

public interface EarsInhibitor {

	/**
	 * @see EarsInhibitorRegistry#register(String, EarsInhibitor)
	 */
	boolean shouldInhibit(EarsFeatureType type, Object peer);
	
}
