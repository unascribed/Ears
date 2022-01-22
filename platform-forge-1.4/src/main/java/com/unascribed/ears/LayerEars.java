package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.UnmanagedEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.legacy.LegacyHelper;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumArmorMaterial;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.Tessellator;
import net.minecraftforge.client.ForgeHooksClient;

public class LayerEars {
	
	private RenderPlayer render;
	private float tickDelta;
	
	public void doRenderLayer(RenderPlayer render, EntityPlayer entity, float limbDistance, float partialTicks) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.render = render;
		this.tickDelta = partialTicks;
		delegate.render(entity);
	}
	
	public void renderRightArm(RenderPlayer render, EntityPlayer entity) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderRightArm({}, {})", render, entity);
		this.render = render;
		this.tickDelta = 0;
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	private final UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer> delegate = new UnmanagedEarsRenderDelegate<EntityPlayer, ModelRenderer>() {
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.showModel;
		}
		
		@Override
		public boolean isSlim() {
			return LegacyHelper.isSlimArms(peer.username);
		}
		
		@Override
		protected void tearDownRenderState() {
			if (glint) teardownGlint();
			super.tearDownRenderState();
		}
		
		@Override
		protected EarsFeatures getEarsFeatures() {
			if (Ears.earsSkinFeatures.containsKey(peer.skinUrl)) {
				EarsFeatures feat = Ears.earsSkinFeatures.get(peer.skinUrl);
				EarsFeaturesStorage.INSTANCE.put(peer.username, LegacyHelper.getUuid(peer.username), feat);
				if (!peer.getHasActivePotion()) {
					return feat;
				}
			}
			return EarsFeatures.DISABLED;
		}
		
		@Override
		protected void doBindSkin() {
			if (glint) teardownGlint();
			RenderEngine engine = FMLClientHandler.instance().getClient().o;
			int id = engine.getTextureForDownloadableImage(peer.skinUrl, peer.getTexture());
			if (id < 0) return;
			engine.bindTexture(id);
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
		
		private float armorR = 1;
		private float armorG = 1;
		private float armorB = 1;
		private float armorA = 1;
		
		private boolean glint = false;
		
		private void teardownGlint() {
			replayBuffer.clear();
			glColor4f(1, 1, 1, 1);
			glMatrixMode(GL_TEXTURE);
			glDepthMask(true);
			glLoadIdentity();
			glMatrixMode(GL_MODELVIEW);
			glEnable(GL_LIGHTING);
			glDisable(GL_BLEND);
			glDepthFunc(GL_LEQUAL);
			glint = false;
		}

		@Override
		protected void doBindBuiltin(TexSource src) {
			if (src.isGlint()) {
				glint = true;
				Minecraft.x().o.bindTexture(Minecraft.x().o.getTexture("%blur%/misc/glint.png"));
				glEnable(GL_BLEND);
				float half = 0.5f;
				glColor4f(half, half, half, 1);
				glDepthFunc(GL_EQUAL);
				glDepthMask(false);
			} else if (canBind(src)) {
				if (glint) teardownGlint();
				int slot = getSlot(src);
				ItemStack equipment = peer.inventory.armorItemInSlot(slot);
				ItemArmor ai = (ItemArmor)equipment.getItem();
				if (ai.getArmorMaterial() == EnumArmorMaterial.CLOTH) {
					int c = ai.getColor(equipment);
					armorR = (c >> 16 & 255) / 255.0F;
					armorG = (c >> 8 & 255) / 255.0F;
					armorB = (c & 255) / 255.0F;
					armorA = 1;
				}
				String s = ForgeHooksClient.getArmorTexture(equipment, "/armor/" + RenderPlayer.armorFilenamePrefix[ai.renderIndex] + "_" + (src == TexSource.LEGGINGS ? 2 : 1) + ".png");
				Minecraft.x().o.bindTexture(Minecraft.x().o.getTexture(s));
			}
		}
		
		@Override
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (glint) teardownGlint();
			super.doBindAux(src, pngData);
		}
		
		@Override
		public boolean canBind(TexSource tex) {
			boolean glint = tex.isGlint();
			if (glint) tex = tex.getParent();
			int slot = getSlot(tex);
			if (slot == -1) return super.canBind(tex);
			ItemStack equipment = peer.inventory.armorItemInSlot(slot);
			if (equipment == null || !(equipment.getItem() instanceof ItemArmor)) return false;
			ItemArmor ia = (ItemArmor)equipment.getItem();
			String s = ForgeHooksClient.getArmorTexture(equipment, "/armor/" + RenderPlayer.armorFilenamePrefix[ia.renderIndex] + "_" + (tex == TexSource.LEGGINGS ? 2 : 1) + ".png");
			return s != null && (!glint || equipment.hasEffect());
		}

		private int getSlot(TexSource tex) {
			return Decider.<TexSource, Integer>begin(tex)
					.map(TexSource.HELMET, 3)
					.map(TexSource.CHESTPLATE, 2)
					.map(TexSource.LEGGINGS, 1)
					.map(TexSource.BOOTS, 0)
					.orElse(-1);
		}
		
		private final List<Runnable> replayBuffer = new ArrayList<>();
		
		@Override
		protected void addVertex(final float x, final float y, final int z, float r, float g, float b, float a, final float u, final float v, final float nX, final float nY, final float nZ) {
			r *= armorR;
			g *= armorG;
			b *= armorB;
			a *= armorA;
			if (glint) {
				replayBuffer.add(new Runnable() {
					@Override
					public void run() {
						Tessellator.instance.setNormal(nX, nY, nZ);
						Tessellator.instance.addVertexWithUV(x, y, z, u, v);
					}
				});
			} else {
				Tessellator.instance.setColorRGBA_F(r, g, b, a);
			}
			Tessellator.instance.setNormal(nX, nY, nZ);
			Tessellator.instance.addVertexWithUV(x, y, z, u, v);
		}
		
		@Override
		public void setEmissive(boolean emissive) {
			super.setEmissive(emissive);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			if (emissive) {
				glDisable(GL11.GL_TEXTURE_2D);
			} else {
				glEnable(GL11.GL_TEXTURE_2D);
			}
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		
		@Override
		protected void drawQuad() {
			if (glint) {
				float t = peer.ticksExisted + tickDelta;
				for(int p = 0; p < 2; p++) {
					glDisable(GL_LIGHTING);
					float f = 0.76f;
					glColor4f(0.5f * f, 0.25f * f, 0.8f * f, 1);
					glBlendFunc(GL_SRC_COLOR, GL_ONE);
					glMatrixMode(GL_TEXTURE);
					glLoadIdentity();
					float ofs = t * (0.001f + p * 0.003f) * 20;
					float third = 1/3f;
					glScalef(third, third, third);
					glRotatef(30 - p * 60f, 0, 0, 1);
					glTranslatef(0, ofs, 0);
					glMatrixMode(GL_MODELVIEW);
					if (p == 1) {
						beginQuad();
						for (Runnable r : replayBuffer) {
							r.run();
						}
					}
					Tessellator.instance.draw();
				}
				replayBuffer.clear();
			} else {
				Tessellator.instance.draw();
			}
		}

		@Override
		protected String getSkinUrl() {
			return peer.skinUrl;
		}

		@Override
		protected int uploadImage(BufferedImage img) {
			return FMLClientHandler.instance().getClient().o.allocateAndSetupTexture(img);
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
			return !peer.getHasActivePotion();
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevDistanceWalkedModified, peer.distanceWalkedModified, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.prevLegYaw, peer.legYaw, tickDelta);
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
