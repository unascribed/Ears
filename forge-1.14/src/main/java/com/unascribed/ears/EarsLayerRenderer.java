package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.mixin.AccessorPlayerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class EarsLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> implements EarsRenderDelegate {
	
	public EarsLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
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
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
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
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance, ((AccessorPlayerModel)getEntityModel()).ears$isSmallArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
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
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerModel)getEntityModel()).ears$isSmallArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	public void renderRightArm(AbstractClientPlayerEntity entity) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
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
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerModel)getEntityModel()).ears$isSmallArms());
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
	}
	
	@Override
	public boolean shouldCombineTextures() {
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
		RendererModel model;
		switch (part) {
			case HEAD:
				model = getEntityModel().bipedHead;
				break;
			case LEFT_ARM:
				model = getEntityModel().bipedLeftArm;
				break;
			case LEFT_LEG:
				model = getEntityModel().bipedLeftLeg;
				break;
			case RIGHT_ARM:
				model = getEntityModel().bipedRightArm;
				break;
			case RIGHT_LEG:
				model = getEntityModel().bipedRightLeg;
				break;
			case TORSO:
				model = getEntityModel().bipedBody;
				break;
			default: return;
		}
		if (!model.showModel) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not visible, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		model.postRender(1/16f);
		ModelBox cuboid = model.cubeList.get(0);
		GlStateManager.scalef(1/16f, 1/16f, 1/16f);
		GlStateManager.translatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
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

		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vc.pos(-g, h+g, 0).tex(uv[0][0], uv[0][1]).normal(0, 0, -1).endVertex();
		vc.pos(w+g, h+g, 0).tex(uv[1][0], uv[1][1]).normal(0, 0, -1).endVertex();
		vc.pos(w+g, -g, 0).tex(uv[2][0], uv[2][1]).normal(0, 0, -1).endVertex();
		vc.pos(-g, -g, 0).tex(uv[3][0], uv[3][1]).normal(0, 0, -1).endVertex();
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vc.pos(-g, -g, 0).tex(uv[3][0], uv[3][1]).normal(0, 0, 1).endVertex();
		vc.pos(w+g, -g, 0).tex(uv[2][0], uv[2][1]).normal(0, 0, 1).endVertex();
		vc.pos(w+g, h+g, 0).tex(uv[1][0], uv[1][1]).normal(0, 0, 1).endVertex();
		vc.pos(-g, h+g, 0).tex(uv[0][0], uv[0][1]).normal(0, 0, 1).endVertex();
		tess.draw();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture();
	}

}
