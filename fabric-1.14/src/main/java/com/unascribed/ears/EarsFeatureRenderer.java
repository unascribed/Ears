package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.Texture;
import net.minecraft.util.Identifier;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(AbstractClientPlayerEntity entity, final float limbAngle, final float limbDistance,
			final float tickDelta, final float age, final float headYaw, final float headPitch, final float scale) {
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		if (tex instanceof EarsAwareTexture && !entity.isInvisible()) {
			if (((EarsAwareTexture)tex).isEarsEnabled()) {
				try {
					GlStateManager.pushMatrix();
					Tessellator tess = Tessellator.getInstance();
					BufferBuilder vc = tess.getBuffer();
					vc.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
					getContextModel().head.applyTransform(0.0625f);
					GlStateManager.enableCull();
					GlStateManager.translated(-0.5, -1, 0);
					vc.vertex(0, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(24/64f, 8/64f).normal(0, 0, -1).next();
					vc.vertex(1, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(40/64f, 8/64f).normal(0, 0, -1).next();
					vc.vertex(1, 0, 0).color(1f, 1f, 1f, 1f).texture(40/64f, 0/64f).normal(0, 0, -1).next();
					vc.vertex(0, 0, 0).color(1f, 1f, 1f, 1f).texture(24/64f, 0/64f).normal(0, 0, -1).next();
					
					vc.vertex(0, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 44/64f).normal(0, 0, 1).next();
					vc.vertex(1, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).normal(0, 0, 1).next();
					vc.vertex(1, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).normal(0, 0, 1).next();
					vc.vertex(0, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 44/64f).normal(0, 0, 1).next();
					tess.draw();
					GlStateManager.popMatrix();
					
					GlStateManager.pushMatrix();
					getContextModel().torso.applyTransform(0.0625f);
					GlStateManager.translated(-0.25, 0.625, 0.15);
					GlStateManager.rotated(30+(limbDistance*40), 1, 0, 0);
					vc.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
					vc.vertex(0, 0, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 16/64f).normal(0, 0, 1).next();
					vc.vertex(0.5f, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 16/64f).normal(0, 0, 1).next();
					vc.vertex(0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).normal(0, 0, 1).next();
					vc.vertex(0, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).normal(0, 0, 1).next();
					
					vc.vertex(0, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).normal(0, 0, -1).next();
					vc.vertex(0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).normal(0, 0, -1).next();
					vc.vertex(0.5f, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 16/64f).normal(0, 0, -1).next();
					vc.vertex(0, 0, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 16/64f).normal(0, 0, -1).next();
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
	public boolean hasHurtOverlay() {
		return true;
	}
}
