package com.unascribed.ears.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;

@Mixin(LivingEntityRenderer.class)
public interface AccessorLivingEntityRenderer {

	@Accessor("features")
	List<FeatureRenderer<?, ?>> ears$getFeatures();
	
}
