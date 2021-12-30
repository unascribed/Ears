package com.unascribed.ears;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
		try {
			Class.forName("org.spongepowered.asm.mixin.Mixins");
		} catch (Throwable t) {
			throw new LoaderException("Ears requires MixinBootstrap on versions earlier than Forge 32.0.72");
		}
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.<Supplier<String>, BiPredicate<String, Boolean>>of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		try {
			Indirection.init();
		} catch (Throwable t) {
			// I can't be bothered to set up ModLauncher-based Forge's client hurdles
		}
	}
	
	public static class Indirection {

		public static void init() {
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> {
				Session s = Minecraft.getInstance().getSession();
				return new ConfirmOpenLinkScreen(
						clicked -> {
							if (clicked) {
								Util.getOSType().openURI(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
							}
							Minecraft.getInstance().displayGuiScreen(screen);
						},
						EarsCommon.getConfigPreviewUrl(), true) {
							@Override
							public void copyLinkToClipboard() {
								mc.keyboardListener.setClipboardString(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
							}
						};
			});
		}
	}
	
}
