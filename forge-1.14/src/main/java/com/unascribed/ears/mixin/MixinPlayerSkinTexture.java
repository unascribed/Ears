package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	@Inject(at=@At("HEAD"), method = "setImage(Lnet/minecraft/client/renderer/texture/NativeImage;)V")
	public void setImage(NativeImage cur, CallbackInfo ci) {
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
	
	@Override
	public boolean isEarsEnabled() {
		return earsEnabled;
	}
	
}
