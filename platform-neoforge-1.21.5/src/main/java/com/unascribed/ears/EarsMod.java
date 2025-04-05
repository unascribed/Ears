package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

@Mod(value = "ears", dist = Dist.CLIENT)
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / NeoForge {}; Env={}:{}",
					SharedConstants.getCurrentVersion().getName(), NeoForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.production ? "production" : "dev");
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
	
}
