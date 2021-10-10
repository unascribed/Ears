package com.unascribed.ears;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.NotRandom;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.mixin.AccessorPlayerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

public class EarsLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> implements EarsRenderDelegate {
	
	public EarsLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}

	private PoseStack m;
	private VertexConsumer vc;
	private int light;
	private int overlay;
	private int skipRendering;
	private int stackDepth = 0;
	private BodyPart permittedBodyPart;
	
	// what the FUCK are these mappings. MOJANG. PLEASE. WHAT THE FUCK
	// PoseStack. POSE STACK. FUCKING POSE STACK OH MY GOD
	@Override
	public void render(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		ResourceLocation skin = getTextureLocation(entity);
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderType.entityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlayCoords(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance, ((AccessorPlayerModel)getParentModel()).ears$isSlim());
			this.m = null;
			this.vc = null;
		}
	}
	
	public void renderLeftArm(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity) {
		ResourceLocation skin = getTextureLocation(entity);
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderLeftArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderLeftArm(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderType.entityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlayCoords(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.LEFT_ARM;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerModel)getParentModel()).ears$isSlim());
			this.m = null;
			this.vc = null;
		}
	}
	
	public void renderRightArm(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity) {
		ResourceLocation skin = getTextureLocation(entity);
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderType.entityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlayCoords(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerModel)getParentModel()).ears$isSlim());
			this.m = null;
			this.vc = null;
		}
	}

	@Override
	public void push() {
		stackDepth++;
		m.pushPose();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		m.popPose();
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
				model = getParentModel().head;
				break;
			case LEFT_ARM:
				model = getParentModel().leftArm;
				break;
			case LEFT_LEG:
				model = getParentModel().leftLeg;
				break;
			case RIGHT_ARM:
				model = getParentModel().rightArm;
				break;
			case RIGHT_LEG:
				model = getParentModel().rightLeg;
				break;
			case TORSO:
				model = getParentModel().body;
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
		model.translateAndRotate(m);
		Cube cuboid = model.getRandomCube(NotRandom.INSTANCE);
		m.scale(1/16f, 1/16f, 1/16f);
		m.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		m.translate(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		m.mulPose(new Vector3f(x, y, z).rotationDegrees(ang));
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.last().pose();
		Matrix3f mn = m.last().normal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;
		
		vc.vertex(mv, -g, h+g, 0).color(1f, 1f, 1f, 1f).uv(uv[0][0], uv[0][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, -1).endVertex();
		vc.vertex(mv, w+g, h+g, 0).color(1f, 1f, 1f, 1f).uv(uv[1][0], uv[1][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, -1).endVertex();
		vc.vertex(mv, w+g, -g, 0).color(1f, 1f, 1f, 1f).uv(uv[2][0], uv[2][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, -1).endVertex();
		vc.vertex(mv, -g, -g, 0).color(1f, 1f, 1f, 1f).uv(uv[3][0], uv[3][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, -1).endVertex();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.last().pose();
		Matrix3f mn = m.last().normal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.vertex(mv, -g, -g, 0).color(1f, 1f, 1f, 1f).uv(uv[3][0], uv[3][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, 1).endVertex();
		vc.vertex(mv, w+g, -g, 0).color(1f, 1f, 1f, 1f).uv(uv[2][0], uv[2][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, 1).endVertex();
		vc.vertex(mv, w+g, h+g, 0).color(1f, 1f, 1f, 1f).uv(uv[1][0], uv[1][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, 1).endVertex();
		vc.vertex(mv, -g, h+g, 0).color(1f, 1f, 1f, 1f).uv(uv[0][0], uv[0][1]).overlayCoords(overlay).uv2(light).normal(mn, 0, 0, 1).endVertex();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		// TODO port this to Core GL (lol no i won't)
	}
}
