package com.unascribed.ears.mixin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.util.EarsStorage;

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
		if (cur == null) return;
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Process player skin");
		earsFeatures = EarsFeaturesParser.detect(new AWTEarsImage(cur), EarsStorage.get(cur, EarsStorage.Key.ALFALFA),
				data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data))));
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures == null ? EarsFeatures.DISABLED : earsFeatures;
	}
	
}
