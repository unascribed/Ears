package com.unascribed.ears;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.debug.EarsLog;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.Tessellator;

public class LayerEars implements EarsRenderDelegate {
	
	private RenderPlayer render;
	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	
	public void doRenderLayer(RenderPlayer render, EntityPlayer entity, float limbDistance, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, limbDistance, partialTicks);
		String skin = entity.skinUrl;
		EarsLog.debug("Platform:Renderer", "render(...): skin={}", skin);
		if (Ears.earsSkinFeatures.containsKey(skin)) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			RenderEngine engine = FMLClientHandler.instance().getClient().p;
			int id = engine.getTextureForDownloadableImage(skin, entity.getTexture());
			if (id < 0) return;
			engine.bindTexture(id);
			this.render = render;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(Ears.earsSkinFeatures.get(skin), this, limbDistance, Ears.slimUsers.contains(entity.username));
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glDisable(GL11.GL_CULL_FACE);
			this.render = null;
		}
	}
	
	public void renderRightArm(RenderPlayer render, EntityPlayer entity) {
		String skin = entity.skinUrl;
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}", skin);
		if (Ears.earsSkinFeatures.containsKey(skin)) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			RenderEngine engine = FMLClientHandler.instance().getClient().p;
			int id = engine.getTextureForDownloadableImage(skin, entity.getTexture());
			if (id < 0) return;
			engine.bindTexture(id);
			this.render = render;
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(Ears.earsSkinFeatures.get(skin), this, 0, Ears.slimUsers.contains(entity.username));
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glDisable(GL11.GL_CULL_FACE);
			this.render = null;
		}
	}

	@Override
	public void push() {
		stackDepth++;
		GL11.glPushMatrix();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		GL11.glPopMatrix();
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
		ModelBiped modelBipedMain = Ears.getModelBipedMain(render);
		ModelRenderer model;
		switch (part) {
			case HEAD:
				model = modelBipedMain.bipedHead;
				break;
			case LEFT_ARM:
				model = modelBipedMain.bipedLeftArm;
				break;
			case LEFT_LEG:
				model = modelBipedMain.bipedLeftLeg;
				break;
			case RIGHT_ARM:
				model = modelBipedMain.bipedRightArm;
				break;
			case RIGHT_LEG:
				model = modelBipedMain.bipedRightLeg;
				break;
			case TORSO:
				model = modelBipedMain.bipedBody;
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
		ModelBox cuboid = (ModelBox)model.cubeList.get(0);
		GL11.glScalef(1/16f, 1/16f, 1/16f);
		GL11.glTranslatef(cuboid.posX1, cuboid.posY2, cuboid.posZ1);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		GL11.glTranslatef(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		GL11.glRotatef(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.instance;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;
		
		tess.startDrawing(GL11.GL_QUADS);
		tess.setNormal(0, 0, -1);
		tess.addVertexWithUV(-g, h+g, 0, uv[0][0], uv[0][1]);
		tess.addVertexWithUV(w+g, h+g, 0, uv[1][0], uv[1][1]);
		tess.addVertexWithUV(w+g, -g, 0, uv[2][0], uv[2][1]);
		tess.addVertexWithUV(-g, -g, 0, uv[3][0], uv[3][1]);
		tess.draw();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		Tessellator tess = Tessellator.instance;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		tess.startDrawing(GL11.GL_QUADS);
		tess.setNormal(0, 0, 1);
		tess.addVertexWithUV(-g, -g, 0, uv[3][0], uv[3][1]);
		tess.addVertexWithUV(w+g, -g, 0, uv[2][0], uv[2][1]);
		tess.addVertexWithUV(w+g, h+g, 0, uv[1][0], uv[1][1]);
		tess.addVertexWithUV(-g, h+g, 0, uv[0][0], uv[0][1]);
		tess.draw();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		GL11.glPointSize(8);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator tess = Tessellator.instance;
		tess.startDrawing(GL11.GL_POINTS);
		tess.setColorRGBA_F(r, g, b, a);
		tess.addVertex(0, 0, 0);
		tess.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
