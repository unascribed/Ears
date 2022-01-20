package com.unascribed.ears;

import java.io.IOException;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
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
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, VertexConsumerProvider, VertexConsumer, AbstractClientPlayerEntity, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {
		
		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerEntityModel<AbstractClientPlayerEntity> model = getContextModel();
			return d.map(BodyPart.HEAD, model.head)
					.map(BodyPart.LEFT_ARM, model.leftArm)
					.map(BodyPart.LEFT_LEG, model.leftLeg)
					.map(BodyPart.RIGHT_ARM, model.rightArm)
					.map(BodyPart.RIGHT_LEG, model.rightLeg)
					.map(BodyPart.TORSO, model.body);
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
		public boolean isSlim() {
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
			matrices.multiply(new Vec3f(x, y, z).getDegreesQuaternion(ang));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadAux(TexSource src, byte[] pngData) {
			Identifier skin = peer.getSkinTexture();
			Identifier id = new Identifier(skin.getNamespace(), src.addSuffix(skin.getPath()));
			if (pngData != null && MinecraftClient.getInstance().getTextureManager().getOrDefault(id, null) == null) {
				try {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, MissingSprite.getMissingSpriteTexture());
				}
			}
		}

		private final Matrix3f IDENTITY3 = new Matrix3f(); {
			IDENTITY3.loadIdentity();
		}
		
		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Matrix4f mm = matrices.peek().getModel();
			Matrix3f mn = emissive ? IDENTITY3 : matrices.peek().getNormal();
			vc.vertex(mm, x, y, z).color(r, g, b, a).texture(u, v).overlay(overlay).light(emissive ? LightmapTextureManager.pack(15, 15) : light).normal(mn, nX, nY, nZ).next();
		}
		
		@Override
		protected void commitQuads() {
			if (vcp instanceof VertexConsumerProvider.Immediate) {
				((VertexConsumerProvider.Immediate)vcp).drawCurrentLayer();
			}
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// TODO port this to core profile (nah)
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
			return peer.getAbilities().flying;
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

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevHorizontalSpeed, peer.horizontalSpeed, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.lastLimbDistance, peer.limbDistance, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevStrideDistance, peer.strideDistance, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevBodyYaw, peer.bodyYaw, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.prevCapeX, peer.capeX, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.prevCapeY, peer.capeY, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.prevCapeZ, peer.capeZ, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevX, peer.getPos().x, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevY, peer.getPos().y, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevZ, peer.getPos().z, MinecraftClient.getInstance().getTickDelta());
		}
	};
}
