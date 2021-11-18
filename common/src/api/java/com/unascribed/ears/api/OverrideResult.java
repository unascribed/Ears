package com.unascribed.ears.api;

public enum OverrideResult {
	/**
	 * No preference. Allow the default, or another override, to take precedence.
	 */
	DEFAULT,
	/**
	 * Stop right now and override to false.
	 */
	FALSE,
	/**
	 * Stop right now and override to true.
	 */
	TRUE,
	;
}
