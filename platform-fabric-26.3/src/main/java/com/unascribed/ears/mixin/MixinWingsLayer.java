package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsMod;
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
public class MixinWingsLayer<S extends HumanoidRenderState, M extends EntityModel<S>> {

	@Inject(at=@At("HEAD"), method="submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", cancellable=true)
	public void render(PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int i, S entity, float f, float g, CallbackInfo ci) {
		if (entity instanceof AvatarRenderState player) {
			if (EarsCommon.shouldSuppressElytra(EarsMod.getEarsFeatures(player))) {
				ci.cancel();
			}
		}
	}

}
