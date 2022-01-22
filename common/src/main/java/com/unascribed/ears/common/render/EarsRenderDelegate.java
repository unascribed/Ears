package com.unascribed.ears.common.render;

import java.util.Locale;

import com.unascribed.ears.api.features.EarsFeatures;

/**
 * Entrypoint to the Ears abstract rendering platform. Every platform provides a concrete
 * implementation, usually based on {@link AbstractEarsRenderDelegate} or one of its many
 * subclasses.
 */
public interface EarsRenderDelegate {

	public enum BodyPart {
		HEAD(8, 8, 8),
		TORSO(8, 12, 4),
		LEFT_ARM(4, 12, 4),
		RIGHT_ARM(4, 12, 4),
		LEFT_LEG(4, 12, 4),
		RIGHT_LEG(4, 12, 4),
		;
		private final int xSize, ySize, zSize;

		BodyPart(int xSize, int ySize, int zSize) {
			this.xSize = xSize;
			this.ySize = ySize;
			this.zSize = zSize;
		}
		
		public int getXSize(boolean slim) {
			if (slim && (this == LEFT_ARM || this == RIGHT_ARM)) return 3;
			return xSize;
		}
		
		public int getYSize(boolean slim) {
			return ySize;
		}
		
		public int getZSize(boolean slim) {
			return zSize;
		}
		
	}
	
	public enum TexSource {
		SKIN(64, 64, true),
		
		WING(20, 16, false),
		CAPE(20, 16, false),
		EMISSIVE_SKIN(64, 64, false),
		EMISSIVE_WING(20, 16, false),
		
		HELMET(64, 32, true),
		CHESTPLATE(64, 32, true),
		LEGGINGS(64, 32, true),
		BOOTS(64, 32, true),
		
		GLINT_HELMET(64, 32, true, true, HELMET),
		GLINT_CHESTPLATE(64, 32, true, true, CHESTPLATE),
		GLINT_LEGGINGS(64, 32, true, true, LEGGINGS),
		GLINT_BOOTS(64, 32, true, true, BOOTS),
		;
		private final int width;
		private final int height;
		private final String lowerName;
		private final boolean builtin;
		private final boolean glint;
		private final TexSource parent;

		TexSource(int width, int height, boolean builtin) {
			this(width, height, builtin, false, null);
		}

		TexSource(int width, int height, boolean builtin, boolean glint, TexSource parent) {
			this.width = width;
			this.height = height;
			this.lowerName = name().toLowerCase(Locale.ROOT);
			this.builtin = builtin;
			this.glint = glint;
			this.parent = parent;
		}
		
		public String addSuffix(String path) {
			if (this == SKIN) return path;
			return path+"/ears/"+lowerName();
		}

		public String lowerName() {
			return lowerName;
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}
		
		public boolean isBuiltIn() {
			return builtin;
		}
		
		public boolean isGlint() {
			return glint;
		}
		
		public TexSource getParent() {
			return parent;
		}
		
		public byte[] getPNGData(EarsFeatures feat) {
			if (this == SKIN) return null;
			if (this == WING && feat.alfalfa.data.get("wing") != null) {
				return feat.alfalfa.data.get("wing").toByteArray();
			}
			if (this == CAPE && feat.alfalfa.data.get("cape") != null) {
				return feat.alfalfa.data.get("cape").toByteArray();
			}
			if (this == EMISSIVE_SKIN && feat.emissive) {
				return feat.emissiveSkin.toByteArray();
			}
			if (this == EMISSIVE_WING && feat.emissive) {
				return feat.emissiveWing.toByteArray();
			}
			return null;
		}
	}
	
	public enum TexRotation {
		NONE(false),
		CW(true),
		CCW(true),
		UPSIDE_DOWN(false),
		;
		public final boolean transpose;
		TexRotation(boolean transpose) {
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
		 * Matches secondary head layer and "leggings" armor layer.
		 */
		HALFPIXEL(0.5f),
		/**
		 * Matches secondary layers.
		 */
		QUARTERPIXEL(0.25f),
		/**
		 * Matches "body" armor layers.
		 */
		FULLPIXEL(1),
		;
		public final float grow;
		QuadGrow(float grow) {
			this.grow = grow;
		}
	}
	
	void setUp();
	void tearDown();
	
	void push();
	void pop();
	
	void anchorTo(BodyPart part);
	void bind(TexSource tex);
	boolean canBind(TexSource tex);
	
	void translate(float x, float y, float z);
	void rotate(float ang, float x, float y, float z);
	void scale(float x, float y, float z);
	
	void renderFront(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow);
	void renderBack(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow);
	void renderDoubleSided(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow);
	
	void renderDebugDot(float r, float g, float b, float a);
	
	float getTime();
	
	float getLimbSwing();
	float getHorizontalSpeed();
	float getStride();
	
	float getBodyYaw();
	
	double getX();
	double getY();
	double getZ();
	
	double getCapeX();
	double getCapeY();
	double getCapeZ();
	
	boolean isSlim();
	
	boolean isFlying();
	boolean isGliding();
	boolean isWearingElytra();
	boolean isWearingChestplate();
	boolean isWearingBoots();
	boolean isJacketEnabled();
	boolean needsSecondaryLayersDrawn();
	
	void setEmissive(boolean emissive);
	
	Object getPeer();
	
}
