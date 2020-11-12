package com.unascribed.ears.common;

public class DebuggingDelegate implements EarsRenderDelegate {

	private final EarsRenderDelegate delegate;

	public DebuggingDelegate(EarsRenderDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void push() {
		EarsLog.debug("Platform:Renderer:Delegate", "push()");
		delegate.push();
	}

	@Override
	public void pop() {
		EarsLog.debug("Platform:Renderer:Delegate", "pop()");
		delegate.pop();
	}

	@Override
	public void anchorTo(BodyPart part) {
		EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(BodyPart.{})", part);
		delegate.anchorTo(part);
	}

	@Override
	public void translate(float x, float y, float z) {
		EarsLog.debug("Platform:Renderer:Delegate", "translate({}, {}, {})", x, y, z);
		delegate.translate(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		EarsLog.debug("Platform:Renderer:Delegate", "rotate({}, {}, {}, {})", ang, x, y, z);
		delegate.rotate(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug("Platform:Renderer:Delegate", "renderFront({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderFront(u, v, w, h, rot, flip, grow);
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug("Platform:Renderer:Delegate", "renderBack({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderBack(u, v, w, h, rot, flip, grow);
	}

	@Override
	public void renderDoubleSided(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		EarsLog.debug("Platform:Renderer:Delegate", "renderDoubleSided({}, {}, {}, {}, {}, {}, {})", u, v, w, h, rot, flip, grow);
		delegate.renderDoubleSided(u, v, w, h, rot, flip, grow);
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		EarsLog.debug("Platform:Renderer:Delegate", "renderDebugDot({}, {}, {}, {})", r, g, b, a);
		delegate.renderDebugDot(r, g, b, a);
	}

}
