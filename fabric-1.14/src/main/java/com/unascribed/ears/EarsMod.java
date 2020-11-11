package com.unascribed.ears;

import com.unascribed.ears.common.EarsLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

public class EarsMod implements ModInitializer {
	@Override
	public void onInitialize() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Fabric {}; Env={}",
					SharedConstants.getGameVersion().getName(),
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}
	}
}
