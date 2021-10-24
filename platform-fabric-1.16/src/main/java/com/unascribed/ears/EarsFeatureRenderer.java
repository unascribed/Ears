package com.unascribed.ears;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.common.util.NotRandom;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	private VertexConsumerProvider.Immediate scratch() {
		return MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
	}
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, scratch(), entity, limbDistance, light, LivingEntityRenderer.getOverlay(entity, 0));
	}

	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, scratch(), entity, 0, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, scratch(), entity, 0, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, VertexConsumerProvider.Immediate, VertexConsumer, AbstractClientPlayerEntity, ModelPart> delegate = new IndirectEarsRenderDelegate<MatrixStack, VertexConsumerProvider.Immediate, VertexConsumer, AbstractClientPlayerEntity, ModelPart>() {
		
		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerEntityModel<AbstractClientPlayerEntity> model = getContextModel();
			return d.map(BodyPart.HEAD, model.head)
					.map(BodyPart.LEFT_ARM, model.leftArm)
					.map(BodyPart.LEFT_LEG, model.leftLeg)
					.map(BodyPart.RIGHT_ARM, model.rightArm)
					.map(BodyPart.RIGHT_LEG, model.rightLeg)
					.map(BodyPart.TORSO, model.torso);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.rotate(matrices);
			Cuboid cuboid = modelPart.getRandomCuboid(NotRandom.INSTANCE);
			matrices.scale(1/16f, 1/16f, 1/16f);
			matrices.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
		}
		
		@Override
		protected boolean isVisible(ModelPart modelPart) {
			return modelPart.visible;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsMod.getEarsFeatures(peer);
		}

		@Override
		protected boolean isSlim() {
			return ((AccessorPlayerEntityModel)getContextModel()).ears$isThinArms();
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
			matrices.multiply(new Vector3f(x, y, z).getDegreesQuaternion(ang));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadAux(TexSource src, byte[] pngData) {
			Identifier skin = peer.getSkinTexture();
			Identifier id = new Identifier(skin.getNamespace(), src.addSuffix(skin.getPath()));
			if (pngData != null && MinecraftClient.getInstance().getTextureManager().getTexture(id) == null) {
				try {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, MissingSprite.getMissingSpriteTexture());
				}
			}
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Matrix4f mm = matrices.peek().getModel();
			Matrix3f mn = matrices.peek().getNormal();
			vc.vertex(mm, x, y, z).color(r, g, b, a).texture(u, v).overlay(overlay).light(light).normal(mn, nX, nY, nZ).next();
		}
		
		@Override
		protected void commitQuads() {
			vcp.draw();
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			Matrix4f mv = matrices.peek().getModel();
			
			GL11.glPointSize(8);
			GlStateManager.disableTexture();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
			bb.vertex(mv, 0, 0, 0).color(r, g, b, a).next();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture();
		}

		@Override
		protected VertexConsumer getVertexConsumer(TexSource src) {
			Identifier id = peer.getSkinTexture();
			if (src != TexSource.SKIN) {
				id = new Identifier(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return vcp.getBuffer(RenderLayer.getEntityTranslucentCull(id));
		}

		@Override
		public float getTime() {
			return peer.age+MinecraftClient.getInstance().getTickDelta();
		}

		@Override
		public boolean isFlying() {
			return peer.abilities.flying;
		}

		@Override
		public boolean isGliding() {
			return peer.isFallFlying();
		}

		@Override
		public boolean isJacketEnabled() {
			return peer.isPartVisible(PlayerModelPart.JACKET);
		}

		@Override
		public boolean isWearingBoots() {
			return peer.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			return peer.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem;
		}
	};
}
