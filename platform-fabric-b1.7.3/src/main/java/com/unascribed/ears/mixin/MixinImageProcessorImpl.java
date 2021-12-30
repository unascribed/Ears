package com.unascribed.ears.mixin;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.SkinStripAlpha;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.ImageProcessorImpl;

@Mixin(ImageProcessorImpl.class)
public abstract class MixinImageProcessorImpl {
	@Shadow
	private int[] field_2445;
	@Shadow
	private int field_2446;
	@Shadow
	private int field_2447;

	@Shadow
	protected abstract void method_1898(int i, int j, int k, int i1);

	@Inject(method = "process", at = @At("HEAD"), cancellable = true)
	public void process(BufferedImage image, CallbackInfoReturnable<BufferedImage> info) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "ImageProcessorImpl.process({})", image);
		if (image == null) {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "ImageProcessorImpl.process(...): Image is null");
			info.setReturnValue(null);
		} else {
			this.field_2446 = 64;
			this.field_2447 = 64;

			BufferedImage newImg = new BufferedImage(64, 64, 2);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);

			if (image.getHeight() == 32) {
				EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "ImageProcessorImpl.process(...): Upgrading legacy skin");
				g.drawImage(newImg, 24, 48, 20, 52, 4, 16, 8, 20, null);
				g.drawImage(newImg, 28, 48, 24, 52, 8, 16, 12, 20, null);
				g.drawImage(newImg, 20, 52, 16, 64, 8, 20, 12, 32, null);
				g.drawImage(newImg, 24, 52, 20, 64, 4, 20, 8, 32, null);
				g.drawImage(newImg, 28, 52, 24, 64, 0, 20, 4, 32, null);
				g.drawImage(newImg, 32, 52, 28, 64, 12, 20, 16, 32, null);
				g.drawImage(newImg, 40, 48, 36, 52, 44, 16, 48, 20, null);
				g.drawImage(newImg, 44, 48, 40, 52, 48, 16, 52, 20, null);
				g.drawImage(newImg, 36, 52, 32, 64, 48, 20, 52, 32, null);
				g.drawImage(newImg, 40, 52, 36, 64, 44, 20, 48, 32, null);
				g.drawImage(newImg, 44, 52, 40, 64, 40, 20, 44, 32, null);
				g.drawImage(newImg, 48, 52, 44, 64, 52, 20, 56, 32, null);
			}

			g.dispose();
			this.field_2445 = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
			EarsCommon.carefullyStripAlpha(new SkinStripAlpha((ImageProcessorImpl) (Object) this), true);
			this.method_1898(32, 0, 64, 32);
			this.method_1898(0, 32, 16, 48);
			this.method_1898(16, 32, 40, 48);
			this.method_1898(40, 32, 56, 48);
			this.method_1898(0, 48, 16, 64);
			this.method_1898(48, 48, 64, 64);

			info.setReturnValue(newImg);
		}
	}
}
