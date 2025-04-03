package com.unascribed.ears;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

public class EarsMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (EarsLog.DEBUG) {
			String ver;
			try {
				ver = SharedConstants.getGameVersion().getName();
			} catch (NoSuchMethodError e) {
				try {
					ver = (String)SharedConstants.class.getDeclaredFields()[3].get(null);
				} catch (Throwable t) {
					ver = "1.19.3?";
				}
			}
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / Fabric {}; Env={}",
					ver,
					FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
					FabricLoader.getInstance().getEnvironmentType());
		}
	}

	public static EarsFeatures getEarsFeatures(PlayerEntityRenderState peer) {
		Identifier skin = peer.skinTextures.texture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder) {
			EarsFeatures feat = ((EarsFeaturesHolder)tex).getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.name, /*peer.getGameProfile().getId()*/null, feat);
			if (!peer.invisible) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}
}
