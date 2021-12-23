package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayerEntity) {
			if (EarsCommon.shouldSuppressElytra(EarsLayerRenderer.getEarsFeatures((AbstractClientPlayerEntity)entity))) {
				ci.cancel();
			}
		}
	}
	
}
