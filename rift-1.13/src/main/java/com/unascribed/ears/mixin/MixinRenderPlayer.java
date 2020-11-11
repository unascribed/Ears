package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsLog;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.model.ModelBase;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> {

	public MixinRenderPlayer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
	}

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V")
	private void init(RenderManager erd, boolean b, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Construct player renderer");
		RenderPlayer self = (RenderPlayer)(Object)this;
		this.addLayer(new EarsLayerRenderer(self));
	}
	
}
