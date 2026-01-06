package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsMod;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {

    @Inject(at=@At("HEAD"), method="submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", cancellable=true)
    public void render(PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider, int i, AvatarRenderState player, float f, float g, CallbackInfo ci) {
        EarsFeatures features = EarsMod.getEarsFeatures(player);
        if (features != null && (features.capeEnabled || EarsInhibitorRegistry.isInhibited(EarsFeatureType.CAPE, player) != null)) {
            ci.cancel();
        }
    }

}
