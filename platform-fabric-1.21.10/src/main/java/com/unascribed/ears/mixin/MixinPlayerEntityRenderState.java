package com.unascribed.ears.mixin;

import com.unascribed.ears.EarsPlayerRenderState;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

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
	public <AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> void ears$update(AvatarlikeEntity acpe, float delta) {
        ClientPlayerLikeState state = acpe.getState();
        ears$capeX = state.lerpX(delta);
        ears$capeY = state.lerpY(delta);
        ears$capeZ = state.lerpZ(delta);
        ears$horizontalSpeed = state.getLerpedDistanceMoved(delta);
        ears$stride = state.lerpMovement(delta);
		ears$flying = acpe instanceof PlayerEntity playerEntity && playerEntity.getAbilities().flying;
	}

}
