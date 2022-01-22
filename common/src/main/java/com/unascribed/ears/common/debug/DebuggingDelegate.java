package com.unascribed.ears.common.debug;

import com.unascribed.ears.common.render.EarsRenderDelegate;

/**
 * A wrapper around another EarsRenderDelegate that {@link EarsLog logs} everything performed.
 */
public class DebuggingDelegate implements EarsRenderDelegate {

	private final EarsRenderDelegate delegate;

	public DebuggingDelegate(EarsRenderDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void push() {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "push()");
		delegate.push();
	}

	@Override
	public void pop() {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "pop()");
		delegate.pop();
	}

	@Override
	public void anchorTo(BodyPart part) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "anchorTo(BodyPart.{})", part);
		delegate.anchorTo(part);
	}

	@Override
	public void translate(float x, float y, float z) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "translate({}, {}, {})", x, y, z);
		delegate.translate(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "rotate({}, {}, {}, {})", ang, x, y, z);
		delegate.rotate(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "renderFront({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderFront(u, v, w, h, rot, flip, grow);
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "renderBack({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderBack(u, v, w, h, rot, flip, grow);
	}
	
	@Override
	public void renderDoubleSided(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "renderDoubleSided({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderDoubleSided(u, v, w, h, rot, flip, grow);
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "renderDebugDot({}, {}, {}, {})", r, g, b, a);
		delegate.renderDebugDot(r, g, b, a);
	}

	@Override
	public void bind(TexSource tex) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "bind({})", tex);
		delegate.bind(tex);
	}

	@Override
	public void scale(float x, float y, float z) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "scale({}, {}, {})", x, y, z);
		delegate.scale(x, y, z);
	}

	@Override
	public void setUp() {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "setUp()");
		delegate.setUp();
	}

	@Override
	public void tearDown() {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "tearDown()");
		delegate.tearDown();
	}

	@Override
	public float getTime() {
		float t = delegate.getTime();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getTime() -> {}", t);
		return t;
	}

	@Override
	public boolean isFlying() {
		boolean b = delegate.isFlying();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isFlying() -> {}", b);
		return b;
	}

	@Override
	public boolean isGliding() {
		boolean b = delegate.isGliding();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isGliding() -> {}", b);
		return b;
	}

	@Override
	public boolean isWearingElytra() {
		boolean b = delegate.isWearingElytra();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isWearingElytra() -> {}", b);
		return b;
	}

	@Override
	public boolean isWearingChestplate() {
		boolean b = delegate.isWearingChestplate();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isWearingChestplate() -> {}", b);
		return b;
	}

	@Override
	public boolean isWearingBoots() {
		boolean b = delegate.isWearingBoots();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isWearingBoots() -> {}", b);
		return b;
	}

	@Override
	public boolean isJacketEnabled() {
		boolean b = delegate.isJacketEnabled();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isJacketEnabled() -> {}", b);
		return b;
	}

	@Override
	public Object getPeer() {
		return delegate.getPeer();
	}
	
	@Override
	public boolean needsSecondaryLayersDrawn() {
		boolean b = delegate.needsSecondaryLayersDrawn();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "needsSecondaryLayersDrawn() -> {}", b);
		return b;
	}

	@Override
	public float getLimbSwing() {
		float t = delegate.getLimbSwing();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getLimbSwing() -> {}", t);
		return t;
	}

	@Override
	public float getHorizontalSpeed() {
		float t = delegate.getHorizontalSpeed();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getHorizontalSpeed() -> {}", t);
		return t;
	}

	@Override
	public float getStride() {
		float t = delegate.getStride();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getStride() -> {}", t);
		return t;
	}

	@Override
	public boolean isSlim() {
		boolean b = delegate.isSlim();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "isSlim() -> {}", b);
		return b;
	}

	@Override
	public float getBodyYaw() {
		float t = delegate.getBodyYaw();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getBodyYaw() -> {}", t);
		return t;
	}

	@Override
	public double getX() {
		double t = delegate.getX();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getX() -> {}", t);
		return t;
	}

	@Override
	public double getY() {
		double t = delegate.getY();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getY() -> {}", t);
		return t;
	}

	@Override
	public double getZ() {
		double t = delegate.getZ();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getZ() -> {}", t);
		return t;
	}

	@Override
	public double getCapeX() {
		double t = delegate.getCapeX();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getCapeX() -> {}", t);
		return t;
	}

	@Override
	public double getCapeY() {
		double t = delegate.getCapeY();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getCapeY() -> {}", t);
		return t;
	}

	@Override
	public double getCapeZ() {
		double t = delegate.getCapeZ();
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "getCapeZ() -> {}", t);
		return t;
	}

	@Override
	public void setEmissive(boolean emissive) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "setEmissive({})", emissive);
		delegate.setEmissive(emissive);
	}
	
	@Override
	public boolean canBind(TexSource tex) {
		boolean b = delegate.canBind(tex);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER_DELEGATE, "canBind({}) -> {}", tex, b);
		return b;
	}
	
}
