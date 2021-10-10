package com.unascribed.ears;

import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class EarsMod implements ModInitializer {
	@Override
	public void onInitialize() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft 1.8.x / Fabric {}; Env={}",
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}
	}
}
