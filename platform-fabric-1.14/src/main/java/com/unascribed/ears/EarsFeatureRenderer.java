package com.unascribed.ears;

import java.io.IOException;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
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
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Texture;
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
			Identifier skin = peer.getSkinTexture();
			Texture tex = MinecraftClient.getInstance().getTextureManager().getTexture(skin);
			EarsLog.debug("Platform:Renderer", "getEarsFeatures(): skin={}, tex={}", skin, tex);
			if (tex instanceof EarsFeaturesHolder && !peer.isInvisible()) {
				return ((EarsFeaturesHolder)tex).getEarsFeatures();
			}
			return EarsFeatures.DISABLED;
		}

		@Override
		protected boolean isSlim() {
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
		protected void doBindSub(TexSource src, byte[] pngData) {
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
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			GL11.glPointSize(8);
			GlStateManager.disableTexture();
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			bb.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
			bb.vertex(0, 0, 0).color(r, g, b, a).next();
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture();
		}
	};

}
