package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.BipedModelLayers;
import com.unascribed.ears.EarsMod;
import com.unascribed.ears.ModelPartTextureFixer;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.model.BipedModel;
import net.minecraft.entity.player.Player;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer extends LivingEntityRenderer {
	@Shadow
	private BipedModel headRenderer;

	public MixinPlayerRenderer() {
		super(new BipedModel(0.0F), 0.5F);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void amendLayers(CallbackInfo info) {
		EarsLog.debug("Platform", "Hacking 64x64 skin support into player model");

		BipedModel model = new BipedModel(0.0F);

		// fix texture size
		model.head = new ModelPart(0, 0);
		((ModelPartTextureFixer) model.head).setTextureHeight(64);
		model.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		model.head.setPivot(0.0F, 0.0F, 0.0F);
		model.helmet = new ModelPart(32, 0);
		((ModelPartTextureFixer) model.helmet).setTextureHeight(64);
		model.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		model.helmet.setPivot(0.0F, 0.0F, 0.0F);
		model.body = new ModelPart(16, 16);
		((ModelPartTextureFixer) model.body).setTextureHeight(64);
		model.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		model.body.setPivot(0.0F, 0.0F, 0.0F);
		model.rightArm = new ModelPart(40, 16);
		((ModelPartTextureFixer) model.rightArm).setTextureHeight(64);
		model.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		model.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		model.rightLeg = new ModelPart(0, 16);
		((ModelPartTextureFixer) model.rightLeg).setTextureHeight(64);
		model.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		model.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);

		// non-flipped left arm/leg
		model.leftArm = new ModelPart(32, 48);
		((ModelPartTextureFixer) model.leftArm).setTextureHeight(64);
		model.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		model.leftArm.setPivot(5.0F, 2.0F, 0.0F);

		model.leftLeg = new ModelPart(16, 48);
		((ModelPartTextureFixer) model.leftLeg).setTextureHeight(64);
		model.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		model.leftLeg.setPivot(2.0F, 12.0F, 0.0F);

		// non-head secondary layers
		ModelPart leftSleeve = new ModelPart(48, 48);
		((ModelPartTextureFixer) leftSleeve).setTextureHeight(64);
		leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F);
		leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
		((BipedModelLayers) model).setLeftSleeve(leftSleeve);

		ModelPart rightSleeve = new ModelPart(40, 32);
		((ModelPartTextureFixer) rightSleeve).setTextureHeight(64);
		rightSleeve.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F);
		rightSleeve.setPivot(-5.0F, 2.0F, 0.0F);
		((BipedModelLayers) model).setRightSleeve(rightSleeve);

		ModelPart jacket = new ModelPart(16, 32);
		((ModelPartTextureFixer) jacket).setTextureHeight(64);
		jacket.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F);
		jacket.setPivot(0.0F, 0.0F, 0.0F);
		((BipedModelLayers) model).setJacket(jacket);

		ModelPart leftPantLeg = new ModelPart(0, 48);
		((ModelPartTextureFixer) leftPantLeg).setTextureHeight(64);
		leftPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F);
		leftPantLeg.setPivot(2.0F, 12.0F, 0.0F);
		((BipedModelLayers) model).setLeftPantLeg(leftPantLeg);

		ModelPart rightPantLeg = new ModelPart(0, 32);
		((ModelPartTextureFixer) rightPantLeg).setTextureHeight(64);
		rightPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F);
		rightPantLeg.setPivot(-2.0F, 12.0F, 0.0F);
		((BipedModelLayers) model).setRightPantLeg(rightPantLeg);

		this.field_909 = model;
		this.headRenderer = model;
	}

	@Inject(method = "method_827", at = @At("RETURN"))
	private void renderSpecials(Player player, float ticks, CallbackInfo info) {
		EarsLog.debug("Platform", "renderSpecials player={}, partialTicks={}", player, ticks);
		EarsMod.layer.doRenderLayer((PlayerRenderer) (Object) this, player, player.field_505 + (player.field_504 - player.field_505) * ticks, ticks);
	}

	@Inject(method = "method_345", at = @At("RETURN"))
	private void amendFirstPersonArm(CallbackInfo info) {
		ModelPart rightSleeve = ((BipedModelLayers) this.headRenderer).getRightSleeve();
		if (rightSleeve != null)
			rightSleeve.render(0.0625F);
		Player player = EarsMod.client.player;
		if (player != null)
			EarsMod.layer.renderRightArm((PlayerRenderer) (Object) this, player);
	}
}
