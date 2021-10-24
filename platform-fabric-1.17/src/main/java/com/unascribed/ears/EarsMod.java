package com.unascribed.ears;

import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

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

	public static EarsFeatures getEarsFeatures(AbstractClientPlayerEntity peer) {
		Identifier skin = peer.getSkinTexture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
			return ((EarsFeaturesHolder)tex).getEarsFeatures();
		}
		return EarsFeatures.DISABLED;
	}
}
