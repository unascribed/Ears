package com.unascribed.ears.common;

import com.unascribed.ears.common.debug.EarsLog;

/**
 * Describes the state of every Ears feature for a player skin.
 */
public class EarsFeatures {

	public enum EarMode {
		NONE,
		ABOVE,
		SIDES,
		BEHIND,
		AROUND,
		FLOPPY,
		CROSS,
		OUT,
		TALL,
		TALL_CROSS,
	}
	public enum EarAnchor {
		CENTER,
		FRONT,
		BACK,
	}
	public enum TailMode {
		NONE,
		DOWN,
		BACK,
		UP,
		VERTICAL,
	}
	public enum WingMode {
		NONE,
		SYMMETRIC_DUAL,
		SYMMETRIC_SINGLE,
		ASYMMETRIC_L,
		ASYMMETRIC_R,
	}
	
	public static final EarsFeatures DISABLED = new EarsFeatures();
	
	public final boolean enabled;
	public final EarMode earMode;
	public final EarAnchor earAnchor;
	public final boolean claws;
	public final boolean horn;
	public final TailMode tailMode;
	public final int tailSegments;
	public final float tailBend0;
	public final float tailBend1;
	public final float tailBend2;
	public final float tailBend3;
	public final int snoutOffset;
	public final int snoutWidth;
	public final int snoutHeight;
	public final int snoutDepth;
	public final float chestSize;
	public final WingMode wingMode;
	public final boolean animateWings;
	public final boolean capeEnabled;
	
	public final Alfalfa alfalfa;

	private EarsFeatures() {
		this.enabled = false;
		this.earMode = EarMode.NONE;
		this.earAnchor = EarAnchor.CENTER;
		this.claws = false;
		this.horn = false;
		this.tailMode = TailMode.NONE;
		this.tailSegments = 0;
		this.tailBend0 = 0;
		this.tailBend1 = 0;
		this.tailBend2 = 0;
		this.tailBend3 = 0;
		this.snoutOffset = 0;
		this.snoutWidth = 0;
		this.snoutHeight = 0;
		this.snoutDepth = 0;
		this.chestSize = 0;
		this.wingMode = WingMode.NONE;
		this.animateWings = true;
		this.capeEnabled = false;
		this.alfalfa = Alfalfa.NONE;
	}
	
	public EarsFeatures(
			EarMode earMode, EarAnchor earAnchor,
			boolean claws, boolean horn,
			TailMode tailMode, int tailSegments, float tailBend0, float tailBend1, float tailBend2, float tailBend3,
			int snoutOffset, int snoutWidth, int snoutHeight, int snoutDepth,
			float chestSize,
			WingMode wingMode, boolean animateWings,
			boolean capeEnabled,
			Alfalfa alfalfa) {
		this.enabled = true;
		this.earMode = earMode;
		this.earAnchor = earAnchor;
		this.claws = claws;
		this.horn = horn;
		this.tailMode = tailMode;
		this.tailSegments = tailSegments;
		this.tailBend0 = tailBend0;
		this.tailBend1 = tailBend1;
		this.tailBend2 = tailBend2;
		this.tailBend3 = tailBend3;
		this.snoutOffset = snoutOffset;
		this.snoutWidth = snoutWidth;
		this.snoutHeight = snoutHeight;
		this.snoutDepth = snoutDepth;
		this.chestSize = chestSize;
		this.wingMode = wingMode;
		this.animateWings = animateWings;
		this.capeEnabled = capeEnabled;
		this.alfalfa = alfalfa;
	}

	/**
	 * Decode the Ears configuration out of the magic pixels in the given skin image, and associate
	 * the given Alfalfa with the resultant features object.
	 */
	public static EarsFeatures detect(EarsImage img, Alfalfa alfalfa) {
		EarsLog.debug("Common:Features", "detect({}, {})", img, alfalfa);
		if (img.getHeight() == 64) {
			int first = img.getARGB(0, 32)&0x00FFFFFF;
			if (first == EarsFeaturesParserV0.MAGIC) {
				// Ears Data v0 (Pixelwise)
				return EarsFeaturesParserV0.parse(img, alfalfa);
			} else if (first == EarsFeaturesParserV1.MAGIC) {
				// Ears Data v1.x (Binary)
				return EarsFeaturesParserV1.parse(img, alfalfa);
			} else {
				EarsLog.debug("Common:Features", "detect(...): Could not find v0 (Pixelwise, #3F23D8) or v1 (Binary, #EA2501) data indicator at 0, 32 - found #{} instead. Disabling",
						EarsFeaturesParserV0.upperHex32Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
	}
	
	@Override
	public String toString() {
		if (!enabled) return "EarsFeatures.DISABLED";
		return "EarsFeatures[earMode=" + earMode
				+ ", earAnchor=" + earAnchor + ", claws=" + claws + ", horn="
				+ horn + ", tailMode=" + tailMode + ", tailBend0=" + tailBend0
				+ ", tailBend1=" + tailBend1 + ", tailBend2=" + tailBend2
				+ ", tailBend3=" + tailBend3 + ", snoutOffset=" + snoutOffset
				+ ", snoutWidth=" + snoutWidth + ", snoutHeight=" + snoutHeight
				+ ", snoutDepth=" + snoutDepth + (chestSize > 0 ? ", chestSize=" + chestSize : "")
				+ ", wingMode=" + wingMode + ", animateWings=" + animateWings
				+ ", alfalfa=" + alfalfa + "]";
	}

}
