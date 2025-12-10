package com.unascribed.ears;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.world.entity.Avatar;

public interface EarsPlayerRenderState {

    double ears$getCapeX();
    double ears$getCapeY();
    double ears$getCapeZ();

    float ears$getHorizontalSpeed();
    float ears$getStride();

    boolean ears$isFlying();

    <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void ears$update(AvatarlikeEntity acpe, float delta);

}
