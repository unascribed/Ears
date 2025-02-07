package com.unascribed.ears.mixin;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.EarsTexture;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.util.EarsStorage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import net.minecraft.util.Identifier;

@Mixin(PlayerSkinTextureDownloader.class)
public abstract class MixinPlayerSkinTextureDownloader {
	@Inject(at=@At("HEAD"), method = "registerTexture", cancellable=true)
	private static void registerTexture(Identifier textureId, NativeImage image, CallbackInfoReturnable<CompletableFuture<Identifier>> ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
		
		// note: due to thread locality of EarsStorage the alfalfa cannot be retrieved in the async future
		AlfalfaData alfalfa = EarsStorage.get(image, EarsStorage.Key.ALFALFA);
		
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		ci.setReturnValue(CompletableFuture.supplyAsync(() -> {
			minecraftClient.getTextureManager().registerTexture(textureId, new EarsTexture(image, alfalfa));
			return textureId;
		}, minecraftClient));
	}
	
	// Adjust alpha stripping
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
}
