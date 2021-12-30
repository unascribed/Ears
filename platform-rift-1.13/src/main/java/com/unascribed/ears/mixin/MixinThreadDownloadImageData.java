package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.util.EarsStorage;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData extends SimpleTexture implements EarsFeaturesHolder {

	public MixinThreadDownloadImageData(ResourceLocation location) {
		super(location);
	}
	
	private EarsFeatures earsFeatures;

	@Inject(at=@At("HEAD"), method = "setImage(Lnet/minecraft/client/renderer/texture/NativeImage;)V")
	public void setImage(NativeImage cur, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
		if (cur == null) return;
		earsFeatures = EarsFeatures.detect(new NativeImageAdapter(cur), EarsStorage.get(cur, EarsStorage.Key.ALFALFA),
				data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
	
}
