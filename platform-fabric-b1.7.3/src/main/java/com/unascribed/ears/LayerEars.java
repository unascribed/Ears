package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.ImmediateEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.accessor.AccessorLivingEntityRenderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.model.BipedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.Player;

public class LayerEars {
	private PlayerRenderer renderer;
	private float brightness;

	public void doRenderLayer(PlayerRenderer renderer, Player entity, float limbDistance, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, limbDistance, partialTicks);
		this.renderer = renderer;
		this.brightness = entity.getBrightnessAtEyes(partialTicks);
		delegate.render(entity, limbDistance);
	}

	public void renderRightArm(PlayerRenderer renderer, Player entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({}, {})", renderer, entity);
		this.renderer = renderer;
		this.brightness = entity.getBrightnessAtEyes(0);
		delegate.render(entity, 0, BodyPart.RIGHT_ARM);
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
		protected boolean isSlim() {
			return EarsMod.slimUsers.contains(peer.name);
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
		
	};
}
