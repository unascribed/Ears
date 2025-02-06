package com.unascribed.ears.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.texture.SkinImageProcessor;;

@Mixin(SkinImageProcessor.class)
public interface AccessorSkinImageProcessor {
	@Invoker("setOpaque")
	void setOpaque(int x1, int y1, int x2, int y2);
}
