package com.unascribed.ears.common;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
	
	public interface PNGLoader {
		EarsImage load(byte[] data) throws IOException;
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
	public final boolean emissive;
	public final byte[] emissiveSkin;
	public final byte[] emissiveWing;
	
	public final Alfalfa alfalfa;

	private EarsFeatures(Builder builder) {
		this.enabled = true;
		this.earMode = builder.earMode;
		this.earAnchor = builder.earAnchor;
		this.claws = builder.claws;
		this.horn = builder.horn;
		this.tailMode = builder.tailMode;
		this.tailSegments = builder.tailSegments;
		this.tailBend0 = builder.tailBend0;
		this.tailBend1 = builder.tailBend1;
		this.tailBend2 = builder.tailBend2;
		this.tailBend3 = builder.tailBend3;
		this.snoutOffset = builder.snoutOffset;
		this.snoutWidth = builder.snoutWidth;
		this.snoutHeight = builder.snoutHeight;
		this.snoutDepth = builder.snoutDepth;
		this.chestSize = builder.chestSize;
		this.wingMode = builder.wingMode;
		this.animateWings = builder.animateWings;
		this.capeEnabled = builder.capeEnabled;
		this.emissive = builder.emissive;
		this.emissiveSkin = builder.emissiveSkin;
		this.emissiveWing = builder.emissiveWing;
		this.alfalfa = builder.alfalfa;
	}

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
		this.emissive = false;
		this.emissiveSkin = new byte[0];
		this.emissiveWing = new byte[0];
		this.alfalfa = Alfalfa.NONE;
	}
	
	/**
	 * Decode the Ears configuration out of the magic pixels in the given skin image, and associate
	 * the given Alfalfa with the resultant features object.
	 */
	public static EarsFeatures detect(EarsImage img, Alfalfa alfalfa, PNGLoader loader) {
		EarsLog.debug("Common:Features", "detect({}, {})", img, alfalfa);
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
				EarsLog.debug("Common:Features", "detect(...): Could not find v0 (Pixelwise, #3F23D8) or v1 (Binary, #EA2501) data indicator at 0, 32 - found #{} instead. Disabling",
						EarsFeaturesParserV0.upperHex32Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
			if (bldr == null) {
				return DISABLED;
			}
			if (bldr.wingMode != WingMode.NONE && !alfalfa.data.containsKey("wing")) {
				EarsLog.debug("Common:Features", "detect(...): Wings are enabled, but there's no wing texture in the alfalfa. Disabling");
				bldr.wingMode(WingMode.NONE);
			}
			if (bldr.emissive && img instanceof WritableEarsImage) {
				WritableEarsImage wimg = (WritableEarsImage)img;
				WritableEarsImage out = wimg.copy();
				Set<Integer> palette = new HashSet<Integer>();
				for (int x = 52; x < 56; x++) {
					for (int y = 32; y < 36; y++) {
						int color = img.getARGB(x, y);
						if (((color >> 24)&0xFF) > 0) {
							EarsLog.debug("Common:Features", "detect(...): Making #{} an emissive color", Integer.toHexString(color|0xFF000000).substring(2).toUpperCase(Locale.ROOT));
							palette.add(color&0x00FFFFFF);
						}
					}
				}
				EarsLog.debug("Common:Features", "detect(...): Found {} color{} in emissive palette", palette.size(), palette.size() == 1 ? "" : "s");
				if (palette.isEmpty()) {
					bldr.emissiveSkin(new byte[0]);
					bldr.emissiveWing(new byte[0]);
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
					EarsLog.debug("Common:Features", "detect(...): Found {} emissive pixel{} in skin", found, found == 1 ? "" : "s");
					if (alfalfa.data.containsKey("wing") && bldr.wingMode != WingMode.NONE) {
						try {
							EarsImage wing = loader.load(alfalfa.data.get("wing").toByteArray());
							if (wing instanceof WritableEarsImage) {
								found = 0;
								WritableEarsImage wwing = (WritableEarsImage)wing;
								WritableEarsImage wout = wwing.copy();
								for (int x = 0; x < 12; x++) {
									for (int y = 0; y < 12; y++) {
										int c = wwing.getARGB(x, y);
										if (palette.contains(c&0x00FFFFFF)) {
											wwing.setARGB(x, y, 0);
											found++;
										} else {
											wout.setARGB(x, y, 0);
										}
									}
								}
								bldr.emissiveWing(QDPNG.write(wout));
								EarsLog.debug("Common:Features", "detect(...): Found {} emissive pixel{} in wing", found, found == 1 ? "" : "s");
							} else {
								bldr.emissiveWing(new byte[0]);
							}
						} catch (IOException e) {
							EarsLog.debug("Common:Features", "detect(...): Exception while loading wing", e);
							bldr.emissiveWing(new byte[0]);
						}
					} else {
						bldr.emissiveWing(new byte[0]);
					}
					bldr.emissiveSkin(QDPNG.write(out));
				}
			} else {
				bldr.emissiveSkin(new byte[0]);
				bldr.emissiveWing(new byte[0]);
			}
			return bldr
					.capeEnabled(false)
					.alfalfa(alfalfa)
					.build();
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
	}
	
	@Override
	public String toString() {
		return "EarsFeatures["+
					"earMode="+earMode+", "+
					"earAnchor="+earAnchor+", "+
					"claws="+claws+", "+
					"horn="+horn+
					"tailMode="+tailMode+
					"tailSegments="+tailSegments+
					"tailBends=["+
						tailBend0+", "+
						tailBend1+", "+
						tailBend2+", "+
						tailBend3+
					"], "+
					"snoutOffset="+snoutOffset+", "+
					"snoutWidth="+snoutWidth+", "+
					"snoutHeight="+snoutHeight+", "+
					"snoutDepth="+snoutDepth+", "+
					(chestSize > 0 ? "chestSize="+chestSize+", " : "")+
					"wingMode="+wingMode+", "+
					"animateWings="+animateWings+", "+
					(capeEnabled ? "capeEnabled="+capeEnabled+", " : "")+
					"emissive="+emissive+", "+
					"emissiveSkin=["+emissiveSkin.length+" bytes], "+
					"emissiveWing=["+emissiveWing.length+" bytes], "+
					"alfalfa="+alfalfa+
				"]";
	}
	
	

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		public Builder tailBends(float tailBend0, float tailBend1, float tailBend2, float tailBend3) {
			return tailBend0(tailBend0).tailBend1(tailBend1).tailBend2(tailBend2).tailBend3(tailBend3);
		}
		
		// AUTOGENERATED
		private EarMode earMode;
		private EarAnchor earAnchor;
		private boolean claws;
		private boolean horn;
		private TailMode tailMode;
		private int tailSegments;
		private float tailBend0;
		private float tailBend1;
		private float tailBend2;
		private float tailBend3;
		private int snoutOffset;
		private int snoutWidth;
		private int snoutHeight;
		private int snoutDepth;
		private float chestSize;
		private WingMode wingMode;
		private boolean animateWings;
		private boolean capeEnabled;
		private boolean emissive;
		private byte[] emissiveSkin;
		private byte[] emissiveWing;
		private Alfalfa alfalfa;

		private Builder() {}

		public Builder earMode(EarMode earMode) {
			this.earMode = earMode;
			return this;
		}

		public Builder earAnchor(EarAnchor earAnchor) {
			this.earAnchor = earAnchor;
			return this;
		}

		public Builder claws(boolean claws) {
			this.claws = claws;
			return this;
		}

		public Builder horn(boolean horn) {
			this.horn = horn;
			return this;
		}

		public Builder tailMode(TailMode tailMode) {
			this.tailMode = tailMode;
			return this;
		}

		public Builder tailSegments(int tailSegments) {
			this.tailSegments = tailSegments;
			return this;
		}

		public Builder tailBend0(float tailBend0) {
			this.tailBend0 = tailBend0;
			return this;
		}

		public Builder tailBend1(float tailBend1) {
			this.tailBend1 = tailBend1;
			return this;
		}

		public Builder tailBend2(float tailBend2) {
			this.tailBend2 = tailBend2;
			return this;
		}

		public Builder tailBend3(float tailBend3) {
			this.tailBend3 = tailBend3;
			return this;
		}

		public Builder snoutOffset(int snoutOffset) {
			this.snoutOffset = snoutOffset;
			return this;
		}

		public Builder snoutWidth(int snoutWidth) {
			this.snoutWidth = snoutWidth;
			return this;
		}

		public Builder snoutHeight(int snoutHeight) {
			this.snoutHeight = snoutHeight;
			return this;
		}

		public Builder snoutDepth(int snoutDepth) {
			this.snoutDepth = snoutDepth;
			return this;
		}

		public Builder chestSize(float chestSize) {
			this.chestSize = chestSize;
			return this;
		}

		public Builder wingMode(WingMode wingMode) {
			this.wingMode = wingMode;
			return this;
		}

		public Builder animateWings(boolean animateWings) {
			this.animateWings = animateWings;
			return this;
		}

		public Builder capeEnabled(boolean capeEnabled) {
			this.capeEnabled = capeEnabled;
			return this;
		}

		public Builder emissive(boolean emissive) {
			this.emissive = emissive;
			return this;
		}

		public Builder emissiveSkin(byte[] emissiveSkin) {
			this.emissiveSkin = emissiveSkin;
			return this;
		}

		public Builder emissiveWing(byte[] emissiveWing) {
			this.emissiveWing = emissiveWing;
			return this;
		}

		public Builder alfalfa(Alfalfa alfalfa) {
			this.alfalfa = alfalfa;
			return this;
		}
		
		public EarsFeatures build() {
			return new EarsFeatures(this);
		}
	}

	
	
}
