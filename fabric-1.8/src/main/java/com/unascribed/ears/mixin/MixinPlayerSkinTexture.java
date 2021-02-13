package com.unascribed.ears.mixin;

import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.AWTEarsImage;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsImage;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.RawEarsImage;

import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

@Mixin(PlayerSkinTexture.class)
public abstract class MixinPlayerSkinTexture extends ResourceTexture implements EarsFeaturesHolder {

	public MixinPlayerSkinTexture(Identifier location) {
		super(location);
	}
	
	private EarsFeatures earsFeatures;

	@Inject(at=@At("HEAD"), method = "method_4198(Ljava/awt/image/BufferedImage;)V")
	public void method_4198(BufferedImage cur, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Process player skin");
		EarsImage img = new AWTEarsImage(cur);
		earsFeatures = EarsFeatures.detect(img);
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
	
}
