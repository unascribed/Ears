package com.unascribed.ears.mixin;

import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsDownloadThread;

import net.minecraft.client.texture.ImageDownload;
import net.minecraft.client.texture.ImageProcessor;

@Mixin(ImageDownload.class)
public class MixinImageDownload {
	@Shadow
	public BufferedImage image;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void replaceThread(String string, ImageProcessor imageProcessor, CallbackInfo info) {
		new EarsDownloadThread(string, imageProcessor, image);
	}
}