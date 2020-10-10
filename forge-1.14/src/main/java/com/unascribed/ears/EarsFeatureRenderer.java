package com.unascribed.ears;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class EarsFeatureRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		ResourceLocation skin = getEntityTexture(entity);
		Texture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		if (tex instanceof EarsAwareTexture && !entity.isInvisible()) {
			if (((EarsAwareTexture)tex).isEarsEnabled()) {
				m.push();
				int overlay = LivingRenderer.getPackedOverlay(entity, 0);
				IVertexBuilder vc = vertexConsumers.getBuffer(RenderType.getEntityCutout(skin));
				getEntityModel().bipedHead.translateRotate(m);
				m.translate(-0.5, -1, 0);
				Matrix4f mv = m.getLast().getMatrix();
				Matrix3f mn = m.getLast().getNormal();
				vc.pos(mv, 0, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(24/64f, 8/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 1, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(40/64f, 8/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 1, 0, 0).color(1f, 1f, 1f, 1f).tex(40/64f, 0/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(24/64f, 0/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				
				vc.pos(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 44/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 1, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 1, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 0, 0.5f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 44/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				m.pop();
				
				m.push();
				getEntityModel().bipedBody.translateRotate(m);
				m.translate(-0.25, 0.625, 0.15);
				m.rotate(Vector3f.XP.rotation(((float)Math.toRadians(30+(limbDistance*40)))));
				mv = m.getLast().getMatrix();
				mn = m.getLast().getNormal();
				vc.pos(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 16/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 0.5f, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 16/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				vc.pos(mv, 0, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, 1).endVertex();
				
				vc.pos(mv, 0, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 0.5f, 0.75f, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 28/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 0.5f, 0, 0).color(1f, 1f, 1f, 1f).tex(64/64f, 16/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				vc.pos(mv, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(56/64f, 16/64f).overlay(overlay).lightmap(light).normal(mn, 0, 0, -1).endVertex();
				m.pop();
			}
		}
	}
}
