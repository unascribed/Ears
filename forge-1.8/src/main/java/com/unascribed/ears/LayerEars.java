package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.EarsRenderDelegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class LayerEars implements LayerRenderer<AbstractClientPlayer>, EarsRenderDelegate {
	
	private final RenderPlayer render;
	
	private AbstractClientPlayer entity;
	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	
	public LayerEars(RenderPlayer render) {
		this.render = render;
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void doRenderLayer(AbstractClientPlayer entity,
			float limbSwing, float limbDistance, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {})", entity, limbSwing, limbDistance, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && Ears.earsSkinFeatures.containsKey(tex)) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(Ears.earsSkinFeatures.get(tex), this, limbDistance);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}
	
	public void renderLeftArm(AbstractClientPlayer entity) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderLeftArm(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && Ears.earsSkinFeatures.containsKey(tex)) {
			EarsLog.debug("Platform:Renderer", "renderLeftArm(...): Checks passed");
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.LEFT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(Ears.earsSkinFeatures.get(tex), this, 0);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}
	
	public void renderRightArm(AbstractClientPlayer entity) {
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}, tex={}", skin, tex);
		if (!entity.isInvisible() && Ears.earsSkinFeatures.containsKey(tex)) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
			this.entity = entity;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(Ears.earsSkinFeatures.get(tex), this, 0);
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
			this.entity = null;
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

	@Override
	public void push() {
		stackDepth++;
		GlStateManager.pushMatrix();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		GlStateManager.popMatrix();
		if (skipRendering > 0) skipRendering--;
	}

	@Override
	public void anchorTo(BodyPart part) {
		if (permittedBodyPart != null && part != permittedBodyPart) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not permissible in this pass, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		ModelRenderer model;
		switch (part) {
			case HEAD:
				model = render.getMainModel().bipedHead;
				break;
			case LEFT_ARM:
				model = render.getMainModel().bipedLeftArm;
				break;
			case LEFT_LEG:
				model = render.getMainModel().bipedLeftLeg;
				break;
			case RIGHT_ARM:
				model = render.getMainModel().bipedRightArm;
				break;
			case RIGHT_LEG:
				model = render.getMainModel().bipedRightLeg;
				break;
			case TORSO:
				model = render.getMainModel().bipedBody;
				break;
			default: return;
		}
		if (!model.showModel) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not visible, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		if (entity.isSneaking()) {
			if (part == BodyPart.LEFT_LEG || part == BodyPart.RIGHT_LEG) {
				GlStateManager.translate(0, 0.1875, 0);
			} else {
				GlStateManager.translate(0, 0.2125, 0);
			}
		}
		model.postRender(1/16f);
		ModelBox cuboid = model.cubeList.get(0);
		GlStateManager.scale(1/16f, 1/16f, 1/16f);
		GlStateManager.translate(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.translate(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.rotate(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer vc = tess.getWorldRenderer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;

		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vc.pos(-g, h+g, 0).tex(uv[0][0], uv[0][1]).normal(0, 0, -1).endVertex();
		vc.pos(w+g, h+g, 0).tex(uv[1][0], uv[1][1]).normal(0, 0, -1).endVertex();
		vc.pos(w+g, -g, 0).tex(uv[2][0], uv[2][1]).normal(0, 0, -1).endVertex();
		vc.pos(-g, -g, 0).tex(uv[3][0], uv[3][1]).normal(0, 0, -1).endVertex();
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer vc = tess.getWorldRenderer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vc.pos(-g, -g, 0).tex(uv[3][0], uv[3][1]).normal(0, 0, 1).endVertex();
		vc.pos(w+g, -g, 0).tex(uv[2][0], uv[2][1]).normal(0, 0, 1).endVertex();
		vc.pos(w+g, h+g, 0).tex(uv[1][0], uv[1][1]).normal(0, 0, 1).endVertex();
		vc.pos(-g, h+g, 0).tex(uv[0][0], uv[0][1]).normal(0, 0, 1).endVertex();
		tess.draw();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture2D();
		WorldRenderer bb = Tessellator.getInstance().getWorldRenderer();
		bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.disableTexture2D();
	}
}
