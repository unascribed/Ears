package com.unascribed.ears.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Mixin(TextureManager.class)
public interface AccessorTextureManager {

	@Accessor("textures")
	Map<Identifier, AbstractTexture> ears$getTextures();
	
}
