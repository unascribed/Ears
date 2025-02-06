package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRendererEarsLayer {
  
    @Shadow
    private BipedEntityModel bipedModel;
    

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void amendFirstPersonArmHead(CallbackInfo info) {
      PlayerEntity player = EarsMod.client.player;
      if (player != null) {
        EarsMod.layer.renderRightArm((PlayerEntityRenderer) (Object) this, player);
      }
    }
    @Inject(method = "renderMore", at = @At("RETURN"))
    private void renderSpecials(PlayerEntity player, float ticks, CallbackInfo info) {
		    EarsLog.debug(EarsLog.Tag.PLATFORM, "renderSpecials player={}, partialTicks={}", player, ticks);
		    EarsMod.layer.doRenderLayer((PlayerEntityRenderer) (Object) this, player, player.lastScreenDistortion + (player.screenDistortion - player.lastScreenDistortion) * ticks, ticks);
    }
}
