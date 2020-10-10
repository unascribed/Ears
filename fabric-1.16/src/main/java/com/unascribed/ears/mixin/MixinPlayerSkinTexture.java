package com.unascribed.ears.mixin;

import java.io.InputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.EarsAwareTexture;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

@Mixin(PlayerSkinTexture.class)
public abstract class MixinPlayerSkinTexture extends ResourceTexture implements EarsAwareTexture {

	public MixinPlayerSkinTexture(Identifier location) {
		super(location);
	}
	
	private boolean earsEnabled = false;

	@Inject(at=@At("RETURN"), method = "loadTexture(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;")
	private void loadTexture(InputStream stream, CallbackInfoReturnable<NativeImage> ci) {
		NativeImage cur = ci.getReturnValue();
		if (cur.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					if ((cur.getPixelColor(x, y)&0x00FFFFFF) != 0xD8233F) {
						allMatch = false;
						break out;
					}
				}
			}
			earsEnabled = allMatch;
		}
	}
	
	@Inject(at = @At("HEAD"), method = "stripAlpha(Lnet/minecraft/client/texture/NativeImage;IIII)V", cancellable = true)
	private static void stripAlpha(NativeImage image, int x, int y, int width, int height, CallbackInfo ci) {
		if (x == 0 && y == 0 && width == 32 && height == 16) {
			// Leave the unused corners of the head texture transparent-capable for ears.
			ci.cancel();
			stripAlpha(image, 8, 0, 16, 8);
			stripAlpha(image, 0, 8, 32, 8);
		}
		if (x == 0 && y == 16 && width == 64 && height == 32) {
			// Leave the unused space to the right of the body texture transparent-capable for ears.
			ci.cancel();
			stripAlpha(image, 0, 16, 56, 32);
		}
	}
	
	@Shadow
	private static void stripAlpha(NativeImage image, int x, int y, int width, int height) {}
	
	@Override
	public boolean isEarsEnabled() {
		return earsEnabled;
	}
	
	
}
