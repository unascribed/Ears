package com.unascribed.ears;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.mixin.accessor.AccessorLivingEntityRenderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.entity.model.BipedModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.Player;

public class LayerEars implements EarsRenderDelegate {
	private PlayerRenderer renderer;
	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	private float brightness;

	public void doRenderLayer(PlayerRenderer renderer, Player entity, float limbDistance, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, limbDistance, partialTicks);
		String skin = entity.skinUrl;
		EarsLog.debug("Platform:Renderer", "render(...): skin={}", skin);
		if (EarsMod.earsSkinFeatures.containsKey(skin)) {
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			TextureManager textureManager = EarsMod.client.textureManager;
			int id = textureManager.method_1093(skin, entity.method_1314());
			if (id < 0) return;
			textureManager.bindTexture(id);
			this.renderer = renderer;
			this.brightness = entity.getBrightnessAtEyes(partialTicks);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			glEnable(GL_CULL_FACE);
			glEnable(GL_RESCALE_NORMAL);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(EarsMod.earsSkinFeatures.get(skin), this, limbDistance, false);
			glDisable(GL_BLEND);
			glDisable(GL_RESCALE_NORMAL);
			glDisable(GL_CULL_FACE);
			this.renderer = null;
		}
	}

	public void renderRightArm(PlayerRenderer renderer, Player entity) {
		String skin = entity.skinUrl;
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}", skin);
		if (EarsMod.earsSkinFeatures.containsKey(skin)) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			TextureManager textureManager = EarsMod.client.textureManager;
			int id = textureManager.method_1093(skin, entity.method_1314());
			if (id < 0) return;
			textureManager.bindTexture(id);
			this.renderer = renderer;
			this.brightness = entity.getBrightnessAtEyes(0);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			glEnable(GL_CULL_FACE);
			glEnable(GL_RESCALE_NORMAL);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(EarsMod.earsSkinFeatures.get(skin), this, 0, false);
			glDisable(GL_BLEND);
			glDisable(GL_RESCALE_NORMAL);
			glDisable(GL_CULL_FACE);
			this.renderer = null;
		}
	}

	@Override
	public void push() {
		stackDepth++;
		glPushMatrix();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		glPopMatrix();
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
		BipedModel modelPlayer = (BipedModel)((AccessorLivingEntityRenderer)renderer).getEntityModel();
		ModelPart model;
		switch (part) {
			case HEAD:
				model = modelPlayer.head;
				break;
			case LEFT_ARM:
				model = modelPlayer.leftArm;
				break;
			case LEFT_LEG:
				model = modelPlayer.leftLeg;
				break;
			case RIGHT_ARM:
				model = modelPlayer.rightArm;
				break;
			case RIGHT_LEG:
				model = modelPlayer.rightLeg;
				break;
			case TORSO:
				model = modelPlayer.body;
				break;
			default: return;
		}
		if (!model.visible) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not visible, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		model.method_1820(1/16f); // postRender (you can tell because it's the one that applies transforms without matrix isolation)
		glScalef(1/16f, 1/16f, 1/16f);
		ModelPartTextureFixer modelWithPositions = (ModelPartTextureFixer) model;
		glTranslated(modelWithPositions.getPosX1(), modelWithPositions.getPosY2(), modelWithPositions.getPosZ1());
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		glTranslatef(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		glRotatef(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;

		// tesselator in beta 1.7.3 is extremely busted and does not seem to handle normals correctly
		// so let's just do this the old-fashioned way
		
		// the JNI overhead will be a bit gnarly but we've got so much frametime to spare on these simplistic old versions
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;
		
		glColor3f(brightness, brightness, brightness);
		glBegin(GL_QUADS);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[0][0], uv[0][1]);
			glVertex3f(-g, h+g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[1][0], uv[1][1]);
			glVertex3f(w+g, h+g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[2][0], uv[2][1]);
			glVertex3f(w+g, -g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[3][0], uv[3][1]);
			glVertex3f(-g, -g, 0);
		glEnd();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		glColor3f(brightness, brightness, brightness);
		glBegin(GL_QUADS);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[3][0], uv[3][1]);
			glVertex3f(-g, -g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[2][0], uv[2][1]);
			glVertex3f(w+g, -g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[1][0], uv[1][1]);
			glVertex3f(w+g, h+g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[0][0], uv[0][1]);
			glVertex3f(-g, h+g, 0);
		glEnd();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		glPointSize(8);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_POINTS);
			glColor4f(r, g, b, a);
			glVertex3f(0, 0, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}
}
