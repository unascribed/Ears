package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.ears.EarsPlayerRenderState;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Mixin(PlayerEntityRenderState.class)
public class MixinPlayerEntityRenderState implements EarsPlayerRenderState {

	private double ears$capeX, ears$capeY, ears$capeZ;
	private float ears$horizontalSpeed, ears$stride;
	private boolean ears$flying;
	
	@Override
	public double ears$getCapeX() {
		return ears$capeX;
	}

	@Override
	public double ears$getCapeY() {
		return ears$capeY;
	}

	@Override
	public double ears$getCapeZ() {
		return ears$capeZ;
	}

	@Override
	public float ears$getHorizontalSpeed() {
		return ears$horizontalSpeed;
	}

	@Override
	public float ears$getStride() {
		return ears$stride;
	}

	@Override
	public boolean ears$isFlying() {
		return ears$flying;
	}

	@Override
	public void ears$update(AbstractClientPlayerEntity acpe, float delta) {
		ears$capeX = MathHelper.lerp(delta, acpe.lastCapeX, acpe.capeX);
		ears$capeY = MathHelper.lerp(delta, acpe.lastCapeY, acpe.capeY);
		ears$capeZ = MathHelper.lerp(delta, acpe.lastCapeZ, acpe.capeZ);
		ears$horizontalSpeed = MathHelper.lerp(delta, acpe.lastDistanceMoved, acpe.distanceMoved);
		ears$stride = MathHelper.lerp(delta, acpe.lastStrideDistance, acpe.strideDistance);
		ears$flying = acpe.getAbilities().flying;
	}

}
