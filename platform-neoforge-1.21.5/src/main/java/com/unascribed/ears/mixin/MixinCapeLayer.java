package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {

    @Inject(at=@At("HEAD"), method="render", cancellable=true)
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, PlayerRenderState player, float f, float g, CallbackInfo ci) {
        EarsFeatures features = EarsLayerRenderer.getEarsFeatures(player);
        if (features != null && (!features.capeEnabled || EarsInhibitorRegistry.isInhibited(EarsFeatureType.CAPE, player) != null)) {
            ci.cancel();
        }
    }

}
