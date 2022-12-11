package com.unascribed.ears.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsFeatureRenderer;
import com.unascribed.ears.common.debug.EarsLog;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	public MixinPlayerEntityRenderer(Context arg, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(arg, model, shadowRadius);
	}

	private EarsFeatureRenderer ears$featureRenderer;
	
	@Inject(at=@At("TAIL"), method="<init>", allow=1)
	private void init(Context arg, boolean bl, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Construct player renderer");
		this.addFeature(ears$featureRenderer = new EarsFeatureRenderer((PlayerEntityRenderer)(Object)this));
	}
	
	@Inject(at = @At("TAIL"), method = "renderLeftArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;)V")
	private void renderLeftArm(MatrixStack ms, VertexConsumerProvider vcp, int light, AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$featureRenderer.renderLeftArm(ms, vcp, light, e);
	}
	
	@Inject(at = @At("TAIL"), method = "renderRightArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;)V")
	private void renderRightArm(MatrixStack ms, VertexConsumerProvider vcp, int light, AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$featureRenderer.renderRightArm(ms, vcp, light, e);
	}
	
}
