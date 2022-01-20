package com.unascribed.ears;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.DirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerEars implements LayerRenderer<AbstractClientPlayer> {
	
	private final RenderPlayer render;
	
	public LayerEars(RenderPlayer render) {
		this.render = render;
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void doRenderLayer(AbstractClientPlayer entity, float limbAngle, float limbDistance,
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
				GlStateManager.translate(0, 0.2f, 0);
			}
			modelPart.postRender(1/16f);
			ModelBox cuboid = modelPart.cubeList.get(0);
			GlStateManager.scale(1/16f, 1/16f, 1/16f);
			GlStateManager.translate(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
		}
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.showModel;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return Ears.getEarsFeatures(peer);
		}

		@Override
		public boolean isSlim() {
			return Ears.isSmallArms(render.getMainModel());
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
			Minecraft.getMinecraft().getTextureManager().bindTexture(peer.getLocationSkin());
		}

		@Override
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (pngData == null) {
				GlStateManager.bindTexture(0);
			} else {
				ResourceLocation skin = peer.getLocationSkin();
				ResourceLocation id = new ResourceLocation(skin.getResourceDomain(), src.addSuffix(skin.getResourcePath()));
				if (Minecraft.getMinecraft().getTextureManager().getTexture(id) == null) {
					try {
						Minecraft.getMinecraft().getTextureManager().loadTexture(id, new DynamicTexture(ImageIO.read(new ByteArrayInputStream(pngData))));
					} catch (IOException e) {
						Minecraft.getMinecraft().getTextureManager().loadTexture(id, TextureUtil.MISSING_TEXTURE);
					}
				}
				Minecraft.getMinecraft().getTextureManager().bindTexture(id);
			}
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			GlStateManager.translate(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			GlStateManager.rotate(ang, x, y, z);
		}

		@Override
		protected void doScale(float x, float y, float z) {
			GlStateManager.scale(x, y, z);
		}

		@Override
		protected void beginQuad() {
			Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Tessellator.getInstance().getBuffer().pos(x, y, z).tex(u, v).color(r, g, b, a).normal(nX, nY, nZ).endVertex();
		}
		
		@Override
		public void setEmissive(boolean emissive) {
			super.setEmissive(emissive);
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			if (emissive) {
				GlStateManager.disableLighting();
				GlStateManager.disableTexture2D();
			} else {
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
			}
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}

		@Override
		protected void drawQuad() {
			Tessellator.getInstance().draw();
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			GL11.glPointSize(8);
			GlStateManager.disableTexture2D();
			VertexBuffer bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
			bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture2D();
		}

		@Override
		public float getTime() {
			return peer.ticksExisted+Minecraft.getMinecraft().getRenderPartialTicks();
		}

		@Override
		public boolean isFlying() {
			return peer.capabilities.isFlying;
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
			ItemStack feet = peer.inventory.armorItemInSlot(0);
			return feet != null && feet.getItem() instanceof ItemArmor;
		}

		@Override
		public boolean isWearingChestplate() {
			ItemStack chest = peer.inventory.armorItemInSlot(2);
			return chest != null && chest.getItem() instanceof ItemArmor;
		}

		@Override
		public boolean isWearingElytra() {
			ItemStack chest = peer.inventory.armorItemInSlot(2);
			return chest != null && chest.getItem() instanceof ItemElytra;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.prevLimbSwingAmount, peer.limbSwingAmount, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevCameraYaw, peer.cameraYaw, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevRenderYawOffset, peer.renderYawOffset, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.prevChasingPosX, peer.chasingPosX, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.prevChasingPosY, peer.chasingPosY, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.prevChasingPosZ, peer.chasingPosZ, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevPosX, peer.posX, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevPosY, peer.posY, Minecraft.getMinecraft().getRenderPartialTicks());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevPosZ, peer.posZ, Minecraft.getMinecraft().getRenderPartialTicks());
		}
	};
}
