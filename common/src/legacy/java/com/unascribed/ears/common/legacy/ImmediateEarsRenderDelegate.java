package com.unascribed.ears.common.legacy;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * A further specialization of {@link UnmanagedEarsRenderDelegate} that renders quads using OpenGL
 * 1.1 immediate mode.
 */
public abstract class ImmediateEarsRenderDelegate<TPeer, TModelPart> extends UnmanagedEarsRenderDelegate<TPeer, TModelPart> {

	@Override
	protected void beginQuad() {
		glBegin(GL_QUADS);
	}
	
	@Override
	protected void addVertex(float x, float y, int z,
			float r, float g, float b, float a,
			float u, float v,
			float nX, float nY, float nZ) {
		glColor4f(r, g, b, a);
		glTexCoord2f(u, v);
		glNormal3f(nX, nY, nZ);
		glVertex3f(x, y, z);
	}
	
	@Override
	protected void drawQuad() {
		glEnd();
	}
	
}
