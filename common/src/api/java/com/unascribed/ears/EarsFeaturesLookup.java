package com.unascribed.ears;

import java.util.UUID;

import com.unascribed.ears.api.features.EarsFeatures;

public interface EarsFeaturesLookup {
	EarsFeatures getById(UUID id);
	EarsFeatures getByUsername(String username);
}