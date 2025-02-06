package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(ElytraFeatureRenderer.class)
public class MixinElytraFeatureRenderer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity) {
			if (EarsCommon.shouldSuppressElytra(EarsMod.getEarsFeatures((AbstractClientPlayerEntity)entity))) {
				ci.cancel();
			}
		}
	}
	
}
