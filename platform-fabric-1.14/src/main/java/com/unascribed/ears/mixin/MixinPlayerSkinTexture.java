package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsImage;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.util.EarsStorage;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

@Mixin(PlayerSkinTexture.class)
public abstract class MixinPlayerSkinTexture extends ResourceTexture implements EarsFeaturesHolder {

	public MixinPlayerSkinTexture(Identifier location) {
		super(location);
	}
	
	private EarsFeatures earsFeatures;

	@Inject(at=@At("HEAD"), method = "method_4534(Lnet/minecraft/client/texture/NativeImage;)V")
	public void method_4534(NativeImage cur, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Process player skin");
		if (cur == null) return;
		EarsImage img = new NativeImageAdapter(cur);
		earsFeatures = EarsFeatures.detect(img, EarsStorage.get(cur, EarsStorage.Key.ALFALFA),
				data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
	
}
