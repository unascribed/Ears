package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.EarsRenderDelegate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.Texture;
import net.minecraft.util.Identifier;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EarsRenderDelegate {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}

	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	
	@Override
	public void render(AbstractClientPlayerEntity entity, final float limbAngle, final float limbDistance,
			final float tickDelta, final float age, final float headYaw, final float headPitch, final float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderLeftArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderLeftArm(...): Checks passed");
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.LEFT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	public void renderRightArm(AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	@Override
	public boolean hasHurtOverlay() {
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
				model = getContextModel().head;
				break;
			case LEFT_ARM:
				model = getContextModel().leftArm;
				break;
			case LEFT_LEG:
				model = getContextModel().leftLeg;
				break;
			case RIGHT_ARM:
				model = getContextModel().rightArm;
				break;
			case RIGHT_LEG:
				model = getContextModel().rightLeg;
				break;
			case TORSO:
				model = getContextModel().torso;
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
		model.applyTransform(1/16f);
		Box cuboid = model.boxes.get(0);
		GlStateManager.scalef(1/16f, 1/16f, 1/16f);
		GlStateManager.translatef(cuboid.xMin, cuboid.yMax, cuboid.zMin);
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

		vc.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		vc.vertex(-g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).normal(0, 0, -1).next();
		vc.vertex(w+g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).normal(0, 0, -1).next();
		vc.vertex(w+g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).normal(0, 0, -1).next();
		vc.vertex(-g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).normal(0, 0, -1).next();
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		vc.vertex(-g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).normal(0, 0, 1).next();
		vc.vertex(w+g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).normal(0, 0, 1).next();
		vc.vertex(w+g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).normal(0, 0, 1).next();
		vc.vertex(-g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).normal(0, 0, 1).next();
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
