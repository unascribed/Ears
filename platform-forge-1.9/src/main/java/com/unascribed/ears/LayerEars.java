package com.unascribed.ears;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsFeatures;
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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class LayerEars implements LayerRenderer<AbstractClientPlayer> {
	
	private final RenderPlayer render;
	
	public LayerEars(RenderPlayer render) {
		this.render = render;
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void doRenderLayer(AbstractClientPlayer entity, float limbAngle, float limbDistance,
			float tickDelta, float age, float headYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		delegate.render(entity, limbDistance, null);
	}
	
	public void renderLeftArm(AbstractClientPlayer entity) {
		EarsLog.debug("Platform:Renderer", "renderLeftArm({})", entity);
		delegate.render(entity, 0, BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(AbstractClientPlayer entity) {
		EarsLog.debug("Platform:Renderer", "renderRightArm({})", entity);
		delegate.render(entity, 0, BodyPart.RIGHT_ARM);
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
			modelPart.postRender(1/16f);
			ModelBox cuboid = modelPart.cubeList.get(0);
			GlStateManager.scale(1/16f, 1/16f, 1/16f);
			GlStateManager.translate(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
			if (peer.isSneaking()) {
				if (part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG) {
					GlStateManager.translate(0, 0.1875f, 0);
				} else {
					GlStateManager.translate(0, 0.2125f, 0);
				}
			}
		}
		
		@Override
		protected boolean isVisible(ModelRenderer modelPart) {
			return modelPart.showModel;
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
		protected boolean isSlim() {
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
		protected void doBindSub(TexSource src, byte[] pngData) {
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
	};
}