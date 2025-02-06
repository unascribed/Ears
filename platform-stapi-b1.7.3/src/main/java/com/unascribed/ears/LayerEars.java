package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.ImmediateEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.legacy.LegacyHelper;
import com.unascribed.ears.mixin.accessor.AccessorLivingEntityRenderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ArmorItem;

public class LayerEars {
	private PlayerEntityRenderer renderer;
	private float brightness;
	private float tickDelta;

	public void doRenderLayer(PlayerEntityRenderer renderer, PlayerEntity entity, float limbDistance, float partialTicks) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.renderer = renderer;
		this.tickDelta = partialTicks;
		this.brightness = entity.getBrightnessAtEyes(partialTicks);
		delegate.render(entity);
	}

	public void renderRightArm(PlayerEntityRenderer renderer, PlayerEntity entity) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderRightArm({}, {})", renderer, entity);
		this.renderer = renderer;
		this.tickDelta = 0;
		this.brightness = entity.getBrightnessAtEyes(0);
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	private final ImmediateEarsRenderDelegate<PlayerEntity, ModelPart> delegate = new ImmediateEarsRenderDelegate<PlayerEntity, ModelPart>() {

		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			BipedEntityModel modelPlayer = (BipedEntityModel)((AccessorLivingEntityRenderer)renderer).getModel();
			return d
				.map(BodyPart.HEAD, modelPlayer.head)
				.map(BodyPart.LEFT_ARM, modelPlayer.leftArm)
				.map(BodyPart.LEFT_LEG, modelPlayer.leftLeg)
				.map(BodyPart.RIGHT_ARM, modelPlayer.rightArm)
				.map(BodyPart.RIGHT_LEG, modelPlayer.rightLeg)
				.map(BodyPart.TORSO, modelPlayer.body);
		}

		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.transform(1/16f); // postRender (you can tell because it's the one that applies transforms without matrix isolation)
			glScalef(1/16f, 1/16f, 1/16f);
			ModelPartAnchor modelWithPositions = (ModelPartAnchor) modelPart;
			glTranslated(modelWithPositions.getPosX1(), modelWithPositions.getPosY2(), modelWithPositions.getPosZ1());
		}

		@Override
		protected void doBindSkin() {
			TextureManager textureManager = EarsMod.client.textureManager;
			int id = textureManager.downloadTexture(peer.skinUrl, peer.getTexture());
			if (id < 0) return;
			textureManager.bindTexture(id);
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			EarsFeatures feat = EarsMod.earsSkinFeatures.get(peer.skinUrl);
			if (feat != null) {
				EarsFeaturesStorage.INSTANCE.put(peer.name, LegacyHelper.getUuid(peer.name), feat);
				return feat;
			}
			return EarsFeatures.DISABLED;
		}

		@Override
		public boolean isSlim() {
			return LegacyHelper.isSlimArms(peer.name);
		}

		@Override
		protected boolean isVisible(ModelPart modelPart) {
			return modelPart.visible;
		}
		
		@Override
		protected float getBrightness() {
			return brightness;
		}

		@Override
		protected String getSkinUrl() {
			return peer.skinUrl;
		}

		@Override
		protected int uploadImage(BufferedImage img) {
			return EarsMod.client.textureManager.load(img);
		}

		@Override
		public float getTime() {
			return peer.age+tickDelta;
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
			ItemStack feet = peer.inventory.getArmorStack(0);
			return feet != null && feet.getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			ItemStack chest = peer.inventory.getArmorStack(2);
			return chest != null && chest.getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			return false;
		}
		
		@Override
		public boolean needsSecondaryLayersDrawn() {
			return false;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevHorizontalSpeed, peer.horizontalSpeed, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.lastWalkAnimationSpeed, peer.walkAnimationSpeed, tickDelta);
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevStepBobbingAmount, peer.stepBobbingAmount, tickDelta);
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.lastBodyYaw, peer.bodyYaw, tickDelta);
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.prevCapeX, peer.capeX, tickDelta);
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.prevCapeY, peer.capeY, tickDelta);
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.prevCapeZ, peer.capeZ, tickDelta);
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevX, peer.x, tickDelta);
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevY, peer.y, tickDelta);
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevZ, peer.z, tickDelta);
		}
		
	};
}
