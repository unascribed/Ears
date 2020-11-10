package com.unascribed.ears.common;

public class EarsFeatures {

	private static final EarsFeatures ENABLED = new EarsFeatures(true);
	private static final EarsFeatures DISABLED = new EarsFeatures(false);
	
	public final boolean earsEnabled;

	private EarsFeatures(boolean earsEnabled) {
		this.earsEnabled = earsEnabled;
	}
	
	public static EarsFeatures detect(EarsImage img) {
		if (img.getHeight() == 64) {
			boolean allMatch = true;
			out: for (int x = 0; x < 4; x++) {
				for (int y = 32; y < 36; y++) {
					if ((img.getARGB(x, y)&0x00FFFFFF) != 0x3F23D8) {
						allMatch = false;
						break out;
					}
				}
			}
			return allMatch ? ENABLED : DISABLED;
		}
		return DISABLED;
	}
	
}
