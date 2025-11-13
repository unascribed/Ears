package com.unascribed.ears.mixin;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.ears.EarsPlayerRenderState;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

@Mixin(AvatarRenderState.class)
public class MixinPlayerRenderState implements EarsPlayerRenderState {

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
    public <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void ears$update(AvatarlikeEntity acpe, float delta) {
        ClientAvatarState state = acpe.avatarState();
        ears$capeX = state.getInterpolatedCloakX(delta);
        ears$capeY = state.getInterpolatedCloakY(delta);
        ears$capeZ = state.getInterpolatedCloakZ(delta);
        ears$horizontalSpeed = state.getInterpolatedWalkDistance(delta);
        ears$stride = state.getInterpolatedBob(delta);
        ears$flying = acpe instanceof Player playerEntity && playerEntity.getAbilities().flying;
    }

}
