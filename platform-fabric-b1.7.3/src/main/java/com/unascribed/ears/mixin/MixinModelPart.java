package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.ModelPartTextureFixer;
import com.unascribed.ears.TextureScaler;

import net.minecraft.class_290;
import net.minecraft.class_552;
import net.minecraft.client.model.ModelPart;

@Mixin(ModelPart.class)
public class MixinModelPart implements ModelPartTextureFixer {
	private int textureWidth = 64;
	private int textureHeight = 32;

	private float posX1;
	private float posY1;
	private float posZ1;
	private float posX2;
	private float posY2;
	private float posZ2;

	@Redirect(method = "addCuboid(FFFIIIF)V", at = @At(value = "NEW", target = "net/minecraft/class_552"))
	private class_552 fixQuad(class_290[] args, int i, int j, int k, int i1) {
		TextureScaler.WIDTH = getTextureWidth();
		TextureScaler.HEIGHT = getTextureHeight();
		class_552 newQuad = new class_552(args, i, j, k, i1);
		TextureScaler.setDefault();

		return newQuad;
	}

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
	public int getTextureWidth() {
		return this.textureWidth;
	}

	@Override
	public void setTextureWidth(int textureWidth) {
		this.textureWidth = textureWidth;
	}

	@Override
	public int getTextureHeight() {
		return this.textureHeight;
	}

	@Override
	public void setTextureHeight(int textureHeight) {
		this.textureHeight = textureHeight;
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
