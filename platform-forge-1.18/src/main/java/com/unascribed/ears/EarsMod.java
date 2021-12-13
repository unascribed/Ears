package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
		ModList.get().getModContainerById("ears").get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((mc, screen) -> {
			User s = Minecraft.getInstance().getUser();
			return new ConfirmLinkScreen(
					clicked -> {
						if (clicked) {
							Util.getPlatform().openUri(EarsCommon.getConfigUrl(s.getName(), s.getUuid()));
						}
						Minecraft.getInstance().setScreen(screen);
					},
					EarsCommon.getConfigPreviewUrl(), true) {
						@Override
						public void copyToClipboard() {
							minecraft.keyboardHandler.setClipboard(EarsCommon.getConfigUrl(s.getName(), s.getUuid()));
						}
					};
		}));
	}

	public static EarsFeatures getEarsFeatures(AbstractClientPlayer peer) {
		ResourceLocation skin = peer.getSkinTextureLocation();
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
			return ((EarsFeaturesHolder)tex).getEarsFeatures();
		}
		return EarsFeatures.DISABLED;
	}
	
}
