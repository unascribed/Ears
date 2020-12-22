package com.unascribed.ears.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class EarsFeatures {

	private enum MagicPixel {
		UNKNOWN(-1),
		BLUE(0x3F23D8),
		GREEN(0x23D848),
		RED(0xD82350),
		PURPLE(0xB923D8),
		CYAN(0x23D8C6),
		YELLOW(0xD7D823),
		ORANGE(0xD86A23)
		;
		private static final Map<Integer, MagicPixel> rgbToValue = new HashMap<>();
		static {
			for (MagicPixel mp : values()) {
				if (mp.rgb != -1) {
					rgbToValue.put(mp.rgb, mp);
				}
			}
		}
		private final int rgb;
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
		;
		public static EarMode fromMP(MagicPixel mp) {
			switch (mp) {
				default:
					EarsLog.debug("Common:Features", "detect(...): {} is not valid for the ear mode pixel; pretending it's Magic Red", mp);
				case RED: return NONE;
				case BLUE: return ABOVE;
				case GREEN: return SIDES;
				case PURPLE: return BEHIND;
				case CYAN: return AROUND;
			}
		}
	}
	public enum EarAnchor {
		CENTER,
		FRONT,
		BACK,
		;
		public static EarAnchor fromMP(MagicPixel mp) {
			switch (mp) {
				default:
					EarsLog.debug("Common:Features", "detect(...): {} is not valid for the ear anchor pixel; pretending it's Magic Blue", mp);
				case BLUE: return CENTER;
				case GREEN: return FRONT;
				case RED: return BACK;
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
		private Protrusions(boolean claws, boolean horn) {
			this.claws = claws;
			this.horn = horn;
		}
		public static Protrusions fromMP(MagicPixel mp) {
			switch (mp) {
				default:
					EarsLog.debug("Common:Features", "detect(...): {} is not valid for the protrusions pixel; pretending it's Magic Red", mp);
				case BLUE: case RED: return NONE;
				case GREEN: return CLAWS;
				case PURPLE: return HORN;
				case CYAN: return CLAWS_AND_HORN;
			}
		}
	}
	public enum TailMode {
		NONE,
		DOWN,
		BACK,
		UP,
		;
		public static TailMode fromMP(MagicPixel mp) {
			switch (mp) {
				default:
					EarsLog.debug("Common:Features", "detect(...): {} is not valid for the tail mode pixel; pretending it's Magic Red", mp);
				case RED: return NONE;
				case BLUE: return DOWN;
				case GREEN: return BACK;
				case PURPLE: return UP;
			}
		}
	}
	public enum TailBend {
		NONE(0),
		DOWN(0, -30),
		UP(0, 30),
		DOWN_DOWN(0, -20, -30),
		UP_UP(0, 20, 30),
		DOWN_UP(0, -30, 60),
		UP_DOWN(0, 30, -60),
		;
		public final float[] angles;
		private TailBend(float... angles) {
			this.angles = angles;
		}
		public static TailBend fromMP(MagicPixel mp) {
			switch (mp) {
				default:
					EarsLog.debug("Common:Features", "detect(...): {} is not valid for the tail bend pixel; pretending it's Magic Blue", mp);
				case BLUE: return NONE;
				case RED: return DOWN;
				case GREEN: return UP;
				case PURPLE: return DOWN_DOWN;
				case CYAN: return UP_UP;
				case ORANGE: return DOWN_UP;
				case YELLOW: return UP_DOWN;
			}
		}
	}

	private static final EarsFeatures DISABLED = new EarsFeatures(false, EarMode.NONE, null, Protrusions.NONE, TailMode.NONE, TailBend.NONE);
	
	public final boolean enabled;
	public final EarMode earMode;
	public final EarAnchor earAnchor;
	public final Protrusions protrusions;
	public final TailMode tailMode;
	public final TailBend tailBend;

	private EarsFeatures(boolean enabled, EarMode earMode, EarAnchor earAnchor, Protrusions protrusions, TailMode tailMode, TailBend tailBend) {
		this.enabled = enabled;
		this.earMode = earMode;
		this.earAnchor = earAnchor;
		this.protrusions = protrusions;
		this.tailMode = tailMode;
		this.tailBend = tailBend;
	}
	
	public static EarsFeatures detect(EarsImage img) {
		EarsLog.debug("Common:Features", "detect({})", img);
		if (img.getHeight() == 64) {
			MagicPixel first = getMagicPixel(img, 0);
			if (first == MagicPixel.BLUE) {
				EarMode earMode = getMagicPixel(img, 1, EarMode::fromMP, "ear mode");
				EarAnchor earAnchor = getMagicPixel(img, 2, EarAnchor::fromMP, "ear anchor", earMode != EarMode.NONE && earMode != EarMode.BEHIND);
				Protrusions protrusions = getMagicPixel(img, 3, Protrusions::fromMP, "protrusions");
				TailMode tailMode = getMagicPixel(img, 4, TailMode::fromMP, "tail mode");
				TailBend tailBend = getMagicPixel(img, 5, TailBend::fromMP, "tail bend");
				return new EarsFeatures(true, earMode, earAnchor, protrusions, tailMode, tailBend);
			} else {
				EarsLog.debug("Common:Features", "detect(...): Pixel at 0, 32 is not #3F23D8 (Magic Blue) - it's #{}. Disabling",  upperHex24Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
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
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Function<MagicPixel, T> converter, String what) {
		return getMagicPixel(img, idx, converter, what, true);
	}
	
	private static <T> T getMagicPixel(EarsImage img, int idx, Function<MagicPixel, T> converter, String what, boolean relevant) {
		if (!relevant) {
			EarsLog.debug("Common:Features", "detect(...): The {} pixel is not relevant; skipping it", what);
			return null;
		}
		MagicPixel mp = getMagicPixel(img, idx);
		T t = converter.apply(mp);
		EarsLog.debug("Common:Features", "detect(...): The {} pixel is {} - setting {} to {}", what, mp, what, t);
		return t;
	}
	
	private static boolean getMagicPixel(EarsImage img, int idx, MagicPixel truePixel, String what) {
		MagicPixel mp = getMagicPixel(img, idx);
		boolean b = mp == truePixel;
		EarsLog.debug("Common:Features", "detect(...): The {} pixel is {} - {} {}", what, mp, b ? "enabling" : "disabling", what);
		return b;
	}

	private static String upperHex24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toHexString(col|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}

	@Override
	public String toString() {
		return "EarsFeatures [enabled=" + enabled + ", earMode=" + earMode
				+ ", earAnchor=" + earAnchor + ", protrusions=" + protrusions
				+ ", tailMode=" + tailMode + ", tailBend=" + tailBend + "]";
	}
	
}
