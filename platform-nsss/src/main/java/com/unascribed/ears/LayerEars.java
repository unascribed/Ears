package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;

import com.mojang.minecraft.entity.EntityPlayer;
import com.mojang.minecraft.entity.item.ItemArmor;
import com.mojang.minecraft.entity.item.ItemStack;
import com.mojang.minecraft.entity.model.ModelBiped;
import com.mojang.minecraft.entity.model.ModelRenderer;
import com.mojang.minecraft.entity.render.RenderPlayer;
import com.mojang.minecraft.render.PositionTexureVertex;
import com.mojang.minecraft.render.RenderEngine;
import com.mojang.minecraft.render.Tessellator;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.UnmanagedEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.common.util.EarsStorage;
import com.unascribed.ears.legacy.LegacyHelper;

public class LayerEars {
	
	private RenderPlayer render;
	private float tickDelta;
	private float brightness;
	
	public void doRenderLayer(RenderPlayer render, EntityPlayer entity, float limbDistance, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.render = render;
		this.tickDelta = partialTicks;
		this.brightness = entity.getEntityBrightness(partialTicks);
		delegate.render(entity);
	}
	
	public void renderRightArm(RenderPlayer render, EntityPlayer entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({}, {})", render, entity);
		this.render = render;
		this.tickDelta = 0;
		this.brightness = entity.getEntityBrightness(0);
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	private final UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer> delegate = new UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer>() {
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.field_1403_h;
		}
		
		@Override
		public boolean isSlim() {
			return LegacyHelper.isSlimArms(peer.playerName);
		}
		
		@Override
		protected EarsFeatures getEarsFeatures() {
			return Ears.earsSkinFeatures.containsKey(peer.skinURL) ? Ears.earsSkinFeatures.get(peer.skinURL) : EarsFeatures.DISABLED;
		}
		
		@Override
		protected void doBindSkin() {
			RenderEngine engine = Ears.game.renderEngine;
			int id = engine.func_1071_a(peer.skinURL, peer.getEntityTexture());
			if (id < 0) return;
			engine.bindTex(id);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelRenderer modelPart) {
			modelPart.func_926_b(1/16f);
			glScalef(1/16f, 1/16f, 1/16f);
			PositionTexureVertex vert = Ears.getVertices(modelPart)[3];
			glTranslatef((float)vert.field_1655_a.xCoord, (float)vert.field_1655_a.yCoord, (float)vert.field_1655_a.zCoord);
		}
		
		@Override
		protected Decider<BodyPart, ModelRenderer> decideModelPart(Decider<BodyPart, ModelRenderer> d) {
			ModelBiped model = Ears.getModelBipedMain(render);
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
		protected void drawQuad() {
			Tessellator.instance.draw();
		}

		@Override
		protected String getSkinUrl() {
			return peer.skinURL;
		}

		@Override
		protected int uploadImage(BufferedImage img) {
			return Ears.game.renderEngine.func_1074_a(img);
		}

		@Override
		public float getTime() {
			return peer.ticksExisted+tickDelta;
		}

		@Override
		public boolean isFlying() {
			return false;
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
		protected float getBrightness() {
			return brightness;
		}
		
		@Override
		public boolean needsSecondaryLayersDrawn() {
			return true;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.field_705_Q, peer.limbSwingAmount, tickDelta);
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.field_775_e, peer.field_774_f, tickDelta);
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevRenderYawOffset, peer.renderYawOffset, tickDelta);
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(EarsStorage.get(peer, Ears.PREV_CAPE_X), EarsStorage.get(peer, Ears.CAPE_X), tickDelta);
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(EarsStorage.get(peer, Ears.PREV_CAPE_Y), EarsStorage.get(peer, Ears.CAPE_Y), tickDelta);
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(EarsStorage.get(peer, Ears.PREV_CAPE_Z), EarsStorage.get(peer, Ears.CAPE_Z), tickDelta);
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
