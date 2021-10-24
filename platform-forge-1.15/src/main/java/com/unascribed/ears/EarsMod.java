package com.unascribed.ears;

import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
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
	}

	public static EarsFeatures getEarsFeatures(AbstractClientPlayerEntity peer) {
		ResourceLocation skin = peer.getLocationSkin();
		Texture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
			return ((EarsFeaturesHolder)tex).getEarsFeatures();
		}
		return EarsFeatures.DISABLED;
	}
	
}
