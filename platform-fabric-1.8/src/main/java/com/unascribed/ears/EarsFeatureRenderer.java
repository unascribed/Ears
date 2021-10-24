package com.unascribed.ears;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.DirectEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.ModelBox;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EarsFeatureRenderer implements FeatureRenderer<AbstractClientPlayerEntity> {
	
	private final PlayerEntityRenderer render;
	
	public EarsFeatureRenderer(PlayerEntityRenderer render) {
		this.render = render;
		EarsLog.debug("Platform:Renderer", "Constructed");
		((SetTranslucent)render.getModel().field_3782).ears$setTranslucent(true);
		((SetTranslucent)render.getModel().field_3816).ears$setTranslucent(true);
		((SetTranslucent)render.getModel().field_3814).ears$setTranslucent(true);
		((SetTranslucent)render.getModel().field_3815).ears$setTranslucent(true);
		((SetTranslucent)render.getModel().field_3812).ears$setTranslucent(true);
		((SetTranslucent)render.getModel().field_3813).ears$setTranslucent(true);
	}
	
	private float tickDelta;
	
	@Override
	public void render(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		this.tickDelta = tickDelta;
		delegate.render(entity, limbDistance, null);
	}
	
	public void renderLeftArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderLeftArm({})", entity);
		this.tickDelta = 0;
		delegate.render(entity, 0, BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(AbstractClientPlayerEntity entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({})", entity);
		this.tickDelta = 0;
		delegate.render(entity, 0, BodyPart.RIGHT_ARM);
	}
	
	@Override
	public boolean combineTextures() {
		return true;
	}

	private final DirectEarsRenderDelegate<AbstractClientPlayerEntity, ModelPart> delegate = new DirectEarsRenderDelegate<AbstractClientPlayerEntity, ModelPart>() {
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
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerEntityModel model = render.getModel();
			return d.map(BodyPart.HEAD, model.field_3781)
					.map(BodyPart.LEFT_ARM, model.field_3785)
					.map(BodyPart.LEFT_LEG, model.field_3787)
					.map(BodyPart.RIGHT_ARM, model.field_3784)
					.map(BodyPart.RIGHT_LEG, model.field_3786)
					.map(BodyPart.TORSO, model.field_3783);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.method_3106(1/16f);
			ModelBox cuboid = modelPart.field_3941.get(0);
			GlStateManager.scalef(1/16f, 1/16f, 1/16f);
			GlStateManager.translatef(cuboid.minX, cuboid.maxY, cuboid.minZ);
			if (peer.isSneaking()) {
				if (part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG) {
					GlStateManager.translated(0, 0.1875, 0);
				} else {
					GlStateManager.translated(0, 0.2125, 0);
				}
			}
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
			return ((AccessorPlayerEntityModel)render.getModel()).ears$isThinArms();
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
		protected void doBindAux(TexSource src, byte[] pngData) {
			if (pngData == null) {
				GlStateManager.bindTexture(0);
			} else {
				Identifier skin = peer.getSkinTexture();
				Identifier id = new Identifier(skin.getNamespace(), src.addSuffix(skin.getPath()));
				if (MinecraftClient.getInstance().getTextureManager().getTexture(id) == null) {
					try {
						MinecraftClient.getInstance().getTextureManager().loadTexture(id, new NativeImageBackedTexture(ImageIO.read(new ByteArrayInputStream(pngData))));
					} catch (IOException e) {
						MinecraftClient.getInstance().getTextureManager().loadTexture(id, TextureUtil.MISSINGNO);
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
			Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, VertexFormats.field_5175);
		}

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			Tessellator.getInstance().getBuffer().vertex(x, y, z).texture(u, v).normal(nX, nY, nZ).next();
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

		@Override
		public float getTime() {
			return peer.ticksAlive+tickDelta;
		}

		@Override
		public boolean isFlying() {
			return peer.abilities.flying;
		}

		@Override
		public boolean hasEquipment(Equipment e) {
			ItemStack chest = peer.inventory.getArmor(2);
			ItemStack feet = peer.inventory.getArmor(0);
			return Decider.<Equipment, Boolean>begin(e)
					.map(Equipment.ELYTRA, false)
					.map(Equipment.CHESTPLATE, chest != null && chest.getItem() instanceof ArmorItem)
					.map(Equipment.BOOTS, feet != null && feet.getItem() instanceof ArmorItem)
					.orElse(false);
		}

		@Override
		public boolean isGliding() {
			return false;
		}
	};
}
