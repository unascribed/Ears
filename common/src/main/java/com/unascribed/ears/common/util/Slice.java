package com.unascribed.ears.common.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An immutable view into a byte array.
 */
public final class Slice {

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
		return "["+len+" bytes]";
	}

	public static byte[] of(byte[] arr, int ofs, int len) {
		byte[] dst = new byte[len];
		System.arraycopy(arr, ofs, dst, 0, len);
		return dst;
	}

}
