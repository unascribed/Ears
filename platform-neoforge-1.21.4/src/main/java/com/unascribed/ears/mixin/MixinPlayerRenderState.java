package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.ears.EarsPlayerRenderState;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

@Mixin(PlayerRenderState.class)
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
    public void ears$update(AbstractClientPlayer acpe, float delta) {
        ears$capeX = Mth.lerp(delta, acpe.xCloakO, acpe.xCloak);
        ears$capeY = Mth.lerp(delta, acpe.yCloakO, acpe.yCloak);
        ears$capeZ = Mth.lerp(delta, acpe.zCloakO, acpe.zCloak);
        ears$horizontalSpeed = Mth.lerp(delta, acpe.walkDistO, acpe.walkDist);
        ears$stride = Mth.lerp(delta, acpe.oBob, acpe.bob);
        ears$flying = acpe.getAbilities().flying;
    }

}
