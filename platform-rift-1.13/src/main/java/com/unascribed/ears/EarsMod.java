package com.unascribed.ears;

import org.dimdev.riftloader.RiftLoader;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;

import me.shedaniel.api.ConfigRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Util;


public class EarsMod implements InitializationListener {

	@Override
	public void onInitialization() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Minecraft 1.13.2 / Rift vWhoCares; Side={}", RiftLoader.instance.getSide());
		}
		MixinBootstrap.init();
		Mixins.addConfiguration("ears.mixins.json");
		try {
			ConfigRegistry.registerConfig("ears", () -> {
				GuiScreen screen = Minecraft.getInstance().currentScreen;
				Session s = Minecraft.getInstance().getSession();
				Minecraft.getInstance().displayGuiScreen(new GuiConfirmOpenLink(
						(clicked, id) -> {
							if (clicked) {
								Util.getOSType().openURI(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
							}
							Minecraft.getInstance().displayGuiScreen(screen);
						},
						EarsCommon.getConfigPreviewUrl(), 0, true) {
							{disableSecurityWarning();}
							@Override
							public void copyLinkToClipboard() {
								mc.keyboardListener.setClipboardString(EarsCommon.getConfigUrl(s.getUsername(), s.getPlayerID()));
							}
						});
			});
		} catch (Throwable t) {}
	}

	public static EarsFeatures getEarsFeatures(AbstractClientPlayer peer) {
		ResourceLocation skin = peer.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder) {
			EarsFeatures feat = ((EarsFeaturesHolder)tex).getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.getGameProfile().getName(), peer.getGameProfile().getId(), feat);
			if (!peer.isInvisible()) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}
	
}
