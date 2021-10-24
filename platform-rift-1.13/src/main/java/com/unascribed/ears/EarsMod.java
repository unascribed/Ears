package com.unascribed.ears;

import org.dimdev.riftloader.RiftLoader;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;


public class EarsMod implements InitializationListener {

	@Override
	public void onInitialization() {
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Minecraft 1.13.2 / Rift vWhoCares; Side={}", RiftLoader.instance.getSide());
		}
		MixinBootstrap.init();
		Mixins.addConfiguration("ears.mixins.json");
	}

	public static EarsFeatures getEarsFeatures(AbstractClientPlayer peer) {
		ResourceLocation skin = peer.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
			return ((EarsFeaturesHolder)tex).getEarsFeatures();
		}
		return EarsFeatures.DISABLED;
	}
	
}
