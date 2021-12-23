package com.unascribed.ears;

import java.io.IOException;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.DirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.util.Identifier;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void render(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		delegate.render(entity, null);
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderLeftArm({})", entity);
		delegate.render(entity, BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({})", entity);
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	@Override
	public boolean hasHurtOverlay() {
		return true;
	}

	private final DirectEarsRenderDelegate<AbstractClientPlayerEntity, ModelPart> delegate = new DirectEarsRenderDelegate<AbstractClientPlayerEntity, ModelPart>() {
		@Override
		protected void setUpRenderState() {
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		
		@Override
		protected void tearDownRenderState() {
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
		
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
			if (peer.isInSneakingPose() && permittedBodyPart == null) {
				GlStateManager.translatef(0, 0.2f, 0);
			}
			modelPart.applyTransform(1/16f);
			Box cuboid = modelPart.boxes.get(0);
			GlStateManager.scalef(1/16f, 1/16f, 1/16f);
			GlStateManager.translatef(cuboid.xMin, cuboid.yMax, cuboid.zMin);
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
			GlStateManager.pushMatrix();
		}

		@Override
		protected void popMatrix() {
			GlStateManager.popMatrix();
		}

		@Override
		protected void doBindSkin() {
			MinecraftClient.getInstance().getTextureManager().bindTexture(peer.getSkinTexture());
		}

		@Override
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (pngData == null) {
				GlStateManager.bindTexture(0);
			} else {
				Identifier skin = peer.getSkinTexture();
				Identifier id = new Identifier(skin.getNamespace(), src.addSuffix(skin.getPath()));
				if (MinecraftClient.getInstance().getTextureManager().getTexture(id) == null) {
					try {
						MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(NativeImage.read(toNativeBuffer(pngData))));
					} catch (IOException e) {
						MinecraftClient.getInstance().getTextureManager().registerTexture(id, MissingSprite.getMissingSpriteTexture());
					}
				}
				MinecraftClient.getInstance().getTextureManager().bindTexture(id);
			}
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			GlStateManager.translatef(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			GlStateManager.rotatef(ang, x, y, z);
		}

		@Override
		protected void doScale(float x, float y, float z) {
			GlStateManager.scalef(x, y, z);
		}

		@Override
		protected void beginQuad() {
			Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Tessellator.getInstance().getBuffer().vertex(x, y, z).color(r, g, b, a).texture(u, v).normal(nX, nY, nZ).next();
		}

		@Override
		protected void drawQuad() {
			Tessellator.getInstance().draw();
		}
		
		@Override
		public void setEmissive(boolean emissive) {
			super.setEmissive(emissive);
			GlStateManager.activeTexture(GLX.GL_TEXTURE1);
			if (emissive) {
				GlStateManager.disableLighting();
				GlStateManager.disableTexture();
			} else {
				GlStateManager.enableLighting();
				GlStateManager.enableTexture();
			}
			GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			GL11.glPointSize(8);
			GlStateManager.disableTexture();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
			bb.vertex(0, 0, 0).color(r, g, b, a).next();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture();
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
			return EarsCommon.lerpDelta(peer.field_7505, peer.field_7483, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.field_6220, peer.field_6283, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.field_7524, peer.field_7500, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.field_7502, peer.field_7521, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.field_7522, peer.field_7499, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevX, peer.x, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevY, peer.y, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevZ, peer.z, MinecraftClient.getInstance().getTickDelta());
		}
	};

}
