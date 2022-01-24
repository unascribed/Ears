package com.unascribed.ears.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * An immutable view into a byte array.
 */
public final class Slice {

	public static final Slice EMPTY = new Slice(new byte[0]);
	
	private final byte[] arr;
	private final int ofs;
	private final int len;

	public Slice(byte[] arr) {
		this(arr, 0, arr.length);
	}

	public Slice(byte[] arr, int ofs, int len) {
		if (ofs < 0) throw new IllegalArgumentException("offset cannot be negative");
		if (ofs > arr.length) throw new IllegalArgumentException("offset cannot be > length");
		if (ofs+len > arr.length) throw new IllegalArgumentException("slice cannot extend past the end of the array");
		this.arr = arr;
		this.ofs = ofs;
		this.len = len;
	}

	public byte get(int idx) {
		if (idx >= len) throw new IndexOutOfBoundsException(idx+" >= "+len);
		return arr[ofs+idx];
	}

	public int size() {
		return len;
	}

	public Slice slice(int offset, int length) {
		return new Slice(arr, ofs+offset, length);
	}

	public Slice slice(int offset) {
		return new Slice(arr, ofs+offset, arr.length - (ofs+offset));
	}

	public byte[] toByteArray() {
		return of(arr, ofs, len);
	}

	public void writeTo(OutputStream os) throws IOException {
		os.write(arr, ofs, len);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + get(i);
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Slice other = (Slice) obj;
		if (len != other.len) return false;
		for (int i = 0; i < len; i++) {
			if (get(i) != other.get(i)) return false;
		}
		return true;
	}

	public boolean equals(byte[] bys) {
		return equals(bys, 0, bys.length);
	}

	public boolean equals(byte[] bys, int ofs, int len) {
		if (ofs < 0 || len < 0 || ofs+len > bys.length) throw new IndexOutOfBoundsException();
		if (len != this.len) return false;
		for (int i = 0; i < len; i++) {
			if (get(i) != bys[ofs+i]) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (len == 0) return "Slice[0 bytes]";
		StringBuilder val = new StringBuilder();
		StringBuilder asc = new StringBuilder();
		for (int i = 0; i < len; i++) {
			int v = (get(i)&0xFF);
			val.append(Integer.toHexString(v|0xF00).substring(1).toUpperCase(Locale.ROOT));
			val.append(" ");
			asc.append(v < 0x20 || v > 0x7F ? '.' : (char)v);
		}
		val.setLength(val.length()-1);
		return "Slice["+len+" bytes; "+val+" | "+asc+"]";
	}

	public static byte[] of(byte[] arr, int ofs, int len) {
		byte[] dst = new byte[len];
		System.arraycopy(arr, ofs, dst, 0, len);
		return dst;
	}

	/**
	 * Convenience method to construct a Slice from a readable literal. Not intended for data
	 * parsing - no validation is performed and various errors can be thrown by this method for
	 * malformed data.
	 * <p>
	 * The format is simple; every pair of characters is interpreted as a hex byte, and any chars
	 * between square brackets ([ and ]) have their lower 8 bits passed through as-is.
	 * For example:<br/>
	 * <code>89[PNG\r\n]1A[\n]</code><br/>
	 * will become<br/>
	 * <code>89 50 4E 47 0D 0A 1A 0A | .PNG....</code>
	 */
	public static Slice parse(String str) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean inGroup = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (inGroup) {
				if (c == ']') {
					inGroup = false;
				} else {
					baos.write(c&0xFF);
				}
			} else {
				if (c == '[') {
					inGroup = true;
				} else {
					char other = str.charAt(i+1);
					int lhs = Character.digit(c, 16);
					int rhs = Character.digit(other, 16);
					int value = (lhs << 4) | rhs;
					baos.write(value);
					i++;
				}
			}
		}
		return new Slice(baos.toByteArray());
	}

}
