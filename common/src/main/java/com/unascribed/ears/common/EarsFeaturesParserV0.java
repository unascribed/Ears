package com.unascribed.ears.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.EarAnchor;
import com.unascribed.ears.api.features.EarsFeatures.EarMode;
import com.unascribed.ears.api.features.EarsFeatures.TailMode;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.debug.EarsLog;

class EarsFeaturesParserV0 {

	public static final int MAGIC = MagicPixel.BLUE.rgb;
	
	enum MagicPixel {
		UNKNOWN(-1),
		BLUE(0x3F23D8),
		GREEN(0x23D848),
		RED(0xD82350),
		PURPLE(0xB923D8),
		CYAN(0x23D8C6),
		ORANGE(0xD87823),
		PINK(0xD823B7),
		PURPLE2(0xD823FF),
		WHITE(0xFEFDF2),
		GRAY(0x5E605A),
		;
		private static final Map<Integer, MagicPixel> rgbToValue = new HashMap<Integer, MagicPixel>();
		static {
			for (MagicPixel mp : values()) {
				if (mp.rgb != -1) {
					rgbToValue.put(mp.rgb, mp);
				}
			}
		}
		final int rgb;
		MagicPixel(int rgb) {
			this.rgb = rgb;
		}
		
		public static MagicPixel from(int argb) {
			return rgbToValue.getOrDefault(argb&0x00FFFFFF, UNKNOWN);
		}
		
		@Override
		public String toString() {
			return "Magic "+name().charAt(0)+name().substring(1).toLowerCase(Locale.ROOT);
		}
		
