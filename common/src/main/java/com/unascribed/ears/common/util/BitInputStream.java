package com.unascribed.ears.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.FilterInputStream;

public class BitInputStream extends FilterInputStream {
	private int data;
	private int index = -1;

	public BitInputStream(final InputStream input) {
		super(input);
	}

	public int readBit() throws IOException {
		if (index<0) {
			data = in.read();
			if (data<0) throw new EOFException();
			index = 6;
			return (data >> 7) & 0x01;
		}
		index--;
		return (data >> index+1) & 0x01;
	}

	public boolean readBoolean() throws IOException {
		return readBit()==1;
	}

	/**
	 * Reads the next byte of data from the stream. As per the InputStream
	 * contract, if the end of the stream has been reached, -1 is returned.
	 * No guarantees are made about byte <em>alignment</em> unless {@link #align}
	 * has been called since the last bitwise read operation.
	 */
	@Override
	public int read() throws IOException {
		int result = 0;
		for(int i=0; i<8; i++) {
			int cur = readBit();
			result <<= 1;
			result |= cur;
		}
		return result;
	}

	public long readL(final int bits) throws IOException {
		if (bits < 0) throw new IllegalArgumentException("Cannot read negative bits. ("+bits+")");
		if (bits == 0) return 0;
		if (bits > 64) throw new IllegalArgumentException("Cannot fit "+bits+" into a long.");
		long result = 0L;
		for(int i=0; i<bits; i++) {
			int cur = readBit();
			result <<= 1;
			result |= cur;
		}
		return result;
	}

	public int read(final int bits) throws IOException {
		if (bits > 32) throw new IllegalArgumentException("Cannot fit "+bits+" into an int.");
		return (int)readL(bits);
	}

	/**
	 * Reads a sign-and-magnitude signed number of the given length (excluding sign bit)
	 */
	public int readSAM(final int bits) throws IOException {
		boolean s = readBoolean();
		int v = read(bits);
		return s ? -v : v;
	}

	/**
	 * Reads a sign-and-magnitude signed number of the given length (excluding sign bit), then
	 * divides it by the max value, giving a unit value from -1 to 1.
	 */
	public float readSAMUnit(final int bits) throws IOException {
		boolean s = readBoolean();
		int v = read(bits);
		int max = (1 << bits)-1;
		float f = v / (float)max;
		if (s) f = -f;
		return f;
	}
	
	public float readUnit(int bits) throws IOException {
		return read(bits)/(float)((1 << bits)-1);
	}

	/**
	 * Aligns the read marker to the start of the next byte. If
	 * the marker is already at the beginning of a byte, this
	 * method does nothing.
	 */
	public void align() throws IOException {
		while(index>0) readBit();
	}

	/**
	 * Closes this Stream. Also closes the underlying Stream.
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

}

