package com.unascribed.ears;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForgeVersion;

@Mod(value = "ears", dist = Dist.CLIENT)
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / NeoForge {}; Env={}:{}",
					SharedConstants.getCurrentVersion().name(), NeoForgeVersion.getVersion(), FMLEnvironment.getDist(), FMLEnvironment.isProduction() ? "production" : "dev");
		}
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> {
			User s = Minecraft.getInstance().getUser();
			return new ConfirmLinkScreen(
					clicked -> {
						if (clicked) {
							Util.getPlatform().openUri(EarsCommon.getConfigUrl(s.getName(), s.getProfileId().toString()));
						}
						Minecraft.getInstance().setScreen(screen);
					},
					EarsCommon.getConfigPreviewUrl(), true) {
				@Override
				public void copyToClipboard() {
					minecraft.keyboardHandler.setClipboard(EarsCommon.getConfigUrl(s.getName(), s.getProfileId().toString()));
				}
			};
		});
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
