package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.model.ModelPlayer;

@Mixin(ModelPlayer.class)
public interface AccessorModelPlayer {

	@Accessor("smallArms")
	boolean ears$isSmallArms();
	
}
