package com.unascribed.ears.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsLog;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

	public MixinPlayerRenderer(EntityRendererManager dispatcher, PlayerModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(dispatcher, model, shadowRadius);
	}

	private EarsLayerRenderer ears$layerRenderer;
	
	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Z)V")
	private void init(EntityRendererManager erd, boolean b, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Construct player renderer");
		this.addLayer(ears$layerRenderer = new EarsLayerRenderer(this));
	}
	
	@Inject(at = @At("TAIL"), method = "renderLeftArm(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V")
	private void renderLeftArm(MatrixStack ms, IRenderTypeBuffer vcp, int light, AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$layerRenderer.renderLeftArm(ms, vcp, light, e);
	}
	
	@Inject(at = @At("TAIL"), method = "renderRightArm(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V")
	private void renderRightArm(MatrixStack ms, IRenderTypeBuffer vcp, int light, AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$layerRenderer.renderRightArm(ms, vcp, light, e);
	}
	
}
