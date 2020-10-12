package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.DownloadImageBuffer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.NativeImage;

@Mixin(DownloadImageBuffer.class)
public abstract class MixinSkinRemappingImageFilter implements IImageBuffer {

	@Inject(at = @At("HEAD"), method = "setAreaOpaque(Lnet/minecraft/client/renderer/texture/NativeImage;IIII)V", cancellable = true)
	private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height, CallbackInfo ci) {
		if (x == 0 && y == 0 && width == 32 && height == 16) {
			// Leave the unused corners of the head texture transparent-capable for ears.
			ci.cancel();
			setAreaOpaque(image, 8, 0, 16, 8);
			setAreaOpaque(image, 0, 8, 32, 8);
		}
		if (x == 0 && y == 16 && width == 64 && height == 32) {
			// Leave the unused space to the right of the body texture transparent-capable for ears.
			ci.cancel();
			setAreaOpaque(image, 0, 16, 56, 32);
		}
	}
	
	@Shadow
	private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height) {}
	
}
