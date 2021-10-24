package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.common.EarsCommon;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;

@Mixin(LayerElytra.class)
public class MixinElytraLayer {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(EntityLivingBase entity, float p_render_2_, float p_render_3_, float p_render_4_, float p_render_5_, float p_render_6_, float p_render_7_, float p_render_8_, CallbackInfo ci) {
		if (entity instanceof AbstractClientPlayer) {
			if (EarsCommon.shouldSuppressElytra(EarsMod.getEarsFeatures((AbstractClientPlayer)entity))) {
				ci.cancel();
			}
		}
	}
	
}
