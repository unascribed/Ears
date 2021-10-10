package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.model.PlayerEntityModel;

@Mixin(PlayerEntityModel.class)
public interface AccessorPlayerEntityModel {

	@Accessor("thinArms")
	boolean ears$isThinArms();
	
}
