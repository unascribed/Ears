package com.unascribed.ears.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(WingsLayer.class)
public class MixinElytraLayer<S extends HumanoidRenderState, M extends EntityModel<S>> {

	@Inject(at=@At("HEAD"), method="render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", cancellable=true)
	public void render(PoseStack p_371573_, MultiBufferSource p_371529_, int p_371828_, S entity, float p_371865_, float p_371528_, CallbackInfo ci) {
		if (entity instanceof PlayerRenderState) {
			if (EarsCommon.shouldSuppressElytra(EarsLayerRenderer.getEarsFeatures((PlayerRenderState) entity))) {
				ci.cancel();
			}
		}
	}
	
}
