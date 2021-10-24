package com.unascribed.ears;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.DirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorPlayerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EarsLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	
	public EarsLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> context) {
		super(context);
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void render(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		delegate.render(entity, limbDistance, null);
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderLeftArm({})", entity);
		delegate.render(entity, 0, BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({})", entity);
		delegate.render(entity, 0, BodyPart.RIGHT_ARM);
	}
	
	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

	private final DirectEarsRenderDelegate<AbstractClientPlayerEntity, RendererModel> delegate = new DirectEarsRenderDelegate<AbstractClientPlayerEntity, RendererModel>() {
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
		protected Decider<BodyPart, RendererModel> decideModelPart(Decider<BodyPart, RendererModel> d) {
			PlayerModel<AbstractClientPlayerEntity> model = getEntityModel();
			return d.map(BodyPart.HEAD, model.bipedHead)
					.map(BodyPart.LEFT_ARM, model.bipedLeftArm)
					.map(BodyPart.LEFT_LEG, model.bipedLeftLeg)
					.map(BodyPart.RIGHT_ARM, model.bipedRightArm)
					.map(BodyPart.RIGHT_LEG, model.bipedRightLeg)
					.map(BodyPart.TORSO, model.bipedBody);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, RendererModel modelPart) {
			modelPart.postRender(1/16f);
			ModelBox cuboid = modelPart.cubeList.get(0);
			GlStateManager.scalef(1/16f, 1/16f, 1/16f);
			GlStateManager.translatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
			if (peer.isSneaking()) {
				if (part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG) {
					GlStateManager.translatef(0, 0.1875f, 0);
				} else {
					GlStateManager.translatef(0, 0.2125f, 0);
				}
			}
		}
		
		@Override
		protected boolean isVisible(RendererModel modelPart) {
			return modelPart.showModel;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsMod.getEarsFeatures(peer);
		}

		@Override
		protected boolean isSlim() {
			return ((AccessorPlayerModel)getEntityModel()).ears$isSmallArms();
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
		protected void drawQuad() {
			Tessellator.getInstance().draw();
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			GL11.glPointSize(8);
			GlStateManager.disableTexture();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
			bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture();
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
		public boolean hasEquipment(Equipment e) {
			ItemStack chest = peer.getItemStackFromSlot(EquipmentSlotType.CHEST);
			return Decider.<Equipment, Boolean>begin(e)
					.map(Equipment.ELYTRA, chest.getItem() instanceof ElytraItem)
					.map(Equipment.CHESTPLATE, chest.getItem() instanceof ArmorItem)
					.orElse(false);
		}

		@Override
		public boolean isGliding() {
			return peer.isElytraFlying();
		}
	};

}
