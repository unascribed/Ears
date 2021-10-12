package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.BipedModelLayers;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedModel;
import net.minecraft.client.render.entity.model.EntityModel;

@Mixin(BipedModel.class)
public class MixinBipedModel extends EntityModel implements BipedModelLayers {
	@Shadow
	public ModelPart leftArm;
	@Shadow
	public ModelPart leftLeg;

	public ModelPart leftSleeve = null;
	public ModelPart rightSleeve = null;
	public ModelPart leftPantLeg = null;
	public ModelPart rightPantLeg = null;
	public ModelPart jacket = null;

	@Inject(method = "render", at = @At("RETURN"))
	private void addLayerParts(float f, float f1, float f2, float f3, float f4, float f5, CallbackInfo info) {
		if (this.leftSleeve != null)
			this.leftSleeve.render(f5);
		if (this.rightSleeve != null)
			this.rightSleeve.render(f5);
		if (this.leftPantLeg != null)
			this.leftPantLeg.render(f5);
		if (this.rightPantLeg != null)
			this.rightPantLeg.render(f5);
		if (this.jacket != null)
			this.jacket.render(f5);
	}

	@Inject(method = "setAngles", at = @At("RETURN"))
	private void setLayerAngles(float f, float f1, float f2, float f3, float f4, float f5, CallbackInfo info) {
		BipedModel realModel = (BipedModel) (Object) this;

		if (this.leftSleeve != null) {
			this.leftSleeve.pitch = realModel.leftArm.pitch;
			this.leftSleeve.yaw = realModel.leftArm.yaw;
			this.leftSleeve.roll = realModel.leftArm.roll;
			this.leftSleeve.pivotX = realModel.leftArm.pivotX;
			this.leftSleeve.pivotY = realModel.leftArm.pivotY;
			this.leftSleeve.pivotZ = realModel.leftArm.pivotZ;
		}
		if (this.rightSleeve != null) {
			this.rightSleeve.pitch = realModel.rightArm.pitch;
			this.rightSleeve.yaw = realModel.rightArm.yaw;
			this.rightSleeve.roll = realModel.rightArm.roll;
			this.rightSleeve.pivotX = realModel.rightArm.pivotX;
			this.rightSleeve.pivotY = realModel.rightArm.pivotY;
			this.rightSleeve.pivotZ = realModel.rightArm.pivotZ;
		}
		if (this.leftPantLeg != null) {
			this.leftPantLeg.pitch = realModel.leftLeg.pitch;
			this.leftPantLeg.yaw = realModel.leftLeg.yaw;
			this.leftPantLeg.roll = realModel.leftLeg.roll;
			this.leftPantLeg.pivotX = realModel.leftLeg.pivotX;
			this.leftPantLeg.pivotY = realModel.leftLeg.pivotY;
			this.leftPantLeg.pivotZ = realModel.leftLeg.pivotZ;
		}
		if (this.rightPantLeg != null) {
			this.rightPantLeg.pitch = realModel.rightLeg.pitch;
			this.rightPantLeg.yaw = realModel.rightLeg.yaw;
			this.rightPantLeg.roll = realModel.rightLeg.roll;
			this.rightPantLeg.pivotX = realModel.rightLeg.pivotX;
			this.rightPantLeg.pivotY = realModel.rightLeg.pivotY;
			this.rightPantLeg.pivotZ = realModel.rightLeg.pivotZ;
		}
		if (this.jacket != null) {
			this.jacket.pitch = realModel.body.pitch;
			this.jacket.yaw = realModel.body.yaw;
			this.jacket.roll = realModel.body.roll;
			this.jacket.pivotX = realModel.body.pivotX;
			this.jacket.pivotY = realModel.body.pivotY;
			this.jacket.pivotZ = realModel.body.pivotZ;
		}
	}

	@Override
	public ModelPart getLeftSleeve() {
		return this.leftSleeve;
	}

	@Override
	public void setLeftSleeve(ModelPart part) {
		this.leftSleeve = part;
	}

	@Override
	public ModelPart getRightSleeve() {
		return this.rightSleeve;
	}

	@Override
	public void setRightSleeve(ModelPart part) {
		this.rightSleeve = part;
	}

	@Override
	public ModelPart getLeftPantLeg() {
		return this.leftPantLeg;
	}

	@Override
	public void setLeftPantLeg(ModelPart part) {
		this.leftPantLeg = part;
	}

	@Override
	public ModelPart getRightPantLeg() {
		return this.rightPantLeg;
	}

	@Override
	public void setRightPantLeg(ModelPart part) {
		this.rightPantLeg = part;
	}

	@Override
	public ModelPart getJacket() {
		return this.jacket;
	}

	@Override
	public void setJacket(ModelPart part) {
		this.jacket = part;
	}
}