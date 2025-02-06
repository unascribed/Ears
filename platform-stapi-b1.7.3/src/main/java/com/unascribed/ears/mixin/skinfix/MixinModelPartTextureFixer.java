package com.unascribed.ears.mixin.skinfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.unascribed.ears.ModelPartTextureFixer;
import com.unascribed.ears.TextureScaler;

import net.minecraft.client.model.Vertex;
import net.minecraft.client.model.Quad;
import net.minecraft.client.model.ModelPart;

@Mixin(ModelPart.class)
public class MixinModelPartTextureFixer implements ModelPartTextureFixer {
	private int textureWidth = 64;
	private int textureHeight = 32;

	@Redirect(method = "addCuboid(FFFIIIF)V", at = @At(value = "NEW", target = "net/minecraft/client/model/Quad"))
	private Quad fixQuad(Vertex[] args, int i, int j, int k, int i1) {
		TextureScaler.WIDTH = getTextureWidth();
		TextureScaler.HEIGHT = getTextureHeight();
		Quad newQuad = new Quad(args, i, j, k, i1);
		TextureScaler.setDefault();

		return newQuad;
	}

	@Override
	public int getTextureWidth() {
		return this.textureWidth;
	}

	@Override
	public void setTextureWidth(int textureWidth) {
		this.textureWidth = textureWidth;
	}

	@Override
	public int getTextureHeight() {
		return this.textureHeight;
	}

	@Override
	public void setTextureHeight(int textureHeight) {
		this.textureHeight = textureHeight;
	}
}
