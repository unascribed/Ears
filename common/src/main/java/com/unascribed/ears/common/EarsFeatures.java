package com.unascribed.ears.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.unascribed.ears.common.debug.EarsLog;

public class EarsFeatures {

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
				EarsLog.debug("Common:Features", "All legal magic pixels:");
				for (MagicPixel mp : values()) {
					if (mp == UNKNOWN) continue;
					EarsLog.debug("Common:Features", "- {}: #{}",
							mp, upperHex24Dbg(mp.rgb));
				}
			}
		}
	}
	
	public enum EarMode {
		NONE,
		ABOVE,
		SIDES,
		BEHIND,
		AROUND,
		FLOPPY,
		CROSS,
		OUT,
		;
		
		public static final Map<MagicPixel, EarMode> BY_MAGIC = buildMap(
				MagicPixel.RED, NONE,
				MagicPixel.BLUE, ABOVE,
				MagicPixel.GREEN, SIDES,
				MagicPixel.PURPLE, BEHIND,
				MagicPixel.CYAN, AROUND,
				MagicPixel.ORANGE, FLOPPY,
				MagicPixel.PINK, CROSS,
				MagicPixel.PURPLE2, OUT
		);
	}
	public enum EarAnchor {
		CENTER,
		FRONT,
		BACK,
		;
		
		public static final Map<MagicPixel, EarAnchor> BY_MAGIC = buildMap(
				MagicPixel.BLUE, CENTER,
				MagicPixel.GREEN, FRONT,
				MagicPixel.RED, BACK
		);
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
	public enum TailMode {
		NONE,
		DOWN,
		BACK,
		UP,
		VERTICAL,
		;
		public static final Map<MagicPixel, TailMode> BY_MAGIC = buildMap(
				MagicPixel.RED, NONE,
				MagicPixel.BLUE, DOWN,
				MagicPixel.GREEN, BACK,
				MagicPixel.PURPLE, UP,
				MagicPixel.ORANGE, VERTICAL
		);
	}

	private static final EarsFeatures DISABLED = new EarsFeatures(false, EarMode.NONE, null, Protrusions.NONE, TailMode.NONE, 0, 0, 0, 0);
	
	public final boolean enabled;
	public final EarMode earMode;
	public final EarAnchor earAnchor;
	public final Protrusions protrusions;
	public final TailMode tailMode;
	public final float tailBend0;
	public final float tailBend1;
	public final float tailBend2;
	public final float tailBend3;

	EarsFeatures(boolean enabled, EarMode earMode, EarAnchor earAnchor, Protrusions protrusions, TailMode tailMode, float tailBend0, float tailBend1, float tailBend2, float tailBend3) {
		this.enabled = enabled;
		this.earMode = earMode;
		this.earAnchor = earAnchor;
		this.protrusions = protrusions;
		this.tailMode = tailMode;
		this.tailBend0 = tailBend0;
		this.tailBend1 = tailBend1;
		this.tailBend2 = tailBend2;
		this.tailBend3 = tailBend3;
	}
	
	public static EarsFeatures detect(EarsImage img) {
		EarsLog.debug("Common:Features", "detect({})", img);
		if (img.getHeight() == 64) {
			MagicPixel first = getMagicPixel(img, 0);
			if (first == MagicPixel.BLUE) {
				EarMode earMode = getMagicPixel(img, 1, EarMode.BY_MAGIC, EarMode.NONE, "ear mode");
				EarAnchor earAnchor = getMagicPixel(img, 2, EarAnchor.BY_MAGIC, EarAnchor.CENTER, "ear anchor", earMode != EarMode.NONE && earMode != EarMode.BEHIND);
				Protrusions protrusions = getMagicPixel(img, 3, Protrusions.BY_MAGIC, Protrusions.NONE, "protrusions");
				TailMode tailMode = getMagicPixel(img, 4, TailMode.BY_MAGIC, TailMode.NONE, "tail mode");
				int tailBend = getPixel(img, 5);
				float tailBend0 = 0;
				float tailBend1 = 0;
				float tailBend2 = 0;
				float tailBend3 = 0;
				if (MagicPixel.from(tailBend) == MagicPixel.BLUE) {
					EarsLog.debug("Common:Features", "detect(...): The tail bend pixel is Magic Blue, pretending it's black");
				} else {
					tailBend0 = pxValToUnit(255-((tailBend&0xFF000000) >>> 24))*90;
					tailBend1 = pxValToUnit((tailBend&0x00FF0000) >> 16)*90;
					tailBend2 = pxValToUnit((tailBend&0x0000FF00) >> 8)*90;
					tailBend3 = pxValToUnit((tailBend&0x000000FF) >> 0)*90;
					if (tailBend1 != 0) {
						if (tailBend2 != 0) {
							if (tailBend3 != 0) {
								EarsLog.debug("Common:Features", "detect(...): The tail bend pixel is #{} - 4 segments with angles {}, {}, {}, {}", upperHex32Dbg(tailBend), tailBend0, tailBend1, tailBend2, tailBend3);
							} else {
								EarsLog.debug("Common:Features", "detect(...): The tail bend pixel is #{} - 3 segments with angles {}, {}, {}", upperHex32Dbg(tailBend), tailBend0, tailBend1, tailBend2);
							}
						} else {
							EarsLog.debug("Common:Features", "detect(...): The tail bend pixel is #{}XX - 2 segments with angles {}, {}", upperHex32f24Dbg(tailBend), tailBend0, tailBend1);
						}
					} else {
						EarsLog.debug("Common:Features", "detect(...): The tail bend pixel is #{}XXXX - 1 segment with angle {}", upperHex32f16Dbg(tailBend), tailBend0);
					}
				}
				return new EarsFeatures(true, earMode, earAnchor, protrusions, tailMode, tailBend0, tailBend1, tailBend2, tailBend3);
			} else {
				EarsLog.debug("Common:Features", "detect(...): Pixel at 0, 32 is not #3F23D8 (Magic Blue) - it's #{}. Disabling",  upperHex32Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
	}
	
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
		int x = idx%4;
		int y = 32+(idx/4);
		int rgb = img.getARGB(x, y);
		MagicPixel mp = MagicPixel.from(rgb);
		if (mp == MagicPixel.UNKNOWN) {
			EarsLog.debug("Common:Features", "detect(...): Pixel at {}, {} is not a valid magic pixel - it's #{}", x, y, upperHex24Dbg(img.getARGB(0, 32)));
		}
		return mp;
	}
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Map<MagicPixel, T> map, T def, String what) {
		return getMagicPixel(img, idx, map, def, what, true);
	}
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Map<MagicPixel, T> map, T def, String what, boolean relevant) {
		if (!relevant) {
			EarsLog.debug("Common:Features", "detect(...): The {} pixel is not relevant; skipping it", what);
			return null;
		}
		MagicPixel mp = getMagicPixel(img, idx);
		T t = map.get(mp);
		if (t == null) {
			if (def == null) return null;
			EarsLog.debug("Common:Features", "detect(...): {} is not valid for the {} pixel; pretending it's {}", mp, what, def);
			return def;
		}
		EarsLog.debug("Common:Features", "detect(...): The {} pixel is {} - setting {} to {}", what, mp, what, t);
		return t;
	}
	
	@SuppressWarnings("unused")
	private static boolean getMagicPixel(EarsImage img, int idx, MagicPixel truePixel, String what) {
		MagicPixel mp = getMagicPixel(img, idx);
		boolean b = mp == truePixel;
		EarsLog.debug("Common:Features", "detect(...): The {} pixel is {} - {} {}", what, mp, b ? "enabling" : "disabling", what);
		return b;
	}

	@SuppressWarnings("unused")
	private static String upperHex32f8Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>24)&0xFF)|0xFF00).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	private static String upperHex32f16Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>16)&0xFFFF)|0xFF0000).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	private static String upperHex32f24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(((col>>8)&0xFFFFFF)|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	private static String upperHex32Dbg(int col) {
		return EarsLog.DEBUG ? Long.toHexString((col&0xFFFFFFFFL)|0xFF00000000L).substring(2).toUpperCase(Locale.ROOT) : "";
	}
	
	private static String upperHex24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(col|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}

	@Override
	public String toString() {
		return "EarsFeatures[enabled=" + enabled + ", earMode=" + earMode
				+ ", earAnchor=" + earAnchor + ", protrusions=" + protrusions
				+ ", tailMode=" + tailMode + ", tailBend={" + tailBend0
				+ ", " + tailBend1 + ", " + tailBend2 + ", " + tailBend3 + "}]";
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
