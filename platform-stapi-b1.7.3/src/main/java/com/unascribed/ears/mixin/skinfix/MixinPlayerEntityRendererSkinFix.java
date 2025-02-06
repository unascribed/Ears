package com.unascribed.ears.mixin.skinfix;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.ears.EarsMod;
import com.unascribed.ears.ModelPartTextureFixer;
import com.unascribed.ears.ModelPartTrans;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.legacy.LegacyHelper;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRendererSkinFix extends LivingEntityRenderer {
	@Shadow
	private BipedEntityModel bipedModel;

	public MixinPlayerEntityRendererSkinFix() {
		super(new BipedEntityModel(0.0F), 0.5F);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void amendLayers(CallbackInfo info) {
		EarsLog.debug(EarsLog.Tag.PLATFORM, "Hacking 64x64 skin support into player model");

		BipedEntityModel model = new BipedEntityModel(0.0F);

		// fix texture size
		model.head = new ModelPart(0, 0);
		((ModelPartTextureFixer) model.head).setTextureHeight(64);
		model.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		model.head.setPivot(0.0F, 0.0F, 0.0F);
		model.hat = new ModelPartTrans(32, 0);
		((ModelPartTextureFixer) model.hat).setTextureHeight(64);
		model.hat.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		model.hat.setPivot(0.0F, 0.0F, 0.0F);
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

		// slim arms
		ModelPart slimLeftArm =  new ModelPart(32, 48);
		((ModelPartTextureFixer) slimLeftArm).setTextureHeight(64);
		slimLeftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		slimLeftArm.setPivot(5.0F, 2.0F, 0.0F);

		ModelPart slimRightArm =  new ModelPart(40, 16);
		((ModelPartTextureFixer) slimRightArm).setTextureHeight(64);
		slimRightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
		slimRightArm.setPivot(-5.0F, 2.0F, 0.0F);

		EarsMod.fatLeftArm = model.leftArm;
		EarsMod.fatRightArm = model.rightArm;

		EarsMod.slimLeftArm = slimLeftArm;
		EarsMod.slimRightArm = slimRightArm;

		this.model = model;
		this.bipedModel = model;
	}

	private void fixSlimArm(PlayerEntity player) {
        boolean slim = LegacyHelper.isSlimArms(player.name);
        BipedEntityModel headModel = this.bipedModel;
        BipedEntityModel entityModel = (BipedEntityModel)this.model;

        if (slim) {
          headModel.leftArm = EarsMod.slimLeftArm;
          headModel.rightArm = EarsMod.slimRightArm;

          entityModel.leftArm = EarsMod.slimLeftArm;
          entityModel.rightArm = EarsMod.slimRightArm;
        } else {
          headModel.leftArm = EarsMod.fatLeftArm;
          headModel.rightArm = EarsMod.fatRightArm;

          entityModel.leftArm = EarsMod.fatLeftArm;
          entityModel.rightArm = EarsMod.fatRightArm;
        }
    }

	@Inject(method = "renderHand", at = @At("HEAD"))
    private void amendFirstPersonArmHead(CallbackInfo info) {
		PlayerEntity player = EarsMod.client.player;
		if (player != null) {
		  fixSlimArm(player);
		}
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
    @Inject(method = "renderMore", at = @At("RETURN"))
    private void renderSpecials(PlayerEntity player, float ticks, CallbackInfo info) {
		fixSlimArm(player);
	}
	
	@Inject(method = "renderHand", at = @At("RETURN"))
	private void amendFirstPersonArm(CallbackInfo info) {
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	@Inject(method="bindTexture", at=@At("HEAD"))
	protected void bindTextureHead(PlayerEntity arg, int i, float f, CallbackInfoReturnable<Boolean> ci) {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	@Inject(method="bindTexture", at=@At("RETURN"))
	protected void bindTextureReturn(PlayerEntity arg, int i, float f, CallbackInfoReturnable<Boolean> ci) {
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
