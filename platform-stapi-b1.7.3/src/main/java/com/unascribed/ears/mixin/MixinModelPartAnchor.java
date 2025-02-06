package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.ModelPartAnchor;

import net.minecraft.client.model.ModelPart;

@Mixin(ModelPart.class)
public class MixinModelPartAnchor implements ModelPartAnchor {
	private float posX1;
	private float posY1;
	private float posZ1;
	private float posX2;
	private float posY2;
	private float posZ2;

	@Inject(method = "addCuboid(FFFIIIF)V", at = @At("HEAD"))
	private void cuboidPositions(float f, float f1, float f2, int i, int j, int k, float f3, CallbackInfo info) {
		setPosX1(f);
		setPosY1(f1);
		setPosZ1(f2);
		setPosX2(f + i);
		setPosY2(f1 + j);
		setPosZ2(f2 + k);
	}

	@Override
	public float getPosX1() {
		return this.posX1;
	}

	@Override
	public void setPosX1(float f) {
		this.posX1 = f;
	}

	@Override
	public float getPosY1() {
		return this.posY1;
	}

	@Override
	public void setPosY1(float f) {
		this.posY1 = f;
	}

	@Override
	public float getPosZ1() {
		return this.posZ1;
	}

	@Override
	public void setPosZ1(float f) {
		this.posZ1 = f;
	}

	@Override
	public float getPosX2() {
		return this.posX2;
	}

	@Override
	public void setPosX2(float f) {
		this.posX2 = f;
	}

	@Override
	public float getPosY2() {
		return this.posY2;
	}

	@Override
	public void setPosY2(float f) {
		this.posY2 = f;
	}

	@Override
	public float getPosZ2() {
		return this.posZ2;
	}

	@Override
	public void setPosZ2(float f) {
		this.posZ2 = f;
	}
}
