package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.RawEarsImage;

import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

@Mixin(DownloadingTexture.class)
public abstract class MixinDownloadingTexture extends SimpleTexture implements EarsFeaturesHolder {

	public MixinDownloadingTexture(ResourceLocation location) {
		super(location);
	}
	
	private EarsFeatures earsFeatures;

	@Inject(at=@At("HEAD"), method = "setImage(Lnet/minecraft/client/renderer/texture/NativeImage;)V")
	public void setImage(NativeImage cur, CallbackInfo ci) {
		earsFeatures = EarsFeatures.detect(new RawEarsImage(cur.makePixelArray(), cur.getWidth(), cur.getHeight(), false));
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
	
}
