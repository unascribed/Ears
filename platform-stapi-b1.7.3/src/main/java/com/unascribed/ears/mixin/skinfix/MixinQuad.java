package com.unascribed.ears.mixin.skinfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.unascribed.ears.TextureScaler;

import net.minecraft.client.model.Quad;

@Mixin(Quad.class)
public class MixinQuad {
	@ModifyConstant(method = "<init>([Lnet/minecraft/client/model/Vertex;IIII)V", constant = @Constant(floatValue = 64.0F))
	private float getWidth(float width) {
		return TextureScaler.WIDTH;
	}

	@ModifyConstant(method = "<init>([Lnet/minecraft/client/model/Vertex;IIII)V", constant = @Constant(floatValue = 32.0F))
	private float getHeight(float height) {
		return TextureScaler.HEIGHT;
	}
}
