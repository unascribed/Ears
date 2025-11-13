package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.common.EarsCommon;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public class MixinElytraLayer<S extends HumanoidRenderState, M extends EntityModel<S>> {

	@Inject(at=@At("HEAD"), method="submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", cancellable=true)
	public void render(PoseStack p_435137_, SubmitNodeCollector p_434138_, int p_434689_, S entity, float p_433309_, float p_432928_, CallbackInfo ci) {
		if (entity instanceof AvatarRenderState) {
			if (EarsCommon.shouldSuppressElytra(EarsLayerRenderer.getEarsFeatures((AvatarRenderState) entity))) {
				ci.cancel();
			}
		}
	}
	
}
