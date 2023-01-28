package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.PlayerModel;

@Mixin(PlayerModel.class)
public interface AccessorPlayerModel {

	@Accessor("slim")
	boolean ears$isSlim();
	
}
