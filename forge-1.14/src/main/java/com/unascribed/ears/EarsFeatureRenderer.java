package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class EarsFeatureRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(AbstractClientPlayerEntity entity, final float limbAngle, final float limbDistance,
			final float tickDelta, final float age, final float headYaw, final float headPitch, final float scale) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		if (tex instanceof EarsAwareTexture && !entity.isInvisible()) {
			if (((EarsAwareTexture)tex).isEarsEnabled()) {
				try {
					GlStateManager.pushMatrix();
					Tessellator tess = Tessellator.getInstance();
					BufferBuilder vc = tess.getBuffer();
					vc.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
					getEntityModel().bipedHead.postRender(0.0625f);
					GlStateManager.enableCull();
					GlStateManager.translated(-0.5, -1, 0);
					vc.pos(0, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(24/64f, 8/64f).normal(0, 0, -1).endVertex();
					vc.pos(1, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(40/64f, 8/64f).normal(0, 0, -1).endVertex();
					vc.pos(1, 0, 0).color(1f, 1f, 1f, 1f).tex(40/64f, 0/64f).normal(0, 0, -1).endVertex();
					vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(24/64f, 0/64f).normal(0, 0, -1).endVertex();
					
					vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 44/64f).normal(0, 0, 1).endVertex();
					vc.pos(1, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).normal(0, 0, 1).endVertex();
					vc.pos(1, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).normal(0, 0, 1).endVertex();
					vc.pos(0, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 44/64f).normal(0, 0, 1).endVertex();
					tess.draw();
					GlStateManager.popMatrix();
					
					GlStateManager.pushMatrix();
					getEntityModel().bipedBody.postRender(0.0625f);
					GlStateManager.translated(-0.25, 0.625, 0.15);
					GlStateManager.rotated(30+(limbDistance*40), 1, 0, 0);
					vc.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
					vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 16/64f).normal(0, 0, 1).endVertex();
					vc.pos(0.5f, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 16/64f).normal(0, 0, 1).endVertex();
					vc.pos(0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).normal(0, 0, 1).endVertex();
					vc.pos(0, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).normal(0, 0, 1).endVertex();
					
					vc.pos(0, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).normal(0, 0, -1).endVertex();
					vc.pos(0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).normal(0, 0, -1).endVertex();
					vc.pos(0.5f, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 16/64f).normal(0, 0, -1).endVertex();
					vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 16/64f).normal(0, 0, -1).endVertex();
					tess.draw();
					GlStateManager.popMatrix();
					GlStateManager.disableCull();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}
