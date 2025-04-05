package com.unascribed.ears.mixin;

import java.util.Map;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.renderer.texture.TextureManager;

@Mixin(TextureManager.class)
public interface AccessorTextureManager {

    @Accessor("byPath")
    Map<ResourceLocation, AbstractTexture> ears$getTextures();

}