package com.unascribed.ears;

import org.lwjgl.opengl.GL11;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsLog;
import com.unascribed.ears.common.EarsRenderDelegate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBox;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class EarsLayerRenderer implements LayerRenderer<AbstractClientPlayer>, EarsRenderDelegate {

	private final RenderPlayer render;
	
	private int skipRendering;
	private int stackDepth;
	
	public EarsLayerRenderer(RenderPlayer render) {
		this.render = render;
		EarsLog.debug("Platform:Renderer", "Constructed");
	}
	
	@Override
	public void render(AbstractClientPlayer entity, final float limbAngle, final float limbDistance,
			final float tickDelta, final float age, final float headYaw, final float headPitch, final float scale) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {}, {}, {}, {}, {}, {}, {})", entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
		ResourceLocation skin = entity.getLocationSkin();
		ITextureObject tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug("Platform:Renderer", "render(...): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder && !entity.isInvisible()) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			this.skipRendering = 0;
			this.stackDepth = 0;
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();
			EarsCommon.render(((EarsFeaturesHolder)tex).getEarsFeatures(), this, limbDistance);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableCull();
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
		model.postRender(1/16f);
		ModelBox cuboid = model.cubeList.get(0);
		GlStateManager.scalef(1/16f, 1/16f, 1/16f);
		GlStateManager.translatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.translatef(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		GlStateManager.rotatef(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);

		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		vc.pos(0, h, 0).color(1f, 1f, 1f, 1f).tex(uv[0][0], uv[0][1]).normal(0, 0, -1).endVertex();
		vc.pos(w, h, 0).color(1f, 1f, 1f, 1f).tex(uv[1][0], uv[1][1]).normal(0, 0, -1).endVertex();
		vc.pos(w, 0, 0).color(1f, 1f, 1f, 1f).tex(uv[2][0], uv[2][1]).normal(0, 0, -1).endVertex();
		vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(uv[3][0], uv[3][1]).normal(0, 0, -1).endVertex();
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder vc = tess.getBuffer();
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		
		vc.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		vc.pos(0, 0, 0).color(1f, 1f, 1f, 1f).tex(uv[3][0], uv[3][1]).normal(0, 0, 1).endVertex();
		vc.pos(w, 0, 0).color(1f, 1f, 1f, 1f).tex(uv[2][0], uv[2][1]).normal(0, 0, 1).endVertex();
		vc.pos(w, h, 0).color(1f, 1f, 1f, 1f).tex(uv[1][0], uv[1][1]).normal(0, 0, 1).endVertex();
		vc.pos(0, h, 0).color(1f, 1f, 1f, 1f).tex(uv[0][0], uv[0][1]).normal(0, 0, 1).endVertex();
		tess.draw();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		GL11.glPointSize(8);
		GlStateManager.disableTexture2D();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(r, g, b, a).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();
	}
}
