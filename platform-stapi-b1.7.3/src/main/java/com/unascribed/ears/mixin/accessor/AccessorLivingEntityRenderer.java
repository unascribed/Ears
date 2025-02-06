package com.unascribed.ears.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;

@Mixin(LivingEntityRenderer.class)
public interface AccessorLivingEntityRenderer {
	@Accessor("model")
	EntityModel getModel();
}
