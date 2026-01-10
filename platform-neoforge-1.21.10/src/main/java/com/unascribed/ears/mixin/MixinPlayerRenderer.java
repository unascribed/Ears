package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.EarsLayerRenderer;
import com.unascribed.ears.EarsPlayerRenderState;
import com.unascribed.ears.common.debug.EarsLog;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class MixinPlayerRenderer<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {

	public MixinPlayerRenderer(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}


	private EarsLayerRenderer ears$layerRenderer;
	
	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(EntityRendererProvider.Context erd, boolean b, CallbackInfo ci) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Construct player renderer");
		this.addLayer(ears$layerRenderer = new EarsLayerRenderer((AvatarRenderer)(Object)this));
	}

	@Inject(at = @At("TAIL"), method = "renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;ZLnet/minecraft/client/player/AbstractClientPlayer;)V")
	private void renderLeftArm(PoseStack ms, SubmitNodeCollector vcp, int light, ResourceLocation skinTexture, boolean sleeveVisible, AbstractClientPlayer player, CallbackInfo ci) {
		ears$layerRenderer.renderLeftArm(ms, vcp, light);
	}
	
	@Inject(at = @At("TAIL"), method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;ZLnet/minecraft/client/player/AbstractClientPlayer;)V")
	private void renderRightArm(PoseStack ms, SubmitNodeCollector vcp, int light, ResourceLocation skinTexture, boolean sleeveVisible, AbstractClientPlayer player, CallbackInfo ci) {
		ears$layerRenderer.renderRightArm(ms, vcp, light);
	}

	@Inject(at=@At("TAIL"), method="extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V")
	public void ears$updateRenderState(AvatarlikeEntity p, AvatarRenderState s, float f, CallbackInfo ci) {
		((EarsPlayerRenderState)s).ears$update(p, f);
	}
	
}
