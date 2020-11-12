package com.unascribed.ears.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EarsFeatures {

	private enum MagicPixel {
		UNKNOWN(-1),
		BLUE(0x3F23D8),
		GREEN(0x23D848),
		RED(0xD82350),
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
	}

	private static final EarsFeatures DISABLED = new EarsFeatures(false, false, false, false);
	
	public final boolean enabled;
	public final boolean newRegions;
	public final boolean backPointingEars;
	public final boolean asymmetricalBackPointingEars;

	private EarsFeatures(boolean enabled, boolean newRegions, boolean backPointingEars, boolean asymmetricalBackPointingEars) {
		this.enabled = enabled;
		this.newRegions = newRegions;
		this.backPointingEars = backPointingEars;
		this.asymmetricalBackPointingEars = asymmetricalBackPointingEars;
	}
	
	public static EarsFeatures detect(EarsImage img) {
		EarsLog.debug("Common:Features", "detect({})", img);
		if (img.getHeight() == 64) {
			MagicPixel first = MagicPixel.from(img.getARGB(0, 32));
			if (first == MagicPixel.BLUE) {
				MagicPixel second = MagicPixel.from(img.getARGB(1, 32));
				boolean newRegions;
				if (second == MagicPixel.BLUE) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 1, 32 is Magic Blue, assuming legacy regions");
					newRegions = false;
				} else if (second == MagicPixel.GREEN) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 1, 32 is Magic Green, enabling new regions");
					newRegions = true;
				} else if (second == MagicPixel.RED) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 1, 32 is Magic Red, disabling new regions");
					newRegions = false;
				} else {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 1, 32 is not a recognized magic color (#3F23D8, #23D848, or #D82350) - it's #{}",  upperHex24Dbg(img.getARGB(1, 32)));
					return DISABLED;
				}
				MagicPixel third = MagicPixel.from(img.getARGB(2, 32));
				boolean backEars;
				boolean asymBackEars;
				if (third == MagicPixel.BLUE) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 2, 32 is Magic Blue, assuming no back-pointing ears");
					backEars = false;
					asymBackEars = false;
				} else if (third == MagicPixel.GREEN) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 2, 32 is Magic Green, using asymmetric back-pointing ears");
					backEars = true;
					asymBackEars = true;
				} else if (third == MagicPixel.RED) {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 2, 32 is Magic Red, using symmetric back-pointing ears");
					backEars = true;
					asymBackEars = false;
				} else {
					EarsLog.debug("Common:Features", "detect(...): Pixel at 2, 32 is not a recognized magic color (#3F23D8, #23D848, or #D82350) - it's #{}",  upperHex24Dbg(img.getARGB(2, 32)));
					return DISABLED;
				}
				return new EarsFeatures(true, newRegions, backEars, asymBackEars);
			} else {
				EarsLog.debug("Common:Features", "detect(...): Pixel at 0, 32 is not #3F23D8 (Magic Blue) - it's #{}",  upperHex24Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
	}
	
	private static String upperHex24Dbg(int col) {
		return EarsLog.DEBUG ? Integer.toString(col|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "";
	}

	@Override
	public String toString() {
		return "EarsFeatures[enabled="+enabled+", newRegions="+newRegions+", asymmetricalBackPointingEars="+asymmetricalBackPointingEars+"]";
	}
	
}
