package com.unascribed.ears.mixin;

import java.io.InputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.EarsAwareTexture;

import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

@Mixin(DownloadingTexture.class)
public abstract class MixinPlayerSkinTexture extends SimpleTexture implements EarsAwareTexture {

	public MixinPlayerSkinTexture(ResourceLocation location) {
		super(location);
	}
	
	private boolean earsEnabled = false;

	@Inject(at=@At("RETURN"), method = "loadTexture(Ljava/io/InputStream;)Lnet/minecraft/client/renderer/texture/NativeImage;")
	private void loadTexture(InputStream stream, CallbackInfoReturnable<NativeImage> ci) {
		NativeImage cur = ci.getReturnValue();
		if (cur.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					if ((cur.getPixelRGBA(x, y)&0x00FFFFFF) != 0xD8233F) {
						allMatch = false;
						break out;
					}
				}
			}
			earsEnabled = allMatch;
		}
	}
	
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
	
	@Override
	public boolean isEarsEnabled() {
		return earsEnabled;
	}
	
	
}
