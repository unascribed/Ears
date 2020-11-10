package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.texture.ImageFilter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SkinRemappingImageFilter;

@Mixin(SkinRemappingImageFilter.class)
public abstract class MixinSkinRemappingImageFilter implements ImageFilter {

	private static boolean ears$reentering;
	
	@Inject(at = @At("HEAD"), method = "method_3312(Lnet/minecraft/client/texture/NativeImage;IIII)V", cancellable = true)
	private static void method_3312(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
		if (ears$reentering) return;
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			try {
				ears$reentering = true;
				EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> method_3312(image, _x1, _y1, _x2, _y2), image.getHeight() != 32);
			} finally {
				ears$reentering = false;
			}
		}
		ci.cancel();
	}
	
	@Shadow
	private static void method_3312(NativeImage image, int x, int y, int width, int height) {}
	
}
