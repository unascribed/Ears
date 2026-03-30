package com.unascribed.ears;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

public class EarsMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (EarsLog.DEBUG) {
			String ver;
			try {
				ver = SharedConstants.getCurrentVersion().name();
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

	public static EarsFeatures getEarsFeatures(AvatarRenderState peer) {
		Identifier skin = peer.skin.body().id();
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder) {
			EarsFeatures feat = ((EarsFeaturesHolder)tex).getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.scoreText == null ? Minecraft.getInstance().name() : peer.scoreText.getString(), /*peer.getGameProfile().getId()*/null, feat);
			if (!peer.isInvisible) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}
}
