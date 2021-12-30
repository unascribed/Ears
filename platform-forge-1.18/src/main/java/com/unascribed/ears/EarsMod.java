package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getCurrentVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
		ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		try {
			Indirection.init();
		} catch (Throwable t) {
			// I can't be bothered to set up ModLauncher-based Forge's client hurdles
		}
	}
	
	public static class Indirection {

		public static void init() {
			ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((mc, screen) -> {
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
	}
	
}
