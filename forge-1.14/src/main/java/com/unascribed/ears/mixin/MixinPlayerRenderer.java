package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsLog;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

	public MixinPlayerRenderer(EntityRendererManager dispatcher, PlayerModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(dispatcher, model, shadowRadius);
	}

	private EarsLayerRenderer ears$LayerRenderer;
	
	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Z)V")
	private void init(EntityRendererManager erd, boolean b, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Construct player renderer");
		this.addLayer(ears$LayerRenderer = new EarsLayerRenderer(this));
	}
	
	@Inject(at = @At("TAIL"), method = "renderLeftArm(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V")
	private void renderLeftArm(AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$LayerRenderer.renderLeftArm(e);
	}
	
	@Inject(at = @At("TAIL"), method = "renderRightArm(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V")
	private void renderRightArm(AbstractClientPlayerEntity e, CallbackInfo ci) {
		ears$LayerRenderer.renderRightArm(e);
	}
	
}
