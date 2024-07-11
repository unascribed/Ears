package com.unascribed.ears;

import java.io.IOException;

import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorPlayerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;

public class EarsLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	
	public EarsLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context) {
		super(context);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlayCoords(entity, 0));
	}
	
	public void renderLeftArm(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlayCoords(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(PoseStack m, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlayCoords(entity, 0), BodyPart.RIGHT_ARM);
	}

	public static EarsFeatures getEarsFeatures(AbstractClientPlayer peer) {
		ResourceLocation skin = peer.getSkin().texture();
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder) {
			EarsFeatures feat = ((EarsFeaturesHolder)tex).getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.getGameProfile().getName(), peer.getGameProfile().getId(), feat);
			if (!peer.isInvisible()) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}

	// official Mojang mappings continue to baffle me. PoseStack????????????
	// MCP mappings were bad but they never managed to make me irrationally angry
	private final IndirectEarsRenderDelegate<PoseStack, MultiBufferSource, VertexConsumer, AbstractClientPlayer, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {
		
		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerModel<AbstractClientPlayer> model = getParentModel();
			return d.map(BodyPart.HEAD, model.head)
					.map(BodyPart.LEFT_ARM, model.leftArm)
					.map(BodyPart.LEFT_LEG, model.leftLeg)
					.map(BodyPart.RIGHT_ARM, model.rightArm)
					.map(BodyPart.RIGHT_LEG, model.rightLeg)
					.map(BodyPart.TORSO, model.body);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.translateAndRotate(matrices);
			Cube cuboid = modelPart.getRandomCube(NotRandom119.INSTANCE);
			matrices.scale(1/16f, 1/16f, 1/16f);
			matrices.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
		}
		
		@Override
		protected boolean isVisible(ModelPart modelPart) {
			return modelPart.visible;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsLayerRenderer.getEarsFeatures(peer);
		}

		@Override
		public boolean isSlim() {
			return ((AccessorPlayerModel)getParentModel()).ears$isSlim();
		}

		@Override
		protected void pushMatrix() {
			matrices.pushPose();
		}

		@Override
		protected void popMatrix() {
			matrices.popPose();
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			matrices.translate(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			matrices.mulPose(new Quaternionf(new AxisAngle4f(ang*Mth.DEG_TO_RAD, x, y, z)));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadAux(TexSource src, byte[] pngData) {
			ResourceLocation skin = peer.getSkin().texture();
			ResourceLocation id = new ResourceLocation(skin.getNamespace(), src.addSuffix(skin.getPath()));
			if (pngData != null && Minecraft.getInstance().getTextureManager().getTexture(id, null) == null) {
				try {
					Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					Minecraft.getInstance().getTextureManager().register(id, MissingTextureAtlasSprite.getTexture());
				}
			}
		}

		private final Matrix3f IDENTITY3 = new Matrix3f();

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Matrix4f mm = matrices.last().pose();
			Matrix3f mn = emissive ? IDENTITY3 : matrices.last().normal();
			vc.vertex(mm, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(overlay).uv2(emissive ? LightTexture.pack(15, 15) : light).normal(mn, nX, nY, nZ).endVertex();
		}
		
		@Override
		protected void commitQuads() {
			if (vcp instanceof MultiBufferSource.BufferSource) {
				((MultiBufferSource.BufferSource)vcp).endLastBatch();
			}
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// TODO port this to core profile (no)
		}

		@Override
		protected VertexConsumer getVertexConsumer(TexSource src) {
			ResourceLocation id = peer.getSkin().texture();
			if (src != TexSource.SKIN) {
				id = new ResourceLocation(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return vcp.getBuffer(RenderType.entityTranslucentCull(id));
		}

		@Override
		public float getTime() {
			return peer.tickCount+Minecraft.getInstance().getFrameTime();
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
			return peer.isModelPartShown(PlayerModelPart.JACKET);
		}

		@Override
		public boolean isWearingBoots() {
			return peer.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			return peer.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.walkDistO, peer.walkDist, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public float getLimbSwing() {
			return peer.walkAnimation.speed(Minecraft.getInstance().getFrameTime());
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.oBob, peer.bob, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.yBodyRotO, peer.yBodyRot, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.xCloakO, peer.xCloak, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.yCloakO, peer.yCloak, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.zCloakO, peer.zCloak, Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.xOld, peer.getX(), Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.yOld, peer.getY(), Minecraft.getInstance().getFrameTime());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.zOld, peer.getZ(), Minecraft.getInstance().getFrameTime());
		}
	};
}
