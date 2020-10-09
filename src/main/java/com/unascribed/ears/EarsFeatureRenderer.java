package com.unascribed.ears;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		Identifier skin = getTexture(entity);
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
		if (tex instanceof EarsAwareTexture && !entity.isInvisible()) {
			if (((EarsAwareTexture)tex).isEarsEnabled()) {
				try {
					m.push();
					int overlay = LivingEntityRenderer.getOverlay(entity, 0);
					VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(skin));
					getContextModel().head.rotate(m);
					m.translate(-0.5, -1, 0);
					Matrix4f mv = m.peek().getModel();
					Matrix3f mn = m.peek().getNormal();
					vc.vertex(mv, 0, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(24/64f, 8/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 1, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(40/64f, 8/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(40/64f, 0/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(24/64f, 0/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					
					vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 44/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 1, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 0, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 44/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					m.pop();
					
					m.push();
					getContextModel().torso.rotate(m);
					m.translate(-0.25, 0.625, 0.15);
					m.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float)Math.toRadians(30+(limbDistance*40))));
					mv = m.peek().getModel();
					mn = m.peek().getNormal();
					vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 16/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 0.5f, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 16/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					vc.vertex(mv, 0, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, 1).next();
					
					vc.vertex(mv, 0, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 28/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 0.5f, 0, 0).color(1f, 1f, 1f, 1f).texture(64/64f, 16/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					vc.vertex(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(56/64f, 16/64f).overlay(overlay).light(light).normal(mn, 0, 0, -1).next();
					m.pop();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
}
