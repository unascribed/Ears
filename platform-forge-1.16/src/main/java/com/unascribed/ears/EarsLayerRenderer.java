package com.unascribed.ears;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class EarsLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	
	public EarsLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void render(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, vertexConsumers, entity, light, LivingRenderer.getPackedOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingRenderer.getPackedOverlay(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, IRenderTypeBuffer vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingRenderer.getPackedOverlay(entity, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, IRenderTypeBuffer, IVertexBuilder, AbstractClientPlayerEntity, ModelRenderer> delegate = new IndirectEarsRenderDelegate<MatrixStack, IRenderTypeBuffer, IVertexBuilder, AbstractClientPlayerEntity, ModelRenderer>() {
		
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
			return EarsMod.getEarsFeatures(peer);
		}

		@Override
		public boolean isSlim() {
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
		protected void doUploadAux(TexSource src, byte[] pngData) {
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

		private final Matrix3f IDENTITY3 = new Matrix3f(); {
			IDENTITY3.setIdentity();
		}
		
		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Matrix4f mm = matrices.getLast().getMatrix();
			Matrix3f mn = emissive ? IDENTITY3 : matrices.getLast().getNormal();
			vc.pos(mm, x, y, z).color(r, g, b, a).tex(u, v).overlay(overlay).lightmap(emissive ? LightTexture.packLight(15, 15) : light).normal(mn, nX, nY, nZ).endVertex();
		}
		
		@Override
		protected void commitQuads() {
			if (vcp instanceof IRenderTypeBuffer.Impl) {
				((IRenderTypeBuffer.Impl)vcp).finish();
			}
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

		@Override
		public boolean isGliding() {
			return peer.isElytraFlying();
		}

		@Override
		public boolean isJacketEnabled() {
			return peer.isWearing(PlayerModelPart.JACKET);
		}

		@Override
		public boolean isWearingBoots() {
			return peer.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			return peer.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ElytraItem;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.prevLimbSwingAmount, peer.limbSwingAmount, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevCameraYaw, peer.cameraYaw, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevRenderYawOffset, peer.renderYawOffset, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.prevChasingPosX, peer.chasingPosX, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.prevChasingPosY, peer.chasingPosY, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.prevChasingPosZ, peer.chasingPosZ, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevPosX, peer.getPosX(), Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevPosY, peer.getPosY(), Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevPosZ, peer.getPosZ(), Minecraft.getInstance().getRenderPartialTicks());
		}
	};
}
