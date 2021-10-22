package com.unascribed.ears.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.SetTranslucent;

import net.minecraft.client.model.ModelPart;

@Mixin(ModelPart.class)
public class MixinModelPart implements SetTranslucent {

	private boolean ears$translucent;
	
	@Inject(method="render(F)V", at=@At("HEAD"))
	public void renderHead(float f, CallbackInfo ci) {
		if (ears$translucent) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	@Inject(method="render(F)V", at=@At("RETURN"))
	public void renderReturn(float f, CallbackInfo ci) {
		if (ears$translucent) {
			GlStateManager.disableBlend();
		}
	}

	@Override
	public void ears$setTranslucent(boolean translucent) {
		this.ears$translucent = translucent;
	}
	
}
