package com.unascribed.ears;

import net.minecraft.client.player.AbstractClientPlayer;

public interface EarsPlayerRenderState {

    double ears$getCapeX();
    double ears$getCapeY();
    double ears$getCapeZ();

    float ears$getHorizontalSpeed();
    float ears$getStride();

    boolean ears$isFlying();

    void ears$update(AbstractClientPlayer acpe, float delta);

}
