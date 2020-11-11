package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsLog;

import net.minecraft.client.renderer.DownloadImageBuffer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.NativeImage;

@Mixin(DownloadImageBuffer.class)
public abstract class MixinDownloadImageBuffer implements IImageBuffer {

	private static boolean ears$reentering;
	
	@Inject(at = @At("HEAD"), method = "setAreaOpaque(Lnet/minecraft/client/renderer/texture/NativeImage;IIII)V", cancellable = true)
	private static void setAreaOpaque(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "stripAlpha({}, {}, {}, {}, {}) reentering={}", image, x1, y2, x2, y2, ears$reentering);
		if (ears$reentering) return;
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			try {
				ears$reentering = true;
				EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setAreaOpaque(image, _x1, _y1, _x2, _y2), image.getHeight() != 32);
			} finally {
				ears$reentering = false;
			}
		}
		ci.cancel();
	}
	
	@Shadow
	private static void setAreaOpaque(NativeImage image, int x1, int y1, int x2, int y2) {}
	
}
