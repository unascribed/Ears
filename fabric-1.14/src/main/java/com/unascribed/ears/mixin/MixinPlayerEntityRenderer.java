package com.unascribed.ears.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsFeatureRenderer;
import com.unascribed.ears.common.EarsLog;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	public MixinPlayerEntityRenderer(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(dispatcher, model, shadowRadius);
	}

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V")
	private void init(EntityRenderDispatcher erd, boolean b, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Construct player renderer");
		this.addFeature(new EarsFeatureRenderer(this));
	}
	
}
