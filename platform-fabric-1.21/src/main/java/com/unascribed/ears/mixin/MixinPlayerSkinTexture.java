package com.unascribed.ears.mixin;

import java.io.InputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.NativeImageAdapter;
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

	@Inject(at=@At("RETURN"), method = "loadTexture(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;")
	private void loadTexture(InputStream stream, CallbackInfoReturnable<NativeImage> ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
		NativeImage cur = ci.getReturnValue();
		if (cur != null) {
			earsFeatures = EarsFeaturesParser.detect(new NativeImageAdapter(cur), EarsStorage.get(cur, EarsStorage.Key.ALFALFA),
					data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
		}
	}
	
	private static boolean ears$reentering = false;
	
	@Inject(at = @At("HEAD"), method = "stripAlpha(Lnet/minecraft/client/texture/NativeImage;IIII)V", cancellable = true)
	private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "stripAlpha({}, {}, {}, {}, {}) reentering={}", image, x1, y2, x2, y2, ears$reentering);
		if (ears$reentering) return;
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			try {
				ears$reentering = true;
				EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
				EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> stripAlpha(image, _x1, _y1, _x2, _y2), image.getHeight() != 32);
			} finally {
				ears$reentering = false;
			}
		}
		ci.cancel();
	}
	
	@Shadow
	private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures == null ? EarsFeatures.DISABLED : earsFeatures;
	}
	
}
