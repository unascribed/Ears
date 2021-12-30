package com.unascribed.ears;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.DirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorModelPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBox;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.util.ResourceLocation;

public class EarsLayerRenderer implements LayerRenderer<AbstractClientPlayer> {
	
	private final RenderPlayer render;
	
	public EarsLayerRenderer(RenderPlayer render) {
		this.render = render;
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(AbstractClientPlayer entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		delegate.render(entity, null);
	}
	
	public void renderLeftArm(AbstractClientPlayer entity) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderLeftArm({})", entity);
		delegate.render(entity, BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(AbstractClientPlayer entity) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderRightArm({})", entity);
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

	private final DirectEarsRenderDelegate<AbstractClientPlayer, ModelRenderer> delegate = new DirectEarsRenderDelegate<AbstractClientPlayer, ModelRenderer>() {
		@Override
		protected void setUpRenderState() {
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		@Override
		protected void tearDownRenderState() {
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
		}
		
		@Override
		protected Decider<BodyPart, ModelRenderer> decideModelPart(Decider<BodyPart, ModelRenderer> d) {
			ModelPlayer model = render.getMainModel();
			return d.map(BodyPart.HEAD, model.bipedHead)
					.map(BodyPart.LEFT_ARM, model.bipedLeftArm)
					.map(BodyPart.LEFT_LEG, model.bipedLeftLeg)
					.map(BodyPart.RIGHT_ARM, model.bipedRightArm)
					.map(BodyPart.RIGHT_LEG, model.bipedRightLeg)
					.map(BodyPart.TORSO, model.bipedBody);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelRenderer modelPart) {
			if (peer.isSneaking() && permittedBodyPart == null) {
				GlStateManager.translatef(0, 0.2f, 0);
			}
			modelPart.postRender(1/16f);
			ModelBox cuboid = modelPart.cubeList.get(0);
			GlStateManager.scalef(1/16f, 1/16f, 1/16f);
			GlStateManager.translatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
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
			return ((AccessorModelPlayer)render.getMainModel()).ears$isSmallArms();
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
			Minecraft.getInstance().getTextureManager().bindTexture(peer.getLocationSkin());
		}

		@Override
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (pngData == null) {
				GlStateManager.bindTexture(0);
			} else {
				ResourceLocation skin = peer.getLocationSkin();
				ResourceLocation id = new ResourceLocation(skin.getNamespace(), src.addSuffix(skin.getPath()));
				if (Minecraft.getInstance().getTextureManager().getTexture(id) == null) {
					try {
						Minecraft.getInstance().getTextureManager().loadTexture(id, new DynamicTexture(NativeImage.read(toNativeBuffer(pngData))));
					} catch (IOException e) {
						Minecraft.getInstance().getTextureManager().loadTexture(id, MissingTextureSprite.getDynamicTexture());
					}
				}
				Minecraft.getInstance().getTextureManager().bindTexture(id);
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
			Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Tessellator.getInstance().getBuffer().pos(x, y, z).tex(u, v).normal(nX, nY, nZ).endVertex();
		}
		
		@Override
		public void setEmissive(boolean emissive) {
			super.setEmissive(emissive);
			GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
			if (emissive) {
				GlStateManager.disableLighting();
				GlStateManager.disableTexture2D();
			} else {
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
			}
			GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
		}

		@Override
		protected void drawQuad() {
			Tessellator.getInstance().draw();
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			GL11.glPointSize(8);
			GlStateManager.disableTexture2D();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
			bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture2D();
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
			return peer.isWearing(EnumPlayerModelParts.JACKET);
		}

		@Override
		public boolean isWearingBoots() {
			return peer.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemArmor;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemArmor;
		}

		@Override
		public boolean isWearingElytra() {
			return peer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemElytra;
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
			return EarsCommon.lerpDelta(peer.prevPosX, peer.posX, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevPosY, peer.posY, Minecraft.getInstance().getRenderPartialTicks());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevPosZ, peer.posZ, Minecraft.getInstance().getRenderPartialTicks());
		}
	};
}
