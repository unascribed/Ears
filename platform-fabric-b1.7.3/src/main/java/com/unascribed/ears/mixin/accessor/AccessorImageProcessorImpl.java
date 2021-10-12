package com.unascribed.ears.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.ImageProcessorImpl;

@Mixin(ImageProcessorImpl.class)
public interface AccessorImageProcessorImpl {
	@Invoker("method_1899")
	void setAreaOpaque(int x1, int y1, int x2, int y2);
}
