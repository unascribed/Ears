package com.unascribed.ears.mixin;

import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
public interface AccessorPlayerModel {

	@Accessor("slim")
	boolean ears$isSlim();
	
}
