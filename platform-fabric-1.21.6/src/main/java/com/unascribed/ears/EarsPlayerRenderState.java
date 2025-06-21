package com.unascribed.ears;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public interface EarsPlayerRenderState {

	double ears$getCapeX();
	double ears$getCapeY();
	double ears$getCapeZ();
	
	float ears$getHorizontalSpeed();
	float ears$getStride();
	
	boolean ears$isFlying();

	void ears$update(AbstractClientPlayerEntity acpe, float delta);
	
}
