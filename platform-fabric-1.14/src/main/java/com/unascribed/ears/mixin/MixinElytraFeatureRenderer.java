package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.entity.LivingEntity;

@Mixin(ElytraFeatureRenderer.class)
public class MixinElytraFeatureRenderer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(LivingEntity entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity) {
			if (EarsCommon.shouldSuppressElytra(EarsMod.getEarsFeatures((AbstractClientPlayerEntity)entity))) {
				ci.cancel();
			}
		}
	}
	
}
