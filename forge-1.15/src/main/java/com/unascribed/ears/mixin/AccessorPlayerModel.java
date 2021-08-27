package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.model.PlayerModel;

@Mixin(PlayerModel.class)
public interface AccessorPlayerModel {

	@Accessor("smallArms")
	boolean ears$isSmallArms();
	
}
