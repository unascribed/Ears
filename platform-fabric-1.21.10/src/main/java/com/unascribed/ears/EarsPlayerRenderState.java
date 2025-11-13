package com.unascribed.ears;

import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.entity.PlayerLikeEntity;

public interface EarsPlayerRenderState {

	double ears$getCapeX();
	double ears$getCapeY();
	double ears$getCapeZ();
	
	float ears$getHorizontalSpeed();
	float ears$getStride();

	boolean ears$isFlying();

	<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> void ears$update(AvatarlikeEntity acpe, float delta);
	
}
