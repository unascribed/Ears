package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public MixinPlayerRenderer(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	private EarsLayerRenderer ears$layerRenderer;
	
	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V")
	private void init(EntityRendererProvider.Context erd, boolean b, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Construct player renderer");
		this.addLayer(ears$layerRenderer = new EarsLayerRenderer(this));
	}

	@Inject(at = @At("TAIL"), method = "renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;)V")
	private void renderLeftArm(PoseStack ms, MultiBufferSource vcp, int light, AbstractClientPlayer e, CallbackInfo ci) {
		ears$layerRenderer.renderLeftArm(ms, vcp, light, e);
	}
	
	@Inject(at = @At("TAIL"), method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;)V")
	private void renderRightArm(PoseStack ms, MultiBufferSource vcp, int light, AbstractClientPlayer e, CallbackInfo ci) {
		ears$layerRenderer.renderRightArm(ms, vcp, light, e);
	}
	
}
