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
import java.util.Map;

import com.unascribed.ears.api.Slice;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.common.EarsCommon.Rectangle;
import com.unascribed.ears.common.debug.EarsLog;

/**
 * Extra data stored in the alpha channel of forced-opaque areas.
 */
public class Alfalfa {
	
	/**
	 * The portions of the skin that are forced opaque, minus the front of the head, to avoid
	 * messing up previews in MultiMC and various avatar rendering services.
	 * @see EarsCommon#FORCED_OPAQUE_REGIONS
	 */
	public static final List<Rectangle> ENCODE_REGIONS = Collections.unmodifiableList(Arrays.asList(
			new Rectangle(8, 0, 24, 8),
			new Rectangle(0, 8, 8, 16),
			new Rectangle(16, 8, 32, 16),
			
			new Rectangle(4, 16, 12, 20),
			new Rectangle(20, 16, 36, 20),
			new Rectangle(44, 16, 52, 20),
			
			new Rectangle(0, 20, 56, 32),
			
			new Rectangle(20, 48, 28, 52),
			new Rectangle(36, 48, 44, 52),
				
			new Rectangle(16, 52, 48, 64)
		));
	
	// cannot be longer than 64 entries (as if we'll ever reach that)
	private static final List<String> PREDEF_KEYS = Collections.unmodifiableList(Arrays.asList(
		"END", "wing", "erase", "cape"
	));
	
	public static final int MAGIC = 0xEA1FA1FA; // EALFALFA

	public static AlfalfaData read(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		dis.skipBytes(1);
		int magic = dis.readInt();
		if (magic != MAGIC) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Magic number does not match. Expected {}, got {}", Integer.toHexString(MAGIC), Long.toHexString(magic));
			return AlfalfaData.NONE;
		}
		int version = dis.readUnsignedByte();
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Discovered Alfalfa v{} data", version);
		if (version != 1) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Don't know how to read this version, ignoring");
			return AlfalfaData.NONE;
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
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Found entry {} with {} byte{} of data", k, data.length, data.length == 1 ? "" : "s");
		}
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Found {} entr{}", map.size(), map.size() == 1 ? "y" : "ies");
		return new AlfalfaData(version, map);
	}
	
	public static void write(AlfalfaData data, OutputStream out) throws IOException {
		if (data.version == 0) return;
		if (data.version != 1) throw new IOException("Don't know how to write Alfalfa version "+data.version);
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(MAGIC);
		dos.writeByte(data.version);
		for (Map.Entry<String, Slice> en : data.data.entrySet()) {
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

	public static AlfalfaData read(EarsImage img) {
		if (img.getWidth() != 64 || img.getHeight() != 64) return AlfalfaData.NONE;
		BigInteger bi = BigInteger.ZERO;
		int read = 0;
		for (Rectangle rect : ENCODE_REGIONS) {
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
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Found no data in alpha channel");
			return AlfalfaData.NONE;
		}
		EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Read {} ayte{} of data from alpha channel", read, read == 1 ? "" : "s");
		try {
			return read(new ByteArrayInputStream(bi.toByteArray()));
		} catch (Exception e) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "Alfalfa.read: Exception while reading data", e);
			return AlfalfaData.NONE;
		}
	}

	public static void write(AlfalfaData data, WritableEarsImage img) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(data, baos);
		byte[] bys = baos.toByteArray();
		if (bys.length > 1428) {
			throw new IllegalArgumentException("Cannot write more than 1428 bytes of data (got "+bys.length+" bytes)");
		}
		BigInteger _7F = BigInteger.valueOf(0x7F);
		BigInteger bi = new BigInteger(1, bys);
		int written = 0;
		for (Rectangle rect : ENCODE_REGIONS) {
			for (int x = rect.x1; x < rect.x2; x++) {
				for (int y = rect.y1; y < rect.y2; y++) {
					int argb = img.getARGB(x, y);
					int a = (argb>>24)&0xFF;
					if (a == 0) {
						argb = 0xFF000000;
					}
					int v = bi.shiftRight(written*7).and(_7F).intValue();
					a = (0x7F-v)|0x80;
					argb = (argb&0x00FFFFFF)|((a&0xFF) << 24);
					img.setARGB(x, y, argb);
					written++;
				}
			}
		}
	}
	
	public static AlfalfaData read(ByteArrayInputStream in) throws IOException {
		return read((InputStream)in);
	}
	
	public static void write(AlfalfaData data, ByteArrayOutputStream out) {
		try {
			write(data, (OutputStream)out);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
}