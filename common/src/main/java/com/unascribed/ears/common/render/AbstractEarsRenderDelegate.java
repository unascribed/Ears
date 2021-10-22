package com.unascribed.ears.common.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.util.Decider;

public abstract class AbstractEarsRenderDelegate<TPeer, TModelPart> implements EarsRenderDelegate {
	
	protected abstract EarsFeatures getEarsFeatures();
	protected abstract boolean isSlim();
	
	protected TPeer peer;
	protected EarsFeatures feat;
	protected int skipRendering;
	protected int stackDepth;
	protected BodyPart permittedBodyPart;
	protected TexSource bound;

	@Override
	public void setUp() {
		this.skipRendering = 0;
		this.stackDepth = 0;
		this.bound = TexSource.SKIN;
		doBindSkin();
		setUpRenderState();
	}
	
	protected abstract void setUpRenderState();

	@Override
	public void tearDown() {
		tearDownRenderState();
		this.peer = null;
	}
	
	protected abstract void tearDownRenderState();
	
	@Override
	public void push() {
		stackDepth++;
		pushMatrix();
		if (skipRendering > 0) skipRendering++;
	}
	
	protected abstract void pushMatrix();

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		popMatrix();
		if (skipRendering > 0) skipRendering--;
	}
	
	protected abstract void popMatrix();

	@Override
	public void anchorTo(BodyPart part) {
		if (permittedBodyPart != null && part != permittedBodyPart) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not permissible in this pass, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		TModelPart modelPart = decideModelPart(Decider.<BodyPart, TModelPart>begin(part)).orElse(null);
		if (modelPart == null || !isVisible(modelPart)) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not {}, skip rendering until pop", modelPart == null ? "valid" : "visible");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		doAnchorTo(part, modelPart);
	}
	
	protected abstract boolean isVisible(TModelPart modelPart);
	protected abstract Decider<BodyPart, TModelPart> decideModelPart(Decider<BodyPart, TModelPart> d);
	protected abstract void doAnchorTo(BodyPart part, TModelPart modelPart);

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		doTranslate(x, y, z);
	}

	protected abstract void doTranslate(float x, float y, float z);

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		doRotate(ang, x, y, z);
	}

	protected abstract void doRotate(float ang, float x, float y, float z);

	@Override
	public void scale(float x, float y, float z) {
		if (skipRendering > 0) return;
		doScale(x, y, z);
	}

	protected abstract void doScale(float x, float y, float z);

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip, bound);
		float g = grow.grow;
		
		float b = getBrightness();

		beginQuad();
		addVertex(-g, h+g, 0, b, b, b, 1f, uv[0][0], uv[0][1], 0, 0, -1);
		addVertex(w+g, h+g, 0, b, b, b, 1f, uv[1][0], uv[1][1], 0, 0, -1);
		addVertex(w+g, -g, 0, b, b, b, 1f, uv[2][0], uv[2][1], 0, 0, -1);
		addVertex(-g, -g, 0, b, b, b, 1f, uv[3][0], uv[3][1], 0, 0, -1);
		drawQuad();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally(), bound);
		float g = grow.grow;
		
		float b = getBrightness();
		
		beginQuad();
		addVertex(-g, -g, 0, b, b, b, 1f, uv[3][0], uv[3][1], 0, 0, 1);
		addVertex(w+g, -g, 0, b, b, b, 1f, uv[2][0], uv[2][1], 0, 0, 1);
		addVertex(w+g, h+g, 0, b, b, b, 1f, uv[1][0], uv[1][1], 0, 0, 1);
		addVertex(-g, h+g, 0, b, b, b, 1f, uv[0][0], uv[0][1], 0, 0, 1);
		drawQuad();
	}
	
	protected abstract void beginQuad();
	protected abstract void addVertex(float x, float y, int z,
			float r, float g, float b, float a,
			float u, float v,
			float nX, float nY, float nZ);
	protected abstract void drawQuad();
	
	protected float getBrightness() {
		return 1;
	}

	@Override
	public void renderDoubleSided(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
		renderFront(u, v, width, height, rot, flip, grow);
		renderBack(u, v, width, height, rot, flip.flipHorizontally(), grow);
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		doRenderDebugDot(r, g, b, a);
	}

	protected abstract void doRenderDebugDot(float r, float g, float b, float a);

	@Override
	public void bind(TexSource src) {
		if (src == TexSource.SKIN) {
			doBindSkin();
		} else {
			doBindSub(src, src.getPNGData(feat));
		}
		this.bound = src;
	}
	
	protected abstract void doBindSkin();
	protected abstract void doBindSub(TexSource src, byte[] pngData);
	
	protected ByteBuffer toNativeBuffer(byte[] arr) {
		ByteBuffer buf = ByteBuffer.allocateDirect(arr.length).order(ByteOrder.nativeOrder());
		buf.put(arr).flip();
		return buf;
	}
	
}
