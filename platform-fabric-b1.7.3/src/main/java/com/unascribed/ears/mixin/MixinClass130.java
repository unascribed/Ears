package com.unascribed.ears.mixin;

import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsDownloadThread;

import net.minecraft.class_130;
import net.minecraft.client.ImageProcessor;

@Mixin(class_130.class)
public class MixinClass130 {
	@Shadow
	public BufferedImage image;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void replaceThread(String string, ImageProcessor imageProcessor, CallbackInfo info) {
		new EarsDownloadThread(string, imageProcessor, image);
	}
}