package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.NotRandom;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EarsRenderDelegate {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}

	private MatrixStack m;
	private VertexConsumer vc;
	private int light;
	private int overlay;
	private int skipRendering;
	private int stackDepth = 0;
	private BodyPart permittedBodyPart;
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		Identifier skin = getTexture(entity);
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlay(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance, ((AccessorPlayerEntityModel)getContextModel()).ears$isThinArms());
			this.m = null;
			this.vc = null;
		}
	}
	
	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderLeftArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderLeftArm(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlay(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.LEFT_ARM;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerEntityModel)getContextModel()).ears$isThinArms());
		}
	}
	
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		Identifier skin = entity.getSkinTexture();
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			this.m = m;
			this.vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(skin));
			this.light = light;
			this.overlay = LivingEntityRenderer.getOverlay(entity, 0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, 0, ((AccessorPlayerEntityModel)getContextModel()).ears$isThinArms());
		}
	}

	@Override
	public void push() {
		stackDepth++;
		m.push();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		m.pop();
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
				model = getContextModel().field_27433;
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
		model.rotate(m);
		Cuboid cuboid = model.getRandomCuboid(NotRandom.INSTANCE);
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
		m.multiply(new Vector3f(x, y, z).getDegreesQuaternion(ang));
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		Matrix3f mn = m.peek().getNormal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;
		
		vc.vertex(mv, -g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, w+g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, w+g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
		vc.vertex(mv, -g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		Matrix3f mn = m.peek().getNormal();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.vertex(mv, -g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[3][0], uv[3][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, w+g, -g, 0).color(1f, 1f, 1f, 1f).texture(uv[2][0], uv[2][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, w+g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[1][0], uv[1][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
		vc.vertex(mv, -g, h+g, 0).color(1f, 1f, 1f, 1f).texture(uv[0][0], uv[0][1]).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		Matrix4f mv = m.peek().getModel();
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture();
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor4f(r, g, b, a);
		Vector4f v = new Vector4f(0, 0, 0, 0);
		v.transform(mv);
		GL11.glVertex3f(v.getX(), v.getY(), v.getZ());
		GL11.glEnd();
		GlStateManager.enableTexture();
	}
}
