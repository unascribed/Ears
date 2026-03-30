package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.EarsPlayerRenderState;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class MixinAvatarRenderer<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {

	public MixinAvatarRenderer(Context arg, PlayerModel model, float shadowRadius) {
		super(arg, model, shadowRadius);
	}

	private EarsLayerRenderer ears$featureRenderer;

	@Inject(at=@At("TAIL"), method="<init>", allow=1)
	private void init(Context arg, boolean bl, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Construct player renderer");
		this.addLayer(ears$featureRenderer = new EarsLayerRenderer((AvatarRenderer)(Object)this));
	}

	@Inject(at = @At("TAIL"), method = "renderLeftHand")
	private void renderLeftArm(PoseStack ms, SubmitNodeCollector queue, int light, Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
		ears$featureRenderer.renderLeftArm(ms, queue, light);
	}

	@Inject(at = @At("TAIL"), method = "renderRightHand")
	private void renderRightArm(PoseStack ms, SubmitNodeCollector queue, int light, Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
		ears$featureRenderer.renderRightArm(ms, queue, light);
	}

	@Inject(at=@At("TAIL"), method="extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V")
	public void ears$updateRenderState(AvatarlikeEntity p, AvatarRenderState s, float f, CallbackInfo ci) {
		((EarsPlayerRenderState)s).ears$update(p, f);
	}

}
