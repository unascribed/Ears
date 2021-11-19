package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.ImmediateEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.legacy.LegacyHelper;
import com.unascribed.ears.mixin.accessor.AccessorLivingEntityRenderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.model.BipedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemInstance;
import net.minecraft.item.armour.ArmourItem;

public class LayerEars {
	private PlayerRenderer renderer;
	private float brightness;
	private float tickDelta;

	public void doRenderLayer(PlayerRenderer renderer, Player entity, float limbDistance, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.renderer = renderer;
		this.tickDelta = partialTicks;
		this.brightness = entity.getBrightnessAtEyes(partialTicks);
		delegate.render(entity);
	}

	public void renderRightArm(PlayerRenderer renderer, Player entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({}, {})", renderer, entity);
		this.renderer = renderer;
		this.tickDelta = 0;
		this.brightness = entity.getBrightnessAtEyes(0);
		delegate.render(entity, BodyPart.RIGHT_ARM);
	}
	
	private final ImmediateEarsRenderDelegate<Player, ModelPart> delegate = new ImmediateEarsRenderDelegate<Player, ModelPart>() {

		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			BipedModel modelPlayer = (BipedModel)((AccessorLivingEntityRenderer)renderer).getEntityModel();
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
			modelPart.method_1820(1/16f); // postRender (you can tell because it's the one that applies transforms without matrix isolation)
			glScalef(1/16f, 1/16f, 1/16f);
			ModelPartTextureFixer modelWithPositions = (ModelPartTextureFixer) modelPart;
			glTranslated(modelWithPositions.getPosX1(), modelWithPositions.getPosY2(), modelWithPositions.getPosZ1());
		}

		@Override
		protected void doBindSkin() {
			TextureManager textureManager = EarsMod.client.textureManager;
			int id = textureManager.method_1093(peer.skinUrl, peer.method_1314());
			if (id < 0) return;
			textureManager.bindTexture(id);
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsMod.earsSkinFeatures.get(peer.skinUrl);
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
			return EarsMod.client.textureManager.glLoadImage(img);
		}

		@Override
		public float getTime() {
			return peer.field_1645+tickDelta;
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
			ItemInstance feet = peer.inventory.getArmourItem(0);
			return feet != null && feet.getType() instanceof ArmourItem;
		}

		@Override
		public boolean isWearingChestplate() {
			ItemInstance chest = peer.inventory.getArmourItem(2);
			return chest != null && chest.getType() instanceof ArmourItem;
		}

		@Override
		public boolean isWearingElytra() {
			return false;
		}
		
		@Override
		public boolean needsSecondaryLayersDrawn() {
			return true;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.field_1634, peer.field_1635, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.field_1048, peer.limbDistance, tickDelta);
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.field_524, peer.field_525, tickDelta);
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.field_1013, peer.field_1012, tickDelta);
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.field_530, peer.field_533, tickDelta);
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.field_531, peer.field_534, tickDelta);
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.field_532, peer.field_535, tickDelta);
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
