package com.unascribed.ears.mixin;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
public interface AccessorModelPart {

	@Accessor("children")
	Map<String, ModelPart> ears$getChildren();

}
