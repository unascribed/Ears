package com.unascribed.ears.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.unascribed.ears.common.EarsCommon.Rectangle;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.util.Slice;

/**
 * Describes the state of every Ears feature for a player skin.
 */
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
	public enum WingMode {
		NONE,
		SYMMETRIC_DUAL,
		SYMMETRIC_SINGLE,
		ASYMMETRIC_L,
		ASYMMETRIC_R,
		;
		public static final Map<MagicPixel, WingMode> BY_MAGIC = buildMap(
				MagicPixel.BLUE, NONE,
				MagicPixel.RED, NONE,
				MagicPixel.PINK, SYMMETRIC_DUAL,
				MagicPixel.GREEN, SYMMETRIC_SINGLE,
				MagicPixel.CYAN, ASYMMETRIC_L,
				MagicPixel.ORANGE, ASYMMETRIC_R
		);
	}
	
	/**
	 * Extra data stored in the alpha channel of forced-opaque areas.
	 */
	public static class Alfalfa {
		
		// cannot be longer than 64 entries (as if we'll ever reach that)
		private static final List<String> PREDEF_KEYS = Collections.unmodifiableList(Arrays.asList(
			"END", "wing"
		));
		
		public static final Alfalfa NONE = new Alfalfa(0, Collections.<String, Slice>emptyMap());
		
		public static final int MAGIC = 0xEA1FA1FA; // EALFALFA
		
		public final int version;
		public final Map<String, Slice> data;
		
		public Alfalfa(int version, Map<String, Slice> data) {
			this.version = version;
			this.data = Collections.unmodifiableMap(new HashMap<String, Slice>(data));
		}

		@Override
		public String toString() {
			return "Alfalfa[version=" + version + ", data=" + data + "]";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			result = prime * result + version;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Alfalfa other = (Alfalfa) obj;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			if (version != other.version)
				return false;
			return true;
		}

		public static Alfalfa read(InputStream in) throws IOException {
			DataInputStream dis = new DataInputStream(in);
			dis.skipBytes(1);
			int magic = dis.readInt();
			if (magic != MAGIC) {
				EarsLog.debug("Common:Features", "Alfalfa.read: Magic number does not match. Expected {}, got {}", Integer.toHexString(MAGIC), Long.toHexString(magic));
				return NONE;
			}
			int version = dis.readUnsignedByte();
			EarsLog.debug("Common:Features", "Alfalfa.read: Discovered Alfalfa v{} data", version);
			if (version != 1) {
				EarsLog.debug("Common:Features", "Alfalfa.read: Don't know how to read this version, ignoring");
				return NONE;
			}
			byte[] buf = new byte[255];
			Map<String, Slice> map = new HashMap<String, Slice>();
			while (true) {
				String k;
				int first = dis.readUnsignedByte();
				if (first < 64) {
					if (first < PREDEF_KEYS.size()) {
						k = PREDEF_KEYS.get(first);
					} else {
						k = "!unk"+first;
					}
				} else {
					StringBuilder sb = new StringBuilder();
					sb.appendCodePoint(first);
					while (true) {
						int b = dis.readUnsignedByte();
						if ((b&0x80) != 0) {
							sb.appendCodePoint(b & 0x7F);
							break;
						} else {
							sb.appendCodePoint(b);
						}
					}
					k = sb.toString();
				}
				if ("END".equals(k)) break;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while (true) {
					int len = dis.readUnsignedByte();
					dis.readFully(buf, 0, len);
					baos.write(buf, 0, len);
					if (len != 255) break;
				}
				byte[] data = baos.toByteArray();
				map.put(k, new Slice(data, 0, data.length));
				EarsLog.debug("Common:Features", "Alfalfa.read: Found entry {} with {} byte{} of data", k, data.length, data.length == 1 ? "" : "s");
			}
			EarsLog.debug("Common:Features", "Alfalfa.read: Found {} entr{}", map.size(), map.size() == 1 ? "y" : "ies");
			return new Alfalfa(version, map);
		}
		
		public void write(OutputStream out) throws IOException {
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(MAGIC);
			dos.writeByte(version);
			for (Map.Entry<String, Slice> en : data.entrySet()) {
				String k = en.getKey();
				int idx = PREDEF_KEYS.indexOf(k);
				if (k.startsWith("!unk")) {
					dos.writeByte(Integer.parseInt(k.substring(4)));
				} else if (idx == -1) {
					for (int i = 0; i < k.length(); i++) {
						char c = k.charAt(i);
						if (c < 64 && i == 0) throw new IOException("Cannot write an entry with name "+en.getKey()+" - it must start with an ASCII character with value 64 (@) or greater");
						if (c > 127) throw new IOException("Cannot write an entry with name "+en.getKey()+" - it must only contain ASCII characters");
						if (i == k.length()-1) c |= 0x80;
						dos.writeByte(c);
					}
				} else {
					dos.writeByte(idx);
				}
				int fullLen = en.getValue().size();
				int pos = 0;
				do {
					int len = Math.min(255, fullLen-pos);
					dos.writeByte(len);
					en.getValue().slice(pos, len).writeTo(dos);
					pos += len;
				} while (pos < fullLen);
			}
			dos.writeByte(0);
		}

		public static Alfalfa read(EarsImage img) {
			BigInteger bi = BigInteger.ZERO;
			int read = 0;
			for (Rectangle rect : EarsCommon.FORCED_OPAQUE_REGIONS) {
				for (int x = rect.x1; x < rect.x2; x++) {
					for (int y = rect.y1; y < rect.y2; y++) {
						int a = (img.getARGB(x, y)>>24)&0xFF;
						if (a == 0) {
							continue;
						}
						int v = 0x7F-(a&0x7F);
						bi = bi.or(BigInteger.valueOf(v).shiftLeft(read*7));
						read++;
					}
				}
			}
			if (bi.equals(BigInteger.ZERO)) {
				EarsLog.debug("Common:Features", "Alfalfa.read: Found no data in alpha channel");
				return NONE;
			}
			EarsLog.debug("Common:Features", "Alfalfa.read: Read {} ayte{} of data from alpha channel", read, read == 1 ? "" : "s");
			try {
				return read(new ByteArrayInputStream(bi.toByteArray()));
			} catch (Exception e) {
				EarsLog.debug("Common:Features", "Alfalfa.read: Exception while reading data", e);
				return NONE;
			}
		}

		public void write(WritableEarsImage img) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			write(baos);
			byte[] data = baos.toByteArray();
			if (data.length > 1428) {
				throw new IllegalArgumentException("Cannot write more than 1428 bytes of data (got "+data.length+" bytes)");
			}
			BigInteger _7F = BigInteger.valueOf(0x7F);
			BigInteger bi = new BigInteger(1, data);
			int written = 0;
			for (Rectangle rect : EarsCommon.FORCED_OPAQUE_REGIONS) {
				for (int x = rect.x1; x < rect.x2; x++) {
					for (int y = rect.y1; y < rect.y2; y++) {
						int argb = img.getARGB(x, y);
						int a = (argb>>24)&0xFF;
						if (a == 0) {
							argb = 0xFF000000;
						}
						int v = bi.shiftRight(written*7).and(_7F).intValueExact();
						a = (0x7F-v)|0x80;
						argb = (argb&0x00FFFFFF)|((a&0xFF) << 24);
						img.setARGB(x, y, argb);
						written++;
					}
				}
			}
		}
		
		public static Alfalfa read(ByteArrayInputStream in) throws IOException {
			return read((InputStream)in);
		}
		
		public void write(ByteArrayOutputStream out) {
			try {
				write((OutputStream)out);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		}
		
	}

	public static final EarsFeatures DISABLED = new EarsFeatures(false, EarMode.NONE, null, Protrusions.NONE, TailMode.NONE, 0, 0, 0, 0, 0, 0, 0, 0, 0, WingMode.NONE, Alfalfa.NONE);
	
	public final boolean enabled;
	public final EarMode earMode;
	public final EarAnchor earAnchor;
	public final Protrusions protrusions;
	public final TailMode tailMode;
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
	
	public final Alfalfa alfalfa;

	EarsFeatures(boolean enabled,
			EarMode earMode, EarAnchor earAnchor,
			Protrusions protrusions,
			TailMode tailMode, float tailBend0, float tailBend1, float tailBend2, float tailBend3,
			int snoutOffset, int snoutWidth, int snoutHeight, int snoutDepth,
			float chestSize, WingMode wingMode,
			Alfalfa alfalfa) {
		this.enabled = enabled;
		this.earMode = earMode;
		this.earAnchor = earAnchor;
		this.protrusions = protrusions;
		this.tailMode = tailMode;
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
		this.alfalfa = alfalfa;
	}

	/**
	 * Decode the Ears configuration out of the magic pixels in the given skin image, and associate
	 * the given Alfalfa with the resultant features object.
	 */
	public static EarsFeatures detect(EarsImage img, Alfalfa alfalfa) {
		EarsLog.debug("Common:Features", "detect({}, {})", img, alfalfa);
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
				int snout = getPixel(img, 6);
				int etc = getPixel(img, 7);
				int snoutOffset = 0;
				int snoutWidth = 0;
				int snoutHeight = 0;
				int snoutDepth = 0;
				if (MagicPixel.from(snout) == MagicPixel.BLUE) {
					EarsLog.debug("Common:Features", "detect(...): The snout pixel is Magic Blue, pretending it's black");
				} else {
					snoutOffset = ((etc&0x0000FF00)>>8);
					snoutWidth = ((snout&0x00FF0000)>>16);
					snoutHeight = ((snout&0x0000FF00)>>8);
					snoutDepth = ((snout&0x000000FF));
					if (snoutOffset > 8-snoutHeight) snoutOffset = 8-snoutHeight;
					if (snoutWidth > 7) snoutWidth = 7;
					if (snoutHeight > 4) snoutHeight = 4;
					if (snoutDepth > 6) snoutDepth = 6;
					EarsLog.debug("Common:Features", "detect(...): The snout pixel is #{} and the etc pixel is #{} - snout geometry is {}x{}x{}+0,{}", upperHex24Dbg(snout), upperHex24Dbg(etc), snoutWidth, snoutHeight, snoutDepth, snoutOffset);
				}
				float chestSize = 0;
				if (MagicPixel.from(etc) == MagicPixel.BLUE) {
					EarsLog.debug("Common:Features", "detect(...): The etc pixel is Magic Blue, pretending it's black");
				} else {
					// leave the upper half of the range in case we want it for something later
					chestSize = ((etc&0x00FF0000)>>16)/128f;
					if (chestSize > 1) chestSize = 1;
					EarsLog.debug("Common:Features", "detect(...): The etc pixel is #{} - {}% size", upperHex24Dbg(etc), (int)(chestSize*100));
				}
				WingMode wingMode = getMagicPixel(img, 8, WingMode.BY_MAGIC, WingMode.NONE, "wing mode");
				if (wingMode != WingMode.NONE && !alfalfa.data.containsKey("wing")) {
					EarsLog.debug("Common:Features", "detect(...): Wings are enabled, but there's no wing texture in the alfalfa. Disabling");
					wingMode = WingMode.NONE;
				}
				return new EarsFeatures(true,
						earMode, earAnchor,
						protrusions,
						tailMode, tailBend0, tailBend1, tailBend2, tailBend3,
						snoutOffset, snoutWidth, snoutHeight, snoutDepth,
						chestSize, wingMode,
						alfalfa);
			} else {
				EarsLog.debug("Common:Features", "detect(...): Pixel at 0, 32 is not #3F23D8 (Magic Blue) - it's #{}. Disabling",  upperHex32Dbg(img.getARGB(0, 32)));
				return DISABLED;
			}
		}
		EarsLog.debug("Common:Features", "detect(...): Legacy skin, ignoring");
		return DISABLED;
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
