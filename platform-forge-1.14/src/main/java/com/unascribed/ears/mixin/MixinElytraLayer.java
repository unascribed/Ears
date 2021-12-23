package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(LivingEntity entity, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity) {
			if (EarsCommon.shouldSuppressElytra(EarsLayerRenderer.getEarsFeatures((AbstractClientPlayerEntity)entity))) {
				ci.cancel();
			}
		}
	}
	
}
