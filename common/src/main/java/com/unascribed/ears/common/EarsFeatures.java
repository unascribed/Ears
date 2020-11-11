package com.unascribed.ears.common;

import java.util.Locale;

public class EarsFeatures {

	private static final EarsFeatures ENABLED = new EarsFeatures(true);
	private static final EarsFeatures DISABLED = new EarsFeatures(false);
	
	public final boolean earsEnabled;

	private EarsFeatures(boolean earsEnabled) {
		this.earsEnabled = earsEnabled;
	}
	
	public static EarsFeatures detect(EarsImage img) {
		EarsLog.debug("Common:Features", "detect({})", img);
		if (img.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					int col = (img.getARGB(x, y)&0x00FFFFFF);
					if (col != 0x3F23D8) {
						EarsLog.debug("Common:Features", "detect(...): Pixel at {}, {} is not #3F23D8 (it's #{})", x, y,
								EarsLog.DEBUG ? Integer.toString(col|0xFF000000).substring(2).toUpperCase(Locale.ROOT) : "");
						allMatch = false;
						break out;
					}
				}
			}
			return allMatch ? ENABLED : DISABLED;
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
	}
	
	@Override
	public String toString() {
		return "EarsFeatures."+(earsEnabled ? "ENABLED" : "DISABLED");
	}
	
}
