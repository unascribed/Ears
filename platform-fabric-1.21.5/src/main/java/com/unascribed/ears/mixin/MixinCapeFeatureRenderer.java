package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(CapeFeatureRenderer.class)
public class MixinCapeFeatureRenderer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState player, float f, float g, CallbackInfo ci) {
		EarsFeatures features = EarsMod.getEarsFeatures(player);
		if (features != null && (!features.capeEnabled || EarsInhibitorRegistry.isInhibited(EarsFeatureType.CAPE, player) != null)) {
			ci.cancel();
		}
	}
	
}
