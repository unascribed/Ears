package com.unascribed.ears.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsFeatureRenderer;
import com.unascribed.ears.common.debug.EarsLog;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity> {

	public MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher, EntityModel model, float shadowSize) {
		super(dispatcher, model, shadowSize);
	}

	private EarsFeatureRenderer ears$featureRenderer;
	
	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V")
	private void init(EntityRenderDispatcher erd, boolean b, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Construct player renderer");
		this.addFeature(ears$featureRenderer = new EarsFeatureRenderer((PlayerEntityRenderer)(Object)this));
	}
	
	@Inject(at = @At("TAIL"), method = "method_4122(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V")
	private void renderLeftArm(AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$featureRenderer.renderLeftArm(e);
	}
	
	@Inject(at = @At("TAIL"), method = "method_4121(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V")
	private void renderRightArm(AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$featureRenderer.renderRightArm(e);
	}
	
}
