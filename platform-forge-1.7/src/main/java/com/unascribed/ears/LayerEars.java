package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.PartiallyUnmanagedEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerEars {
	
	private RenderPlayer render;
	private float tickDelta;
	
	public void doRenderLayer(RenderPlayer render, AbstractClientPlayer entity, float limbDistance, float partialTicks) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.render = render;
		this.tickDelta = partialTicks;
		delegate.render(entity);
	}
	
	public void renderRightArm(RenderPlayer render, AbstractClientPlayer entity) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderRightArm({}, {})", render, entity);
		this.render = render;
		this.tickDelta = 0;
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	private final PartiallyUnmanagedEarsRenderDelegate<AbstractClientPlayer, ModelRenderer> delegate = new PartiallyUnmanagedEarsRenderDelegate<AbstractClientPlayer, ModelRenderer>() {
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.showModel;
		}
		
		@Override
		public boolean isSlim() {
			return render.modelBipedMain.bipedLeftArm == Ears.slimLeftArm;
		}
		
		@Override
		protected EarsFeatures getEarsFeatures() {
			ResourceLocation skin = peer.getLocationSkin();
			ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(skin);
			if (Ears.earsSkinFeatures.containsKey(tex) && !peer.isInvisible()) {
				return Ears.earsSkinFeatures.get(tex);
			} else {
				return EarsFeatures.DISABLED;
			}
		}
		
		@Override
		protected void doBindSkin() {
			Minecraft.getMinecraft().renderEngine.bindTexture(peer.getLocationSkin());
		}

		@Override
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (pngData == null) {
				glBindTexture(GL_TEXTURE_2D, 0);
			} else {
				ResourceLocation skin = peer.getLocationSkin();
				ResourceLocation id = new ResourceLocation(skin.getResourceDomain(), src.addSuffix(skin.getResourcePath()));
				if (Minecraft.getMinecraft().getTextureManager().getTexture(id) == null) {
					try {
						Minecraft.getMinecraft().getTextureManager().loadTexture(id, new DynamicTexture(ImageIO.read(new ByteArrayInputStream(pngData))));
					} catch (IOException e) {
						Minecraft.getMinecraft().getTextureManager().loadTexture(id, TextureUtil.missingTexture);
					}
				}
				Minecraft.getMinecraft().getTextureManager().bindTexture(id);
			}
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelRenderer modelPart) {
			modelPart.postRender(1/16f);
			ModelBox cuboid = (ModelBox)modelPart.cubeList.get(0);
			glScalef(1/16f, 1/16f, 1/16f);
			glTranslatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
		}
		
		@Override
		protected Decider<BodyPart, ModelRenderer> decideModelPart(Decider<BodyPart, ModelRenderer> d) {
			ModelBiped model = render.modelBipedMain;
			return d.map(BodyPart.HEAD, model.bipedHead)
					.map(BodyPart.LEFT_ARM, model.bipedLeftArm)
					.map(BodyPart.LEFT_LEG, model.bipedLeftLeg)
					.map(BodyPart.RIGHT_ARM, model.bipedRightArm)
					.map(BodyPart.RIGHT_LEG, model.bipedRightLeg)
					.map(BodyPart.TORSO, model.bipedBody);
		}
		
		@Override
		protected void beginQuad() {
			Tessellator.instance.startDrawing(GL_QUADS);
		}
		
		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Tessellator.instance.setColorRGBA_F(r, g, b, a);
			Tessellator.instance.setNormal(nX, nY, nZ);
			Tessellator.instance.addVertexWithUV(x, y, z, u, v);
		}
		
		@Override
		public void setEmissive(boolean emissive) {
			super.setEmissive(emissive);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			if (emissive) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			} else {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		
		@Override
		protected void drawQuad() {
			Tessellator.instance.draw();
		}

		@Override
		public float getTime() {
			return peer.ticksExisted+tickDelta;
		}

		@Override
		public boolean isFlying() {
			return peer.capabilities.isFlying;
		}

		@Override
		public boolean isGliding() {
			return false;
		}

		@Override
		public boolean isJacketEnabled() {
			return true;
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
			return false;
		}
		
		@Override
		public boolean needsSecondaryLayersDrawn() {
			return !peer.isInvisible();
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.prevLimbSwingAmount, peer.limbSwingAmount, tickDelta);
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevCameraYaw, peer.cameraYaw, tickDelta);
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevRenderYawOffset, peer.renderYawOffset, tickDelta);
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.field_71091_bM, peer.field_71094_bP, tickDelta);
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.field_71096_bN, peer.field_71095_bQ, tickDelta);
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.field_71097_bO, peer.field_71085_bR, tickDelta);
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevPosX, peer.posX, tickDelta);
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevPosY, peer.posY, tickDelta);
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevPosZ, peer.posZ, tickDelta);
		}
	};
}
