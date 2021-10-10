package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.Tessellator;

public class ModelTransBox extends ModelBox {

	public ModelTransBox(ModelRenderer parent, int texU, int texV, float x, float y, float z, int xSize, int ySize, int zSize, float grow) {
		super(parent, texU, texV, x, y, z, xSize, ySize, zSize, grow);
	}

	@Override
	public void render(Tessellator p_78245_1_, float p_78245_2_) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.render(p_78245_1_, p_78245_2_);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void addBoxTo(ModelRenderer rend, int texOfsX, int texOfsY, float p_78790_1_, float p_78790_2_, float p_78790_3_, int p_78790_4_, int p_78790_5_, int p_78790_6_, float p_78790_7_) {
        rend.cubeList.add(new ModelTransBox(rend, texOfsX, texOfsY, p_78790_1_, p_78790_2_, p_78790_3_, p_78790_4_, p_78790_5_, p_78790_6_, p_78790_7_));
    }
	
}
