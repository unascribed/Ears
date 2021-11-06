package com.unascribed.ears.common.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends FilterOutputStream {
	private int buffer = 0;
	private int index = 0;

	public BitOutputStream(OutputStream out) {
		super(out);
	}

	public void writeBit(int bit) throws IOException {
		buffer <<= 1;
		buffer |= (bit & 0x01);
		index++;
		if (index >= 8) {
			out.write(buffer);
			index = 0;
			buffer = 0;
		}
	}

	@Override
	@Deprecated
	public void write(int value) throws IOException {
		if (index == -1) {
			out.write(value);
		} else {
			for(int i = 7; i >= 0; i--) {
				writeBit((value>>i) & 1);
			}
		}
	}

	public void write(boolean value) throws IOException {
		writeBit(value ? 1 : 0);
	}

	public void write(int bits, long value) throws IOException {
		if (bits < 1 || bits >= 64) {
			throw new IllegalArgumentException("bits(" + bits + ") is out of range");
		}

		long cur = Long.reverse(value) >> (64-bits);
		for(int i = 0; i < bits; i++) {
			writeBit((int)(cur&1));
			cur >>= 1;
		}
	}

	public void writeSAM(int bits, int value) throws IOException {
		write(value < 0);
		write(bits, Math.abs(value));
	}

	public void writeSAMUnit(int bits, float value) throws IOException {
		write(value < 0);
		int max = (1 << bits)-1;
		write(bits, (int)Math.abs(value*max));
	}
	
	public void writeUnit(int bits, float value) throws IOException {
		int max = (1 << bits)-1;
		write(bits, (int)Math.ceil(value*max));
	}

	@Override
	public void write(final byte[] value) throws IOException {
		if (index==0) {
			//Optimal case is optimal
			out.write(value);
		} else {
			for(byte b : value) {
				write(b);
			}
		}
	}

	public void align() throws IOException {
		while (index != 0) {
			writeBit(0);
		}
	}

	/**
	 * Close this Stream. Also closes the underlying Stream.
	 */
	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	@Override
	public void flush() throws IOException {
		align();
	}


}

