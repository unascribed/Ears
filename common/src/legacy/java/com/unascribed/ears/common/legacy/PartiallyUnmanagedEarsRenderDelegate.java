package com.unascribed.ears.common.legacy;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import com.unascribed.ears.common.render.DirectEarsRenderDelegate;

/**
 * A specialization of {@link DirectEarsRenderDelegate} that performs matrix manipulation and
 * render setup by making direct OpenGL calls.
 */
public abstract class PartiallyUnmanagedEarsRenderDelegate<TPeer, TModelPart> extends DirectEarsRenderDelegate<TPeer, TModelPart> {
	
	@Override
	protected void setUpRenderState() {
		glEnable(GL_CULL_FACE);
		glEnable(GL_RESCALE_NORMAL);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected void tearDownRenderState() {
		glDisable(GL_BLEND);
		glDisable(GL_RESCALE_NORMAL);
		glDisable(GL_CULL_FACE);
	}
	
	@Override
	protected void pushMatrix() {
		glPushMatrix();
	}
	
	@Override
	protected void popMatrix() {
		glPopMatrix();
	}
	
	@Override
	protected void doTranslate(float x, float y, float z) {
		glTranslatef(x, y, z);
	}
	
	@Override
	protected void doScale(float x, float y, float z) {
		glScalef(x, y, z);
	}
	
	@Override
	protected void doRotate(float ang, float x, float y, float z) {
		glRotatef(ang, x, y, z);
	}

	@Override
	protected void doRenderDebugDot(float r, float g, float b, float a) {
		glPointSize(8);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_POINTS);
			glColor4f(r, g, b, a);
			glVertex3f(0, 0, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}
	
	@Override
	public void setEmissive(boolean emissive) {
		super.setEmissive(emissive);
		if (emissive) {
			glDisable(GL_LIGHTING);
		} else {
			glEnable(GL_LIGHTING);
		}
	}
	
}
