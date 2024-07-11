package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(PoseStack p_116951_, MultiBufferSource p_116952_, int p_116953_, LivingEntity entity, float p_116955_, float p_116956_, float p_116957_, float p_116958_, float p_116959_, float p_116960_, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayer) {
			if (EarsCommon.shouldSuppressElytra(EarsLayerRenderer.getEarsFeatures((AbstractClientPlayer)entity))) {
				ci.cancel();
			}
		}
	}
	
}
