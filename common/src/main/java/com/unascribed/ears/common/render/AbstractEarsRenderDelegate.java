package com.unascribed.ears.common.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.util.Decider;

/**
 * Implements some basic shared render logic to reduce duplicated code.
 * <p>
 * It will handle managing render skipping, permitted body parts, stack over/underflow detection,
 * tracking which texture is bound, and building quads with the right UVs.
 * <p>
 * A subclass should be used if possible. For "exotic" platforms (such as the Manipulator), consider
 * implementing EarsRenderDelegate directly. AbstractEarsRenderDelegate and friends all make various
 * assumptions that are only true within Minecraft itself.
 * 
 * @see com.unascribed.ears.common.legacy.ImmediateEarsRenderDelegate Immediate, for versions with a broken Tesselator and no state or texture manager (e.g. Beta 1.7, 1.2.5)
 * @see com.unascribed.ears.common.legacy.UnmanagedEarsRenderDelegate Unmanaged, for versions without a state manager or texture manager (e.g. 1.4, 1.5)
 * @see com.unascribed.ears.common.legacy.PartiallyUnmanagedEarsRenderDelegate PartiallyUnmanaged, for versions without a state manager (e.g. 1.6, 1.7)
 * @see DirectEarsRenderDelegate Direct, for versions with a state manager (e.g. 1.8, 1.12, 1.14)
 * @see IndirectEarsRenderDelegate Indirect, for versions with the RenderLayer/VertexConsumerProvider abstraction (e.g. 1.15, 1.16)
 * 
 * @param <TPeer> the type of the "render peer"; usually something like AbstractClientPlayer
 * @param <TModelPart> the type of model parts; usually ModelPart (Yarn/Mojmap) or ModelRenderer (MCP)
 */
public abstract class AbstractEarsRenderDelegate<TPeer, TModelPart> implements EarsRenderDelegate {
	
	protected abstract EarsFeatures getEarsFeatures();
	
	protected TPeer peer;
	protected EarsFeatures feat;
	protected int skipRendering;
	protected int stackDepth;
	protected BodyPart permittedBodyPart;
	protected TexSource bound;
	protected boolean emissive;

	@Override
	public void setUp() {
		this.skipRendering = 0;
		this.stackDepth = 0;
		this.bound = TexSource.SKIN;
		doBindSkin();
		setUpRenderState();
	}
	
	@Override
	public TPeer getPeer() {
		return peer;
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
			EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "anchorTo(...): Part is not permissible in this pass, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		TModelPart modelPart = decideModelPart(Decider.<BodyPart, TModelPart>begin(part)).orElse(null);
		if (modelPart == null || !isVisible(modelPart)) {
			EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "anchorTo(...): Part is not {}, skip rendering until pop", modelPart == null ? "valid" : "visible");
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
		
		float b = emissive ? 1 : getBrightness();

		float nX = 0;
		float nY = emissive ? 1 : 0;
		float nZ = emissive ? 0 : -1;
		
		beginQuad();
		addVertex(-g, h+g, 0, b, b, b, 1f, uv[0][0], uv[0][1], nX, nY, nZ);
		addVertex(w+g, h+g, 0, b, b, b, 1f, uv[1][0], uv[1][1], nX, nY, nZ);
		addVertex(w+g, -g, 0, b, b, b, 1f, uv[2][0], uv[2][1], nX, nY, nZ);
		addVertex(-g, -g, 0, b, b, b, 1f, uv[3][0], uv[3][1], nX, nY, nZ);
		drawQuad();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally(), bound);
		float g = grow.grow;
		
		float b = emissive ? 1 : getBrightness();
		
		float nX = 0;
		float nY = emissive ? 1 : 0;
		float nZ = emissive ? 0 : 1;
		
		beginQuad();
		addVertex(-g, -g, 0, b, b, b, 1f, uv[3][0], uv[3][1], nX, nY, nZ);
		addVertex(w+g, -g, 0, b, b, b, 1f, uv[2][0], uv[2][1], nX, nY, nZ);
		addVertex(w+g, h+g, 0, b, b, b, 1f, uv[1][0], uv[1][1], nX, nY, nZ);
		addVertex(-g, h+g, 0, b, b, b, 1f, uv[0][0], uv[0][1], nX, nY, nZ);
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
		if (src == this.bound) return;
		if (src == TexSource.SKIN) {
			doBindSkin();
		} else if (!src.isBuiltIn()) {
			doBindAux(src, src.getPNGData(feat));
		} else if (canBind(src)) {
			doBindBuiltin(src);
		} else {
			EarsLog.debug(EarsLog.Tag.COMMON_RENDERER, "Attempt to bind unsupported texture {}", src);
		}
		this.bound = src;
	}
	
	protected abstract void doBindSkin();
	protected abstract void doBindAux(TexSource src, byte[] pngData);
	
	protected void doBindBuiltin(TexSource src) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean canBind(TexSource tex) {
		return tex == TexSource.SKIN || !tex.isBuiltIn();
	}
	
	public static ByteBuffer toNativeBuffer(byte[] arr) {
		ByteBuffer buf = ByteBuffer.allocateDirect(arr.length).order(ByteOrder.nativeOrder());
		((Buffer)buf.put(arr)).flip();
		return buf;
	}
	
	@Override
	public boolean needsSecondaryLayersDrawn() {
		return false;
	}
	
	@Override
	public void setEmissive(boolean emissive) {
		this.emissive = emissive;
	}
	
}
