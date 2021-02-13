package com.unascribed.ears.common;

public interface EarsRenderDelegate {

	public enum BodyPart {
		HEAD(8, 8, 8),
		TORSO(8, 12, 4),
		LEFT_ARM(4, 12, 4),
		RIGHT_ARM(4, 12, 4),
		LEFT_LEG(4, 12, 4),
		RIGHT_LEG(4, 12, 4),
		;
		public final int xSize, ySize, zSize;

		private BodyPart(int xSize, int ySize, int zSize) {
			this.xSize = xSize;
			this.ySize = ySize;
			this.zSize = zSize;
		}
		
	}
	
	public enum TexRotation {
		NONE(false),
		CW(true),
		CCW(true),
		UPSIDE_DOWN(false),
		;
		public final boolean transpose;
		private TexRotation(boolean transpose) {
			this.transpose = transpose;
		}
		
		public TexRotation cw() {
			switch (this) {
				case NONE: return CW;
				case CW: return UPSIDE_DOWN;
				case UPSIDE_DOWN: return CCW;
				case CCW: return NONE;
				default: throw new AssertionError("missing case for "+this);
			}
		}
		
		public TexRotation ccw() {
			switch (this) {
				case NONE: return CCW;
				case CCW: return UPSIDE_DOWN;
				case UPSIDE_DOWN: return CW;
				case CW: return NONE;
				default: throw new AssertionError("missing case for "+this);
			}
		}
		
	}
	
	public enum TexFlip {
		NONE,
		HORIZONTAL,
		VERTICAL,
		BOTH,
		;
		
		public TexFlip flipHorizontally() {
			switch (this) {
				case BOTH: return VERTICAL;
				case HORIZONTAL: return NONE;
				case NONE: return HORIZONTAL;
				case VERTICAL: return BOTH;
				default: throw new AssertionError("missing case for "+this);
			}
		}
		
		public TexFlip flipVertically() {
			switch (this) {
				case BOTH: return HORIZONTAL;
				case HORIZONTAL: return BOTH;
				case NONE: return VERTICAL;
				case VERTICAL: return NONE;
				default: throw new AssertionError("missing case for "+this);
			}
		}
	}
	
	public enum QuadGrow {
		NONE(0),
		/**
		 * Matches secondary layers.
		 */
		HALFPIXEL(0.5f),
		;
		public final float grow;
		QuadGrow(float grow) {
			this.grow = grow;
		}
	}
	
	void push();
	void pop();
	
	void anchorTo(BodyPart part);
	void translate(float x, float y, float z);
	void rotate(float ang, float x, float y, float z);
	void renderFront(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow);
	void renderBack(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow);
	
	void renderDebugDot(float r, float g, float b, float a);
	
}
