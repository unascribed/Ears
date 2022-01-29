package com.unascribed.ears.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.unascribed.ears.api.Slice;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.debug.EarsLog;

public class EarsFeaturesParser {
	
	public interface PNGLoader {
		EarsImage load(byte[] data) throws IOException;
	}

	/**
	 * Decode the Ears configuration out of the magic pixels in the given skin image, and associate
	 * the given Alfalfa with the resultant features object.
	 */
	public static EarsFeatures detect(EarsImage img, AlfalfaData alfalfa, PNGLoader loader) {
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect({}, {})", img, alfalfa);
		if (img.getHeight() == 64) {
			int first = img.getARGB(0, 32)&0x00FFFFFF;
			EarsFeatures.Builder bldr;
			if (first == EarsFeaturesParserV0.MAGIC) {
				// Ears Data v0 (Pixelwise)
				bldr = EarsFeaturesParserV0.parse(img);
			} else if (first == EarsFeaturesParserV1.MAGIC) {
				// Ears Data v1.x (Binary)
				bldr = EarsFeaturesParserV1.parse(img);
			} else {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Could not find v0 (Pixelwise, #3F23D8) or v1 (Binary, #EA2501) data indicator at 0, 32 - found #{} instead. Disabling",
						EarsFeaturesParserV0.upperHex32Dbg(img.getARGB(0, 32)));
				return EarsFeatures.DISABLED;
			}
			if (bldr == null) {
				return EarsFeatures.DISABLED;
			}
			if (bldr.getWingMode() != EarsFeatures.WingMode.NONE && !alfalfa.data.containsKey("wing")) {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Wings are enabled, but there's no wing texture in the alfalfa. Disabling");
				bldr.wingMode(WingMode.NONE);
			}
			// loader will only be null in the Manipulator, which won't give us 12x12 wings
			if (alfalfa.data.containsKey("wing") && loader != null) {
				try {
					EarsImage wing = loader.load(alfalfa.data.get("wing").toByteArray());
					if (wing.getWidth() == 12 && wing.getHeight() == 12) {
						EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Upgrading legacy 12x12 wing to 20x16");
						WritableEarsImage wingOut = new RawEarsImage(new int[20*16], 20, 16, false);
						for (int x = 0; x < 12; x++) {
							for (int y = 0; y < 12; y++) {
								wingOut.setARGB(x, y+2, wing.getARGB(x, y));
							}
						}
						Map<String, Slice> newData = new HashMap<String, Slice>(alfalfa.data);
						newData.put("wing", new Slice(QDPNG.write(wingOut)));
						alfalfa = new AlfalfaData(alfalfa.version, newData);
					} else if (wing.getWidth() != 20 || wing.getHeight() != 16) {
						EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Unknown wing size {}x{}. Disabling", wing.getWidth(), wing.getHeight());
						bldr.wingMode(WingMode.NONE);
					}
				} catch (Throwable t) {
					EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Exception while attempting to load wing. Disabling", t);
					bldr.wingMode(WingMode.NONE);
				}
			}
			if (bldr.isEmissive() && img instanceof WritableEarsImage && loader != null) {
				WritableEarsImage wimg = (WritableEarsImage)img;
				WritableEarsImage out = wimg.copy();
				Set<Integer> palette = new HashSet<Integer>();
				for (int x = 52; x < 56; x++) {
					for (int y = 32; y < 36; y++) {
						int color = img.getARGB(x, y);
						if (((color >> 24)&0xFF) > 0) {
							EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Making #{} an emissive color", Integer.toHexString(color|0xFF000000).substring(2).toUpperCase(Locale.ROOT));
							palette.add(color&0x00FFFFFF);
						}
					}
				}
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Found {} color{} in emissive palette", palette.size(), palette.size() == 1 ? "" : "s");
				if (palette.isEmpty()) {
					bldr.emissiveSkin(Slice.EMPTY);
					bldr.emissiveWing(Slice.EMPTY);
					bldr.emissive(false);
				} else {
					int found = 0;
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++) {
							int c = wimg.getARGB(x, y);
							if (palette.contains(c&0x00FFFFFF)) {
								wimg.setARGB(x, y, 0);
								found++;
							} else {
								out.setARGB(x, y, 0);
							}
						}
					}
					EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Found {} emissive pixel{} in skin", found, found == 1 ? "" : "s");
					if (alfalfa.data.containsKey("wing") && bldr.getWingMode() != EarsFeatures.WingMode.NONE) {
						try {
							EarsImage wing = loader.load(alfalfa.data.get("wing").toByteArray());
							if (wing instanceof WritableEarsImage) {
								found = 0;
								WritableEarsImage wwing = (WritableEarsImage)wing;
								WritableEarsImage wout = wwing.copy();
								for (int x = 0; x < wing.getWidth(); x++) {
									for (int y = 0; y < wing.getHeight(); y++) {
										int c = wwing.getARGB(x, y);
										if (palette.contains(c&0x00FFFFFF)) {
											wwing.setARGB(x, y, 0);
											found++;
										} else {
											wout.setARGB(x, y, 0);
										}
									}
								}
								bldr.emissiveWing(new Slice(QDPNG.write(wout)));
								EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Found {} emissive pixel{} in wing", found, found == 1 ? "" : "s");
							} else {
								bldr.emissiveWing(Slice.EMPTY);
							}
						} catch (IOException e) {
							EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Exception while loading wing", e);
							bldr.emissiveWing(Slice.EMPTY);
						}
					} else {
						bldr.emissiveWing(Slice.EMPTY);
					}
					bldr.emissiveSkin(new Slice(QDPNG.write(out)));
				}
			} else {
				bldr.emissive(false);
				bldr.emissiveSkin(Slice.EMPTY);
				bldr.emissiveWing(Slice.EMPTY);
			}
			return bldr
					.alfalfa(alfalfa)
					.build();
		}
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Legacy skin, ignoring");
		return EarsFeatures.DISABLED;
	}

}
