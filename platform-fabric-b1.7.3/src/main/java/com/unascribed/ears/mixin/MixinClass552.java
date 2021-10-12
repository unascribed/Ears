package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.unascribed.ears.TextureScaler;

import net.minecraft.class_552;

@Mixin(class_552.class)
public class MixinClass552 {
	@ModifyConstant(method = "<init>([Lnet/minecraft/class_290;IIII)V", constant = @Constant(floatValue = 64.0F))
	private float getWidth(float width) {
		return TextureScaler.WIDTH;
	}

	@ModifyConstant(method = "<init>([Lnet/minecraft/class_290;IIII)V", constant = @Constant(floatValue = 32.0F))
	private float getHeight(float height) {
		return TextureScaler.HEIGHT;
	}
}
