package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	@Inject(at=@At("HEAD"), method = "method_4534(Lnet/minecraft/client/texture/NativeImage;)V")
	public void method_4534(NativeImage cur, CallbackInfo ci) {
		if (cur.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					if ((cur.getPixelRgba(x, y)&0x00FFFFFF) != 0xD8233F) {
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
