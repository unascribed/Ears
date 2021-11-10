package com.unascribed.ears;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

@Mod("ears")
public class EarsMod {
	
	public EarsMod() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft {} / Forge {}; Env={}:{}",
					SharedConstants.getVersion().getName(), ForgeVersion.getVersion(), FMLEnvironment.dist, FMLEnvironment.naming);
		}
		try {
			Class.forName("org.spongepowered.asm.mixin.Mixins");
		} catch (Throwable t) {
			throw new LoaderException("Ears requires MixinBootstrap on versions earlier than Forge 32.0.72");
		}
		ModList.get().getModContainerById("ears").get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> {
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

	public static EarsFeatures getEarsFeatures(AbstractClientPlayerEntity peer) {
		ResourceLocation skin = peer.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
			return ((EarsFeaturesHolder)tex).getEarsFeatures();
		}
		return EarsFeatures.DISABLED;
	}
	
}
