package com.unascribed.ears;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.common.util.NotRandom;
import com.unascribed.ears.mixin.AccessorPlayerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class EarsLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	
	public EarsLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	private IRenderTypeBuffer.Impl scratch() {
		return Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
	}
	
	@Override
	public void render(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, scratch(), entity, limbDistance, light, LivingRenderer.getPackedOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, scratch(), entity, 0, light, LivingRenderer.getPackedOverlay(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, scratch(), entity, 0, light, LivingRenderer.getPackedOverlay(entity, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, IRenderTypeBuffer.Impl, IVertexBuilder, AbstractClientPlayerEntity, ModelRenderer> delegate = new IndirectEarsRenderDelegate<MatrixStack, IRenderTypeBuffer.Impl, IVertexBuilder, AbstractClientPlayerEntity, ModelRenderer>() {
		
		@Override
		protected Decider<BodyPart, ModelRenderer> decideModelPart(Decider<BodyPart, ModelRenderer> d) {
			PlayerModel<AbstractClientPlayerEntity> model = getEntityModel();
			return d.map(BodyPart.HEAD, model.bipedHead)
					.map(BodyPart.LEFT_ARM, model.bipedLeftArm)
					.map(BodyPart.LEFT_LEG, model.bipedLeftLeg)
					.map(BodyPart.RIGHT_ARM, model.bipedRightArm)
					.map(BodyPart.RIGHT_LEG, model.bipedRightLeg)
					.map(BodyPart.TORSO, model.bipedBody);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelRenderer modelPart) {
			modelPart.translateRotate(matrices);
			ModelBox cuboid = modelPart.getRandomCube(NotRandom.INSTANCE);
			matrices.scale(1/16f, 1/16f, 1/16f);
			matrices.translate(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
		}
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.showModel;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			ResourceLocation skin = peer.getLocationSkin();
			Texture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
			EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
			if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
				return ((EarsFeaturesHolder)tex).getEarsFeatures();
			}
			return EarsFeatures.DISABLED;
		}

		@Override
		protected boolean isSlim() {
			return ((AccessorPlayerModel)getEntityModel()).ears$isSmallArms();
		}

		@Override
		protected void pushMatrix() {
			matrices.push();
		}

		@Override
		protected void popMatrix() {
			matrices.pop();
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			matrices.translate(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			matrices.rotate(new Vector3f(x, y, z).rotationDegrees(ang));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadSub(TexSource src, byte[] pngData) {
			ResourceLocation skin = peer.getLocationSkin();
			ResourceLocation id = new ResourceLocation(skin.getNamespace(), src.addSuffix(skin.getPath()));
			if (pngData != null && Minecraft.getInstance().getTextureManager().getTexture(id) == null) {
				try {
					Minecraft.getInstance().getTextureManager().loadTexture(id, new DynamicTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					Minecraft.getInstance().getTextureManager().loadTexture(id, MissingTextureSprite.getDynamicTexture());
				}
			}
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Matrix4f mm = matrices.getLast().getMatrix();
			Matrix3f mn = matrices.getLast().getNormal();
			vc.pos(mm, x, y, z).color(r, g, b, a).tex(u, v).overlay(overlay).lightmap(light).normal(mn, nX, nY, nZ).endVertex();
		}
		
		@Override
		protected void commitQuads() {
			vcp.finish();
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			Matrix4f mv = matrices.getLast().getMatrix();
			
			GL11.glPointSize(8);
			GlStateManager.disableTexture();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
			bb.pos(mv, 0, 0, 0).color(r, g, b, a).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture();
		}

		@Override
		protected IVertexBuilder getVertexConsumer(TexSource src) {
			ResourceLocation id = peer.getLocationSkin();
			if (src != TexSource.SKIN) {
				id = new ResourceLocation(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return vcp.getBuffer(RenderType.getEntityTranslucentCull(id));
		}

		@Override
		public float getTime() {
			return peer.ticksExisted+Minecraft.getInstance().getRenderPartialTicks();
		}

		@Override
		public boolean isFlying() {
			return peer.abilities.isFlying;
		}
	};
}
