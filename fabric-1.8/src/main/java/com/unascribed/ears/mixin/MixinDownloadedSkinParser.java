package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsLog;

import net.minecraft.client.render.DownloadedSkinParser;

@Mixin(DownloadedSkinParser.class)
public abstract class MixinDownloadedSkinParser {

	@Shadow
	private int[] data;
	
	private static boolean ears$reentering;
	
	@Inject(at = @At("HEAD"), method = "setOpaque(IIII)V", cancellable = true)
	private void setOpaque(int x1, int y1, int x2, int y2, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "stripAlpha({}, {}, {}, {}) reentering={}", x1, y2, x2, y2, ears$reentering);
		if (ears$reentering) return;
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			try {
				ears$reentering = true;
				EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setOpaque(_x1, _y1, _x2, _y2), data.length > 64*32);
			} finally {
				ears$reentering = false;
			}
		}
		ci.cancel();
	}
	
	@Shadow
	private void setOpaque(int x1, int y1, int x2, int y2) {}
	
}
