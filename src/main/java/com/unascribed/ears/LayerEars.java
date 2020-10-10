package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class LayerEars implements LayerRenderer<AbstractClientPlayer> {
	
	private final RenderPlayer render;
	
	public LayerEars(RenderPlayer render) {
		this.render = render;
	}
	
	@Override
	public void doRenderLayer(AbstractClientPlayer entity,
			float limbSwing, float limbDistance, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(skin);
		if (!entity.isInvisible() && Ears.earsSkinStatuses.containsKey(tex) && Ears.earsSkinStatuses.get(tex)) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			GlStateManager.pushMatrix();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder vc = tess.getBuffer();
			vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
			render.getMainModel().bipedHead.postRender(0.0625f);
			GlStateManager.enableCull();
			GlStateManager.translate(-0.5, -1, 0);
			vc.pos(0, 0.5f, 0).tex(24/64f, 8/64f).normal(0, 0, -1).endVertex();
			vc.pos(1, 0.5f, 0).tex(40/64f, 8/64f).normal(0, 0, -1).endVertex();
			vc.pos(1, 0, 0).tex(40/64f, 0/64f).normal(0, 0, -1).endVertex();
			vc.pos(0, 0, 0).tex(24/64f, 0/64f).normal(0, 0, -1).endVertex();
			
			vc.pos(0, 0, 0).tex(64/64f, 44/64f).normal(0, 0, 1).endVertex();
			vc.pos(1, 0, 0).tex(64/64f, 28/64f).normal(0, 0, 1).endVertex();
			vc.pos(1, 0.5f, 0).tex(56/64f, 28/64f).normal(0, 0, 1).endVertex();
			vc.pos(0, 0.5f, 0).tex(56/64f, 44/64f).normal(0, 0, 1).endVertex();
			tess.draw();
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();
			render.getMainModel().bipedBody.postRender(0.0625f);
			GlStateManager.translate(-0.25, 0.625, 0.15);
			GlStateManager.rotate(30+(limbDistance*40), 1, 0, 0);
			vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
			vc.pos(0, 0, 0).tex(56/64f, 16/64f).normal(0, 0, 1).endVertex();
			vc.pos(0.5f, 0, 0).tex(64/64f, 16/64f).normal(0, 0, 1).endVertex();
			vc.pos(0.5f, 0.75f, 0).tex(64/64f, 28/64f).normal(0, 0, 1).endVertex();
			vc.pos(0, 0.75f, 0).tex(56/64f, 28/64f).normal(0, 0, 1).endVertex();
			
			vc.pos(0, 0.75f, 0).tex(56/64f, 28/64f).normal(0, 0, -1).endVertex();
			vc.pos(0.5f, 0.75f, 0).tex(64/64f, 28/64f).normal(0, 0, -1).endVertex();
			vc.pos(0.5f, 0, 0).tex(64/64f, 16/64f).normal(0, 0, -1).endVertex();
			vc.pos(0, 0, 0).tex(56/64f, 16/64f).normal(0, 0, -1).endVertex();
			tess.draw();
			GlStateManager.popMatrix();
			GlStateManager.disableCull();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}