		static {
			if (EarsLog.DEBUG) {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "All legal magic pixels:");
				for (MagicPixel mp : values()) {
					if (mp == UNKNOWN) continue;
					EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "- {}: #{}",
							mp, upperHex24Dbg(mp.rgb));
				}
			}
		}
	}
	
	public enum Protrusions {
		NONE(false, false),
		CLAWS(true, false),
		HORN(false, true),
		CLAWS_AND_HORN(true, true),
		;
		public final boolean claws, horn;
		Protrusions(boolean claws, boolean horn) {
			this.claws = claws;
			this.horn = horn;
		}
		
		public static final Map<MagicPixel, Protrusions> BY_MAGIC = buildMap(
				MagicPixel.BLUE, NONE,
				MagicPixel.RED, NONE,
				MagicPixel.GREEN, CLAWS,
				MagicPixel.PURPLE, HORN,
				MagicPixel.CYAN, CLAWS_AND_HORN
		);
	}
	
	public static final Map<MagicPixel, EarMode> EAR_MODE_BY_MAGIC = buildMap(
			MagicPixel.RED, EarMode.NONE,
			MagicPixel.BLUE, EarMode.ABOVE,
			MagicPixel.GREEN, EarMode.SIDES,
			MagicPixel.PURPLE, EarMode.BEHIND,
			MagicPixel.CYAN, EarMode.AROUND,
			MagicPixel.ORANGE, EarMode.FLOPPY,
			MagicPixel.PINK, EarMode.CROSS,
			MagicPixel.PURPLE2, EarMode.OUT,
			MagicPixel.WHITE, EarMode.TALL,
			MagicPixel.GRAY, EarMode.TALL_CROSS
	);
	public static final Map<MagicPixel, EarAnchor> EAR_ANCHOR_BY_MAGIC = buildMap(
			MagicPixel.BLUE, EarAnchor.CENTER,
			MagicPixel.GREEN, EarAnchor.FRONT,
			MagicPixel.RED, EarAnchor.BACK
	);
	
	public static final Map<MagicPixel, Protrusions> PROTRUSIONS_BY_MAGIC = buildMap(
			MagicPixel.BLUE, Protrusions.NONE,
			MagicPixel.RED, Protrusions.NONE,
			MagicPixel.GREEN, Protrusions.CLAWS,
			MagicPixel.PURPLE, Protrusions.HORN,
			MagicPixel.CYAN, Protrusions.CLAWS_AND_HORN
	);
	public static final Map<MagicPixel, TailMode> TAIL_MODE_BY_MAGIC = buildMap(
			MagicPixel.RED, TailMode.NONE,
			MagicPixel.BLUE, TailMode.DOWN,
			MagicPixel.GREEN, TailMode.BACK,
			MagicPixel.PURPLE, TailMode.UP,
			MagicPixel.ORANGE, TailMode.VERTICAL
	);
	public static final Map<MagicPixel, WingMode> WING_MODE_BY_MAGIC = buildMap(
			MagicPixel.BLUE, WingMode.NONE,
			MagicPixel.RED, WingMode.NONE,
			MagicPixel.PINK, WingMode.SYMMETRIC_DUAL,
			MagicPixel.GREEN, WingMode.SYMMETRIC_SINGLE,
			MagicPixel.CYAN, WingMode.ASYMMETRIC_L,
			MagicPixel.ORANGE, WingMode.ASYMMETRIC_R
	);

	public static EarsFeatures.Builder parse(EarsImage img) {
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Found v0 (Pixelwise) data.");
		EarMode earMode = getMagicPixel(img, 1, EAR_MODE_BY_MAGIC, EarMode.NONE, "ear mode");
		EarAnchor earAnchor = getMagicPixel(img, 2, EAR_ANCHOR_BY_MAGIC, EarAnchor.CENTER, "ear anchor", earMode != EarMode.NONE && earMode != EarMode.BEHIND);
		Protrusions protrusions = getMagicPixel(img, 3, PROTRUSIONS_BY_MAGIC, Protrusions.NONE, "protrusions");
		TailMode tailMode = getMagicPixel(img, 4, TAIL_MODE_BY_MAGIC, TailMode.NONE, "tail mode");
		int tailBend = getPixel(img, 5);
		float tailBend0 = 0;
		float tailBend1 = 0;
		float tailBend2 = 0;
		float tailBend3 = 0;
		int tailSegments = 0;
		if (MagicPixel.from(tailBend) == MagicPixel.BLUE) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The tail bend pixel is Magic Blue, pretending it's black");
		} else {
			tailSegments++;
			tailBend0 = pxValToUnit(255-((tailBend&0xFF000000) >>> 24))*90;
			tailBend1 = pxValToUnit((tailBend&0x00FF0000) >> 16)*90;
			tailBend2 = pxValToUnit((tailBend&0x0000FF00) >> 8)*90;
			tailBend3 = pxValToUnit((tailBend&0x000000FF) >> 0)*90;
			if (tailBend1 != 0) {
				tailSegments++;
				if (tailBend2 != 0) {
					tailSegments++;
					if (tailBend3 != 0) {
						tailSegments++;
						EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The tail bend pixel is #{} - 4 segments with angles {}, {}, {}, {}", upperHex32Dbg(tailBend), tailBend0, tailBend1, tailBend2, tailBend3);
					} else {
						EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The tail bend pixel is #{} - 3 segments with angles {}, {}, {}", upperHex32Dbg(tailBend), tailBend0, tailBend1, tailBend2);
					}
				} else {
					EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The tail bend pixel is #{}XX - 2 segments with angles {}, {}", upperHex32f24Dbg(tailBend), tailBend0, tailBend1);
				}
			} else {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The tail bend pixel is #{}XXXX - 1 segment with angle {}", upperHex32f16Dbg(tailBend), tailBend0);
			}
		}
		int snout = getPixel(img, 6);
		int etc = getPixel(img, 7);
		int snoutOffset = 0;
		int snoutWidth = 0;
		int snoutHeight = 0;
		int snoutDepth = 0;
		if (MagicPixel.from(snout) == MagicPixel.BLUE) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The snout pixel is Magic Blue, pretending it's black");
		} else {
			snoutOffset = ((etc&0x0000FF00)>>8);
			snoutWidth = ((snout&0x00FF0000)>>16);
			snoutHeight = ((snout&0x0000FF00)>>8);
			snoutDepth = ((snout&0x000000FF));
			if (snoutOffset > 8-snoutHeight) snoutOffset = 8-snoutHeight;
			if (snoutWidth > 7) snoutWidth = 7;
			if (snoutHeight > 4) snoutHeight = 4;
			if (snoutDepth > 8) snoutDepth = 8;
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The snout pixel is #{} and the etc pixel is #{} - snout geometry is {}x{}x{}+0,{}", upperHex24Dbg(snout), upperHex24Dbg(etc), snoutWidth, snoutHeight, snoutDepth, snoutOffset);
		}
		float chestSize = 0;
		boolean capeEnabled = false;
		if (MagicPixel.from(etc) == MagicPixel.BLUE) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The etc pixel is Magic Blue, pretending it's black");
		} else {
			// leave the upper half of the range in case we want it for something later
			chestSize = ((etc&0x00FF0000)>>16)/128f;
			if (chestSize > 1) chestSize = 1;
			capeEnabled = (etc & 16) != 0;
			if (chestSize > 0) {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The etc pixel is #{} - {}% size", upperHex24Dbg(etc), (int)(chestSize*100));
			}
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The etc pixel is #{} - cape enabled: {}", upperHex24Dbg(etc), capeEnabled);
		}
		WingMode wingMode = getMagicPixel(img, 8, WING_MODE_BY_MAGIC, WingMode.NONE, "wing mode");
		boolean animateWings = getMagicPixel(img, 9) != MagicPixel.RED;
		boolean emissive = getMagicPixel(img, 10) == MagicPixel.ORANGE;
		return EarsFeatures.builder()
				.earMode(earMode)
				.earAnchor(earAnchor)
				.claws(protrusions.claws)
				.horn(protrusions.horn)
				.tailMode(tailMode)
				.tailSegments(tailSegments)
				.tailBends(tailBend0, tailBend1, tailBend2, tailBend3)
				.snoutOffset(snoutOffset)
				.snoutWidth(snoutWidth)
				.snoutHeight(snoutHeight)
				.snoutDepth(snoutDepth)
				.chestSize(chestSize)
				.wingMode(wingMode)
				.animateWings(animateWings)
				.capeEnabled(capeEnabled)
				.emissive(emissive);
	}

	/**
	 * Convert a pixel value to a float from -1 to 1, using an encoding that puts 0 at pixel value
	 * 0, thereby shifting all other possible values forward by one.
	 * <p>
	 * This allows a black pixel to mean 0 for all of its values.
	 */
	private static float pxValToUnit(int i) {
		if (i == 0) return 0;
		int j = i-128;
		if (j < 0) j -= 1;
		if (j >= 0) j += 1;
		return j/128f;
	}

	private static int getPixel(EarsImage img, int idx) {
		int x = idx%4;
		int y = 32+(idx/4);
		return img.getARGB(x, y);
	}
	
	private static MagicPixel getMagicPixel(EarsImage img, int idx) {
		return getMagicPixel(img, idx, true);
	}
		
	private static MagicPixel getMagicPixel(EarsImage img, int idx, boolean complain) {
		int x = idx%4;
		int y = 32+(idx/4);
		int rgb = img.getARGB(x, y);
		MagicPixel mp = MagicPixel.from(rgb);
		if (complain && mp == MagicPixel.UNKNOWN) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Pixel at {}, {} is not a valid magic pixel - it's #{}", x, y, upperHex24Dbg(img.getARGB(0, 32)));
		}
		return mp;
	}
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Map<MagicPixel, T> map, T def, String what) {
		return getMagicPixel(img, idx, map, def, what, true);
	}
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Map<MagicPixel, T> map, T def, String what, boolean relevant) {
		if (!relevant) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The {} pixel is not relevant; skipping it", what);
			return null;
		}
		MagicPixel mp = getMagicPixel(img, idx);
		T t = map.get(mp);
		if (t == null) {
			if (def == null) return null;
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): {} is not valid for the {} pixel; pretending it's {}", mp, what, def);
			return def;
		}
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The {} pixel is {} - setting {} to {}", what, mp, what, t);
		return t;
	}
	
	@SuppressWarnings("unused")
	private static boolean getMagicPixel(EarsImage img, int idx, MagicPixel truePixel, String what) {
		MagicPixel mp = getMagicPixel(img, idx);
		boolean b = mp == truePixel;
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): The {} pixel is {} - {} {}", what, mp, b ? "enabling" : "disabling", what);
		return b;
	}

	@SuppressWarnings("unused")
	static String upperHex32f8Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>24)&0xFF)|0xFF00).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	static String upperHex32f16Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>16)&0xFFFF)|0xFF0000).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	static String upperHex32f24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>8)&0xFFFFFF)|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	static String upperHex32Dbg(int col) {
		return EarsLog.DEBUG ? Long.toHexString((col&0xFFFFFFFFL)|0xFF00000000L).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	static String upperHex24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(col|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}

	@SuppressWarnings("unchecked")
	private static <S, K extends S, V extends S> Map<K, V> buildMap(S... arr) {
		if (arr.length%2 != 0) throw new IllegalArgumentException("Must have a multiple of 2 arguments");
		Map<K, V> map = new HashMap<K, V>();
		for (int i = 0; i < arr.length; i += 2) {
			map.put((K)arr[i], (V)arr[i+1]);
		}
		return Collections.unmodifiableMap(map);
	}

}
