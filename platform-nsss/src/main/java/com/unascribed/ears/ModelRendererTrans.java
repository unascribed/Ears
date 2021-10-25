package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import com.mojang.minecraft.entity.model.ModelRenderer;

public class ModelRendererTrans extends ModelRenderer {

	public ModelRendererTrans(int xTexOffset, int yTexOffset) {
		super(xTexOffset, yTexOffset);
	}

	@Override
	public void render(float f) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		super.render(f);
		glDisable(GL_BLEND);
	}

}
