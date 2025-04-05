package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(ElytraFeatureRenderer.class)
public class MixinElytraFeatureRenderer<S extends BipedEntityRenderState, M extends EntityModel<S>> {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S entity, float f, float g, CallbackInfo ci) {
		if (entity instanceof PlayerEntityRenderState player) {
			if (EarsCommon.shouldSuppressElytra(EarsMod.getEarsFeatures(player))) {
				ci.cancel();
			}
		}
	}
	
}
