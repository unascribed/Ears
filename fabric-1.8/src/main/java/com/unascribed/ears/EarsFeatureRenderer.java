package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.ModelBox;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.texture.Texture;
import net.minecraft.util.Identifier;

public class EarsFeatureRenderer implements FeatureRenderer<AbstractClientPlayerEntity>, EarsRenderDelegate {
	
	private final PlayerEntityRenderer render;
	
	private AbstractClientPlayerEntity entity;
	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	
	public EarsFeatureRenderer(PlayerEntityRenderer render) {
		this.render = render;
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	
	
	@Override
	public void render(AbstractClientPlayerEntity entity,
			float limbSwing, float limbDistance, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {})", entity, limbSwing, limbDistance, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && tex instanceof EarsFeaturesHolder) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			MinecraftClient.getInstance().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance, ((AccessorPlayerEntityModel)render.getModel()).ears$isThinArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderLeftArm(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && tex instanceof EarsFeaturesHolder) {
			EarsLog.debug("Platform:Renderer", "renderLeftArm(...): Checks passed");
			MinecraftClient.getInstance().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.LEFT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerEntityModel)render.getModel()).ears$isThinArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}

	public void renderRightArm(AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && tex instanceof EarsFeaturesHolder) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			MinecraftClient.getInstance().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerEntityModel)render.getModel()).ears$isThinArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}
	
	@Override
	public boolean combineTextures() {
		return true;
	}

	@Override
	public void push() {
		stackDepth++;
		GlStateManager.pushMatrix();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		GlStateManager.popMatrix();
		if (skipRendering > 0) skipRendering--;
	}

	@Override
	public void anchorTo(BodyPart part) {
		if (permittedBodyPart != null && part != permittedBodyPart) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not permissible in this pass, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		ModelPart model;
		switch (part) {
			case HEAD:
				model = render.getModel().field_3781;
				break;
			case LEFT_ARM:
				model = render.getModel().field_3785;
				break;
			case LEFT_LEG:
				model = render.getModel().field_3787;
				break;
			case RIGHT_ARM:
				model = render.getModel().field_3784;
				break;
			case RIGHT_LEG:
				model = render.getModel().field_3786;
				break;
			case TORSO:
				model = render.getModel().field_3783;
				break;
			default: return;
		}
		if (!model.visible) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not visible, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		if (entity.isSneaking()) {
			if (part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG) {
				GlStateManager.translated(0, 0.1875, 0);
			} else {
				GlStateManager.translated(0, 0.2125, 0);
			}
		}
		model.method_3106(1/16f);
		ModelBox cuboid = model.field_3941.get(0);
		GlStateManager.scalef(1/16f, 1/16f, 1/16f);
		GlStateManager.translatef(cuboid.minX, cuboid.maxY, cuboid.minZ);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.translatef(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.rotatef(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;

		vc.begin(GL11.GL_QUADS, VertexFormats.field_5175);
		vc.vertex(-g, h+g, 0).texture(uv[0][0], uv[0][1]).normal(0, 0, -1).next();
		vc.vertex(w+g, h+g, 0).texture(uv[1][0], uv[1][1]).normal(0, 0, -1).next();
		vc.vertex(w+g, -g, 0).texture(uv[2][0], uv[2][1]).normal(0, 0, -1).next();
		vc.vertex(-g, -g, 0).texture(uv[3][0], uv[3][1]).normal(0, 0, -1).next();
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.begin(GL11.GL_QUADS, VertexFormats.field_5175);
		vc.vertex(-g, -g, 0).texture(uv[3][0], uv[3][1]).normal(0, 0, 1).next();
		vc.vertex(w+g, -g, 0).texture(uv[2][0], uv[2][1]).normal(0, 0, 1).next();
		vc.vertex(w+g, h+g, 0).texture(uv[1][0], uv[1][1]).normal(0, 0, 1).next();
		vc.vertex(-g, h+g, 0).texture(uv[0][0], uv[0][1]).normal(0, 0, 1).next();
		tess.draw();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
		bb.vertex(0, 0, 0).color(r, g, b, a).next();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture();
	}
}
